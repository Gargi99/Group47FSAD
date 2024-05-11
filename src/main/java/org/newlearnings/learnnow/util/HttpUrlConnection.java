package org.newlearnings.learnnow.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HttpUrlConnection {
	private static final String JDBCURL = "jdbc:mysql://localhost:3306/learnnow";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "Root";
	
	/**
	 * Initiates connection to local database.
	 * 
	 * @return connection
	 */
	public static Connection getHttpUrlConnection() {
		Connection connection = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(JDBCURL, USERNAME, PASSWORD);
			System.out.println("Connection is successful at: " + JDBCURL);
			
		} catch (ClassNotFoundException exception) {
			exception.printStackTrace();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		
		return connection;
	}

}
