package acces;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author itu
 */
public class Connexion {
    public Connection toPg() throws Exception {
 		Connection conn = null;
 		try {
 			Class.forName("org.postgresql.Driver");
 			conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/","postgres","2348");
 		} catch (SQLException e) {
 			System.out.println(e);
 		}
 		return conn; 		
 	}
}
