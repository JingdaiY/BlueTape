package sql;

import java.util.ArrayList;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import java.util.Date;

//This is a wrapping class to disable some methods not allowed in login checking.
public class LoginAgent extends QueryAgent
	{
	private DBCollection collection;
	
	public LoginAgent(String inCollectionName)
		{
		super(inCollectionName);
		this.collection=super.getCollection();
		}
	
	/*@Override
	public void pullValues(ArrayList<KeyValuePair> inIdentifierList, ArrayList<KeyValuePair> inUpdateKeyValuePairList)
		{

		}*/
	/*
	@Override
	public boolean checkExistence(ArrayList<KeyValuePair> inKeyValuePairList, String inField, String inFileName)
		{
		return false;
		}*/
	
	protected void replaceValue(ArrayList<KeyValuePair> inIdentifierList, KeyValuePair inReplaceKvp)
		{
		BasicDBObject q=Tools.listToDBObject(inIdentifierList);
		BasicDBObject doc=new BasicDBObject();
		String tempKey=inReplaceKvp.getKey();
		String tempStr=((inReplaceKvp.getValues()).get(0)).str;
		String tempType=((inReplaceKvp.getValues()).get(0)).type;
		if(tempType.equals("time"))
			{
			ArrayList<Date> tempArrayList=new ArrayList<Date>();
			tempArrayList.add(Tools.parseDate(tempStr));
			doc.append("$set", new BasicDBObject(tempKey, new BasicDBObject("time", tempArrayList)));
			}
		else
			{
			if(tempType.equals("int"))
				{
				ArrayList<Long> tempArrayList=new ArrayList<Long>();
				tempArrayList.add(Long.parseLong(tempStr));
				doc.append("$set", new BasicDBObject(tempKey, new BasicDBObject("int", tempArrayList)));
				}
			else
				{
				if(tempType.equals("double"))
					{
					ArrayList<Double> tempArrayList=new ArrayList<Double>();
					tempArrayList.add(Double.parseDouble(tempStr));
					doc.append("$set", new BasicDBObject(tempKey, new BasicDBObject("double", tempArrayList)));
					}
				else
					{
					ArrayList<String> tempArrayList=new ArrayList<String>();
					tempArrayList.add(tempStr);
					doc.append("$set", new BasicDBObject(tempKey, new BasicDBObject(tempType, tempArrayList)));
					}
				}
			}
		System.out.println("replace query="+q+", doc="+doc);
		this.collection.update(q, doc);
		}
	
	public boolean checkMatch(ArrayList<KeyValuePair> inKeyValuePairList)
		{
		BasicDBObject q=Tools.queryToDBObject(inKeyValuePairList);
		System.out.println("check match query="+q.toString());
		return (this.collection.count(q)==1);
		}
	
	@Override
	public String query(KeyValuePair inKeyValuePair)
		{
		return "";
		}
	
	/*@Override
	public int addData(ArrayList<KeyValuePair> inIdentifierList, ArrayList<KeyValuePair> inUpdateKeyValuePairList)
		{
		return -1;
		}*/
	
	@Override
	public ArrayList<String> deleteData(ArrayList<KeyValuePair> inIdentifierList)
		{
		return new ArrayList<String>();
		}
	}
