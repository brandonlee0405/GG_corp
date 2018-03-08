package com.brandonlee.instagram;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.brandonlee.instagram.Utils.GridViewImageHelper;
import com.brandonlee.instagram.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

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

    private Button mFollow, mUnfollow;

    private int numFollowers = 0;
    private int numFollowing = 0;
    private int numPosts = 0;

    ArrayList<String> imgUrls = new ArrayList<>();

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
        mFollow = findViewById(R.id.btnFollow);
        mUnfollow = findViewById(R.id.btnUnfollow);

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
        isFollowing();
        getFollowingCount();
        getFollowersCount();
        getPostsCount();

        mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Now Following");

                FirebaseDatabase.getInstance().getReference()
                        .child("dbname_following")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(user_id)
                        .child(getString(R.string.field_user_id))
                        .setValue(user_id);

                FirebaseDatabase.getInstance().getReference()
                        .child("dbname_followers")
                        .child(user_id)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.field_user_id))
                        .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                setFollowing();
            }
        });

        mUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Now Unfollowing");

                FirebaseDatabase.getInstance().getReference()
                        .child("dbname_following")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(user_id)
                        .removeValue();

                FirebaseDatabase.getInstance().getReference()
                        .child("dbname_followers")
                        .child(user_id)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .removeValue();

                setUnfollowing();
            }
        });

    }

    private void isFollowing() {
        Log.d(TAG, "isFollowing: Checking if following this user");

        setUnfollowing();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("dbname_following")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild(getString(R.string.field_user_id)).equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue());

                    setFollowing();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getFollowersCount() {
        numFollowers = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("dbname_followers")
                .child(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found following user:" + singleSnapshot.getValue());
                    numFollowers++;
                }
                mFollowers.setText(String.valueOf(numFollowers));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFollowingCount() {
        numFollowing = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("dbname_following")
                .child(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found following user:" + singleSnapshot.getValue());
                    numFollowing++;
                }
                mFollowing.setText(String.valueOf(numFollowing));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPostsCount() {
        numPosts = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_user_photos))
                .child(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found post:" + singleSnapshot.getValue());
                    numPosts++;
                }
                mPosts.setText(String.valueOf(numPosts));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setFollowing() {
        Log.d(TAG, "setFollowing: updating the following");

        mFollow.setVisibility(View.GONE);
        mUnfollow.setVisibility(View.VISIBLE);


    }

    private void setUnfollowing() {
        Log.d(TAG, "setFollowing: updating the unfollowing");

        mFollow.setVisibility(View.VISIBLE);
        mUnfollow.setVisibility(View.GONE);


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

                        gridImage();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void gridImage() {
        imgUrls = new ArrayList<>();

        //test images
        /*imgUrls.add("https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_192653_?alt=media&token=9aaaa744-3f29-481c-a162-10388c644686");
        imgUrls.add("https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_211314_?alt=media&token=9e7a376c-48c6-4402-956e-4bd32c1d6c3a");
        imgUrls.add("https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_223323_?alt=media&token=4b1f3887-368b-4838-8c36-1226f8973993");
        imgUrls.add("https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_223323_?alt=media&token=4b1f3887-368b-4838-8c36-1226f8973993");
        imgUrls.add("https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_223323_?alt=media&token=4b1f3887-368b-4838-8c36-1226f8973993");
        imgUrls.add("https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_192653_?alt=media&token=9aaaa744-3f29-481c-a162-10388c644686");
        imgUrls.add("https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_192653_?alt=media&token=9aaaa744-3f29-481c-a162-10388c644686");*/

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference
                .child(getString(R.string.dbname_user_photos))
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
                        for (DataSnapshot ss : singleSnapshot.getChildren()) {
                            if (ss.exists()) {
                                imgUrls.add(ss.child("image_path").getValue().toString());
                            }
                        }

                        setupImageGridView(imgUrls);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setupImageGridView(ArrayList<String> imgUrls) {
        GridViewImageHelper helper =  new GridViewImageHelper(this,R.layout.layout_grid_imageview,"",imgUrls);
        mGridView.setAdapter(helper);
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(ProfileActivity.this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }
}
