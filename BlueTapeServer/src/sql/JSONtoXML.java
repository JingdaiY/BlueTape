package sql;

import java.io.StringWriter;
import java.util.Stack;

public class JSONtoXML
	{
	public static String convert(String inJSON)
		{
		int i,j;
		int indent=-1;
		boolean list=false;
		boolean quote=false;
		boolean override=false;
		StringWriter tempsw=new StringWriter();
		StringWriter sw=new StringWriter();
		Stack<String> stk=new Stack<String>();
		char readchar;
                
		try
			{
			for(i=0;i<inJSON.length();i++)
				{
				readchar=inJSON.charAt(i);
				if(override)
					{
					tempsw.write(readchar);
					override=!override;
					}
				else
					{
					if(quote)
						{
						if(readchar=='"')
							{
							quote=!quote;
							}
						else
							{
							tempsw.write(readchar);
							}
						}
					else
						{
						if(readchar=='{')
							{
							for(j=0;j<indent;j++)
								{
								sw.write("\t");
								}
							indent++;
							if(stk.isEmpty())
								{
								
								}
							else
								{
								//stk.push(tempsw.toString());
								tempsw=new StringWriter();
								sw.write("<"+stk.peek()+">\r\n");
								}
							}
						else
							{
							if(readchar=='}')
								{
								if(stk.isEmpty())
									{
									tempsw=new StringWriter();
									indent=0;
									}
								else
									{
									if((tempsw.toString()).length()!=0)
										{
										for(j=0;j<indent;j++)
											{
											sw.write("\t");
											}
										sw.write("</"+stk.peek()+">"+tempsw.toString()+"</"+stk.pop()+">\r\n");
										tempsw=new StringWriter();
										}
									indent--;
									//sw.write("\r\n");
									for(j=0;j<indent;j++)
										{
										sw.write("\t");
										}
									if(!stk.isEmpty())
										{
										sw.write("</"+stk.pop()+">\r\n");
										}
									}
								}
							else
								{
								if(readchar=='[')
									{
									list=true;
									}
								else
									{
									if(readchar==']')
										{
										//sw.write("\r\n");
										for(j=0;j<indent;j++)
											{
											sw.write("\t");
											}
										sw.write("<"+stk.peek()+">");
										sw.write(tempsw.toString());
										sw.write("</"+stk.pop()+">\r\n");
										tempsw=new StringWriter();
										list=false;
										}
									else
										{
										if(readchar==':')
											{
											stk.push(tempsw.toString());
											tempsw=new StringWriter();
											}
										else
											{
											if(readchar==',')
												{
												if((tempsw.toString()).length()!=0)
													{
													//sw.write("\r\n");
													for(j=0;j<indent;j++)
														{
														sw.write("\t");
														}
													sw.write("<"+stk.peek()+">");
													sw.write(tempsw.toString());
													tempsw=new StringWriter();
													sw.write("</");
													if(list)
														{
														sw.write(stk.peek());
														}
													else
														{
														sw.write(stk.pop());
														}
													sw.write(">\r\n");
													}
												else
													{
													
													}
												}
											else
												{
												if((readchar=='"'))
													{
													quote=!quote;
													}
												else
													{
													if(((readchar==' ')&&(!quote))||((readchar=='\t')||(readchar=='\r')||(readchar=='\n')))
														{
														}
													else
														{
														tempsw.write(readchar);
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		catch(Exception e)
			{
			e.printStackTrace();
			return sw.toString();
			}
		return sw.toString(); 
		}
	}
