package sql;

import java.sql.*;

public class Blacklist
	{
	public static Connection conn;

	public static ResultSet bDataExecuteQuery(String syntax)
		{
		// this method is created for get the data from the database
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

	public static int bDataExecuteUpdate(String syntax)
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

	// in bSingleSearch(type, s), type should be Num_B, ID_B or IP_B!!
	public static ResultSet bSingleSearch(String type, String s)
			throws SQLException
		{
		String sql = "select * from blacklist where " + type + "='" + s
				+ "' order by Num_B";
		ResultSet result = bDataExecuteQuery(sql);
		return result;
		}

	public static int bAddData(String ID_B, String IP_B)
		{
		String sql = "insert into blacklist (ID_B,IP_B) values('" + ID_B
				+ "','" + IP_B + "')";
		int result = bDataExecuteUpdate(sql);
		return result;
		}

	// in bUpdateDate(Num_B, type, s), type should be Num_B, ID_B or IP_B!!
	public static int bUpdateData(String Num_B, String type, String s)
		{
		String sql = "update blacklist set " + type + " = '" + s
				+ "' WHERE Num_B = '" + Num_B + "'";
		int result = bDataExecuteUpdate(sql);
		return result;
		}

	public static int bDeleteData(String Num_B) throws SQLException
		{
		ResultSet searchIP = bSingleSearch("Num_B", Num_B);
		if (searchIP.next() == false)
			return -1;
		String sql = "DELETE FROM blacklist WHERE Num_B = '" + Num_B + "' ";
		int result = bDataExecuteUpdate(sql);
		return result;
		}

	// if return -1,it means there is no matched result to be deleted.

	public static void main(String[] args) throws SQLException
		{

		// int r2= bUpdateData("1","ID_B","3");
		// int r1= bAddData("6","2");
		// int r4= bAddData("6","1");
		// int r3= bDeleteData("8");
		// int r4= bDeleteData("9");
		// int r5= bDeleteData("10");
		// int r6= bDeleteData("11");

		}
	}
