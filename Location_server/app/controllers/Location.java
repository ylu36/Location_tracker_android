package models;
public class Location {
    public String username;
    public long timestamp;
    public double latitude, longitude, totalDistance;
    public double speed;

    public Location(String u, long t, double lat, double longitude, double dt1, double s) {
        this.username = u;
        this.timestamp = t;
        this.latitude = lat;
        this.longitude = longitude;
        this.totalDistance = dt1;
        this.speed = s;
    }
}