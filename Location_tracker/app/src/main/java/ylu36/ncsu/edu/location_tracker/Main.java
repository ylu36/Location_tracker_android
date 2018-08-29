package ylu36.ncsu.edu.location_tracker;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.*;
import android.os.Bundle;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.location.Criteria;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main extends AppCompatActivity {
    static final int MY_PERMISSIONS_REQUEST_CONST = 1;
    EditText hostField, usernameField;
    Button btn;
    LocationManager locationManager;
    LocationListener locationListener;
    String provider;
    Criteria criteria;
    String[] permissions = {Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    public void getLocation(final Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String username = usernameField.getText().toString();
                Toast msg = Toast.makeText(getBaseContext(),username,Toast.LENGTH_LONG);
                msg.show();
//                updateLocation(location, username);
            }
        });
    }

    public static boolean hasPermissions(Activity activity, String[] permissions) {
       boolean flag = true;
       for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    flag = false;
                    break;
                }
       }
       return flag;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hostField = findViewById(R.id.editText);
        usernameField = findViewById(R.id.editText2);
        btn = findViewById(R.id.button);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
               getLocation(location);
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
        if(provider != null) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(provider, 1000, 10, locationListener);
                //btn.setText(R.string.pause);
                Toast.makeText(this, "Best Provider is " + provider, Toast.LENGTH_LONG).show();
            }
        }
        btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String str = hostField.getText().toString();
                Toast msg = Toast.makeText(getBaseContext(),str,Toast.LENGTH_LONG);
                msg.show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST_CONST);
        }
    }
}
