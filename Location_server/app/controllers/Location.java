package models;
public class Location {
    public String username;
    public long timestamp;
    public double latitude, longitude, distance;

    public Location(String u, long t, double lat, double longitude, double d) {
        this.username = u;
        this.timestamp = t;
        this.latitude = lat;
        this.longitude = longitude;
        this.distance = d;
    }
}