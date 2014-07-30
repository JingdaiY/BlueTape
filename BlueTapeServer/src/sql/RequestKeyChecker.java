package sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RequestKeyChecker
	{
	private KeyListArray RequestList;
	private KeyListArray IdentifierList;
	private KeyListArray FileList;
	private ArrayList<ActionTable> ActionList;
	private static Logger logger=LogManager.getLogger("requestchecker");
	
	public RequestKeyChecker(String inFilename)
		{
		String readline;
		String [] readstrs;
		//KeyList tempKeyList;
		String tempInstruction="";
		String tempAction="";
		String tempAuth="";
		TypedString tempTypedString=new TypedString();
		ArrayList<TypedString> keyList=new ArrayList<TypedString>();
		ArrayList<TypedString> idList=new ArrayList<TypedString>();
		ArrayList<TypedString> fList=new ArrayList<TypedString>();
		boolean hasIdentifier=false;
		boolean hasFile=false;
		this.RequestList=new KeyListArray();
		this.IdentifierList=new KeyListArray();
		this.FileList=new KeyListArray();
		this.ActionList=new ArrayList<ActionTable>();
		int i;
		try
			{
			File requestXML=new File(inFilename+".xml");
			FileInputStream fis=new FileInputStream(requestXML);
			BufferedReader br=new BufferedReader(new InputStreamReader(fis));
			while((readline=br.readLine())!=null)
				{
				if((readline.contains("<request")))
					{
					tempInstruction="";
					tempAction="";
					tempAuth="";
					tempTypedString=new TypedString();
					keyList=new ArrayList<TypedString>();
					idList=new ArrayList<TypedString>();
					fList=new ArrayList<TypedString>();
					hasIdentifier=false;
					hasFile=false;
					readstrs=readline.split("(<request )|(>)");
					//logger.error(readstrs[1]);
					readstrs=(readstrs[1]).split("\"");
					for(i=0;i<readstrs.length-1;i++)
						{
						//logger.error(readstrs[i]);
						if(readstrs[i].contains("name="))
							{
							tempInstruction=readstrs[i+1];
							}
						if(readstrs[i].contains("action="))
							{
							tempAction=readstrs[i+1];
							}
						if(readstrs[i].contains("auth="))
							{
							tempAuth=readstrs[i+1];
							}
						}
					if(tempAction.equals("")||(tempInstruction.equals("")))
						{
						logger.error("Action="+tempAction+", Instruction="+tempInstruction+". Missing proper request attributes in "+inFilename+".xml");
						}
					else
						{
						if(tempAuth.equals(""))
							{
							this.ActionList.add(new ActionTable(tempInstruction, tempAction, "AUTH_ALL"));
							}
						else
							{
							this.ActionList.add(new ActionTable(tempInstruction, tempAction, tempAuth));
							}
						}
					}
				else
					{
					if((readline.contains("<identifier")))
						{
						hasIdentifier=true;
						tempTypedString=new TypedString();
						readstrs=readline.split("</?identifier>?");
						readstrs=readstrs[1].split(">");
						tempTypedString.str=readstrs[1];
						readstrs=(readstrs[0]).split("\"");
						for(i=0;i<readstrs.length-1;i++)
							{
							if(readstrs[i].contains("type="))
								{
								tempTypedString.type=readstrs[i+1];
								}
							}
						if((tempTypedString.str.equals(""))||(tempTypedString.type.equals("")))
							{
							logger.error("corrupt XML: line="+readline+" in "+inFilename+".xml");
							}
						else
							{
							idList.add(tempTypedString);
							}
						}
					else
						{
						if((readline.contains("<key")))
							{
							tempTypedString=new TypedString();
							readstrs=readline.split("</?key>?");
							readstrs=readstrs[1].split(">");
							tempTypedString.str=readstrs[1];
							readstrs=(readstrs[0]).split("\"");
							//logger.error(readstrs[1]);
							for(i=0;i<readstrs.length-1;i++)
								{
								if(readstrs[i].contains("type="))
									{
									tempTypedString.type=readstrs[i+1];
									}
								}
							if((tempTypedString.str.equals(""))||(tempTypedString.type.equals("")))
								{
								logger.error("corrupt XML: line="+readline+" in "+inFilename+".xml");
								}
							else
								{
								keyList.add(tempTypedString);
								}
							}
						else
							{
							if((readline.contains("<file")))
								{
								hasFile=true;
								tempTypedString=new TypedString();
								readstrs=readline.split("</?file>?");
								readstrs=readstrs[1].split(">");
								tempTypedString.str=readstrs[1];
								readstrs=(readstrs[0]).split("\"");
								//readstrs=(readstrs[1]).split("(\")|(>)");
								for(i=0;i<readstrs.length-1;i++)
									{
									if(readstrs[i].contains("type="))
										{
										tempTypedString.type=readstrs[i+1];
										}
									}
								//readstrs=readstrs[1].split(">");
								//tempTypedString.str=readstrs[1];
								if((tempTypedString.str.equals(""))||(tempTypedString.type.equals("")))
									{
									logger.error("corrupt XML: line="+readline+" in "+inFilename+".xml");
									}
								else
									{
									fList.add(tempTypedString);
									}
								}
							else
								{
								if((readline.contains("</request>")))
									{
									this.RequestList.add(new KeyValuePair(tempInstruction, keyList));
									if(hasIdentifier)
										{
										this.IdentifierList.add(new KeyValuePair(tempInstruction, idList));
										}
									if(hasFile)
										{
										this.FileList.add(new KeyValuePair(tempInstruction, fList));
										}
									}
								else
									{
									logger.error("corrupt XML: line="+readline+" in "+inFilename+".xml");
									}
								}
							}
						}
					}
				}
			br.close();
			}
		catch(Exception e)
			{
			logger.error(e);
			//e.printStackTrace();
			}
		}
	
	public ArrayList<TypedString> getRequestList(String inKey)
		{
		return this.RequestList.getTypedString(inKey);
		}
	
	public ArrayList<TypedString> getIdentifierList(String inKey)
		{
		return this.IdentifierList.getTypedString(inKey);
		}
	
	public ArrayList<TypedString> getFileList(String inKey)
		{
		return this.FileList.getTypedString(inKey);
		}
	
	protected String dump()
		{
		int i;
		StringWriter sw=new StringWriter();
		sw.write("KeyList<br>");
		for(i=0;i<this.RequestList.size();i++)
			{
			sw.write((this.RequestList.getKeyList(i)).toString()+"<br>");
			}
		sw.write("IdentifierList<br>");
		for(i=0;i<this.IdentifierList.size();i++)
			{
			sw.write((this.IdentifierList.getKeyList(i)).toString()+"<br>");
			}
		sw.write("FileList<br>");
		for(i=0;i<this.FileList.size();i++)
			{
			sw.write((this.FileList.getKeyList(i)).toString()+"<br>");
			}
		return sw.toString();
		}
	
	protected String getAction(String inName)
		{
		String ret="";
		int i;
		for(i=0;i<this.ActionList.size();i++)
			{
			if(((this.ActionList.get(i)).name).equals(inName))
				{
				ret=this.ActionList.get(i).action;
				i=this.ActionList.size()+1;
				}
			}
		return ret;
		}
	
	protected String getAuth(String inName)
		{
		String ret="AUTH_NONE";
		int i;
		for(i=0;i<this.ActionList.size();i++)
			{
			if(((this.ActionList.get(i)).name).equals(inName))
				{
				ret=this.ActionList.get(i).auth;
				i=this.ActionList.size()+1;
				}
			}
		return ret;
		}
	
	class ActionTable
		{
		String name;
		String action;
		String auth;
		public ActionTable(String inName, String inAction, String inAuth)
			{
			name=inName;
			action=inAction;
			auth=inAuth;
			}
		}
	}
