package semproject.nevent;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import static semproject.nevent.R.id.map;

//HOW DID IMPLEMENT COME??
public class NMapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private Marker currentMarker=null;
    Serializable locationmarker;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating_action);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        if (client == null) {
            client = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        //for floating action botton
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentMarker!=null){

                    LatLng ltln=currentMarker.getPosition();
                    Double latitude=ltln.latitude;
                    Double longitude=ltln.longitude;

                    Intent i = new Intent(NMapsActivity.this, Upload.class);
                    i.putExtra("longitude",longitude);
                    i.putExtra("latitude",latitude);
                    finish();
                    startActivity(i);

                }else{
                    Toast.makeText(NMapsActivity.this, "Select Destination", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Sydney and move the camera
        //LatLng ku = new LatLng(27.6186480, 85.5375810);
        //mMap.addMarker(new MarkerOptions().position(ku).title("Marker in Kathmandu University"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ku, (float) 7.0));
//CODE FOR ASKING PERMISSON FOR ANDROIND APP
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            return;
        } else {
            locateMyLocation();
        }
        Button clear = (Button) findViewById(R.id.Bclear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
            }
        });
        //infowindow click listener (this is for showing decriptions os insted of toast put thedescription from online db
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //TODO:add your desiredcode here and removve thetoast.
                Toast.makeText(NMapsActivity.this, currentMarker.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        //end of infowindow clicklistener
    }

    //CODE FOR SHOWING CURRENT LOCATION AND ADDING MARKER ON LONG PRESS
    private void locateMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }//if this line is not put error is shownS

        mMap.setMyLocationEnabled(true);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear(); //clear maps everytime a new marker is created.
                MarkerOptions options = new MarkerOptions().position(latLng).title("");
                currentMarker = mMap.addMarker(options);
              // LatLng add=latLng;
                //pass_value(add);

            }
        });


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
    }

    //??
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locateMyLocation();
    }


//????????????????

    @Override
    public void onLocationChanged(Location location) {
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 7.0f));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(9.0f), 2000, null);
    }


    //FOR THE SEARCH BUTTON
    public void onSearch(View view) {
        EditText tlocation = (EditText) findViewById(R.id.Tlocation);
        String location = tlocation.getText().toString();

        if (location != null || !location.equals("")) {
            List<android.location.Address> addressList = null;// intitializing value for list

// GEOCODER CHANGES THE LOCATION NAME TO LATLANG VALUES
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            android.location.Address address = addressList.get(0);//ie first of index
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("SEARCHED LOCATION"));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 14.0));

/*
         EXAMPLE CODE TO ADD COLOR IN MARKER

        Marker melbourne = mMap.addMarker(new MarkerOptions().position(MELBOURNE)
            .icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        MAIN codefor color change
                     .icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                   */
        }

    }

    //TO CLEAR MARKER
    public void onClear() {

    }
//TO CALCULATE DISTANCE BETWEEN 2 LATITUDES AND LONGITUDE
   /* float distanceBetween (double startLatitude,
                          double startLongitude,
                          double endLatitude,
                          double endLongitude,
                          float[] results)
    {
        float a =results[0];
        return(a);


    }

    if distance greater than 5 km then filter those

//to calculate the distance between this location and the given location(NOTE LOCATION NOT LATLANG)

     float distanceTo (Location dest)
     {}

Returns the approximate distance in meters between this location and the given location. Distance is defined using the WGS84 ellipsoid.
//link https://developer.android.com/reference/android/location/Location.html#distanceBetween(double,%20double,%20double,%20double,%20float[])
   */


//to pass the latlang of marker this will be used to pass the location of the event
    /**public LatLng pass_value(LatLng latLng)
    {
        LatLng address=latLng;

        return latLng;
    }**/



    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    // Create an instance of GoogleAPIClient.
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("NMaps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
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
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                client);
        if (mLastLocation != null) {
            Toast.makeText(this,mLastLocation.toString(), Toast.LENGTH_SHORT).show();

            
       } else{
            Toast.makeText(this, "NULLL", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
