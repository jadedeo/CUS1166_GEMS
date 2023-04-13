package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class GEMSConnect {
	/*
	static Connection con;
	static Statement st;

	public GEMSConnect() {
		connect();
	}
	
	public static void connect() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Diver Loaded Successfully");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/GEMSDB","root","mysqlroot123");
			System.out.println("Successful Connection");
			st = con.createStatement();
			System.out.println("Statement created Successfully");
			System.out.println("Now you can access the database");
			System.out.println();
		}
		catch(ClassNotFoundException cnfe) {
			System.err.println(cnfe);
		}
		catch(SQLException sqle) {
			System.err.println(sqle);
		}
	}
	*/
	
	Connection con = null;
	
	public static Connection connect() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			//System.out.println("Driver Loaded Successfully");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/GEMSDB","root","mysqlroot123");
			System.out.println("Successful Connection");
			//st = con.createStatement();
			//System.out.println("Statement created Successfully");
			//System.out.println("Now you can access the database");
			System.out.println();
			return con;
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(null, e);
			return null;
		}
	}
	
	
}