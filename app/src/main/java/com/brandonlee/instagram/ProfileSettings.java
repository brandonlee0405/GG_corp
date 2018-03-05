package com.brandonlee.instagram;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brandonlee.instagram.Database.LoginActivity;
import com.brandonlee.instagram.Database.User;
import com.brandonlee.instagram.Database.UserAccountSettings;
import com.brandonlee.instagram.Database.UserSettings;
import com.brandonlee.instagram.Utils.FirebaseMethods;
import com.brandonlee.instagram.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSettings extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "ProfileSettings";

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    private String userID;

    //EditProfile Widgets
    private EditText mDisplayName, mUsername, mDescription, mEmail;
    private CircleImageView mProfilePhoto;


    private UserSettings mUserSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        mDisplayName = (EditText)findViewById(R.id.etFullName);
        mUsername = (EditText)findViewById(R.id.etUsername);
        mDescription = (EditText)findViewById(R.id.userBio);
        mEmail = (EditText)findViewById(R.id.etEmail);
        mProfilePhoto = (CircleImageView) findViewById(R.id.profile_photo);

        mFirebaseMethods = new FirebaseMethods(ProfileSettings.this);

        initImageLoader();
        //setProfileImage();

        setupFirebaseAuth();

        findViewById(R.id.btnSignOut).setOnClickListener(this);
        findViewById(R.id.btnSubmit).setOnClickListener(this);


    }

    private void setProfileWidgets(UserSettings userSettings) {
        Log.d(TAG, "setProfileWidgets: setting widgets with data from firebase " + userSettings.getUser().getEmail());

        mUserSettings = userSettings;
        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");

        // Got to do the profile photo
        mDescription.setText(settings.getDescription());
        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mEmail.setText(userSettings.getUser().getEmail());

    }


    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(ProfileSettings.this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }


    /**
     * Retrieves the data contained in the widgets and write to the database
     * Before writing, check the username is unique
     */
    private void saveProfileSettings() {
        final String displayName = mDisplayName.getText().toString();
        final String username = mUsername.getText().toString();
        final String description = mDescription.getText().toString();
        final String email = mEmail.getText().toString();


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // User did change their username
                if(!mUserSettings.getUser().getUsername().equals(username)) {
                    checkIfUsernameExists(username);
                }

                // Update your fullname
                if(!mUserSettings.getSettings().getDisplay_name().equals(displayName)) {
                    mFirebaseMethods.updateUserAccountSettings(displayName, null);
                }

                // Update your description
                if(!mUserSettings.getSettings().getDescription().equals(description)) {
                    mFirebaseMethods.updateUserAccountSettings(null, description);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Checking if username exists in our databse
     * @param username
     */
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: Check if " + username + " already exists.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()) {
                    // Add the username
                    mFirebaseMethods.updateUsername(username);
                    Toast.makeText(ProfileSettings.this, "Saved Username", Toast.LENGTH_SHORT).show();
                }
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    if (singleSnapshot.exists()) {
                        Log.d(TAG, "checkIfUsernameExists: Found a match: " + singleSnapshot.getValue(User.class).getUsername());
                        Toast toast = Toast.makeText(ProfileSettings.this, "That username already exists.", Toast.LENGTH_SHORT);
                        TextView v = (TextView)toast.getView().findViewById(android.R.id.message);
                        v.setTextColor(Color.BLACK);
                        toast.show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnSignOut:
                Log.d(TAG, "onClick: Sign out the user");
                mAuth.signOut();
                ProfileSettings.this.finish();
                break;
                
            case R.id.btnSubmit:
                Log.d(TAG, "onClick: Submit updated info");
                saveProfileSettings();
                break;
        }
    }


    /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");


                    Intent intent = new Intent(ProfileSettings.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                // ...
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "onDataChange: Retrieve user info from database");
                // retrieve user info from the database
                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));
                // retrieve images from the user

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
