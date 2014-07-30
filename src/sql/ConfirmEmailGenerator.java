package sql;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfirmEmailGenerator
	{
	private static Logger logger=LogManager.getLogger("ConfirmEmailGenerator");
	private String emailAddress;
	private String emailPassword;
	private String subject;
	private String contentText;
	private String activationURL;
	private String filename;
	public ConfirmEmailGenerator(String inFilename)
		{
		this.emailAddress="";
		this.emailPassword="";
		this.subject="";
		this.contentText="";
		this.activationURL="";
		filename=inFilename;
		String readline;
		int readEmailMode=0;
		int readPasswordMode=0;
		int readSubjectMode=0;
		int readContentTextMode=0;
		int readActivationURLMode=0;
		
		try
			{
			FileInputStream fis=new FileInputStream(new File(inFilename+".xml"));
			BufferedReader br=new BufferedReader(new InputStreamReader(fis));
			StringWriter emailsw=new StringWriter();
			StringWriter passwdsw=new StringWriter();
			StringWriter subjectsw=new StringWriter();
			StringWriter contentsw=new StringWriter();
			StringWriter urlsw=new StringWriter();
			
			BufferedWriter emailbw=new BufferedWriter(emailsw);
			BufferedWriter passwdbw=new BufferedWriter(passwdsw);
			BufferedWriter subjectbw=new BufferedWriter(subjectsw);
			BufferedWriter contentbw=new BufferedWriter(contentsw);
			BufferedWriter urlbw=new BufferedWriter(urlsw);
			
			while((readline=br.readLine())!=null)
				{
				if(readEmailMode<2)
					{
					readEmailMode=this.readContent(readEmailMode, readline+" ", "FromEmail", emailbw);
					}
				if(readPasswordMode<2)
					{
					readPasswordMode=this.readContent(readPasswordMode, readline+" ", "EmailPassword", passwdbw);
					}
				if(readSubjectMode<2)
					{
					readSubjectMode=this.readContent(readSubjectMode, readline+" ", "Subject", subjectbw);
					}
				if(readContentTextMode<2)
					{
					readContentTextMode=this.readContent(readContentTextMode, readline+" ", "Content", contentbw);
					}
				if(readActivationURLMode<2)
					{
					readActivationURLMode=this.readContent(readActivationURLMode, readline+" ", "ActivationURL", urlbw);
					}
				}
			br.close();
			if(readEmailMode!=2)
				{
				logger.error("No source email address is assigned in XML: "+this.filename+".xml");
				}
			else
				{
				emailbw.flush();
				this.emailAddress=emailsw.toString();
				}
			if(readPasswordMode!=2)
				{
				logger.error("No source email address password is assigned in XML: "+this.filename+".xml");
				}
			else
				{
				passwdbw.flush();
				this.emailPassword=passwdsw.toString();
				}
			if(readSubjectMode!=2)
				{
				this.subject="[Do not reply] Default confirmation email";
				logger.warn("No subject assigned in XML: "+this.filename+".xml");
				}
			else
				{
				subjectbw.flush();
				this.subject=subjectsw.toString();
				}
			if(readActivationURLMode!=2)
				{
				logger.error("No activation URL is assigned in XML: "+this.filename+".xml");
				}
			else
				{
				urlbw.flush();
				this.activationURL=(urlsw.toString()).trim();
				}
			if(readContentTextMode!=2)
				{
				this.contentText="Thank you registration.";
				logger.warn("No description content in XML: "+this.filename+".xml");
				}
			else
				{
				contentbw.flush();
				this.contentText=contentsw.toString();
				}
			emailbw.close();
			passwdbw.close();
			subjectbw.close();
			urlbw.close();
			contentbw.close();
			}
		catch(Exception e)
			{
			e.printStackTrace();
			logger.error(e);
			}
		}
	
	protected String getContentText()
		{
		return this.contentText;
		}
	
	protected String getActivationURL()
		{
		return this.activationURL;
		}
	
	protected String getSubject()
		{
		return this.subject;
		}
	
	protected String getEmailAddress()
		{
		return this.emailAddress;
		}
	
	protected String getEmailPassword()
		{
		return this.emailPassword;
		}
	
	protected String getConfirmationEmail(long inPin, String inUserId)
		{
		StringWriter sw=new StringWriter();
		BufferedWriter bw=new BufferedWriter(sw);
		try
			{
			bw.append(this.contentText+"\r\n");
			bw.append("Click the link: "+this.activationURL+"?ConfirmCode="+inPin+"&UserId="+inUserId+"\r\n");
			bw.append("Or copy and paste the link above to your web browser to submit the confirmation code.");
			bw.flush();
			}
		catch (IOException e)
			{
			logger.error(e);
			}
		return sw.toString();
		}
	
	private int readContent(int currentReadMode, String readline, String targetTag, BufferedWriter bw) throws IOException
		{
		String[] strarray;
		String regexTag="</?"+targetTag+">";
		strarray=readline.split(regexTag);
		System.out.println(java.util.Arrays.toString(strarray)+" strarray.length="+strarray.length);
		switch(currentReadMode)
			{
			case 0:
				{
				switch(strarray.length)
					{
					case 3:
						{
						currentReadMode=2;
						bw.append(strarray[1]);
						break;
						}
					case 2:
						{
						currentReadMode=1;
						bw.append(strarray[1]+"\r\n");
						break;
						}
					case 1:
						{
						break;
						}
					default:
						{
						logger.error("corrept XML: line="+readline+" in "+this.filename+".xml");
						break;
						}
					}
				break;
				}
			case 1:
				{
				switch(strarray.length)
					{
					case 2:
						{
						currentReadMode=2;
						bw.append(strarray[0]);
						break;
						}
					case 1:
						{
						bw.append(strarray[0]+"\r\n");
						break;
						}
					default:
						{
						logger.error("corrept XML: line="+readline+" in "+this.filename+".xml");
						break;
						}
					}
				break;
				}
			default:
				{
				logger.error("corrept XML: line="+readline+" in "+this.filename+".xml");
				break;
				}
			}
		System.out.println("currentReadMode="+currentReadMode);
		return currentReadMode;
		}
	
	public static void main(String[] args)
		{
		if(args.length!=1)
			{
			System.out.println("Usage: java sql.ConfirmEmailGenerator <definition XML file>");
			}
		else
			{
			ConfirmEmailGenerator ceg=new ConfirmEmailGenerator(args[0]);
			System.out.println("Email subject="+ceg.getSubject());
			System.out.println("Email paragraph="+ceg.getConfirmationEmail(123456, "asuma02ad@live.com"));
			System.out.println("From="+ceg.getEmailAddress()+", Password="+ceg.getEmailPassword());
			}
		}
	}
