package ylu36.ncsu.edu.location_tracker;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.os.Bundle;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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

public class Main extends AppCompatActivity {
    double totalDistance;
    EditText hostField, usernameField;
    ToggleButton btn;
    TextView resultView, totalDistanceField;
    LocationManager locationManager;
    LocationListener locationListener;
    String provider;

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

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(criteria, false);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
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

        btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (isChecked) {
                    // The toggle is enabled
                    if(provider != null) {
                        if(ContextCompat.checkSelfPermission(Main.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            Log.i("locationManager", "called here");
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

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                double distance = Double.parseDouble(response.getString("totalDistance"));
                                double speed = Double.parseDouble(response.getString("speed"));
                                double freq;
                                if(speed <= 1) freq = 5;
                                else if(speed >= 20) freq = 1;
                                else freq = Math.abs(5 - speed / 5);
                                resultView.setText(String.format("%.2f seconds", freq));
                                totalDistanceField.setText(String.format("%.2f m", distance));
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
        }catch(Exception e){
            Log.e("Cannot create json",e.getMessage());
        }
    }
}