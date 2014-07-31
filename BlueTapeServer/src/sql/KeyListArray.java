package sql;

import java.util.ArrayList;

public class KeyListArray
	{
	private ArrayList<KeyValuePair> requestKeyList; 
	
	public KeyListArray()
		{
		this.requestKeyList=new ArrayList<KeyValuePair>();
		}
	
	public KeyListArray(KeyValuePair inKeyList)
		{
		this.requestKeyList=new ArrayList<KeyValuePair>();
		this.requestKeyList.add(inKeyList);
		}
	
	protected void add(KeyValuePair inKeyList)
		{
		this.requestKeyList.add(inKeyList);
		//System.out.println("Key="+inKeyList.key+", size="+inKeyList.valueSize());
		}
	
	protected ArrayList<TypedString> getTypedString(String inKey)
		{
		int i;
		ArrayList<TypedString> retValues=new ArrayList<TypedString>();
		for(i=0;i<this.requestKeyList.size();i++)
			{
			if(((this.requestKeyList.get(i)).getKey()).equals(inKey))
				{
				retValues=this.requestKeyList.get(i).getValues();
				i=this.requestKeyList.size()+1;
				}
			}
		return retValues;
		}
	
	protected KeyValuePair getKeyList(int inIndex)
		{
		return this.requestKeyList.get(inIndex);
		}
	
	protected void setKeyList(String inKey, ArrayList<TypedString> inTypedString)
		{
		int i;
		for(i=0;i<this.requestKeyList.size();i++)
			{
			if(((this.requestKeyList.get(i)).getKey()).equals(inKey))
				{
				this.requestKeyList.get(i).setValues(inTypedString);
				i=this.requestKeyList.size()+1;
				}
			}
		}
	
	protected void deleteKeyList(String inKey)
		{
		int i;
		for(i=0;i<this.requestKeyList.size();i++)
			{
			if(((this.requestKeyList.get(i)).getKey()).equals(inKey))
				{
				this.requestKeyList.remove(i);
				i=this.requestKeyList.size()+1;
				}
			}
		}
	
	public int size()
		{
		return this.requestKeyList.size();
		}
	}
