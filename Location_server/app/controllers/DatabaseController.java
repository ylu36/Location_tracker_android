package dbconnection;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseController {

    static String url = "jdbc:sqlite:db/location.db";
    public static void connect() {
        Connection conn = null;
        try {
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println("err: "+e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println("err is: "+ex.getMessage());
            }
        }
    }

    public static void dropTable() {
                
        // SQL statement for creating a new table
        String sql = "DROP TABLE Location;";
        
        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            System.out.println("table Location has been dropped!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createNewTable() {
                
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS Location (\n"
                + "	id integer PRIMARY KEY,\n"
                + " username text NOT NULL,\n"
                + "	timestamp integer,\n"
                + " latitude real,\n"
                + " longitude real\n"
                // + " distance real\n"
                + ");";
        
        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            System.out.println("table Location has been created!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insert(String username, long timestamp, double latitude, double longitude) {
        String sql = "INSERT INTO Location(username, timestamp, latitude, longitude) VALUES(?,?,?,?)";
 
        try (
            Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setLong(2, timestamp);
            pstmt.setDouble(3, latitude);
            pstmt.setDouble(4, longitude);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void selectAll(){
        String sql = "SELECT id, username, timestamp, latitude, longitude FROM Location";
        
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("id") +  "\t" + 
                                   rs.getString("username") + "\t" +
                                   rs.getDouble("latitude"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}