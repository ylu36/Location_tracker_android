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
    private static final double d2r = Math.PI / 180.;
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
                + " totalDistance real,\n"
                + " speed real\n"
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
    // ref: https://en.wikipedia.org/wiki/Haversine_formula
    public double calculateDistance(double lat1, double long1, double lat2, double long2) {
        double dlong = (long2 - long1) * d2r;
        double dlat = (lat2 - lat1) * d2r;
        double a = Math.pow(Math.sin(dlat/2.0), 2) + Math.cos(lat1*d2r) * Math.cos(lat2*d2r) * Math.pow(Math.sin(dlong/2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = 6367 * c * 1000;

        return d;
    }

    public double[] getDistanceAndSpeed(Location location) {        
        double[] res = new double[2];
        String sql = "SELECT latitude, longitude, timestamp from location where username=? order by id asc";
        long timeSince = 0;
        try (
            Connection conn = db.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, location.username);
                ResultSet result = pstmt.executeQuery();
                Double currentLat = location.latitude;
                Double currentLon = location.longitude;
                Long currentTime = location.timestamp;
                Double dist = 0.0;
                dist = calculateDistance(result.getDouble(1), result.getDouble(2), currentLat, currentLon);
                res[0] = dist;
                timeSince = (currentTime - result.getLong(3))/1000;
                res[1] = dist / timeSince;
            return res;
        } catch (SQLException e) {
            System.out.println("err is "+e.getMessage());
        }
        return res;
    }

    public void insert(String username, long timestamp, double latitude, double longitude, double distance, double speed) {
        String sql = "INSERT INTO Location(username, timestamp, latitude, longitude, totalDistance, speed) VALUES(?,?,?,?,?,?)";
 
        try (
            Connection conn = db.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setLong(2, timestamp);
            pstmt.setDouble(3, latitude);
            pstmt.setDouble(4, longitude);
            pstmt.setDouble(5, distance);
            pstmt.setDouble(6, speed);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("err is: "+e.getMessage());
        }
    }

    public void selectAll(){
        String sql = "SELECT id, username, timestamp, latitude, longitude, totalDistance, speed FROM Location";
        
        try (Connection conn = db.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
             double freq = 0;
             double speed = rs.getDouble("speed");
             if(speed <= 1) freq = 5;
             else if(speed >= 20) freq = 1;
             else freq = Math.abs(5 - speed / 5);
            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("id") +  "\t" + 
                                   rs.getString("username") + "\t" +
                                   rs.getLong("timestamp") + "\t" +
                                   rs.getDouble("latitude")+ '\t' + 
                                   rs.getDouble("longitude")+ '\t'+
                                   rs.getDouble("totalDistance")+ '\t'+
                                   rs.getDouble("speed") + '\t' + 
                                   freq);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}