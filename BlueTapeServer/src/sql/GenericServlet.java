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

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("serial")
@MultipartConfig
public class GenericServlet extends HttpServlet
	{
	private QueryAgent qAgent;
	private RequestKeyChecker requestKeyChecker;
	private boolean forceSSL;
	private String collectionName;
	private CredentialLogic crlogic;
	private Logger logger;
	
	public void init()
		{
		String tempParam;
		tempParam=getServletConfig().getInitParameter("requestFormat");
		if(tempParam==null)
			{
			return;
			}
		else
			{
			this.requestKeyChecker=new RequestKeyChecker(tempParam);
			}
		tempParam=getServletConfig().getInitParameter("collectionName");
		if(tempParam==null)
			{
			return;
			}
		else
			{
			this.collectionName=tempParam;
			this.qAgent=new QueryAgent(tempParam);
			this.logger=LogManager.getLogger(tempParam);
			}
		this.forceSSL=Boolean.valueOf(getServletConfig().getInitParameter("ForceSSL"));
		if(this.forceSSL)	//account management is utilized only when SSL is enforced.
			{
			tempParam=getServletConfig().getInitParameter("accountCollectionName");
			if(tempParam==null)
				{
				this.crlogic=new CredentialLogic("DefaultAccountCollection");	//Default account manager collection name.
				}
			else
				{
				this.crlogic=new CredentialLogic(tempParam);
				}
			}
		if(!(new File(this.collectionName)).exists())
			{
			(new File(this.collectionName)).mkdir();
			}
		}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
		{
		
		PrintWriter writer;
		if(!request.isSecure()&&forceSSL)
			{
			response.setContentType("text/html");
			writer = response.getWriter();
			writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Unsecure Connection</p></body></html>");
			}
		else
			{
			KeyValuePair kvp;
			String [] strarray;
			String instruction;
			String action="Search";
			String auth="AUTH_ALL";
			ArrayList<TypedString> keyList=new ArrayList<TypedString>();
			ArrayList<TypedString> identifierList=new ArrayList<TypedString>();
			ArrayList<TypedString> tempArrayList=new ArrayList<TypedString>();
			ArrayList<String> fieldList=new ArrayList<String>();
			ArrayList<KeyValuePair> idpList=new ArrayList<KeyValuePair>();
			instruction=request.getParameter("Request");
			System.out.println("GET instruction="+instruction);
			String tempstr;
			String temptype;
			String tempFileName;
			HttpSession session=request.getSession();
			FileInputStream fis;
			OutputStream os;
			int readint;
			int i, j;
			if(instruction!=null)
				{
				keyList=this.requestKeyChecker.getRequestList(instruction);
				identifierList=this.requestKeyChecker.getIdentifierList(instruction);
				action=this.requestKeyChecker.getAction(instruction);
				auth=this.requestKeyChecker.getAuth(instruction);
				System.out.println("action="+action);
				if(action.equals(""))
					{
					response.setContentType("text/html");
					writer = response.getWriter();
					writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Unknown action associated to instruction: </p></body></html>"+instruction);
					this.logger.trace("Unknown action associated to instruction: "+instruction);
					return;
					}
				}
			else
				{
				action="dump";	//debug
				}
			for(i=0;i<keyList.size();i++)
				{
				if((request.getParameter(keyList.get(i).str)!=null)&&(!(request.getParameter(keyList.get(i).str)).equals("")))
					{
					fieldList.add(keyList.get(i).str);
					//single string, i.e., only the last key is recognized as the requested filename. (Only one file per request can be retrieved)
					}
				}
			if(fieldList.size()<1)
				{
				for(i=0;i<keyList.size();i++)
					{
					fieldList.add(keyList.get(i).str);
					}
				}
			for(i=0;i<identifierList.size();i++)
				{
				if((request.getParameter(identifierList.get(i).str)!=null)&&(!(request.getParameter(identifierList.get(i).str)).equals("")))
					{
					tempstr=request.getParameter(identifierList.get(i).str);
					System.out.println("Read identifier="+tempstr);
					temptype=identifierList.get(i).type;
					tempArrayList=new ArrayList<TypedString>();
					if(temptype.equals("enc"))
						{
						tempArrayList.add(new TypedString(Tools.sha256(tempstr), "enc"));
						//1. A hash tag is considered a normal character in an encoded field. Thus the array has at most one row.
						//2. The data is handled in database as a normal string.
						//3. These measures make it impossible to "search" through an encoded field, which can possibly store passwords.
						//4. Encoded fields can only be used as database find/match identifiers; encoded fields can never be exposed outside.
						}
					else
						{
						if(tempstr.startsWith("#"))
							{
							tempArrayList.add(new TypedString("LT", temptype));	//put a mark to the first entry of the array list.
							}
						strarray=tempstr.split("#");
						for(j=0;j<strarray.length;j++)
							{
							if(!strarray[j].equals(""))
								{
								tempArrayList.add(new TypedString(strarray[j], temptype));
								}
							}
						if(tempstr.endsWith("#"))
							{
							tempArrayList.add(new TypedString("GT", temptype));	//put an empty entry to the last entry of the array list.
							}
						}
					kvp=new KeyValuePair(identifierList.get(i).str, tempArrayList);
					idpList.add(kvp);
					}
				}
			
			if (action.equals("Search"))
				{
				if(this.crlogic.validSession(session.getId(), auth))
				//if((auth.equals("AUTH_ALL"))||(this.crlogic.validSession(session.getId())))
					{
					for(i=0;i<idpList.size();i++)
						{
						System.out.println("Search String: "+idpList.get(i).toString());
						}
					response.setContentType("text/html");
					writer = response.getWriter();
					writer.write(this.qAgent.query(idpList, fieldList));
					}
				else
					{
					writer = response.getWriter();
					writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Login required for instruction: </p></body></html>"+instruction);
					}
				}
			else
				{
				if(action.equals("GetFile"))
					{
					if(this.crlogic.validSession(session.getId(), auth))
					//if((auth.equals("AUTH_ALL"))||(this.crlogic.validSession(session.getId())))
						{
						tempFileName=request.getParameter(keyList.get(keyList.size()-1).str);
						System.out.println("tempFileName="+tempFileName);
						if(this.qAgent.checkExistence(idpList, keyList.get(keyList.size()-1).str, tempFileName))	//single string, i.e., only the last key is recognized as the requested filename. (Only one file per request can be retrieved)
							{
							System.out.println("check file existence success");
							fis=new FileInputStream(this.collectionName+File.separator+tempFileName);
							os=response.getOutputStream();
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
						writer = response.getWriter();
						writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Login required for instruction: </p></body></html>"+instruction);
						}
					}
				else
					{
					response.setContentType("text/html");
					writer = response.getWriter();
					writer.write(this.requestKeyChecker.dump());
					}
				}
			}
		}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
		{
		PrintWriter writer = response.getWriter();
		response.setContentType("text/html"); // required
		if(!request.isSecure()&&forceSSL)
			{
			writer = response.getWriter();
			writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Unsecure Connection</p></body></html>");
			}
		else
			{
			KeyValuePair kvp;
			String [] strarray;
			String instruction;
			String action="Search";	//default action, I make it a read-only action to reduce potential demage to the database.
			String auth="AUTH_ALL";
			ArrayList<TypedString> keyList=new ArrayList<TypedString>();
			ArrayList<TypedString> identifierList=new ArrayList<TypedString>();
			ArrayList<TypedString> fileList=new ArrayList<TypedString>();
			ArrayList<TypedString> tempArrayList=new ArrayList<TypedString>();
			ArrayList<KeyValuePair> kvpList=new ArrayList<KeyValuePair>();
			ArrayList<KeyValuePair> idpList=new ArrayList<KeyValuePair>();
			Part filePart;
			instruction=request.getParameter("Request");
			Random r=new Random(System.currentTimeMillis());
			String tempstr;
			String temptype;
			String tempFileName;
			HttpSession session=request.getSession();
			int i, j;
			if(instruction!=null)
				{
				keyList=this.requestKeyChecker.getRequestList(instruction);
				identifierList=this.requestKeyChecker.getIdentifierList(instruction);
				fileList=this.requestKeyChecker.getFileList(instruction);
				action=this.requestKeyChecker.getAction(instruction);
				auth=this.requestKeyChecker.getAuth(instruction);
				if(action.equals(""))
					{
					writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Unknown action associated to instruction: </p></body></html>"+instruction);
					this.logger.trace("Unknown action associated to instruction: "+instruction);
					return;
					}
				}
			for(i=0;i<keyList.size();i++)
				{
				if((request.getParameter(keyList.get(i).str)!=null)&&(!(request.getParameter(keyList.get(i).str)).equals("")))
					{
					tempstr=request.getParameter(keyList.get(i).str);
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
					//System.out.println("Read Value="+tempstr+", type="+temptype);
					kvp=new KeyValuePair(keyList.get(i).str, tempArrayList);
					kvpList.add(kvp);
					}
				}
			for(i=0;i<identifierList.size();i++)
				{
				if((request.getParameter(identifierList.get(i).str)!=null)&&(!(request.getParameter(identifierList.get(i).str)).equals("")))
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
			
			this.logger.trace("Go into the server");
			if (action.equals("Add"))
				{
				if(this.crlogic.validSession(session.getId(), auth))
					{
					try
						{
						this.logger.trace("Incoming Add request");
						this.qAgent.addData(idpList, kvpList);
						}
					catch (Exception e)
						{
						e.printStackTrace();
						}
					}
				else
					{
					if(tempFileNameList.size()>0)
						{
						Tools.fileDeletor(this.collectionName, tempFileNameList);	//reject uploaded files from the file system.
						}
					writer = response.getWriter();
					writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Login required for instruction: </p></body></html>"+instruction);
					}
				}
			else
				{
				if (action.equals("Update"))
					{
					if(this.crlogic.validSession(session.getId(), auth))
						{
						this.logger.trace("Incoming Update request");
						ArrayList<String> filesToDelete=this.qAgent.deleteData(idpList);
						Tools.fileDeletor(this.collectionName, filesToDelete);
						this.qAgent.addData(idpList, kvpList);
						}
					else
						{
						if(tempFileNameList.size()>0)
							{
							Tools.fileDeletor(this.collectionName, tempFileNameList);	//reject uploaded files from the file system.
							}
						writer = response.getWriter();
						writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Login required for instruction: </p></body></html>"+instruction);
						}
					}
				else
					{
					if (action.equals("Delete"))
						{
						if(this.crlogic.validSession(session.getId(), auth))
							{
							this.logger.trace("Incoming Delete request");
							ArrayList<String> filesToDelete=this.qAgent.deleteData(idpList);
							Tools.fileDeletor(this.collectionName, filesToDelete);
							}
						else
							{
							writer = response.getWriter();
							writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Login required for instruction: </p></body></html>"+instruction);
							}
						}
					else
						{
						if(action.equals("Push"))
							{
							if(this.crlogic.validSession(session.getId(), auth))
								{
								System.out.println(kvpList.toString());
								this.qAgent.pushValues(idpList, kvpList);
								}
							else
								{
								if(tempFileNameList.size()>0)
									{
									Tools.fileDeletor(this.collectionName, tempFileNameList);	//reject uploaded files from the file system.
									}
								writer = response.getWriter();
								writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Login required for instruction: </p></body></html>"+instruction);
								}
							}
						else
							{
							if(action.equals("Pull"))
								{
								if(this.crlogic.validSession(session.getId(), auth))
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
												if(this.qAgent.checkExistence(idpList, (kvpList.get(i)).getKey(), (searchedFiles.get(j)).str))
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
									this.qAgent.pullValues(idpList, kvpList);
									Tools.fileDeletor(this.collectionName, filesToDelete);
									}
								else
									{
									writer = response.getWriter();
									writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Login required for instruction: </p></body></html>"+instruction);
									}
								}
							else
								{
								if(tempFileNameList.size()>0)
									{
									Tools.fileDeletor(this.collectionName, tempFileNameList);	//reject uploaded files from the file system.
									}
								writer.print("<!DOCTYPE html><html><body><h1>Error!</h1><p>Unknown Request.</p></body></html>");
								this.logger.trace("Unknown request");
								return;
								}
							}
						}
					}
				}
			}
		}
	}
