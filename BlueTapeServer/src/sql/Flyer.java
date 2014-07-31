package sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Flyer
	{
	public static Connection conn;

	public static ResultSet fDataExecuteQuery(String syntax)
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
                

	public static int fDataExecuteUpdate(String syntax)
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

	public static ResultSet fSingleSearch(String type, String s,
			String Email_Address) throws SQLException
		{
		String sql = "";
		if (type.equalsIgnoreCase("Location"))
			{
			int i = 0;
			String ss = "";
			while (i < s.length())
				{
				if (s.charAt(i) == ' ')
					ss = ss + "%";
				else
					ss += s.charAt(i);
				i++;
				}
			sql = "select * from Flyer where Location LIKE '%" + ss + "%'";
			}
		else
			sql = "select * from Flyer where " + type + "='" + s + "'";
		ResultSet blacklist = Blacklist.bSingleSearch("ID_B", Email_Address);
		return blacklist;
		// if(blacklist.next()){
		// sql = sql + " and IP not in ("+blacklist.getString("IP_B");
		// while (blacklist.next()) {
		// sql = sql + " ,"+blacklist.getString("IP_B");
		// }
		// sql = sql + ") order by IP";
		// }
		//
		// ResultSet result = fDataExecuteQuery(sql);
		// return result;
		}

	// ! means location,? means time,% means or,* means and, $ means not.
	public static ResultSet fMultipleSearch(String sql) throws SQLException
		{

		ResultSet result = fDataExecuteQuery(sql);
		return result;
		}

	public static ResultSet FavouriteList(String Email_Address)
			throws SQLException
		{
		String sql = "select * from Flyers,whitelist where Flyers.IP=whitelist.IP_W and whitelist.ID_W='"
				+ Email_Address + "'";
		System.out.println(sql);
		ResultSet result = fDataExecuteQuery(sql);
		return result;
		}

	public static int fAddData(String Email_Address, String Title,
			String Description, String Time, String Location, String Sponsor,
			String Tags, String Image)
		{
		String sql = "insert into Flyers (Email_Address, Title, Description, Time, Location, Sponsor, Tags, Image) values('"
				+ Email_Address
				+ "','"
				+ Title
				+ "','"
				+ Description
				+ "','"
				+ Time
				+ "','"
				+ Location
				+ "','"
				+ Sponsor
				+ "','"
				+ Tags
				+ "','" + Image + "')";
		int result = fDataExecuteUpdate(sql);
		return result;
		}

	public static int fUpdateData(String IP, String type, String s)
		{
		String sql = "update Flyers set " + type + " = '" + s + "' WHERE IP ="
				+ IP;
		int result = fDataExecuteUpdate(sql);
		return result;
		}

	public static int fDeleteData(String IP, String Email_Address)
			throws SQLException
		{
		ResultSet searchIP = fSingleSearch("IP", IP, Email_Address);
		if (searchIP.next() == false)
			return -1;
		String sql = "DELETE FROM Flyers WHERE IP = '" + Email_Address + "' ";
		int result = fDataExecuteUpdate(sql);
		return result;
		}

	public static ResultSet fRandom(String BeginTime, String EndTime)
			throws SQLException
		{
		// String
		// sql="SELECT * FROM 'Flyers' AS t1 JOIN (SELECT ROUND(RAND() * ((SELECT MAX(IP) FROM 'Flyers')-(SELECT MIN(IP) FROM 'Flyers'))+(SELECT MIN(IP) FROM 'Flyers')) AS IP) AS t2 WHERE t1.IP >= t2.IP and t1.Time> '"+BeginTime+"' AND t1.Time<'"+EndTime+"' ORDER BY t1.IP LIMIT 1";
		String sql = "SELECT * FROM Flyers ORDER BY RAND() LIMIT 5";
		ResultSet result = fDataExecuteQuery(sql);
		return result;
		}

	// if return -1,it means there is no matched result to be deleted.
	public static int AddFavourite(String Email_address, String IP)
		{
		String sql = "insert into whitelist (ID_W,IP_W) values('"
				+ Email_address + "','" + IP + "')";
		System.out.println(sql);
		int result = fDataExecuteUpdate(sql);
		return result;
		}

	// Oct.6th changed the below part of code;
	public static ResultSet viewMyFavourite(String Email_Address, String IP)
			throws SQLException
		{ // (此处的ID是指此时用户的Email_Address)
		// return Whitelist.WSingleSearch("ID_W",Email_Address);
		String ip = "0";
		if (IP != null && !IP.equals(' ') && IP.length() > 0)
			ip = IP;
		String sql = "SELECT Flyers.IP, Flyers.Description, Flyers.Title, Flyers.Time, Flyers.Location, Flyers.Sponsor, Flyers.Tags, Flyers.Image"
				+ " FROM Flyers,whitelist WHERE IP= "
				+ IP
				+ " or (Flyers.IP=whitelist.IP_W and whitelist.ID_W='"
				+ Email_Address + "')";
		System.out.println(sql);
		ResultSet result = fDataExecuteQuery(sql);
		return result;
		}

	public static String numberOfEvents(String year, String month)
			throws SQLException
		{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		xml = xml + "<year>\n" + "\t<name>" + year + "</name>\n" + "<month>\n"
				+ "\t<name>" + month + "</name>\n";
		double beginningTime = Double.parseDouble(year) * 10000.0;
		double endTime = Double.parseDouble(year) * 10000.0;
		if (month.equalsIgnoreCase("January"))
			{
			beginningTime += 100;
			endTime += 101;
			}
		if (month.equalsIgnoreCase("February"))
			{
			beginningTime += 200;
			endTime += 201;
			}
		if (month.equalsIgnoreCase("March"))
			{
			beginningTime += 300;
			endTime += 301;
			}
		if (month.equalsIgnoreCase("May"))
			{
			beginningTime += 500;
			endTime += 501;
			}
		if (month.equalsIgnoreCase("July"))
			{
			beginningTime += 700;
			endTime += 701;
			}
		if (month.equalsIgnoreCase("August"))
			{
			beginningTime += 800;
			endTime += 801;
			}
		if (month.equalsIgnoreCase("October"))
			{
			beginningTime += 1000;
			endTime += 1001;
			}
		if (month.equalsIgnoreCase("November"))
			{
			beginningTime += 1100;
			endTime += 1101;
			}
		if (month.equalsIgnoreCase("April"))
			{
			beginningTime += 400;
			endTime += 401;
			}
		if (month.equalsIgnoreCase("June"))
			{
			beginningTime += 600;
			endTime += 601;
			}
		if (month.equalsIgnoreCase("September"))
			{
			beginningTime += 900;
			endTime += 901;
			}
		if (month.equalsIgnoreCase("Dectember"))
			{
			beginningTime += 1200;
			endTime += 1201;
			}
		for (int i = 1; i < 32; i++)
			{
			beginningTime++;
			endTime++;
			String sql = "select * from Flyers where Time>" + beginningTime
					+ " and " + "Time<" + endTime;
			ResultSet result = fDataExecuteQuery(sql);
			int count = 0;
			if (result.next())
				{
				xml = xml + "<day>" + i + "\n";
				xml += "\t<event>" + count + "\n";
				xml = xml + "\t\t<Email_Address>"
						+ result.getString("Email_Address")
						+ "</Email_Address>\n";
				xml = xml + "\t\t<Description>"
						+ result.getString("Description") + "</Description>\n";
				xml = xml + "\t\t<Title>" + result.getString("Title")
						+ "</Title>\n";
				xml = xml + "\t\t<Time>" + result.getString("Time")
						+ "</Time>\n";
				xml = xml + "\t\t<Location>" + result.getString("Location")
						+ "</Location>\n";
				xml = xml + "\t\t<Sponsor>" + result.getString("Sponsor")
						+ "</Sponsor>\n";
				xml = xml + "\t\t<Tags>" + result.getString("Tags")
						+ "</Tags>\n";
				xml = xml + "\t\t<Image>" + result.getString("Image")
						+ "</Image>\n";
				xml = xml + "\t\t<IP>" + result.getString("IP") + "</IP>\n";
				xml += "\t</event>\n";
				count++;
				}
			while (result.next())
				{
				xml += "\t<event>" + count + "\n";
				xml = xml + "\t\t<Email_Address>"
						+ result.getString("Email_Address")
						+ "</Email_Address>\n";
				xml = xml + "\t\t<Description>"
						+ result.getString("Description") + "</Description>\n";
				xml = xml + "\t\t<Title>" + result.getString("Title")
						+ "</Title>\n";
				xml = xml + "\t\t<Time>" + result.getString("Time")
						+ "</Time>\n";
				xml = xml + "\t\t<Location>" + result.getString("Location")
						+ "</Location>\n";
				xml = xml + "\t\t<Sponsor>" + result.getString("Sponsor")
						+ "</Sponsor>\n";
				xml = xml + "\t\t<Tags>" + result.getString("Tags")
						+ "</Tags>\n";
				xml = xml + "\t\t<Image>" + result.getString("Image")
						+ "</Image>\n";
				xml = xml + "\t\t<IP>" + result.getString("IP") + "</IP>\n";
				xml += "\t</event>\n";
				count++;
				}
			if (count != 0)
				{
				xml = xml + "<TotalNumber>" + count + "</TotalNumber>\n";
				xml += "</day>\n";
				}
			}
		xml = xml + "</month>\n" + "</year>";
		return xml;
		}

	public static void main(String[] args) throws SQLException
		{

		}

	}