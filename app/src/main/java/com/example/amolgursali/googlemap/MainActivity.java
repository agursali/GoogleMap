package com.example.amolgursali.googlemap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements LocationListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private static final int INTERVAL=1000;
    private static final int FASTEST_INTERVAL=1000;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=0;
    GoogleMap googleMap;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    SupportMapFragment supportMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        buildGoogleApiClient();
        createLocationRequest();
        setContentView(R.layout.activity_main);
        supportMapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap=supportMapFragment.getMap();
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void createLocationRequest()
    {
        googleApiClient.connect();
        locationRequest=new LocationRequest().setInterval(INTERVAL).setFastestInterval(FASTEST_INTERVAL).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected synchronized void  buildGoogleApiClient()
    {
        googleApiClient=new GoogleApiClient.Builder(this).addOnConnectionFailedListener(this).addConnectionCallbacks(this).addApi(LocationServices.API).build();
    }

    private boolean isGooglePlayServicesAvailable()
    {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the task you need to do.

                } else {

                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        MarkerOptions markerOptions=new MarkerOptions();
        LatLng latlng=new LatLng(location.getLatitude(),location.getLongitude());
        markerOptions.position(latlng);
        Marker marker=googleMap.addMarker(markerOptions);
        marker.setTitle("Current Location");
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,
                18));

    }
    private void startLocationUpdates()
    {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
    }
    private void stopLocationUpdates()
    {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
    }
}
