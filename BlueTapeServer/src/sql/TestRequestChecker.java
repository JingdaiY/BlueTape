package sql;

public class TestRequestChecker
	{
	public TestRequestChecker(String inFilename)
		{
		RequestKeyChecker rkc=new RequestKeyChecker(inFilename);
		System.out.println(rkc.dump());
		}
	
	public static void main(String[] args)
		{
		// TODO Auto-generated method stub
		if(args.length!=1)
			{
			System.out.println("Missing filename");
			}
		else
			{
			new TestRequestChecker(args[0]);
			}
		}

	}
