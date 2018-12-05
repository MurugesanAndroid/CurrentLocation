package com.example.a3386.googlemap;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(MainActivity.this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                if (isgpsonoroff()) {
                    firstLocation();
                } else {
                    enableGPS(MainActivity.this);
                }
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            if (isgpsonoroff()) {

                firstLocation();
            } else {
                enableGPS(MainActivity.this);
            }
        }
    }


    public void firstLocation() {
        LocationProvider.requestSingleUpdate(MainActivity.this, new LocationProvider.LocationCallback() {
            @Override
            public void onNewLocationAvailable(LocationProvider.GPSCoordinates location) {
                Log.d("Location", "my location is " + location.latitude + " " + location.longitude);
                getLocation(location.longitude, location.latitude);
            }
        });
    }

    private void getLocation(float lon, float lat) {

        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String cityName = addresses.get(0).getLocality();
        String PostalCode = addresses.get(0).getPostalCode();
        String stateName = addresses.get(0).getAdminArea();
        String countryCode = addresses.get(0).getCountryCode();

        Log.d("cityName", "cityName" + cityName);
        Log.d("PostalCode", "PostalCode" + PostalCode);
        Log.d("stateName", "stateName" + stateName);
        Log.d("countryCode", "countryCode" + countryCode);


    }

    public static void enableGPS(final Context activity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Enable the GPS";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        });
        builder.create().show();
    }

    boolean isgpsonoroff() {
        LocationManager manager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return statusOfGPS;
    }
}
