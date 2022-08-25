package com.example.victor_friends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.example.victor_friends.adapters.FriendAdapter;
import com.example.victor_friends.helpers.LocationHelper;
import com.example.victor_friends.models.Friend;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.victor_friends.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String TAG = this.getClass().getCanonicalName();
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Friend friend;
    private LocationCallback locationCallback;
    private LocationHelper locationHelper;
    private LatLng currentLocation;
    private LatLng friendLocation;
    private String friendName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        this.currentLocation = new LatLng(0,0);

        this.locationHelper = LocationHelper.getInstance();
        this.locationHelper.checkPermissions(this);


        Intent currentIntent = this.getIntent();

        //My last location
        this.currentLocation = new LatLng(currentIntent.getDoubleExtra("EXTRA_MY_LAST_LAT", 0),
                currentIntent.getDoubleExtra("EXTRA_MY_LAST_LNG", 0));

        if(currentIntent !=null){
            //friend location
            try {
                this.friendName = currentIntent.getStringExtra("EXTRA_FRIEND_NAME");
                this.friendLocation = doReverseGeocoding(currentIntent.getStringExtra("EXTRA_FRIEND_ADDRESS"));

            }catch(Exception e){
                Log.e(TAG, "onCreate: " + e.getLocalizedMessage() );
            }
        }else{
            this.currentLocation= new LatLng(0,0);
            Log.d(TAG, "onCreate: Extras Intent not received" );
        }

        if(this.locationHelper.locationPermissionGranted){
            this.initiateLocationListener();
        }else{
            Log.d(TAG, "onCreate: Location Permission Denied");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initiateLocationListener(){
        this.locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location loc : locationResult.getLocations()){
                    currentLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your location"));
                }
            }
        };

        this.locationHelper.requestLocationUpdates(this, locationCallback);
    }

    private LatLng doReverseGeocoding(String address){
        Log.d(TAG, "onClick: Perform reverse geocoding to get coordinates from address.");
        if (this.locationHelper.locationPermissionGranted){
            String givenAddress = address;
            LatLng obtainedCoordinates = this.locationHelper.performReverseGeocoding(this, givenAddress);

            if (obtainedCoordinates != null){
                return obtainedCoordinates;
            }else{
                AlertDialog.Builder alertBuilde = new AlertDialog.Builder(this);
                alertBuilde.setMessage("Opp!! \nWe cannot find your friend!");
                alertBuilde.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       //Nothing
                        Log.d(TAG, "onClick: Show location");
                    }
                });

                alertBuilde.show();
                Log.d(TAG, "doReverseGeocoding: Coordinates cannot be obtained for given address");
            }
        }else{
            Log.d(TAG, "Couldn't obtain the coordinates as not permission granted for location");
        }
        return null;
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
        UiSettings myUiSetting = googleMap.getUiSettings();
        myUiSetting.setZoomControlsEnabled(true);
        myUiSetting.setZoomGesturesEnabled(true);
        myUiSetting.setCompassEnabled(true);

        mMap.setBuildingsEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //My Location
        LatLng myLastLocation = currentLocation;
        mMap.addMarker(new MarkerOptions().position(myLastLocation).title("Your last location"));

        //Friend Location
        LatLng friendsLocation = this.friendLocation;
        mMap.addMarker(new MarkerOptions().position(friendsLocation).title(friendName +"'s Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(friendsLocation, 15.0f));
    }
}
