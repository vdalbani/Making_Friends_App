package com.example.victor_friends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.victor_friends.databinding.ActivityMainBinding;
import com.example.victor_friends.models.Friend;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //Define tag
    private final String TAG = this.getClass().getCanonicalName();

    //View binding
    ActivityMainBinding binding;

    //Declare object
    Friend newFriend;

    //Declare array of friends
    ArrayList<Friend> friendArrayList;

    //Button
    private Button btnAddFriend;

    //Variables
    private String friendName;
    private String friendEmail = "Not provided";
    private int friendNumber;
    private String friendAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        //Initiate
        this.friendArrayList = new ArrayList<>();
        this.binding.btnAddFriend.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v != null){
            Log.d(TAG, "onClick: VALID");
            //Validate
                if(validateFriend()){
                    createNewFriend();
                    friendConfirmation();
                }else{
                    Log.d(TAG, "onClick: VALIDATION WAS NOT VALID");
                }
        }else{
            Log.d(TAG, "onClick: VIEW NOT VALID");
        }

    }

//    Validation
    public boolean validateFriend(){
        boolean validUser = true;
        this.friendEmail = this.binding.editEmail.getText().toString();

        if(this.binding.editName.getText().toString().isEmpty()) {
            this.binding.editName.setError("Name cannot be empty");
            validUser = false;
        }else if(this.binding.editPhone.getText().toString().isEmpty()){
            this.binding.editPhone.setError("Cannot be <10 or >12 digits");
            validUser = false;
        }else if(this.binding.editPhone.getText().length() < 10||
                this.binding.editPhone.getText().length()>12){
            this.binding.editPhone.setError("Cannot be <10 or >12 digits");
            validUser = false;
        }else if(this.binding.editAddress.getText().toString().isEmpty()){
            this.binding.editAddress.setError("Address cannot be empty");
             validUser = false;
        }else if(this.binding.editEmail.getText().toString().isEmpty()){
            this.binding.editEmail.setText("Not provided");
            validUser = true;
        }
        Log.d(TAG, "validateFriend: LENGHT: " + this.binding.editPhone.getText().toString().length());

        return validUser;
    }

//    Create a new friend
    public void createNewFriend(){
        this.newFriend = new Friend();

        this.newFriend.setFriendName(this.binding.editName.getText().toString());
        this.newFriend.setFriendEmail(this.binding.editEmail.getText().toString());
        this.newFriend.setFriendNumber(Integer.parseInt(this.binding.editPhone.getText().toString()));
        this.newFriend.setFriendAddress(this.binding.editAddress.getText().toString());

        Log.d(TAG, "createNewFriend: NEW FRIEND CREATED" + newFriend.toString());
        this.friendArrayList.add(newFriend);
        Log.d(TAG, "createNewFriend: NEW FRIEND ADDED TO THE ARRAY LIST" + friendArrayList);
    }

//    Create Confirmation
    public void friendConfirmation(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("Congrats!! \nYou have made a new friend!");
        alertBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetFriendForm();
            }
        });

        alertBuilder.show();
    }

//    Reset Values
    public void resetFriendForm(){
        this.binding.editName.setText("");
        this.binding.editEmail.setText("");
        this.binding.editPhone.setText("");
        this.binding.editAddress.setText("");
    }

//    Show List of friends
    public void showFriendsList(){
        Intent friendListIntent = new Intent(this, FriendsListActivity.class);
        friendListIntent.putParcelableArrayListExtra("EXTRA_FRIENDS_LIST", friendArrayList);
        startActivity(friendListIntent);
    }
//    Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_show_list:{
                this.showFriendsList();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}