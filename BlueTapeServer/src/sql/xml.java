package sql;

import java.sql.*;

public class xml
	{
	public static String generateXML(ResultSet rs) throws SQLException
		{

		final StringBuffer buffer = new StringBuffer(1024 * 4);
		// if (rs == null) return "";
		// if (!rs.next()) return "";
		buffer.append("<?xml version=\1.0\" encoding=\"UTF-8\"?>\n"); // XML header
		buffer.append("<ResultSet>\n");
		ResultSetMetaData rsmd = rs.getMetaData(); //Get result set's meta data
		int colCount = rsmd.getColumnCount(); //total column count
		int id = 0;
		while (rs.next())
			{
			// Processing the data from the result set induvidually
			// The format is row id, col name, col context
			buffer.append("\t<row id=\"").append(id).append("\">\n");
			for (int i = 1; i <= colCount; i++)
				{
				int type = rsmd.getColumnType(i); // get column type
				buffer.append("\t\t<col name=\"" + rsmd.getColumnName(i)+ "\">");
				buffer.append(getvalue(rs, i, type));
				buffer.append("</col>\n");
				}
			buffer.append("\t</row>\n");
			id++;
			}
		buffer.append("</ResultSet>");
		// rs.close();
		return buffer.toString();
		}

	/**
	 * This method gets the value of the specified column
	 * and write as a string
	 * 
	 * @param ResultSet rs (input result set)
	 * @param int colNum (column index being read)
	 * @param int type (data type)
	 */

	// public static boolean isNumeric(String str){
	// int a = 0;
	// for (int i = str.length();--i>=0;){
	// if (Character.isDigit(str.charAt(i))){
	// a++;
	// }
	// }
	// if(a>=8)
	// return true;
	// else
	// return false;
	// }
	private static String getvalue(final ResultSet rs, int colNum, int type)
			throws SQLException
		{
		switch (type)
			{
			case Types.ARRAY:
			case Types.BLOB:
			case Types.CLOB:
			case Types.DISTINCT:
			case Types.LONGVARBINARY:
			case Types.VARBINARY:
			case Types.BINARY:
			case Types.REF:
			case Types.STRUCT:
			return "undefined";
			default:
				{
				Object value = rs.getObject(colNum);
				if (rs.wasNull() || (value == null))
					return ("null");
				else
					return (value.toString());
				}
			}
		}

	}
