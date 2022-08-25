package com.example.victor_friends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.example.victor_friends.adapters.FriendAdapter;
import com.example.victor_friends.databinding.ActivityFriendsListBinding;
import com.example.victor_friends.helpers.LocationHelper;
import com.example.victor_friends.models.Friend;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import java.util.ArrayList;

public class FriendsListActivity extends AppCompatActivity implements OnFriendItemClickListener{

    private final String TAG = this.getClass().getCanonicalName();

    //receive array from MainActivity
    private ArrayList<Friend> friendsArrayList;
    //binding
    private ActivityFriendsListBinding binding;
    //adapter
    private FriendAdapter friendAdapter;
    //reference the Location Helper
    private LocationHelper locationHelper;
    //my last location
    private Location myLastLocation;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityFriendsListBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        //get parcelable from MainActivity
        this.friendsArrayList = new ArrayList<>();
        ArrayList<Friend> extraFriendsList = this.getIntent().getParcelableArrayListExtra("EXTRA_FRIENDS_LIST");

        if(extraFriendsList !=null){
            this.friendsArrayList = extraFriendsList;
        }else{
            Log.d(TAG, "onCreate: EXTRA LIST NOT RECEIVED");
        }

        //set adapters
        this.friendAdapter = new FriendAdapter(this, this.friendsArrayList, this);
        this.binding.rvFriendsList.setLayoutManager(new LinearLayoutManager(this));
        this.binding.rvFriendsList.setAdapter(friendAdapter);
//        this.binding.rvFriendsList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        //Current Location
        this.locationHelper = LocationHelper.getInstance();
        this.locationHelper.checkPermissions(this);

        //Validate
        if(this.locationHelper.locationPermissionGranted){
            Log.d(TAG, "onCreate: LOCATION PERMISSION GRANTED");

            this.locationHelper.getLastLocation(this).observe(this, new Observer<Location>() {
                @Override
                public void onChanged(Location location) {
                    if(location !=null){
                        myLastLocation = location;
                    }else{
                        Log.d(TAG, "onChanged: LOCATION FRIEND LIST NOT RECEIVED");
                    }
                }
            });

            //Initiate Changes listener
            this.initiateLocationListener();
        }else{
            Log.d(TAG, "onCreate: Location Permission Denied");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Validate
        if(requestCode == this.locationHelper.REQUEST_CODE_LOCATION){
            this.locationHelper.locationPermissionGranted = (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);

            if(this.locationHelper.locationPermissionGranted){
                Log.d(TAG, "onRequestPermissionsResult: RESULT: --LOCATION PERMISSION GRANTED");
            }else{
                Log.d(TAG, "onRequestPermissionsResult: RESULT: --LOCATION PERMISSION DENIED");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.locationHelper.stopLocationUpdates(this, this.locationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.locationHelper.requestLocationUpdates(this, this.locationCallback);
    }

    private void initiateLocationListener(){
        this.locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {

                for(Location loc : locationResult.getLocations()){
                    myLastLocation = loc;
                }
            }
        };

        this.locationHelper.requestLocationUpdates(this, locationCallback);
    }

    @Override
    public void onFriendItemClicked(Friend friend) {
        Log.d(TAG, "onFriendItemClicked:TRYING TO PASS THE FRIEND LOCATION INTENT" + friend.toString());

        Intent mapsIntent = new Intent(this, MapsActivity.class);
        //Set My Last Location
        mapsIntent.putExtra("EXTRA_MY_LAST_LAT", this.myLastLocation.getLatitude());
        mapsIntent.putExtra("EXTRA_MY_LAST_LNG", this.myLastLocation.getLongitude());

        //Set Friends Location
        mapsIntent.putExtra("EXTRA_FRIEND_ADDRESS", friend.getFriendAddress());
        mapsIntent.putExtra("EXTRA_FRIEND_NAME", friend.getFriendName());

        //Start Activity
        startActivity(mapsIntent);
    }
}