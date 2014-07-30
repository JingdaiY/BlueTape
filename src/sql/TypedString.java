package sql;

public class TypedString
	{
	public String str;
	public String type;
	
	public TypedString(String in_str, String in_type)
		{
		this.str=in_str;
		this.type=in_type;
		}
	
	public TypedString()
		{
		this("", "String");
		}
	}
