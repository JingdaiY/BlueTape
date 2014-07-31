package sql;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.*;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class Flyer_httpserver extends HttpServlet
	{
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
		{
		// ServletOutputStream out = response.getOutputStream();
		// PrintWriter writer = response.getWriter();
		// response.setContentType("text/html"); //required
		// String[] infor = new String[15];
		// String[] type = new String[15];
		// type[0] = "Request";
		// type[1] = "Email_address";
		// type[2] = "Description";
		// type[3] = "Title";
		// type[4] = "Time";
		// type[5] = "Location";
		// type[6] = "Sponsor";
		// type[7] = "Tags";//tags
		// type[8] = "Image";//url or address and studentgroup
		// type[9] = "IP";
		// type[10] = "beginningTime";
		// type[11] = "endTime";
		// infor[0] = request.getParameter("Request");
		// infor[1] = request.getParameter("Email_address"); //get the ID of the
		// client
		// infor[2] = request.getParameter("Description");
		// infor[3] = request.getParameter("Title");
		// infor[4] = request.getParameter("Time");
		// infor[5] = request.getParameter("Location");
		// infor[6] = request.getParameter("Sponsor");
		// infor[7] = request.getParameter("Tags");
		// infor[8] = request.getParameter("Image");
		// infor[9] = request.getParameter("IP");
		// infor[10] = request.getParameter("beginningTime");
		// infor[11] = request.getParameter("endTime");
		// // else if (infor.startsWith(Protocol.PRIVATE_ROUND)
		// // && infor.endsWith(Protocol.PRIVATE_ROUND)){
		// // String userAndMsg = getRealMsg(infor);
		// // String user = userAndMsg.split(Protocol.SPLIT_SIGN)[0];
		// // String msg = userAndMsg.split(Protocol.SPLIT_SIGN)[1];
		// // Server.clients.get(user).println(Server.clients
		// // .getKeyByValue(ps) + "..." + msg);
		// // }
		// int result=5;
		// ResultSet resultSet=null;
		// if(infor[0].equals("viewMyFavourite")){
		// try {
		// resultSet = Flyers.viewMyFavourite(infor[1]);
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// // else if(infor[0].equals("fview")){
		// // resultSet = Flyers.fview(infor[9]);
		// // }
		// else if(infor[0].equals("fAdd")){
		// result =
		// Flyers.fAddData(infor[2],infor[3],infor[4],infor[5],infor[6],infor[7],infor[8]);
		// }
		// else if(infor[0].equals("fUpdate")){
		// for(int i =2; i<=8; i++){
		// if (infor[i]!=null) result =
		// Flyers.fUpdateData(infor[9],type[i],infor[i]);
		// }
		// }
		// else if(infor[0].equals("fDelete")){
		// try {
		// result = Flyers.fDeleteData(infor[9],infor[1]);
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		//
		// else if(infor[0].equals("fSingleSearch")){
		// for(int i=3;i<=8; i++){
		// if(infor[i]!=null)
		// try {
		// resultSet = Flyers.fSingleSearch(type[i],infor[i],infor[1]);
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// }
		// else if(infor[0].equals("fTimeSearch")){
		// try {
		// resultSet = Flyers.fTimeSearch(infor[10], infor[11], infor[1]);
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// else if(infor[0].equals("fMultipleSearch")){
		// String sql = "select * from Flyers where ";
		// if(infor[3]!=null) sql+=type[3] + "='" + infor[3] + "' AND ";
		// if(infor[5]!=null) {
		// int i=0;
		// String ss="";
		// while(i<infor[5].length()){
		// if(infor[5].charAt(i)==' ') ss=ss+"%";
		// else ss+=infor[5].charAt(i);
		// i++;
		// }
		// sql+=type[5]+"LIKE '%"+ss+"%' AND ";
		// }
		// for(int i=6;i<=7; i++){
		// if(infor[i]!=null) sql+=type[i] + "='" + infor[i] + "'AND ";
		// }
		// if(infor[10]!=null) sql+=type[4] + ">" + infor[10] + " AND ";
		// //type[4] is "Time"
		// if(infor[11]!=null) sql+=type[4] + "<" + infor[11] + " AND ";
		// //type[4] is "Time"
		// try {
		// ResultSet blacklist;
		// blacklist = Blacklist.bSingleSearch("ID_B", infor[1]);
		// if(blacklist.next()){
		// sql = sql + " and IP not in ("+blacklist.getString("IP_B");
		// while (blacklist.next()) {
		// sql = sql + " ,"+blacklist.getString("IP_B");
		// }
		// sql = sql + ") order by IP";
		// }
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// try {
		// resultSet = Flyers.fMultipleSearch(sql);
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// if(result!=5) writer.print("result");
		// else
		// try {
		// writer.print(xml.generateXML(resultSet));
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } //print() or println() same
		// //out.close();
		// try {
		// Flyers.conn.close();
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		}

	@SuppressWarnings("resource")
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
		{

		PrintWriter writer = response.getWriter();
		response.setContentType("text/html"); // required
		String[] infor = new String[15];
		String[] type = new String[15];
		type[0] = "Request";
		type[1] = "Email_address";
		type[2] = "Description";
		type[3] = "Title";
		type[4] = "Time";
		type[5] = "Location";
		type[6] = "Sponsor";
		type[7] = "Tags";// tags
		type[8] = "Image";// url or address and studentgroup
		type[9] = "IP";
		type[10] = "beginningTime";
		type[11] = "endTime";
		infor[0] = request.getParameter("Request");
		infor[1] = request.getParameter("Email_address"); // get the ID of the
															// client
		infor[2] = request.getParameter("Description");
		infor[3] = request.getParameter("Title");
		infor[4] = request.getParameter("Time");
		infor[5] = request.getParameter("Location");
		infor[6] = request.getParameter("Sponsor");
		infor[7] = request.getParameter("Tags");
		infor[8] = request.getParameter("Image");
		infor[9] = request.getParameter("IP");
		infor[10] = request.getParameter("beginningTime");
		infor[11] = request.getParameter("endTime");
		// else if (infor.startsWith(Protocol.PRIVATE_ROUND)
		// && infor.endsWith(Protocol.PRIVATE_ROUND)){
		// String userAndMsg = getRealMsg(infor);
		// String user = userAndMsg.split(Protocol.SPLIT_SIGN)[0];
		// String msg = userAndMsg.split(Protocol.SPLIT_SIGN)[1];
		// Server.clients.get(user).println(Server.clients
		// .getKeyByValue(ps) + "..." + msg);
		// }
		int result = 5;
		ResultSet resultSet = null;
		if (infor[0].equals("viewMyFavourite"))
			{
			try
				{
				resultSet = Flyer.viewMyFavourite(infor[1], infor[9]);
				}
			catch (SQLException e)
				{
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			}
		// else if(infor[0].equals("fview")){
		// resultSet = Flyers.fview(infor[9]);
		// }
		else
			if (infor[0].equals("fAdd"))
				{
				System.out.println("fAdd");
				result = Flyer.fAddData(infor[1], infor[2], infor[3], infor[4],
						infor[5], infor[6], infor[7], infor[8]);
				}
			else
				if (infor[0].equals("fUpdate"))
					{
					for (int i = 2; i <= 8; i++)
						{
						if (infor[i] != null && infor[i].length() > 0)
							{
							System.out.println("fUpdate");
							result = Flyer.fUpdateData(infor[3], type[i],
									infor[i]);
							}
						}
					}
				else
					if (infor[0].equals("fDelete"))
						{
						try
							{
							result = Flyer.fDeleteData(infor[3], infor[1]);
							}
						catch (SQLException e)
							{
							// TODO Auto-generated catch block
							e.printStackTrace();
							}
						}
					else
						if (infor[0].equals("AddFavourite"))
							{
							result = Flyer.AddFavourite(infor[1], infor[9]);
							}

						else
							if (infor[0].equals("FavouriteList"))
								{
								try
									{
									resultSet = Flyer.FavouriteList(infor[1]);
									}
								catch (SQLException e)
									{
									// TODO Auto-generated catch block
									e.printStackTrace();
									}
								}
							else
								if (infor[0].equals("fSingleSearch"))
									{
									for (int i = 3; i <= 8; i++)
										{
										if (infor[i] != null)
											try
												{
												resultSet = Flyer
														.fSingleSearch(type[i],
																infor[i],
																infor[1]);
												}
											catch (SQLException e)
												{
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
												}
										}
									}
								// else if(infor[0].equals("fTimeSearch")){
								// try {
								// resultSet = Flyer.fTimeSearch(infor[10],
								// infor[11], infor[1]);
								// } catch (SQLException e) {
								// // TODO Auto-generated catch block
								// e.printStackTrace();
								// }
								// }
								else
									if (infor[0].equals("fMultipleSearch"))
										{
										String sql = "select * from Flyers where ";
										boolean wannaAnd = false;
										if (infor[3] != null
												&& infor[3].length() > 0)
											{
											sql += type[3] + "='" + infor[3]
													+ "'";
											wannaAnd = true;
											}
										if (infor[5] != null
												&& infor[5].length() > 0)
											{
											int i = 0;
											String ss = "";
											while (i < infor[5].length())
												{
												if (infor[5].charAt(i) == ' ')
													ss = ss + "%";
												else
													ss += infor[5].charAt(i);
												i++;
												}
											if (wannaAnd)
												sql += " and " + type[5]
														+ " LIKE '%" + ss
														+ "%'";
											else
												sql += type[5] + " LIKE '%"
														+ ss + "%'";
											}
										for (int i = 6; i <= 7; i++)
											{
											if (infor[i] != null
													&& infor[i].length() > 0)
												{
												if (wannaAnd)
													sql += " AND " + type[i]
															+ "='" + infor[i]
															+ "'";
												else
													sql += " " + type[i] + "='"
															+ infor[i] + "'";
												}
											}
										if (infor[10] != null
												&& infor[10].length() > 0)
											{
											if (wannaAnd)
												sql += " AND " + type[4] + ">'"
														+ infor[10] + "' AND ";
											else
												sql += " " + type[4] + ">'"
														+ infor[10] + "' AND ";
											}
										if (infor[11] != null
												&& infor[11].length() > 0)
											{
											if (wannaAnd)
												sql += " AND " + type[4] + "<'"
														+ infor[11] + "'";
											sql += " " + type[4] + "<'"
													+ infor[11] + "'";
											}
										try
											{
											ResultSet blacklist;
											blacklist = Blacklist
													.bSingleSearch("ID_B",
															infor[1]);
											if (blacklist.next())
												{
												sql = sql
														+ " and IP not in ("
														+ blacklist
																.getString("IP_B");
												while (blacklist.next())
													{
													sql = sql
															+ " ,"
															+ blacklist
																	.getString("IP_B");
													}
												sql = sql + ") order by IP";
												}
											}
										catch (SQLException e)
											{
											// TODO Auto-generated catch block
											e.printStackTrace();
											}
										try
											{
											// resultSet.close();
											System.out.println(sql);
											resultSet = Flyer
													.fMultipleSearch(sql);
											}
										catch (SQLException e)
											{
											// TODO Auto-generated catch block
											e.printStackTrace();
											}
										}
									else
										if (infor[0].equals("fRandom"))
											{
											System.out.println("fRandom");
											try
												{
												resultSet = Flyer.fRandom(
														infor[10], infor[11]);
												}
											catch (SQLException e)
												{
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
												}
											}
		if (result != 5)
			writer.print(result);
		else
			try
				{
				writer.print(xml.generateXML(resultSet));
				}
			catch (SQLException e)
				{
				// TODO Auto-generated catch block
				e.printStackTrace();
				} // print() or println() same
		// out.close();
		try
			{
			Flyer.conn.close();
			}
		catch (SQLException e)
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}

	}
