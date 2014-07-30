package sql;

import java.sql.*;

public class Whitelist
	{
	public static Connection conn;

	public static ResultSet WDataExecuteQuery(String syntax)
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

	public static int WDataExecuteUpdate(String syntax)
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

	public static ResultSet WSingleSearch(String type, String s)
			throws SQLException
		{
		String sql = "select * from Whitelist where " + type + "='" + s
				+ "' order by Num_W";
		ResultSet result = WDataExecuteQuery(sql);
		return result;
		}

	public static int WAddData(String ID_W, String IP_W)
		{
		String sql = "insert into Whitelist (ID_W,IP_W) values('" + ID_W
				+ "','" + IP_W + "')";
		int result = WDataExecuteUpdate(sql);
		return result;
		}

	public static int WUpdateData(String Num_W, String type, String s)
		{
		String sql = "update Whitelist set " + type + " = '" + s
				+ "' WHERE Num_W = '" + Num_W + "'";
		int result = WDataExecuteUpdate(sql);
		return result;
		}

	public static int WDeleteData(String Num_W) throws SQLException
		{
		ResultSet searchIP = WSingleSearch("Num_W", Num_W);
		if (searchIP.next() == false)
			return -1;
		String sql = "DELETE FROM Whitelist WHERE Num_W = '" + Num_W + "' ";
		int result = WDataExecuteUpdate(sql);
		return result;
		}

	// if return -1,it means there is no matched result to be deleted.

	public static void main(String[] args) throws SQLException
		{

		// int r2= WUpdateData("1","ID_W","3");
		// int r1= WAddData("5","6","2");
		// int r4= WAddData("6","1");
		int r3 = WDeleteData("12");
		int r4 = WDeleteData("13");
		int r1 = WAddData("6", "10");
		int r = WAddData("6", "11");
		int f = WAddData("6", "12");
		int h = WAddData("6", "13");
		int i = WAddData("6", "14");
		int j = WAddData("6", "15");
		int k = WAddData("6", "16");

		}
	}
