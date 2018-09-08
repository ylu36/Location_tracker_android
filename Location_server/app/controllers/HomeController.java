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

    @BodyParser.Of(BodyParser.Json.class)
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
               
        Location location = new Location(username, timestamp, latitude, longitude, 0, 0);
        // calculate total distance for a user
        double[] res = new double[2];
        res = databaseController.getDistanceAndSpeed(location);
        double distance = res[0];
        double speed = res[1];
        // calculate speed 
        databaseController.insert(username, timestamp, latitude, longitude, distance, speed);
        databaseController.selectAll();
        JsonNode locationJson = Json.toJson(location);
        return ok(locationJson);
    }
}