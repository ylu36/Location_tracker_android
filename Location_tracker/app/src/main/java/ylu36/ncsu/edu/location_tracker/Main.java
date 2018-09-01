package ylu36.ncsu.edu.location_tracker;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.os.Bundle;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.location.Criteria;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.LinkedList;

public class Main extends AppCompatActivity {

    // ref: https://stackoverflow.com/questions/5498865/size-limited-queue-that-holds-last-n-elements-in-java
    private class LimitedQueue<E> extends LinkedList<E> {
        private int limit;

        public LimitedQueue(int limit) {
            this.limit = limit;
        }

        @Override
        public boolean add(E o) {
            super.add(o);
            while (size() > limit) { super.remove(); }
            return true;
        }
    }

    LimitedQueue<Double> distances = new LimitedQueue<>(5);
    double totalDistance;
    EditText hostField, usernameField;
    ToggleButton btn;
    TextView resultView, totalDistanceField;
    LocationManager locationManager;
    LocationListener locationListener;
    String provider;
    Criteria criteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        totalDistance = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hostField = findViewById(R.id.editText);
        usernameField = findViewById(R.id.editText2);
        btn = findViewById(R.id.button);
        resultView = findViewById(R.id.textView);
        resultView.setText("");
        totalDistanceField = findViewById(R.id.textView5);
        totalDistanceField.setText("");
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                if(distances.size() == 5) {
                    // calc average speed
                    double speed = 0.;
                    for(double d: distances)
                        speed += d;
                    speed /= 5;
                    Log.i("speed: ", String.valueOf(speed));
                    if(speed <= 1.0)
                        resultView.setText("5 seconds");
                    else if(speed > 20)
                        resultView.setText("1 second");
                    else {
                        double temp = 5 - speed / 5;
                        resultView.setText(String.format("%.2f seconds", temp));
                    }
                }
                else {
                    Log.i("speed: ", "default speed");
                    resultView.setText("5 seconds");
                }
                sendRequest(location);
            }

            @Override
            public void onStatusChanged(final String provider, final int status, final Bundle extras) {
            }

            @Override
            public void onProviderEnabled(final String provider) {
            }

            @Override
            public void onProviderDisabled(final String provider) {
            }
        };
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        provider = locationManager.getBestProvider(criteria, false);
        btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    if(provider != null) {
                        if(ContextCompat.checkSelfPermission(Main.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            locationManager.requestLocationUpdates(provider, 1000, 10, locationListener);
                        }
                    }
                    usernameField.setEnabled(false);
                    hostField.setEnabled(false);
                } else {
                    // The toggle is disabled
                    locationManager.removeUpdates(locationListener);
                    usernameField.setEnabled(true);
                    hostField.setEnabled(true);
                }
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(provider != null) {
                    if(ContextCompat.checkSelfPermission(Main.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(provider, 1000, 10, locationListener);
                    }
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Un-registering Location Listener.
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void sendRequest(Location location) {
        String host = String.valueOf(hostField.getText());
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + host + "/locationupdate";
        JSONObject json = new JSONObject();
        try {
            json.put("username", usernameField.getText());
            json.put("latitude", location.getLatitude());
            json.put("longitude", location.getLongitude());
            json.put("timestamp", System.currentTimeMillis());
        }catch(Exception e){
            Log.e("Cannot create json",e.getMessage());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String str = response.getString("distance");
                            double distance = Double.valueOf(str);
                            distances.add(distance);
                            totalDistance += distance;
                            Log.i("successful request", "Response: " + totalDistance + " with size " + distances.size());
                            totalDistanceField.setText(String.format("%.2f km", totalDistance));
                        } catch (JSONException e) {Log.e("Unsuccessful request", e.getMessage());}
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Unsuccessful request",error.getMessage());
                        resultView.setText("Could not connect to server!");
                    }
                });

        // Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);
    }
}
