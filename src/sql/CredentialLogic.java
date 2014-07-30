package sql;

import java.util.ArrayList;
import java.util.Date;

public class CredentialLogic
	{
	static final long EXPIRATION=10*60*1000;
	private LoginAgent cAgent;
	public CredentialLogic(String inCollectionName)
		{
		this.cAgent=new LoginAgent(inCollectionName);
		}
	
	protected boolean checkPassword(ArrayList<KeyValuePair> inCredentialList)
		{
		boolean result=false;
		result=this.cAgent.checkExistence(inCredentialList);
		return result;
		}
	
	protected boolean validSession(ArrayList<KeyValuePair> inIdentifier, String inSessionId)
		{
		boolean result=false;
		ArrayList<KeyValuePair> queryList=new ArrayList<KeyValuePair>();	
		queryList.addAll(inIdentifier);
		queryList.add(new KeyValuePair("sessionId", new TypedString(Tools.sha256(inSessionId), "enc")));
		ArrayList<TypedString> sessionExpireQuery=new ArrayList<TypedString>();
		sessionExpireQuery.add(new TypedString(Long.toString((new Date()).getTime()-EXPIRATION), "int"));
		sessionExpireQuery.add(new TypedString("GT", "int"));
		queryList.add(new KeyValuePair("sessionTime", sessionExpireQuery));
		if(this.cAgent.checkMatch(queryList))
			{
			result=true;
			this.cAgent.replaceValue(queryList, new KeyValuePair("sessionTime", new TypedString(Long.toString((new Date()).getTime()), "int")));
			}
		else
			{
			result=false;
			}
		return result;
		}
	
	protected boolean validSession(String inSessionId)
		{
		return this.validSession(new ArrayList<KeyValuePair>(), inSessionId);
		}
	
	protected boolean validSession(String inSessionId, String inAuth)
		{
		boolean result=false;
		if(inAuth.equals(""))
			{
			result=true;	//Grant permission if no authorization needed.
			}
		else
			{
			if(this.validSession(inSessionId))
				{
				ArrayList<KeyValuePair> queryList=new ArrayList<KeyValuePair>();
				queryList.add(new KeyValuePair("sessionId", new TypedString(Tools.sha256(inSessionId), "enc")));
				queryList.add(new KeyValuePair("auth", new TypedString(inAuth, "string")));
				if(this.cAgent.checkMatch(queryList))
					{
					result=true;
					}
				else		//The user tries to perform unauthorized action with a valid session, means the user is potentially malicious. 
					{
					result=false;
					}
				}
			else
				{
				return false;
				}
			}
		return result;
		}
	
	protected void endSession(ArrayList<KeyValuePair> inIdentifier, String inSessionId)
		{
		if(inIdentifier.size()>0)	//wild-card session breaking is not allowed. Otherwise someone can break other users' sessions en masse by scanning JSESSIONID.  
			{
			ArrayList<KeyValuePair> queryList=new ArrayList<KeyValuePair>();
			queryList.addAll(inIdentifier);
			queryList.add(new KeyValuePair("sessionId", new TypedString(Tools.sha256(inSessionId), "enc")));
			ArrayList<TypedString> sessionTime=new ArrayList<TypedString>();
			sessionTime.add(new TypedString("0", "int"));
			this.cAgent.replaceValue(queryList, new KeyValuePair("sessionTime", sessionTime));
			}
		}
	
	protected boolean checkAccessRight(ArrayList<KeyValuePair> inIdentifier, String inAccessRightName)
		{
		boolean result=false;
		ArrayList<KeyValuePair> queryList=new ArrayList<KeyValuePair>();
		queryList.addAll(inIdentifier);
		queryList.add(new KeyValuePair("AccessRight", new TypedString(inAccessRightName, "access")));	//"access" is a type used only internally. "AccessRight" is the field used only internally. They should not be used in the XML file defining the customized REST protocol. 
		result=this.cAgent.checkExistence(queryList);
		return result;
		}
	
	protected boolean checkAccessRight(String inSessionId, String inAccessRightName)
		{
		boolean result=false;
		ArrayList<KeyValuePair> queryList=new ArrayList<KeyValuePair>();
		queryList.add(new KeyValuePair("sessionId", new TypedString(Tools.sha256(inSessionId), "enc")));
		queryList.add(new KeyValuePair("AccessRight", new TypedString(inAccessRightName, "access")));
		result=this.cAgent.checkExistence(queryList);
		return result;
		}
	
	protected void markSession(ArrayList<KeyValuePair> inIdentifier, String inSessionId)
		{
		if(this.cAgent.checkExistence(inIdentifier))
			{
			this.cAgent.replaceValue(inIdentifier, new KeyValuePair("sessionId", new TypedString(Tools.sha256(inSessionId), "enc")));
			this.cAgent.replaceValue(inIdentifier, new KeyValuePair("sessionTime", new TypedString(Long.toString((new Date()).getTime()), "int")));
			}
		}
	protected String getUserId(String inSessionId)
		{
		String tempstr="";
		ArrayList<KeyValuePair> queryList=new ArrayList<KeyValuePair>();
		queryList.add(new KeyValuePair("sessionId", new TypedString(Tools.sha256(inSessionId), "enc")));
		ArrayList<String> fieldList=new ArrayList<String>();
		fieldList.add("UserId");
		System.out.println(this.cAgent.query(queryList, fieldList));
		String[] strarray=(this.cAgent.query(queryList, fieldList)).split("\"");
		int i;
		for(i=0;i<strarray.length;i++)
			{
			if(strarray[i].contains("@"))
				{
				tempstr=strarray[i];
				i=strarray.length+1;
				}
			}
		return tempstr;
		}
	protected void setAccessRight(ArrayList<KeyValuePair> inIdentifier, String grantedAuth)
		{
		ArrayList<KeyValuePair> tempArrayList=new ArrayList<KeyValuePair>();
		tempArrayList.add(new KeyValuePair("AccessRight", new TypedString(grantedAuth, "access")));
		this.cAgent.pushValues(inIdentifier, tempArrayList);
		}
	}
