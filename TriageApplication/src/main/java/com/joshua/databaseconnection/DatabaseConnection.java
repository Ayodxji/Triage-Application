package com.joshua.databaseconnection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
	private static final String DB_URL;
	private static final String USER;
	private static final String PASSWORD;

	static {
		Properties props = new Properties();
		try (InputStream in = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
			if (in == null) {
				throw new RuntimeException("db.properties not found on classpath");
			}
			props.load(in);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load db.properties", e);
		}
		DB_URL = props.getProperty("db.url");
		USER = props.getProperty("db.user");
		PASSWORD = props.getProperty("db.password");
	}

	public static Connection getDBConnection() {
		try {
			return DriverManager.getConnection(DB_URL, USER, PASSWORD);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
}
