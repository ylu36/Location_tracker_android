package controllers;
import dbconnection.*;
import com.google.inject.Inject;
import play.data.*;
import play.libs.Json;
import java.util.*;
import java.io.*;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import java.lang.Object;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    private static final double d2r = Math.PI / 180.;

    @Inject        
    DatabaseController databaseController;
    // FormFactory formFactory;
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok(views.html.index.render());
    }
    
    @Inject
    FormFactory formFactory;

    public Result handleupdates() {
        // databaseController.dropTable();
        databaseController.createNewTable();
        DynamicForm dynamicForm = formFactory.form().bindFromRequest();
        String username = dynamicForm.get("username");
        String timestampString = dynamicForm.get("timestamp");
        long timestamp = Long.parseLong(timestampString);
        String latitudeString = dynamicForm.get("latitude");
        String longitudeString = dynamicForm.get("longitude");
        double latitude = latitudeString == null ? null : Double.parseDouble(latitudeString);
        double longitude = longitudeString == null? null : Double.parseDouble(longitudeString);
        double distance = 0;
        Location location = new Location(username, timestamp, latitude, longitude, distance);
        databaseController.insert(username, timestamp, latitude, longitude, distance);
        databaseController.selectAll();
        JsonNode locationJson = Json.toJson(location);
        return ok(locationJson);
    }


    // ref: https://en.wikipedia.org/wiki/Haversine_formula
    public double calculateDistanceInKm(double lat1, double long1, double lat2, double long2) {
        double dlong = (long2 - long1) * d2r;
        double dlat = (lat2 - lat1) * d2r;
        double a = Math.pow(Math.sin(dlat/2.0), 2) + Math.cos(lat1*d2r) * Math.cos(lat2*d2r) * Math.pow(Math.sin(dlong/2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = 6367 * c;

        return d;
    }

    
}