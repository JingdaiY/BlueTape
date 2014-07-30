package sql;

import java.io.StringWriter;
import java.util.ArrayList;

public class KeyValuePair
	{
	private String key;
	private ArrayList<TypedString> value;
	
	public KeyValuePair(String inKey)
		{
		this.key=inKey;
		this.value=new ArrayList<TypedString>();
		}
	
	public KeyValuePair(String inKey, ArrayList<TypedString> inValue)
		{
		this.key=inKey;
		this.value=inValue;
		}
	
	public KeyValuePair(String inKey, TypedString inTypedStr)
		{
		this.key=inKey;
		this.value=new ArrayList<TypedString>();
		this.value.add(inTypedStr);
		}
	
	public void setValues(ArrayList<TypedString> inValue)
		{
		int i;
		for(i=0;i<inValue.size();i++)
			{
			this.value.add(i, inValue.get(i));
			}
		}
	
	public ArrayList<TypedString> getValues()
		{
		return this.value;
		}
	
	public String getKey()
		{
		return this.key;
		}
	
	@Override
	public String toString()
		{
		int i;
		StringWriter sw=new StringWriter();
		sw.write("key="+this.key+"; values=");
		for(i=0;i<this.value.size();i++)
			{
			sw.write("Type="+(this.value.get(i)).type+", String="+(this.value.get(i).str)+" ");
			}
		return sw.toString();
		}
	
	public int valueSize()
		{
		return (this.value).size();
		}
	}
