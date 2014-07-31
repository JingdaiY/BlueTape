package sql;

import java.io.File;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.mongodb.BasicDBObject;

public class Tools
	{
            
	protected static final int APPEND_ACT=1;
	protected static final int REMOVE_ACT=2;
	private static final String [] blockedKeys={"UserId", "Password", "sessionId", "sessionTime", "ConfirmCode", "AuthName"};
	
	public static void fileDeletor(String inCollectionName, ArrayList<String> inFilesToDeleteList)
		{
		int i;
		for(i=0;i<inFilesToDeleteList.size();i++)
			{
			try
				{
				new File(inCollectionName+File.separator+inFilesToDeleteList.get(i)).delete();
				System.out.println("Delete file: "+inCollectionName+File.separator+inFilesToDeleteList.get(i));
				}
			catch(IllegalArgumentException iae)
				{
				System.out.println("Fail to clean up file: "+inCollectionName+File.separator+inFilesToDeleteList.get(i));
				}
			}
		}
	
	public static BasicDBObject patchToDBObject(ArrayList<KeyValuePair> inUpdateKeyValuePairList, int action)
		{
		BasicDBObject tempdbobject=new BasicDBObject();
		String actionStr="$addToSet";
		String tempKey;
		ArrayList<TypedString> tempValues;
		String tempType;
		int i, j;
		switch(action)
			{
			case APPEND_ACT:
				{
				actionStr="$addToSet";
				break;
				}
			case REMOVE_ACT:
				{
				actionStr="$pullAll";
				break;
				}
			default:
				{
				actionStr="$addToSet";
				break;
				}
			}
		for(i=0;i<inUpdateKeyValuePairList.size();i++)
			{
			tempKey=(inUpdateKeyValuePairList.get(i)).getKey();
			tempValues=(inUpdateKeyValuePairList.get(i)).getValues();
			tempType=(tempValues.get(0)).type;
			System.out.println("tempKey="+tempKey+", tempType="+tempType);
			
			if(tempType.equals("int"))
				{
				ArrayList<Long> tempList=new ArrayList<Long>();
				for(j=0;j<tempValues.size();j++)
					{
					tempList.add(Long.parseLong((tempValues.get(j)).str));
					}
				if(action==REMOVE_ACT)
					{
					tempdbobject.append(actionStr, new BasicDBObject(tempKey+".int", tempList));
					}
				else
					{
					tempdbobject.append(actionStr, new BasicDBObject(tempKey+".int", new BasicDBObject("$each", tempList)));
					}
				}
			else
				{
				if(tempType.equals("double"))
					{
					ArrayList<Double> tempList=new ArrayList<Double>();
					for(j=0;j<tempValues.size();j++)
						{
						tempList.add(Double.parseDouble((tempValues.get(j)).str));
						}
					if(action==REMOVE_ACT)
						{
						tempdbobject.append(actionStr, new BasicDBObject(tempKey+".double", tempList));
						}
					else
						{
						tempdbobject.append(actionStr, new BasicDBObject(tempKey+".double", new BasicDBObject("$each", tempList)));
						}
					}
				else
					{
					if(tempType.equals("time"))
						{
						ArrayList<Date> tempList=new ArrayList<Date>();
						for(j=0;j<tempValues.size();j++)
							{
							tempList.add(Tools.parseDate((tempValues.get(j)).str));
							}
						if(action==REMOVE_ACT)
							{
							tempdbobject.append(actionStr, new BasicDBObject(tempKey+".time", tempList));
							}
						else
							{
							tempdbobject.append(actionStr, new BasicDBObject(tempKey+".time", new BasicDBObject("$each", tempList)));
							}
						}
					else
						{
						if(tempType.equals("enc"))
							{
							ArrayList<String> tempList=new ArrayList<String>();
							tempList.add(tempValues.get(0).str);	//Only the first string can enter an encoded field;
							if(action==REMOVE_ACT)
								{
								tempdbobject.append(actionStr, new BasicDBObject(tempKey+".enc", tempList));
								}
							else
								{
								tempdbobject.append("$set", new BasicDBObject(tempKey+".enc", tempList));	//Only one string can be in an encoded field.
								}
							}
						else
							{
							ArrayList<String> tempList=new ArrayList<String>();
							for(j=0;j<tempValues.size();j++)
								{
								tempList.add((tempValues.get(j)).str);
								}
							if(action==REMOVE_ACT)
								{
								tempdbobject.append(actionStr, new BasicDBObject(tempKey+"."+tempType, tempList));
								}
							else
								{
								tempdbobject.append(actionStr, new BasicDBObject(tempKey+"."+tempType, new BasicDBObject("$each", tempList)));
								}
							}
						}
					}
				}
			}
		System.out.println(tempdbobject.toString());
		return tempdbobject;
		}
	
	public static BasicDBObject fieldToDBObject(ArrayList<String> inField)
		{
		BasicDBObject tempdbobject=new BasicDBObject();
		int i;
		for(i=0;i<inField.size();i++)
			{
			tempdbobject.append(inField.get(i), 1);
			}
		tempdbobject.append("_id", 0);
		return tempdbobject;
		}
	
	public static BasicDBObject fieldToDBObject(ArrayList<String> inField, ArrayList<String> blockField)
		{
		BasicDBObject tempdbobject=new BasicDBObject();
		ArrayList<String> filteredFieldList=filterFields(inField, blockField); 
		int i;
		for(i=0;i<filteredFieldList.size();i++)
			{
			tempdbobject.append(filteredFieldList.get(i), 1);
			}
		if(tempdbobject.isEmpty())
			{
			for(i=0;i<blockField.size();i++)
				{
				tempdbobject.append(blockField.get(i), 0);
				}
			}
		tempdbobject.append("_id", 0);
		System.out.println("field object="+tempdbobject.toString());
		return tempdbobject;
		}
	
	public static BasicDBObject removeFieldToDBObject(ArrayList<String> inField)
		{
		BasicDBObject tempdbobject=new BasicDBObject();
		int i;
		for(i=0;i<inField.size();i++)
			{
			tempdbobject.append(inField.get(i),"");
			}
		return (new BasicDBObject("$unset", tempdbobject));
		}
	
	private static ArrayList<String> filterFields(ArrayList<String> inField, ArrayList<String> blockField)
		{
		ArrayList<String> tempFieldList=new ArrayList<String>();
		int i, j;
		boolean deduct=false;
		String tempstr;
		for(i=0;i<inField.size();i++)
			{
			tempstr=inField.get(i);
			//System.out.println("inField="+inField.get(i));
			deduct=false;
			for(j=0;j<blockField.size();j++)
				{
				//System.out.println("BlcokField compared="+blockField.get(j));
				if(tempstr.equals(blockField.get(j)))
					{
					deduct=true;
					j=blockField.size()+1;	//break fromt the inner check loop.
					}
				}
			if(!deduct)
				{
				tempFieldList.add(tempstr);
				}
			}
		return tempFieldList;
		}
	
	public static BasicDBObject queryToDBObject(ArrayList<KeyValuePair> inKeyValuePairList)
		{
		//BasicDBObject dbObject=new BasicDBObject();
		BasicDBObject tempdbobject=new BasicDBObject();
		int i, j;
		ArrayList<TypedString> tempTypedString;
		//ArrayList tempList;
		String tempKey;
		String tempType="string";
		for(i=0;i<inKeyValuePairList.size();i++)
			{
			tempType=(((inKeyValuePairList.get(i)).getValues()).get(0)).type;
			tempKey=(inKeyValuePairList.get(i)).getKey();
			tempTypedString=(inKeyValuePairList.get(i)).getValues();
			//tempdbobject=new BasicDBObject();
			//tempList=new ArrayList();
			if((tempType.equals("string"))||(tempType.equals("file"))||(tempType.equals("enc")))
				{
				for(j=0;j<tempTypedString.size();j++)
					{
					tempdbobject.append(tempKey+"."+tempType, tempTypedString.get(j).str);
					}
				}
			else
				{
				if((tempType.equals("int"))||(tempType.equals("time"))||(tempType.equals("double")))
					{
					if(tempTypedString.size()==1)
						{
						if(tempType.equals("int"))
							{
							tempdbobject.append(tempKey+"."+tempType, Long.parseLong(tempTypedString.get(0).str));
							}
						else
							{
							if(tempType.equals("time"))
								{
								tempdbobject.append(tempKey+"."+tempType, Tools.parseDate(tempTypedString.get(0).str));
								}
							else
								{
								if(tempType.equals("double"))
									{
									tempdbobject.append(tempKey+"."+tempType, Double.parseDouble(tempTypedString.get(0).str));
									}
								else
									{
									tempdbobject.append(tempKey+"."+tempType, tempTypedString.get(0).str);
									}
								}
							}
						}
					else
						{
						if((tempTypedString.get(0).str).equals("LT"))
							{
							if(tempType.equals("int"))
								{
								tempdbobject.append(tempKey+"."+tempType, new BasicDBObject("$lte", Long.parseLong(tempTypedString.get(1).str)));
								}
							else
								{
								if(tempType.equals("time"))
									{
									tempdbobject.append(tempKey+"."+tempType, new BasicDBObject("$lte", Tools.parseDate(tempTypedString.get(1).str)));
									}
								else
									{
									if(tempType.equals("double"))
										{
										tempdbobject.append(tempKey+"."+tempType, new BasicDBObject("$lte", Double.parseDouble(tempTypedString.get(1).str)));
										}
									else
										{
										tempdbobject.append(tempKey+"."+tempType, new BasicDBObject("$lte", tempTypedString.get(1).str));
										}
									}
								}
							}
						else
							{
							if((tempTypedString.get(1).str).equals("GT"))
								{
								if(tempType.equals("int"))
									{
									tempdbobject.append(tempKey+"."+tempType, new BasicDBObject("$gte", Long.parseLong(tempTypedString.get(0).str)));
									}
								else
									{
									if(tempType.equals("time"))
										{
										tempdbobject.append(tempKey+"."+tempType, new BasicDBObject("$gte", Tools.parseDate(tempTypedString.get(0).str)));
										}
									else
										{
										if(tempType.equals("double"))
											{
											tempdbobject.append(tempKey+"."+tempType, new BasicDBObject("$gte", Double.parseDouble(tempTypedString.get(0).str)));
											}
										else
											{
											tempdbobject.append(tempKey+"."+tempType, new BasicDBObject("$gte", tempTypedString.get(0).str));
											}
										}
									}
								}
							else
								{
								if(tempType.equals("int"))
									{
									tempdbobject.append(tempKey+"."+tempType, new BasicDBObject("$gte", Long.parseLong(tempTypedString.get(0).str)));
									tempdbobject.append(tempKey+"."+tempType, new BasicDBObject("$lte", Long.parseLong(tempTypedString.get(1).str)));
									}
								else
									{
									if(tempType.equals("time"))
										{
										tempdbobject.append(tempKey+"."+tempType, new BasicDBObject("$gte", Tools.parseDate(tempTypedString.get(0).str)));
										tempdbobject.append(tempKey+"."+tempType, new BasicDBObject("$lte", Tools.parseDate(tempTypedString.get(1).str)));
										}
									else
										{
										if(tempType.equals("double"))
											{
											tempdbobject.append(tempKey+"."+tempType, new BasicDBObject("$gte", Double.parseDouble(tempTypedString.get(0).str)));
											tempdbobject.append(tempKey+"."+tempType, new BasicDBObject("$lte", Double.parseDouble(tempTypedString.get(1).str)));
											}
										else
											{
											tempdbobject.append(tempKey+"."+tempType, new BasicDBObject("$gte", tempTypedString.get(0).str));
											tempdbobject.append(tempKey+"."+tempType, new BasicDBObject("$lte", tempTypedString.get(1).str));
											}
										}
									}
								}
							}
						}
					}
				else
					{
					for(j=0;j<tempTypedString.size();j++)
						{
						tempdbobject.append(tempKey+"."+tempType, tempTypedString.get(j).str);
						}
					}
				}	
			}
		return tempdbobject;
		}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static BasicDBObject listToDBObject(ArrayList<KeyValuePair> inKeyValuePairList)
		{
		BasicDBObject dbobject=new BasicDBObject();
		BasicDBObject tempdbobject=new BasicDBObject();
		int i, j;
		ArrayList<TypedString> tempTypedString;
		ArrayList tempList;
		String tempKey;
		String tempType="string";
		for(i=0;i<inKeyValuePairList.size();i++)
			{
			tempKey=(inKeyValuePairList.get(i)).getKey();
			tempTypedString=(inKeyValuePairList.get(i)).getValues();
			tempdbobject=new BasicDBObject();
			tempList=new ArrayList();
			for(j=0;j<tempTypedString.size();j++)
				{
				tempType=tempTypedString.get(j).type;
				if(tempType.equals("string")||tempType.equals("file")||tempType.equals("enc"))
					{
					tempList.add(tempTypedString.get(j).str);
					}
				else
					{
					if(tempType.equals("int"))
						{
						try
							{
							tempList.add(Long.parseLong(tempTypedString.get(j).str));
							}
						catch(NumberFormatException nfe)
							{
							tempList.add(tempTypedString.get(j).str);
							}
						}
					else
						{
						if(tempType.equals("time"))
							{
							try
								{
								tempList.add(Tools.parseDate(tempTypedString.get(j).str));
								}
							catch(NumberFormatException nfe)
								{
								tempList.add(tempTypedString.get(j).str);
								}
							}
						else
							{
							if(tempType.equals("double"))
								{
								try
									{
									tempList.add(Double.parseDouble(tempTypedString.get(j).str));
									}
								catch(NumberFormatException nfe)
									{
									tempList.add(tempTypedString.get(j).str);
									}
								}
							else
								{
								tempList.add(tempTypedString.get(j).str);
								}
							}
						}
					}	
				}
			tempdbobject.append(tempType, tempList);
			dbobject.append(tempKey, tempdbobject);
			}
		return dbobject;
		}
	
	public static Date parseDate(String inDate)
		{
		Calendar calendar=Calendar.getInstance();
		String[] dateArray=inDate.split("T");
		String[] timeArray;
		boolean hasTime=false;
		boolean hasTimeZone=false;
		boolean hasSecond=false;
		int hour=0;
		int minute=0;
		int second=0;
		double dsecond;
		String tzd="GMT+0:00";
		if(hasTime=(dateArray.length>1))
			{
			timeArray=dateArray[1].split("(\\+)|(\\-)|Z");
			if(dateArray[1].contains("Z"))
				{
				hasTimeZone=true;
				tzd="GMT+0:00";
				}
			else
				{
				
				if(hasTimeZone=(timeArray.length>1))
					{
					if(dateArray[1].contains("+"))
						{
						tzd="GMT+"+timeArray[1];
						}
					else
						{
						if(dateArray[1].contains("-"))
							{
							tzd="GMT-"+timeArray[1];
							}
						else
							{
							tzd="GMT+0:00";
							}
						}
					}
				}
			timeArray=timeArray[0].split(":");
			
			if(timeArray.length==2)
				{
				hour=Integer.parseInt(timeArray[0]);
				minute=Integer.parseInt(timeArray[1]);
				second=0;
				}
			else
				{
				if(hasSecond=(timeArray.length==3))
					{
					hour=Integer.parseInt(timeArray[0]);
					minute=Integer.parseInt(timeArray[1]);
					if(timeArray[2].contains("."))
						{
						dsecond=Double.parseDouble(timeArray[2]);
						second=(int)dsecond;
						}
					else
						{
						second=Integer.parseInt(timeArray[2]);
						}
					}
				else
					{
					hour=0;
					minute=0;
					second=0;
					dsecond=0;
					}
				}
			}
		if(hasTimeZone)
			{
			calendar.setTimeZone(TimeZone.getTimeZone(tzd));
			}
		dateArray=dateArray[0].split("-");
		
		if(dateArray.length==1)
			{
			calendar.set(Integer.parseInt(dateArray[0]), 0, 0);
			}
		else
			{
			if(dateArray.length==2)
				{
				calendar.set(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1])-1, 1);
				}
			else
				{
				if(dateArray.length==3)
					{
					if(hasTime)
						{
						if(hasSecond)
							{
							calendar.set(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1])-1, Integer.parseInt(dateArray[2]), hour, minute, second);
							}
						else
							{
							calendar.set(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1])-1, Integer.parseInt(dateArray[2]), hour, minute);
							}
						}
					else
						{
						calendar.set(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1])-1, Integer.parseInt(dateArray[2]));
						}
					}
				else
					{
					calendar.set(1970, 0, 1);	//default at epoch.
					}
				}
			}
		return calendar.getTime(); 
		}
		
	private static int findEndIndex(String inJSON, int currentIndex)
		{
		int i=0;
		int markIndex=-1;
		int targetMark=-1;
		char readchar;
		//while((readchar=inJSON.charAt(i))!=-1)
		for(i=0;i<inJSON.length();i++)
			{
			readchar=inJSON.charAt(i);
			if(readchar=='{')
				{
				markIndex++;
				if(currentIndex==i)
					{
					targetMark=markIndex;
					}
				}
			else
				{
				if(readchar=='}')
					{
					if(markIndex==targetMark)
						{
						//System.out.println("find close mark at index="+i);
						return i;
						}
					markIndex--;
					}
				}
			//i++;
			}
		//System.out.println("find close mark at index="+i);
		return i;
		}
		
	private static void parseDocument(String inJSON, ArrayList<TypedString> typedStringList)
		{
		//System.out.println("Parsing document: "+inJSON);
		int i, j;
		char readchar;
		boolean quote=false;
		boolean override=false;
		StringWriter sw=new StringWriter();
		String type="";
		ArrayList<String> strList=new ArrayList<String>();
		for(i=0;i<inJSON.length();i++)
			{
			readchar=inJSON.charAt(i);
			if(override)
				{
				sw.write(readchar);
				override=false;
				}
			else
				{
				if(quote)
					{
					if(readchar=='"')
						{
						sw.write(readchar);
						quote=false;
						}
					}
				else
					{
					if(readchar==':')
						{
						type=removeQuote((sw.toString()).trim());
						sw=new StringWriter();
						}
					else
						{
						if(readchar=='[')
							{
							strList=new ArrayList<String>();
							}
						else
							{
							if(readchar==',')
								{
								strList.add(removeQuote((sw.toString()).trim()));
								sw=new StringWriter();
								}
							else
								{
								if(readchar==']')
									{
									strList.add(removeQuote((sw.toString()).trim()));
									sw=new StringWriter();
									for(j=0;j<strList.size();j++)
										{
										typedStringList.add(new TypedString(strList.get(j), type));
										}
									}
								else
									{
									sw.write(readchar);
									}
								}
							}
						}
					}
				}
			}
		}
	
	public static void parseObject(String inJSON, ArrayList<KeyValuePair> inKeyValuePairList)
		{
		//System.out.println("Parsing Object="+inJSON);
		int i=0;
		char readchar;
		int StartIndex=0;
		int MarkIndex=-1;
		int ArrayIndex=0;
		boolean override=false;
		boolean quote=false;
		while(i<inJSON.length())
			{
			readchar=inJSON.charAt(i);
			if(override)
				{
				override=false;
				}
			else
				{
				if(quote)
					{
					if(readchar=='"')
						{
						quote=false;
						}
					}
				else
					{
					if(readchar=='"')
						{
						quote=true;
						}
					else
						{
						if((MarkIndex==0)&&(ArrayIndex<1))
							{
							if(readchar=='{')
								{
								MarkIndex++;
								}
							else
								{
								if(readchar=='}')
									{
									MarkIndex--;
									}
								else
									{
									if(readchar=='[')
										{
										ArrayIndex++;
										}
									else
										{
										if(readchar==']')
											{
											ArrayIndex--;
											}
										else
											{
											if(readchar==',')
												{
												//System.out.println("start="+StartIndex+", end="+(i-1));
												parseLine(inJSON.substring(StartIndex, i-1), inKeyValuePairList);
												StartIndex=i+1;
												}
											else
												{
												}
											}
										}
									}
								}
							}
						else
							{
							if(readchar=='{')
								{
								if(MarkIndex==-1)
									{
									StartIndex=i+1;
									}
								MarkIndex++;
								}
							else
								{
								if(readchar=='}')
									{
									MarkIndex--;
									}
								else
									{
									if(readchar=='[')
										{
										ArrayIndex++;
										}
									else
										{
										if(readchar==']')
											{
											ArrayIndex--;
											}
										}
									}
								}
							}
						}
					}
				}
			i++;
			}
		parseLine(inJSON.substring(StartIndex, inJSON.length()-1), inKeyValuePairList);
		}
	
	public static void parseLine(String inJSON, ArrayList<KeyValuePair> inKeyValuePairList)
		{
		//System.out.println("Parsing line: "+inJSON);
		int i=0;
		char readchar;
		StringWriter sw=new StringWriter();
		boolean override=false;
		boolean quote=false;
		ArrayList<TypedString> typedStringList=new ArrayList<TypedString>();
		int endIndex=0;
		String tempKey="";
		while(i<inJSON.length()-1)
			{
			readchar=inJSON.charAt(i);
			//System.out.println("Index="+i+", read char="+readchar+", quote="+quote);
			if(override)
				{
				sw.write(readchar);
				i++;
				override=false;
				}
			else
				{
				if(quote)
					{
					if(readchar=='"')
						{
						sw.write(readchar);
						i++;
						quote=false;
						}
					else
						{
						sw.write(readchar);
						i++;
						}
					}
				else
					{
					if(readchar=='"')
						{
						sw.write(readchar);
						i++;
						quote=true;
						}
					else
						{
						if(readchar==':')
							{
							tempKey=removeQuote((sw.toString()).trim());
							sw=new StringWriter();
							i++;
							}
						else
							{
							if(readchar=='{')
								{
								endIndex=findEndIndex(inJSON, i);
								parseDocument(inJSON.substring(i+1, endIndex), typedStringList);
								i=endIndex+1;
								}
							else
								{
								sw.write(readchar);
								i++;
								}
							}
						}
					}
				}
			}
		inKeyValuePairList.add(new KeyValuePair(tempKey, typedStringList));
		}
	
	public static void findFile(String inJSON, ArrayList<String> inFileList)
		{
		ArrayList<KeyValuePair> tempKvpList=new ArrayList<KeyValuePair>();
		ArrayList<TypedString> tempTypedString=new ArrayList<TypedString>();
		parseObject(inJSON, tempKvpList);
		int i, j;
		String tempType;
		String tempStr;
		for(i=0;i<tempKvpList.size();i++)
			{
			//System.out.println(tempKvpList.get(i).getValues());
			tempTypedString=tempKvpList.get(i).getValues();
			tempType=tempTypedString.get(0).type;
			//System.out.println("type="+tempType);
			if(tempType.contains("file"))
				{
				for(j=0;j<tempTypedString.size();j++)
					{
					tempStr=((tempTypedString.get(j)).str).trim();
					//tempStr=tempStr.substring(1, tempStr.length()-1);
					if(tempStr.startsWith("\""))
						{
						if(tempStr.endsWith("\""))
							{
							tempStr=tempStr.substring(1, tempStr.length()-1);
							}
						else
							{
							tempStr=tempStr.substring(1);
							}
						}
					else
						{
						if(tempStr.endsWith("\""))
							{
							tempStr=tempStr.substring(0, tempStr.length()-1);
							}
						}
					//System.out.println("filename "+i+"="+tempStr);
					inFileList.add(tempStr);
					}
				}
			}
		}
	
	public static void findFieldsWithType(String inJSON, String inType, ArrayList<String> outTypeList)
		{
		ArrayList<KeyValuePair> tempKvpList=new ArrayList<KeyValuePair>();
		ArrayList<TypedString> tempTypedString=new ArrayList<TypedString>();
		parseObject(inJSON, tempKvpList);
		int i;
		String tempType;
		for(i=0;i<tempKvpList.size();i++)
			{
			//System.out.println(tempKvpList.get(i).getValues());
			tempTypedString=tempKvpList.get(i).getValues();
			tempType=tempTypedString.get(0).type;
			//System.out.println("type="+tempType);
			if(tempType.contains(inType))
				{
				outTypeList.add(tempKvpList.get(i).getKey());
				}
			}
		}
	
	private static String removeQuote(String inString)
		{
		String tempStr;
		if(inString.startsWith("\""))
			{
			if(inString.endsWith("\""))
				{
				tempStr=inString.substring(1, inString.length()-1);
				}
			else
				{
				tempStr=inString.substring(1);
				}
			}
		else
			{
			if(inString.endsWith("\""))
				{
				tempStr=inString.substring(0, inString.length()-1);
				}
			else
				{
				tempStr=inString;
				}
			}
		//System.out.println("Cleaned string="+tempStr);
		return tempStr;
		}
	
	public static String sha256(String base)
		{
		try
			{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(base.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();
			int i;
			for (i=0;i<hash.length;i++)
				{
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
				}
			return hexString.toString();
			}
		catch(Exception e)
			{
			e.printStackTrace();
			return base;	//when something gets wrong, pass the original string instead of encoded one.
			}
		}
	
	public static boolean checkReserveKeys(String inKey)
		{
		int limit=blockedKeys.length;
		int i;
		boolean match=false;
		for(i=0;i<limit;i++)
			{
			if(inKey.equals(blockedKeys[i]));
				{
				match=true;
				}
			}
		return match;
		}
	
	public static void main(String[] args)
		{
		int i;
		int markedStartIndex=0;
		char readchar;
		/*for(i=0;i<args.length;i++)
			{
			System.out.println(Tools.parseDate(args[i]).getTime());
			}*/
		System.out.println("input string="+args[0]+", length="+args[0].length());
		for(i=0;i<args[0].length();i++)
			{
			readchar=args[0].charAt(i);
			if(readchar=='{')
				{
				System.out.println("["+markedStartIndex+"] at position "+i);
				markedStartIndex++;
				}
			else
				{
				if(readchar=='}')
					{
					markedStartIndex--;
					}
				}
			}
		//System.out.println(args[0].substring(Integer.parseInt(args[1]), findEndIndex(args[0], Integer.parseInt(args[1]))+1));
		ArrayList<KeyValuePair> kvpList=new ArrayList<KeyValuePair>();
		parseObject(args[0], kvpList);
		for(i=0;i<kvpList.size();i++)
			{
			System.out.println((kvpList.get(i)).toString());
			}
		ArrayList<String> fileNameList=new ArrayList<String>();
		findFile(args[0], fileNameList);
		for(i=0;i<fileNameList.size();i++)
			{
			System.out.println(fileNameList.get(i));
			}
		}
	}
