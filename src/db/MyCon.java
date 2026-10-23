package db;
import java.sql.Connection;
import java.sql.DriverManager;

public class MyCon {
	
	//##################################Database Connectivity#######################################

	public static Connection getCon()
	{
		Connection con=null;
		try {
			
		Class.forName("com.mysql.cj.jdbc.Driver");
		con=DriverManager.getConnection("jdbc:mysql://Localhost:3306/medical_billing","root","Root@123");
		}
		catch(Exception e) {
			System.out.println(""+e.getMessage());
		}
		return con;

	}


}
