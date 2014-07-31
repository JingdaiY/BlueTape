package sql;

import java.net.UnknownHostException;
import java.sql.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;

public class User
	{
	public static Connection conn;
	public User()
		{
		
		}

	public static ResultSet dataExecuteQuery(String syntax)
		{
		// this method is created for getting the data from the database
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://cognitum/min_bluetape_db";
		String user = "pmin_bluetape_u";
		String password = "hodedob1u3+4p3";
		try
			{
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, password);
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(syntax);
			return rs;
			}
		catch (ClassNotFoundException e)
			{
			System.out.println("Sorry,can`t find the Driver!");
			e.printStackTrace();
			return null;
			}
		catch (SQLException e)
			{
			e.printStackTrace();
			return null;
			}
		catch (Exception e)
			{
			e.printStackTrace();
			return null;
			}
		}

                
	public static int dataExecuteUpdate(String syntax)
		{
		// this method is created for searching,updating and inserting the data.
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://cognitum/min_bluetape_db";
		String user = "pmin_bluetape_u";
		String password = "hodedob1u3+4p3";
		try
			{
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, password);
			Statement statement = conn.createStatement();
			int rs = statement.executeUpdate(syntax);
			return rs;
			}
		catch (ClassNotFoundException e)
			{
			System.out.println("Sorry,can't find the Driver!");
			e.printStackTrace();
			return 0;
			}
		catch (SQLException e)
			{
			e.printStackTrace();
			return 0;
			}
		catch (Exception e)
			{
			e.printStackTrace();
			return 0;
			}
		}

	public static ResultSet singleSearch(String type, String s)
			throws SQLException
		{
		String sql = "select * from Personal_information where " + type + "='"
				+ s + "'";
		ResultSet result = dataExecuteQuery(sql);
		return result;
		}

	public static ResultSet MultipleSearch(String sql)
		{
		// String sql="select * from Personal_information where ";
		// for(int i=0;i<s.length()-1;i++){
		// if(s.charAt(i)=='=') sql+="='";
		// else
		// if((s.charAt(i+1)=='*'||s.charAt(i+1)=='/'||s.charAt(i+1)==')')&&s.charAt(i)!=')')
		// sql+=s.charAt(i)+"'";
		// else if(s.charAt(i)=='*') sql+=" AND ";
		// else if(s.charAt(i)=='/') sql+=" OR ";
		// else sql+=s.charAt(i);
		// }
		// sql+="'";
		ResultSet result = dataExecuteQuery(sql);
		return result;
		}

	public static int addData(String Email_address, String User_password,
			String FirstName, String LastName, String Gender, String Department)
			throws Exception
		{
		// Encrypt de1 = new Encrypt();
		// byte[] encontent = de1.Encrytor(User_password);
		// String Encrypt_password = new String(encontent);

		String sql = "insert into Personal_information(Email_address, User_password, FirstName, LastName, Gender, Department) values('"
				+ Email_address
				+ "','"
				+ User_password
				+ "','"
				+ FirstName
				+ "','" + LastName + "','" + Gender + "','" + Department + "')";
		System.out.println("I translated it!!");
		int result = dataExecuteUpdate(sql);
		return result;
		}

	public static int updateData(String Email_address, String type, String s)
		{
		String sql = "UPDATE Personal_information SET " + type + " = '" + s
				+ "' WHERE Email_address = '" + Email_address + "'";
		int result = dataExecuteUpdate(sql);
		return result;
		}

	public static int deleteData(String Email_address) throws SQLException
		{
		ResultSet searchId_p = singleSearch("Email_address", Email_address);
		if (searchId_p.next() == false)
			return -1;
		String sql = "DELETE FROM Personal_information WHERE Email_address = '"
				+ Email_address + "' ";
		int result = dataExecuteUpdate(sql);
		return result;
		}

	// if return -1,it means there is no matched result to be deleted.

	public static void main(String[] args) throws Exception
		{

		}
	}
