package com.brandonlee.instagram.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.brandonlee.instagram.Database.User;
import com.brandonlee.instagram.Database.UserAccountSettings;
import com.brandonlee.instagram.Database.UserSettings;
import com.brandonlee.instagram.ProfileSettings;
import com.brandonlee.instagram.R;
import com.brandonlee.instagram.Utils.FirebaseMethods;
import com.brandonlee.instagram.Utils.GridViewImageHelper;
import com.brandonlee.instagram.Utils.UniversalImageLoader;
import com.brandonlee.instagram.del_photo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by BrandonLee on 2/6/18.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ProfileFragment";

    FirebaseUser user;

    ArrayList<String> imgUrls = new ArrayList<>();

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    private TextView mFullname;
    private TextView mDescription;
    private TextView mPosts;
    private TextView mFollowers;
    private TextView mFollowing;
    private CircleImageView mProfilePhoto;
    private GridView mGridView;

    //String[] imgUrls;
    //ArrayList<String> imgUrls;

    private int numFollowers = 0;
    private int numFollowing = 0;
    private int numPosts = 0;

    Activity context;

    private static final int NUM_GRID_COLUMNS = 3;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getActivity();

        mFirebaseMethods = new FirebaseMethods(getActivity());


        mFullname = (TextView)view.findViewById(R.id.textView5);
        mDescription = (TextView)view.findViewById(R.id.textView6);
        mPosts = (TextView)view.findViewById(R.id.profile_post);
        mFollowers = (TextView)view.findViewById(R.id.profile_followers);
        mFollowing = (TextView)view.findViewById(R.id.profile_following);
        mProfilePhoto = (CircleImageView)view.findViewById(R.id.profile_image);
        mGridView = (GridView)view.findViewById(R.id.gridView);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), del_photo.class);
                i.putExtra("imgURL", imgUrls.get(position));
                startActivity(i);
            }
        });

        view.findViewById(R.id.btnEditProfile).setOnClickListener(this);

        setupFirebaseAuth();

        initImageLoader();

        getFollowersCount();
        getFollowingCount();
        getPostsCount();

        //setupGridView();
        //gridImage();
        //mGridView.setAdapter(new ImageAdpaterGridView(context));


        return view;
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
                .equalTo(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    // display message that user was not found
                    //Toast.makeText(getActivity(), "nothing found", Toast.LENGTH_SHORT).show();
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

        setupImageGridView(imgUrls);
    }

    private void setupImageGridView(ArrayList<String> imgUrls) {
        GridViewImageHelper helper =  new GridViewImageHelper(context,R.layout.layout_grid_imageview,"",imgUrls);
        mGridView.setAdapter(helper);
    }


    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void setProfileWidgets(UserSettings userSettings) {
        Log.d(TAG, "setProfileWidgets: setting widgets with data from firebase");

        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");
        //Toast.makeText(getActivity(), "userid: " + user.getUser_id(), Toast.LENGTH_SHORT).show();
        // Got to do the profile photo
        mFullname.setText(settings.getDisplay_name());
        mDescription.setText(settings.getDescription());

    }



    private void getFollowersCount() {
        numFollowers = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("dbname_followers")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                //Toast.makeText(getActivity(),"userid : " + user.getUid(), Toast.LENGTH_SHORT).show();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    gridImage();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnEditProfile) {
            Intent intent = new Intent(context, ProfileSettings.class);
            Log.d(TAG, "onClick: Test the intent");
            startActivity(intent);
        }
    }
}
