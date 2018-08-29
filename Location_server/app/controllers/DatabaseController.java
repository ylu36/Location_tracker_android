package dbconnection;
import javax.inject.Inject;
import java.lang.Object;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import play.db.*;
import models.*;
public class DatabaseController {
    private Database db;
    private DatabaseExecutionContext executionContext;
    
    @Inject
    public DatabaseController(Database db, DatabaseExecutionContext executionContext) {
        this.db = db;
        this.executionContext = executionContext;
    }

    public void dropTable() {
                
        // SQL statement for creating a new table
        String sql = "DROP TABLE Location;";
        
        try (Connection conn = db.getConnection();
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            System.out.println("table Location has been dropped!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createNewTable() {
                
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS Location (\n"
                + "	id integer PRIMARY KEY,\n"
                + " username text NOT NULL,\n"
                + "	timestamp integer,\n"
                + " latitude real,\n"
                + " longitude real,\n"
                 + " distance real\n"
                + ");";
        
        try (Connection conn = db.getConnection();
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            System.out.println("table Location has been created!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Location getPreviousLocation(String username) {
        
        String sql = "SELECT timestamp, latitude, longitude, distance FROM Location where username =" + username + " order by id desc limit 1";
        Location loc = null;
        try (
            Connection conn = db.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                long time = rs.getLong("timestamp");
                double lat = rs.getDouble("latitude");
                double longi = rs.getDouble("longitude");
                double distance = rs.getDouble("distance");
                loc = new Location(username, time, lat, longi, distance);
            }
            
            return loc;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return null;
    }

    public void insert(String username, long timestamp, double latitude, double longitude, double distance) {
        String sql = "INSERT INTO Location(username, timestamp, latitude, longitude, distance) VALUES(?,?,?,?,?)";
 
        try (
            Connection conn = db.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setLong(2, timestamp);
            pstmt.setDouble(3, latitude);
            pstmt.setDouble(4, longitude);
            pstmt.setDouble(5, distance);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void selectAll(){
        String sql = "SELECT id, username, timestamp, latitude, longitude FROM Location";
        
        try (Connection conn = db.getConnection();
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