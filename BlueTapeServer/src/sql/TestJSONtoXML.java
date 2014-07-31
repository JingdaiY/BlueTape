package sql;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.StringWriter;

public class TestJSONtoXML
	{
	public static void main(String[] args)
		{
		FileInputStream fis;
		FileWriter fw;
		int readint;
		StringWriter sw=new StringWriter();
		if(args.length!=2)
			{
			System.out.println("Use: TestJSONtoXML <input file> <output file>");
			}
		else
			{
			try
				{
				fis=new FileInputStream(args[0]);
				fw=new FileWriter(args[1]);
				while((readint=fis.read())!=-1)
					{
					sw.append((char)readint);
					}
				fis.close();
				fw.write(JSONtoXML.convert(sw.toString()));
				sw.close();
				fw.close();
				}
			catch (Exception e)
				{
				e.printStackTrace();
				}
			}
		}
	}