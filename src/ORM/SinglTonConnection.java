package ORM;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Stack;

public class SinglTonConnection {
	
	private static Connection cnx;
	private Statement stm;
	
	
	private  SinglTonConnection() {
		try {
			
			Class.forName(DataSource.driver);
			cnx=DriverManager.getConnection(DataSource.url, DataSource.user, DataSource.pwd);
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public static Connection getConnection() {
		if(cnx==null) new SinglTonConnection();
		 return cnx;
	}
	

}
