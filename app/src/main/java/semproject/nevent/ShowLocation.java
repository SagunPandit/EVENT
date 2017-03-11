package semproject.nevent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowLocation extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private boolean mRequestingLocationUpdates = true;
    private LocationRequest mLocationRequest;
    Location mLastLocation,mCurrentLocation;
    String eventname;
    Double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        eventname=intent.getStringExtra("eventname");
        latitude= Double.parseDouble(intent.getStringExtra("latitude"));
        longitude=Double.parseDouble(intent.getStringExtra("longitude"));
        Log.e("In show location",eventname);
        Log.d("Latitude","Value"+latitude);
        Log.d("Longitude","Value"+longitude);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            return;
        } else {
            //Create the LocationRequest object

            mLocationRequest = LocationRequest.create()

                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

                    .setInterval( 1000)

                    //10 Seconds in millisecond

                    .setFastestInterval(10 * 1000);
            client =new GoogleApiClient.Builder(this)

                    .addConnectionCallbacks(this)

                    .addOnConnectionFailedListener(this)

                    .addApi(LocationServices.API)

                    .build();
            client.connect();
            locateMyLocation();


        }

        //lets imagine a function which shows all events


        //infowindow click listener (this is for showing decriptions os insted of toast put thedescription from online db
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //TODO:add your desiredcode here and removve thetoast.
                Toast.makeText(ShowLocation.this, eventname, Toast.LENGTH_SHORT).show();
            }
        });
        //end of infowindow clicklistener
    }

    //TO BE ADDED
    //FUNCTION TO SHOW ALL EVENT

    public void ShowEvent(double latitiude, double longitude, String EventName) {

        LatLng Mark = new LatLng(latitiude, longitude);
        mMap.addMarker(new MarkerOptions().position(Mark).title(EventName));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Mark, (float) 14.0));

    }



    //CODE FOR SHOWING CURRENT LOCATION
    private void locateMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }//if this line is not put error is shownS

        mMap.setMyLocationEnabled(true);


//CODE FOR RESTRICATION WITHIN NEPAL IN CERTAIN ZOOM LEVEL
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                /*Location mylocation = mMap.getMyLocation();
                LatLng myLatLng = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());
                CameraPosition myPosition = new CameraPosition.Builder()
                        .target(myLatLng).zoom(17).bearing(mylocation.getBearing()).build();
                mMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(myPosition));*/

                //this conditon is removed in the onLocaitonChanged Method
                if (cameraPosition.zoom < 7.0f) {


                    mMap.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(new LatLng(27.7172, 85.3240), 7.0f));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(9.0f), 2000, null);

                }


            }


        });
        ShowEvent(latitude,longitude,eventname);
    }

    //??
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locateMyLocation();
    }


//????????????????

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;



    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "Permission", Toast.LENGTH_SHORT).show();
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                client);
        mCurrentLocation=mLastLocation;
        if (mLastLocation != null) {
            Log.d("Location",mLastLocation.toString());

        } else {
        }

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Failed OnConnectionFailed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (client.isConnected()) {

            LocationServices.FusedLocationApi.

                    removeLocationUpdates(client, this);

            client.disconnect();


        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                client, mLocationRequest, this);
        Toast.makeText(this, eventname, Toast.LENGTH_SHORT).show();
    }
}

