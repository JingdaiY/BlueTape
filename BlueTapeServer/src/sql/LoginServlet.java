package sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet
	{
	private CredentialLogic crlogic;
	private RequestKeyChecker requestKeyChecker;
	private ConfirmEmailGenerator confirmEmailGenerator;
	private String collectionName;
	private LoginAgent gAgent;
	private Logger logger;
	
	public void init()
		{
		String tempParam;
		tempParam=getServletConfig().getInitParameter("requestFormat");
		if(tempParam==null)
			{
			//System.out.println("No request formal XML");
			return;
			}
		else
			{
			this.requestKeyChecker=new RequestKeyChecker(tempParam);
			}
		tempParam=getServletConfig().getInitParameter("collectionName");
		if(tempParam==null)
			{
			this.collectionName="DefaultAccountCollection";
			}
		else
			{
			this.collectionName=tempParam;
			}
		System.out.println("collection name="+this.collectionName);
		tempParam=getServletConfig().getInitParameter("confirmEmailTemplate");
		if(tempParam==null)
			{
			return;
			}
		else
			{
			this.confirmEmailGenerator=new ConfirmEmailGenerator(tempParam);
			}
		this.crlogic=new CredentialLogic(this.collectionName);
		this.gAgent=new LoginAgent(this.collectionName);
		this.logger=LogManager.getLogger(this.collectionName);
		}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
		{
		PrintWriter writer=response.getWriter();
		response.setContentType("text/html");
		
		if(!request.isSecure())
			{
			writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Unsecure Connection</p></body></html>");
			return;
			}
		else
			{
			int i, j;
			KeyValuePair kvp;
			String [] strarray;
			String instruction;
			String action="";
			ArrayList<TypedString> keyList=new ArrayList<TypedString>();	//In login context, "key" means extra information to collect but not essential to complete the task.
			ArrayList<TypedString> identifierList=new ArrayList<TypedString>();
			ArrayList<TypedString> tempArrayList=new ArrayList<TypedString>();
			ArrayList<KeyValuePair> kvpList=new ArrayList<KeyValuePair>();
			ArrayList<KeyValuePair> idpList=new ArrayList<KeyValuePair>();
			ArrayList<String> fieldList=new ArrayList<String>();
			String tempstr, tempkey;
			String temptype;
			HttpSession session=request.getSession();
			instruction=request.getParameter("Request");
			Random r=new Random(System.currentTimeMillis());
			System.out.println("GET instruction="+instruction);
			if(instruction!=null)
				{
				keyList=this.requestKeyChecker.getRequestList(instruction);	//Extra key field can still be customized
				identifierList=this.requestKeyChecker.getIdentifierList(instruction);	//Extra id fields can still be customized
				action=this.requestKeyChecker.getAction(instruction);
				if(action.equals(""))
					{
					writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Unknown action associated to instruction: </p></body></html>"+instruction);
					this.logger.trace("Unknown action associated to instruction: "+instruction);
					return;
					}
				}
			else
				{
				return;
				}
			for(i=0;i<keyList.size();i++)
				{
				tempkey=keyList.get(i).str;
				if(!Tools.checkReserveKeys(tempkey))
				//if((!tempkey.equals("Password"))&&(!tempkey.equals("UserId"))&&(!tempkey.equals("sessionId"))&&(!tempkey.equals("sessionTime")))	//This credential related fields can never be exposed.
					{
					if((request.getParameter(tempkey)!=null)&&(!(request.getParameter(tempkey)).equals("")))
						{
						tempstr=request.getParameter(tempkey);
						temptype=keyList.get(i).type;
						if(temptype.equals("enc"))
							{
							tempArrayList.add(new TypedString(Tools.sha256(tempstr), "enc"));
							}
						else
							{
							strarray=tempstr.split("#");
							tempArrayList=new ArrayList<TypedString>();
							for(j=0;j<strarray.length;j++)
								{
								tempArrayList.add(new TypedString(strarray[j], temptype));
								}
							}
						kvp=new KeyValuePair(tempkey, tempArrayList);
						kvpList.add(kvp);
						fieldList.add(tempkey);
						}
					}
				}
			if(fieldList.size()<1)		//If no valid field specified from the incoming request, present all the field specified in XML.
				{
				for(i=0;i<keyList.size();i++)
					{
					tempstr=keyList.get(i).str;
					if(!Tools.checkReserveKeys(tempstr))
					//if((!tempstr.equals("Password"))&&(!tempstr.equals("UserId"))&&(!tempstr.equals("sessionId"))&&(!tempstr.equals("sessionTime")))
						{
						fieldList.add(keyList.get(i).str);
						}
					}
				}
			for(i=0;i<identifierList.size();i++)
				{
				tempkey=identifierList.get(i).str;
				if((!Tools.checkReserveKeys(tempkey))&&(request.getParameter(tempkey)!=null)&&(!(request.getParameter(tempkey)).equals("")))
				//if((!tempkey.equals("Password"))&&(!tempkey.equals("UserId"))&&(!tempkey.equals("sessionId"))&&(!tempkey.equals("sessionTime"))&&(request.getParameter(tempkey)!=null)&&(!(request.getParameter(tempkey)).equals("")))	//Those fields cannot be specified by XML file to prevent exposing user database field due to misconfiguration.
					{
					tempstr=request.getParameter(tempkey);
					temptype=identifierList.get(i).type;
					tempArrayList=new ArrayList<TypedString>();
					if(temptype.equals("enc"))
						{
						tempArrayList.add(new TypedString(Tools.sha256(tempstr), "enc"));
						}
					else
						{
						tempArrayList.add(new TypedString(tempstr, temptype));
						}
					kvp=new KeyValuePair(tempkey, tempArrayList);
					idpList.add(kvp);
					}
				}
			if(action.equals("Login"))
				{
				tempArrayList=new ArrayList<TypedString>();
				tempArrayList.add(new TypedString(request.getParameter("UserId"), "string"));
				idpList.add(new KeyValuePair("UserId", tempArrayList));
				tempArrayList=new ArrayList<TypedString>();
				tempArrayList.add(new TypedString(Tools.sha256(request.getParameter("Password")), "enc"));
				idpList.add(new KeyValuePair("Password", tempArrayList));
				if(this.crlogic.checkPassword(idpList))
					{
					System.out.println("Session ID="+session.getId());
					this.crlogic.markSession(idpList, session.getId());
					//this.gAgent.pushValues(idpList, kvpList);	//Extra information collection;
					writer.print("<!DOCTYPE html><html><body><h1>Success</h1><p>Login Successfully.</p></body></html>");
					}
				else
					{
					writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Login Failed</p></body></html>");
					return;
					}
				}
			else
				{
				if(action.equals("Logout"))
					{
					tempArrayList=new ArrayList<TypedString>();
					tempArrayList.add(new TypedString(request.getParameter("UserId"), "string"));
					idpList.add(new KeyValuePair("UserId", tempArrayList));
					String sessionId=session.getId();
					System.out.println("session="+sessionId);
					if(this.crlogic.validSession(idpList, sessionId))
						{
						this.gAgent.pushValues(idpList, kvpList);	//Extra information collection;
						this.crlogic.endSession(idpList, sessionId);
						writer.print("<!DOCTYPE html><html><body><h1>Success</h1><p>Logout Successfully.</p></body></html>");
						}
					else
						{
						writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Logout Failed</p></body></html>");
						}
					return;
					}
				else
					{
					if(action.equals("RequestConfirmEmail"))
						{
						String sessionId=session.getId();
						if(this.crlogic.validSession(sessionId))
							{
							long confirmCode=(r.nextLong())%1000000;	//Six-digit confirmation code;
							String userid=this.crlogic.getUserId(sessionId);
							System.out.println("Sending email to "+userid);
							tempArrayList=new ArrayList<TypedString>();
							tempArrayList.add(new TypedString(Tools.sha256(sessionId), "enc"));
							idpList.add(new KeyValuePair("sessionId", tempArrayList));
							this.gAgent.pushValues(idpList, kvpList);
							this.gAgent.replaceValue(idpList, new KeyValuePair("ConfirmCode", new TypedString(Long.toString(confirmCode), "string")));
							try
								{
								SendEmail.send(userid, this.confirmEmailGenerator.getEmailAddress(), this.confirmEmailGenerator.getEmailPassword(), this.confirmEmailGenerator.getSubject(), this.confirmEmailGenerator.getConfirmationEmail(confirmCode, userid));
								}
							catch (Exception e)
								{
								e.printStackTrace();
								}
							}
						else
							{
							response.setContentType("text/html");
							writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Login required for instruction: </p></body></html>"+instruction);
							}
						return;
						}
					else
						{
						if(action.equals("ConfirmCode"))
							{
							writer = response.getWriter();
							response.setContentType("text/html");
							idpList=new ArrayList<KeyValuePair>();
							idpList.add(new KeyValuePair("UserId", new TypedString(request.getParameter("UserId"), "string")));
							idpList.add(new KeyValuePair("ConfirmCode", new TypedString(request.getParameter("ConfirmCode"), "string")));
							if(this.gAgent.checkExistence(idpList))
								{
								this.crlogic.setAccessRight(idpList, "Auth_Email_Confirmed");
								this.gAgent.removeField(idpList, "ConfirmCode");
								writer.print("<!DOCTYPE html><html><body><h1>Email Confirmed</h1><p>You email address is confirmed.</p></body></html>");
								}
							else
								{
								this.gAgent.removeField(idpList, "ConfirmCode");
								writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Incorrect confirmation code.</p></body></html>");
								}	
							}
						else
							{
							String sessionId=session.getId();
							writer = response.getWriter();
							ArrayList<KeyValuePair> altIdList=new ArrayList<KeyValuePair>();
							altIdList.add(new KeyValuePair("sessionId", new TypedString(Tools.sha256(sessionId), "enc")));	//Session Id is the only identifier to access the user data. 
							if(this.crlogic.validSession(sessionId))
								{
								if(action.equals("Search"))	//No one can actually "search" through the user database. Instead, people can only retrieve their own information identified by sessionId.
									{
									response.setContentType("text/html");
									if(fieldList.size()<1)
										{
										writer.write(this.gAgent.query(altIdList, fieldList));
										}
									else
										{
										writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>No field specified for instruction: "+instruction+"</p></body></html>");
										return;
										}
									}
								else
									{
									if(action.equals("GetFile"))
										{
										String tempFileName=request.getParameter(keyList.get(keyList.size()-1).str);
										System.out.println("tempFileName="+tempFileName);
										if(this.gAgent.checkExistence(altIdList, keyList.get(keyList.size()-1).str, tempFileName))	//single string, i.e., only the last key is recognized as the requested filename. (Only one file per request can be retrieved)
											{
											int readint;
											System.out.println("check file existence success");
											FileInputStream fis=new FileInputStream(this.collectionName+File.separator+tempFileName);
											OutputStream os=response.getOutputStream();
											while((readint=fis.read())!=-1)
												{
												os.write(readint);
												}
											os.close();
											fis.close();
											}
										}
									else
										{
										response.setContentType("text/html");
										writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Unsupported action: "+action+" from instruction: "+instruction+"</p></body></html>");
										return;
										}
									}
								}
							else
								{
								response.setContentType("text/html");
								writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Login required for instruction: </p></body></html>"+instruction);
								return;
								}
							}
						}	
					}
				}
			}
		}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
		{
		PrintWriter writer=response.getWriter();
		response.setContentType("text/html");
		if(!request.isSecure())
			{
			writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Unsecure Connection</p></body></html>");
			return;
			}
		else
			{
			KeyValuePair kvp;
			String [] strarray;
			ArrayList<TypedString> keyList=new ArrayList<TypedString>();	//In login context, "key" means extra information to collect but not essential to complete the task.
			ArrayList<TypedString> identifierList=new ArrayList<TypedString>();
			ArrayList<TypedString> fileList=new ArrayList<TypedString>();
			ArrayList<TypedString> tempArrayList=new ArrayList<TypedString>();
			ArrayList<KeyValuePair> kvpList=new ArrayList<KeyValuePair>();
			ArrayList<KeyValuePair> idpList=new ArrayList<KeyValuePair>();
			Part filePart;
			String action="";
			String instruction=request.getParameter("Request");
			Random r=new Random(System.currentTimeMillis());
			String tempstr, tempkey;
			String temptype;
			HttpSession session=request.getSession();
			String tempFileName;
			int i, j;
			if(instruction!=null)
				{
				keyList=this.requestKeyChecker.getRequestList(instruction);
				identifierList=this.requestKeyChecker.getIdentifierList(instruction);
				fileList=this.requestKeyChecker.getFileList(instruction);
				action=this.requestKeyChecker.getAction(instruction);
				if(action.equals(""))
					{
					writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Unknown action associated to instruction: </p></body></html>"+instruction);
					this.logger.trace("Unknown action associated to instruction: "+instruction);
					return;
					}
				}
			else
				{
				return;
				}
			for(i=0;i<keyList.size();i++)
				{
				tempkey=keyList.get(i).str;
				if((!Tools.checkReserveKeys(tempkey))&&(request.getParameter(tempkey)!=null)&&(!(request.getParameter(tempkey)).equals("")))
				//if((!tempkey.equals("Password"))&&(!tempkey.equals("UserId"))&&(!tempkey.equals("sessionId"))&&(!tempkey.equals("sessionTime"))&&(request.getParameter(tempkey)!=null)&&(!(request.getParameter(tempkey)).equals("")))
					{
					tempstr=request.getParameter(tempkey);
					temptype=keyList.get(i).type;
					if(temptype.equals("enc"))
						{
						tempArrayList.add(new TypedString(Tools.sha256(tempstr), "enc"));
						}
					else
						{
						strarray=tempstr.split("#");
						tempArrayList=new ArrayList<TypedString>();
						for(j=0;j<strarray.length;j++)
							{
							tempArrayList.add(new TypedString(strarray[j], temptype));
							}
						}
					kvp=new KeyValuePair(tempkey, tempArrayList);
					kvpList.add(kvp);
					}
				}
			for(i=0;i<identifierList.size();i++)
				{
				tempkey=identifierList.get(i).str;
				if((!Tools.checkReserveKeys(tempkey))&&(request.getParameter(tempkey)!=null)&&(!(request.getParameter(tempkey)).equals("")))
				//if((!identifierList.get(i).str.equals("Password"))&&(!identifierList.get(i).str.equals("UserId"))&&(request.getParameter(identifierList.get(i).str)!=null)&&(!(request.getParameter(identifierList.get(i).str)).equals("")))
					{
					tempstr=request.getParameter(identifierList.get(i).str);
					temptype=identifierList.get(i).type;
					if(temptype.equals("enc"))
						{
						tempArrayList.add(new TypedString(Tools.sha256(tempstr), "enc"));
						}
					else
						{
						strarray=tempstr.split("#");
						tempArrayList=new ArrayList<TypedString>();
						for(j=0;j<strarray.length;j++)
							{
							tempArrayList.add(new TypedString(strarray[j], temptype));
							}
						}
					kvp=new KeyValuePair(identifierList.get(i).str, tempArrayList);
					idpList.add(kvp);
					}
				}
			String ext="";
			int readint;
			boolean fileExists=false;
			String clientFileName;
			ArrayList<String> tempFileNameList=new ArrayList<String>();
			
			for(i=0;i<fileList.size();i++)
				{
				fileExists=false;
				//System.out.println(fileList.get(i).str);
				filePart=request.getPart(fileList.get(i).str);
				//System.out.println(filePart);
				System.out.println(filePart.getHeader("content-disposition"));
				strarray=filePart.getHeader("content-disposition").split(";");
				for(j=0;j<strarray.length;j++)
					{
					if((strarray[j].trim()).startsWith("filename"))
						{
						if((strarray[j].trim()).split("\"").length<2)
							{
							fileExists=false;
							}
						else
							{
							clientFileName=(strarray[j].trim()).split("\"")[1];
							System.out.println("client file name="+clientFileName);
							if(clientFileName.equals(""))
								{
								fileExists=false;
								}
							else
								{
								fileExists=true;
								ext=clientFileName.substring(clientFileName.indexOf('.')+1);
								System.out.println("Extension="+ext);
								if(!(fileList.get(i).type).contains(ext))	//check whether file extension is legitimate
									{
									this.logger.error("Unsupported file type: "+ext);
									ext=(fileList.get(i).type.split("#"))[0];	//Use the first file extension in the list as default. Although something goes wrong anyway in this case.
									}
								}
							}
						j=strarray.length+1;	//break out of the loop
						}
					}
				if(fileExists)
					{
					tempFileName=Long.toString(r.nextLong());
					File tempfile=new File(this.collectionName+File.separator+tempFileName+"."+ext);
					while(tempfile.exists())
						{
						tempFileName=Long.toString(r.nextLong());
						tempfile=new File(this.collectionName+File.separator+tempFileName+"."+ext);	//Regenerate another random file name until it doesn't collide 
						}
					InputStream is=filePart.getInputStream();
					FileOutputStream fos=new FileOutputStream(tempfile);
					while((readint=is.read())!=-1)
						{
						fos.write(readint);
						}
					tempArrayList=new ArrayList<TypedString>();
					tempArrayList.add(new TypedString(tempFileName+"."+ext, "file"));
					tempFileNameList.add(tempFileName+"."+ext);
					kvp=new KeyValuePair(fileList.get(i).str, tempArrayList);
					kvpList.add(kvp);
					fos.close();
					}
				}
			
			
			if(action.equals("Register"))
				{
				String tempUserId=request.getParameter("UserId");
				if((tempUserId!=null)&&(!tempUserId.contains("#")))
					{
					tempArrayList=new ArrayList<TypedString>();
					tempArrayList.add(new TypedString(tempUserId, "string"));
					idpList.add(new KeyValuePair("UserId", tempArrayList));
					String tempPassword=request.getParameter("Password");
					if(tempPassword!=null)
						{
						tempArrayList=new ArrayList<TypedString>();
						tempArrayList.add(new TypedString(Tools.sha256(tempPassword), "enc"));
						kvpList.add(new KeyValuePair("Password", tempArrayList));
						if(this.gAgent.checkExistence(idpList))
							{
							writer.print("<!DOCTYPE html><html><body><h1>Registration Error!</h1><p>User already existed!</p></body></html>");
							this.logger.trace("Registration error. The user already exists.");
							return;
							}
						else
							{
							this.gAgent.addData(idpList, kvpList);
							this.crlogic.markSession(idpList, session.getId());
							this.logger.trace("Successfully add user "+tempUserId);
							System.out.println("Successfully add user "+tempUserId);
							}
						}
					else
						{
						writer.print("<!DOCTYPE html><html><body><h1>Registration Error!</h1><p>No password provided</p></body></html>");
						this.logger.trace("Registration error. No password provided.");
						return;
						}
					}
				else
					{
					writer.print("<!DOCTYPE html><html><body><h1>Registration Error!</h1><p>Invalid user id</p></body></html>");
					this.logger.trace("Registration error. No valid user id provided.");
					return;
					}
				}
			else
				{
				if(action.equals("ChangePassword"))		//Since user id and password are required when changing password, this action doesn't check the session.
					{
					tempstr=request.getParameter("UserId");
					if(tempstr!=null)
						{
						tempArrayList=new ArrayList<TypedString>();
						tempArrayList.add(new TypedString(tempstr, "string"));
						idpList.add(new KeyValuePair("UserId", tempArrayList));
						}
					else
						{
						writer.print("<!DOCTYPE html><html><body><h1>ChangePassword Error!</h1><p>No user id</p></body></html>");
						this.logger.trace("Registration error. No user id provided.");
						return;
						}
					tempstr=request.getParameter("OldPassword");
					if(tempstr!=null)
						{
						tempArrayList=new ArrayList<TypedString>();
						tempArrayList.add(new TypedString(Tools.sha256(tempstr), "enc"));
						idpList.add(new KeyValuePair("Password", tempArrayList));
						}
					else
						{
						writer.print("<!DOCTYPE html><html><body><h1>ChangePassword Error!</h1><p>No previous password</p></body></html>");
						this.logger.trace("Registration error. No previous password provided.");
						return;
						}
					if(this.crlogic.checkPassword(idpList))
						{
						tempstr=request.getParameter("NewPassword");	//it is up to the client to do the confirmation of new password.
						if(tempstr!=null)
							{
							tempArrayList=new ArrayList<TypedString>();
							tempArrayList.add(new TypedString(Tools.sha256(tempstr), "enc"));
							this.gAgent.replaceValue(idpList, new KeyValuePair("Passwod", tempArrayList));
							}
						else
							{
							writer.print("<!DOCTYPE html><html><body><h1>ChangePassword Error!</h1><p>No new password</p></body></html>");
							this.logger.trace("Registration error. No new password provided.");
							return;
							}
						}
					else
						{
						writer.print("<!DOCTYPE html><html><body><h1>Changing Password Error</h1><p>User ID doesn't match or wrong password!</p></body></html>");
						this.logger.trace("Changing password error. The user ID doesn't match the previous password.");
						return;
						}
					}
				else
					{
					if(action.equals("RequestAuth"))
						{
						
						}
					else
						{
						String sessionId=session.getId();
						writer = response.getWriter();
						ArrayList<KeyValuePair> altIdList=new ArrayList<KeyValuePair>();
						altIdList.add(new KeyValuePair("sessionId", new TypedString(Tools.sha256(sessionId), "enc")));	//Session Id is the only identifier to access the user data. 
						if(this.crlogic.validSession(sessionId))
							{
							if(action.equals("Push"))	//Delete and update actions are not supported in this servlet.
								{
								System.out.println(kvpList.toString());
								this.gAgent.pushValues(altIdList, kvpList);
								}
							else
								{
								if(action.equals("Pull"))
									{
									ArrayList<String> filesToDelete=new ArrayList<String>();
									ArrayList<TypedString> searchedFiles=new ArrayList<TypedString>();
									for(i=0;i<kvpList.size();i++)
										{
										if((((kvpList.get(i)).getValues()).get(0)).type.equals("file"))
											{
											searchedFiles=(kvpList.get(i)).getValues();
											for(j=0;j<searchedFiles.size();j++)
												{
												if(this.gAgent.checkExistence(altIdList, (kvpList.get(i)).getKey(), (searchedFiles.get(j)).str))
													{
													filesToDelete.add((searchedFiles.get(j).str));
													}
												else
													{
													searchedFiles.remove(j);
													System.out.println("File to be deleted not found: "+(searchedFiles.get(j)).str);
													}
												}
											kvpList.set(i, new KeyValuePair((kvpList.get(i)).getKey(), searchedFiles));	//replace this entry of key with only existing file names;
											}
										}
									this.gAgent.pullValues(idpList, kvpList);
									Tools.fileDeletor(this.collectionName, filesToDelete);
									}
								else
									{
									writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Unknown Request.</p></body></html>");
									this.logger.trace("Unknown request");
									return;
									}
								}
							}
						else
							{
							if(tempFileNameList.size()>0)
								{
								Tools.fileDeletor(this.collectionName, tempFileNameList);	//reject uploaded files from the file system.
								}
							response.setContentType("text/html");
							writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Login required for instruction: </p></body></html>"+instruction);
							return;
							}
						}
					}
				}
			}
		}
	}
