package db;
import java.sql.Connection;
import java.sql.DriverManager;
public class DatabaseConnectivity {

	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con= DriverManager.getConnection("jdbc:mysql://Localhost:3306/medical_billing","root","Root@123");
			if(con!=null) {
				System.out.println("Connection Successful");
			}
			else
			{
				System.out.println("Connection failed");
			}
			
		}
		catch(Exception e) {
			System.out.println(e);
		}
		

	}

	public static Connection getConnection() {
		// TODO Auto-generated method stub
		return null;
	}


	}


