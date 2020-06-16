package com.srai.configuration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.stereotype.Service;

@Service
public class ConnectionCreation {

	public void connection(String databaseName) throws MalformedURLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		File file = new File("/home/krishna/Downloads/mysql-connector-java-5.1.49.jar");
		URL u = new URL("jar:file://" + file.getPath() + "!/");
		String classname = "com.mysql.cj.jdbc.Driver";
		URLClassLoader ucl = new URLClassLoader(new URL[] { u });
		System.out.println(ucl);
		Driver d = (Driver) Class.forName(classname, true, ucl).newInstance();
		System.out.println(d);
		DriverManager.registerDriver(new DelegatingDriver(d));
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "root");
		System.out.println(con);
		Statement stmt = con.createStatement();
		String sql = "CREATE DATABASE " + databaseName;
		stmt.executeUpdate(sql);
	}
}
