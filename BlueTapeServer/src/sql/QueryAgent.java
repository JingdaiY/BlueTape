package sql;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

public class QueryAgent
	{
	private String databaseName;
	private DBCollection collection; 
	private String user;
	private String password;
	private static Logger logger=LogManager.getLogger("QueryAgent");
	private DB db;

	public QueryAgent(String inCollectionName)
		{
		
		this.user="pmin_bluetape_u";
		this.password="hodedob1u3+4p3";
		this.databaseName="min_bluetape_db";
		
		ArrayList<ServerAddress> serverAddressList=new ArrayList<ServerAddress>();
		ArrayList<MongoCredential> credentialList=new ArrayList<MongoCredential>();
		try
			{
			serverAddressList.add(new ServerAddress("localhost", 27017));
			credentialList.add(MongoCredential.createMongoCRCredential(this.user, this.databaseName, this.password.toCharArray()));
			MongoClient mongoClient = new MongoClient(serverAddressList, credentialList);
			this.db=mongoClient.getDB(this.databaseName);
			this.collection=this.db.getCollection(inCollectionName);
			//System.out.println("Collection "+inCollectionName+" obtained");
			}
		catch (UnknownHostException e)
			{
			logger.error(e);
			}
		}
	
	public void pushValues(ArrayList<KeyValuePair> inIdentifierList, ArrayList<KeyValuePair> inUpdateKeyValuePairList)
		{
		BasicDBObject q=Tools.listToDBObject(inIdentifierList);
		BasicDBObject doc=Tools.patchToDBObject(inUpdateKeyValuePairList, Tools.APPEND_ACT);
		if(!doc.isEmpty())
			{
			this.collection.update(q, doc);
			}
		}
	
	public void pullValues(ArrayList<KeyValuePair> inIdentifierList, ArrayList<KeyValuePair> inUpdateKeyValuePairList)
		{
		BasicDBObject q=Tools.listToDBObject(inIdentifierList);
		BasicDBObject doc=Tools.patchToDBObject(inUpdateKeyValuePairList, Tools.REMOVE_ACT);
		if(!doc.isEmpty())
			{
			this.collection.update(q, doc);
			}
		}
	
	public boolean checkExistence(ArrayList<KeyValuePair> inKeyValuePairList, String inField, String inFileName)
		{
		BasicDBObject q=Tools.listToDBObject(inKeyValuePairList);
		q.append(inField+".file", inFileName);
		long matchcount=this.collection.count(q);
		return (matchcount>0);
		}
	
	public boolean checkExistence(ArrayList<KeyValuePair> inKeyValuePairList)
		{
		BasicDBObject q=Tools.listToDBObject(inKeyValuePairList);
		System.out.println("Check query="+q.toString());
		return (this.collection.count(q)==1);
		}
	
	public String query(ArrayList<KeyValuePair> inKeyValuePairList, ArrayList<String> inField)
		{
		DBCursor cursor=null;
		BasicDBObject query=Tools.queryToDBObject(inKeyValuePairList);
		//BasicDBObject fields=Tools.fieldToDBObject(inField);
		BasicDBObject fields;
		ArrayList<String> blockField=new ArrayList<String>();
		DBObject r=this.collection.findOne(query, new BasicDBObject("_id", 0));
		//System.out.println("findone="+r.toString());
		try
			{
			Tools.findFieldsWithType(r.toString(), "enc", blockField);
			System.out.println("block field="+blockField.get(0));
			fields=Tools.fieldToDBObject(inField, blockField);
			cursor=this.collection.find(query, fields);
			}
		catch(MongoException me)
			{
			logger.error(me);
			}
		return cursor.toArray().toString();
		}
	
	public String query(ArrayList<KeyValuePair> inKeyValuePairList)
		{
		DBCursor cursor=null;
		BasicDBObject dbobject=Tools.listToDBObject(inKeyValuePairList);
		try
			{
			cursor=this.collection.find(dbobject);
			}
		catch(MongoException me)
			{
			logger.error(me);
			}
		//return JSONtoXML.convert(cursor.toString());
		return cursor.toArray().toString();	//debug
		}
	
	public String query(KeyValuePair inKeyValuePair)
		{
		ArrayList<KeyValuePair> keyValuePairList=new ArrayList<KeyValuePair>();
		keyValuePairList.add(inKeyValuePair);
		return this.query(keyValuePairList);
		}
	
	public int addData(ArrayList<KeyValuePair> inIdentifierList, ArrayList<KeyValuePair> inUpdateKeyValuePairList)
		{
		int result=0;
		BasicDBObject q=Tools.listToDBObject(inIdentifierList);
		if(this.collection.getCount(q)>0)
			{
			System.out.println("Duplicated document: "+this.collection.find(q).toArray());
			result=-1;
			}
		else
			{
			ArrayList<KeyValuePair> tempKvpList=new ArrayList<KeyValuePair>();
			tempKvpList.addAll(inIdentifierList);
			tempKvpList.addAll(inUpdateKeyValuePairList);
			try
				{
				this.collection.insert(Tools.listToDBObject(tempKvpList));
				result=1;
				}
			catch(MongoException me)
				{
				result=-1;
				logger.error(me);
				}
			}
		return result;
		}
	
	public void removeField(ArrayList<KeyValuePair> inIdentifierList, ArrayList<String> inFieldList)
		{
		BasicDBObject q=Tools.listToDBObject(inIdentifierList);
		this.collection.update(q, Tools.removeFieldToDBObject(inFieldList));
		}
	
	public void removeField(ArrayList<KeyValuePair> inIdentifierList, String inField)
		{
		ArrayList<String> fieldList=new ArrayList<String>();
		fieldList.add(inField);
		this.removeField(inIdentifierList, fieldList);
		}
	
	public ArrayList<String> deleteData(ArrayList<KeyValuePair> inIdentifierList)
		{
		ArrayList<String> fileList=new ArrayList<String>();
		BasicDBObject q=Tools.listToDBObject(inIdentifierList);
		DBObject r=this.collection.findOne(q, new BasicDBObject("_id", 0));
		//System.out.println(r.toString());
		Tools.findFile(r.toString(), fileList);
		this.collection.remove(q);
		return fileList;
		}
	
	protected DBCollection getCollection()
		{
		return this.collection;
		}
	}
