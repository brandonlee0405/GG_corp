package com.brandonlee.instagram;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.brandonlee.instagram.Database.User;
import com.brandonlee.instagram.Fragments.HomeFragment;
import com.brandonlee.instagram.Utils.UniversalImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    private String user_id;

    private TextView mFullname;
    private TextView mDescription;
    private TextView mPosts;
    private TextView mFollowers;
    private TextView mFollowing;
    private CircleImageView mProfilePhoto;
    private GridView mGridView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: Did it come here?");

        mFullname = findViewById(R.id.textView5);
        mDescription = findViewById(R.id.textView6);
        mPosts = findViewById(R.id.profile_post);
        mFollowers = findViewById(R.id.profile_followers);
        mFollowing = findViewById(R.id.profile_following);
        mProfilePhoto = findViewById(R.id.profile_image);
        mGridView = findViewById(R.id.gridView);

        initImageLoader();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                user_id = null;
            } else {
                user_id = extras.getString("USER_ID");
            }
        } else {
            user_id = (String) savedInstanceState.getSerializable("USER_ID");
        }

        if (user_id == null) {
            Toast.makeText(this, "no username found.", Toast.LENGTH_SHORT).show();
        }
        else {
            fillUserInfo();
        }
    }

    private void fillUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference
                .child(getString(R.string.dbname_user_account_settings))
                .orderByKey()
                .equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    // display message that user was not found
                    Toast.makeText(ProfileActivity.this, "nothing found", Toast.LENGTH_SHORT).show();
                }

                // get userId's of following
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    if (singleSnapshot.exists()) {
                        mFullname.setText(singleSnapshot.child("display_name").getValue().toString());
                        mDescription.setText(singleSnapshot.child("description").getValue().toString());
                        mPosts.setText(singleSnapshot.child("posts").getValue().toString());
                        mFollowing.setText(singleSnapshot.child("following").getValue().toString());
                        mFollowers.setText(singleSnapshot.child("followers").getValue().toString());

                        UniversalImageLoader.setImage(singleSnapshot.child("profile_photo").getValue().toString(), mProfilePhoto, null, "");

                        //fillGrid();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fillGrid() {

    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(ProfileActivity.this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }
}
