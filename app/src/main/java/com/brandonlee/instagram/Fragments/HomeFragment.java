package com.brandonlee.instagram.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.brandonlee.instagram.Database.User;
import com.brandonlee.instagram.ProfileActivity;
import com.brandonlee.instagram.R;
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
import com.google.firebase.storage.StorageReference;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by BrandonLee on 2/6/18.
 */

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    class Pic {
        private String owner;
        private String timestamp;
        private String downloadUrl;

        Pic() {
            owner = null;
            timestamp = null;
            downloadUrl = null;
        }

        public void setOwner(String o) {
            owner = o;
        }

        public void setTimestamp(String t) {
            timestamp = t;
        }

        public void setDownloadUrl(String d) {
            downloadUrl = d;
        }

        public String getOwner() {
            return owner;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }
    }

    private HashMap<String, Pair<String, String>> followingInfo = new HashMap<String, Pair<String, String>>();
    private ArrayList<String> following = new ArrayList<>();
    private ArrayList<Pic> pics = new ArrayList<>();

    // testing data
    int[] TEST_PROFILE_PICS = {R.drawable.sample_profile_pic, R.drawable.sample_profile_pic_2, R.drawable.sample_profile_pic_3};
    int[] TEST_PHOTOS = {R.drawable.sample_pic, R.drawable.sample_pic_2, R.drawable.sample_pic_3};
    String[] TEST_NAMES = {"Tiff94", "Arthurrr", "Crazy_Sydney"};

    // testing data
    ArrayList<String> PROFILE_PICS = new ArrayList<>();
    ArrayList<String> PHOTOS = new ArrayList<>();
    ArrayList<String> NAMES = new ArrayList<>();

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        listView = (ListView)view.findViewById(R.id.listView);

        //CustomAdapter customAdapter = new CustomAdapter();
        //listView.setAdapter(customAdapter);

        if(isAdded()){
            mFirebaseMethods = new FirebaseMethods(getActivity());
            initImageLoader();
            setupFirebaseAuth();
        }

        return view;
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return PROFILE_PICS.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.customlayout,null);
            ImageView profile_pic = (ImageView)view.findViewById(R.id.imageView_profilepic);
            final TextView name = (TextView)view.findViewById(R.id.textView_name);
            ImageView photo = (ImageView)view.findViewById(R.id.imageView_photo);

            //profile_pic.setImageResource(TEST_PROFILE_PICS[i]);
            //photo.setImageResource(TEST_PHOTOS[i]);
            //name.setText(TEST_NAMES[i]);

            if (PROFILE_PICS.size() == 0 || PHOTOS.size() == 0 || NAMES.size() == 0) {
                Toast.makeText(getActivity(), "No pictures to display.", Toast.LENGTH_SHORT).show();
            } else {
                UniversalImageLoader.setImage(PROFILE_PICS.get(i), profile_pic, null, "");
                //profile_pic.setRotation((float) 90.0);
                UniversalImageLoader.setImage(PHOTOS.get(i), photo, null, "");
                //photo.setRotation((float) 90.0);
                name.setText(NAMES.get(i));


                name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        goToProfile(name.getText().toString());

                    }
                });
            }

            return view;
        }
    }

    private void getFollowing(FirebaseUser user) {

        if(isAdded()) {
            Log.d(TAG, "setProfileWidgets: setting widgets with data from firebase");

            //Toast.makeText(getActivity(), "userid: " + user.getUid(), Toast.LENGTH_SHORT).show();

            // query database
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            Query query = reference
                    .child(getString(R.string.dbname_following))
                    .orderByKey()
                    .equalTo(user.getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.exists()) {
                        // display message that user was not found
                        Toast.makeText(getActivity(), "following not found.", Toast.LENGTH_SHORT).show();
                    }

                    // get userId's of following
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        if (singleSnapshot.exists()) {
                            // go to user's profile
                            for (DataSnapshot ss : singleSnapshot.getChildren()) {
                                if (ss.exists()) {
                                    following.add(ss.child("user_id").getValue().toString());
                                    Toast.makeText(getActivity(), ss.child("user_id").getValue().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            getFollowingInfo(0);
                        }

                    }


                    // retrieve pics from following


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

    private void getFollowingInfo(final int index) {

        if (isAdded()) {
            // get following usernames and profile pics
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            Query query = reference
                    .child(getString(R.string.dbname_user_account_settings))
                    .orderByKey()
                    .equalTo(following.get(index));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.exists()) {
                        // display message that user was not found
                        Toast.makeText(getActivity(), "no pics found.", Toast.LENGTH_SHORT).show();
                    }
                    ;
                    // get userId's of following
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        if (singleSnapshot.exists()) {
                            //Toast.makeText(getActivity(), singleSnapshot.child("username").getValue().toString(), Toast.LENGTH_SHORT).show();
                            //Toast.makeText(getActivity(), singleSnapshot.child("profile_photo").getValue().toString(), Toast.LENGTH_SHORT).show();

                            String username = singleSnapshot.child("username").getValue().toString();
                            String profile_pic = singleSnapshot.child("profile_photo").getValue().toString();
                            Pair<String, String> pair = new Pair<>(username, profile_pic);

                            followingInfo.put(following.get(index), pair);
                        }

                    }

                    if (index < following.size() - 1) {
                        getFollowingInfo(index + 1);
                    } else {
                        getPics(0);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void getPics(final int index) {
        if (isAdded()) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            Query query = reference
                    .child(getString(R.string.dbname_user_photos))
                    .orderByKey()
                    .equalTo(following.get(index));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.exists()) {
                        // display message that user was not found
                        Toast.makeText(getActivity(), "no pics found.", Toast.LENGTH_SHORT).show();
                    }

                    // get userId's of following
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        if (singleSnapshot.exists()) {
                            for (DataSnapshot ss : singleSnapshot.getChildren()) {
                                if (ss.exists()) {
                                    if (ss.child("archived").getValue().toString().equals("0")) {
                                        Toast.makeText(getActivity(), "pic is not archived", Toast.LENGTH_SHORT).show();
                                        Pic pic = new Pic();
                                        pic.setOwner(ss.child("user_id").getValue().toString());
                                        pic.setTimestamp(ss.child("time_created").getValue().toString());
                                        pic.setDownloadUrl(ss.child("image_path").getValue().toString());
                                        pics.add(pic);
                                    }
                                }
                            }
                        }
                    }

                    if (index < following.size() - 1) {
                        getPics(index + 1);
                    } else {
                        // done querying
                        Toast.makeText(getActivity(), "ready to display pics", Toast.LENGTH_SHORT).show();
                        displayPics();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void displayPics() {
        // sort the pics chronologically
        if (pics != null) {
            Collections.sort(pics, new Comparator<Pic>() {
                @Override
                public int compare(Pic p1, Pic p2) {
                    return p2.getTimestamp().compareTo(p1.getTimestamp());
                }
            });
        }

        for (int i = 0; i < pics.size(); i++) {
            //Toast.makeText(getActivity(), pics.get(i).getTimestamp() + "(" + pics.get(i).getOwner() + ")", Toast.LENGTH_SHORT).show();

            //set username (found in followingInfo hash table)
            NAMES.add(followingInfo.get(pics.get(i).getOwner()).first);

            //set profile pic (found in followingInfo hash table)
            PROFILE_PICS.add(followingInfo.get(pics.get(i).getOwner()).second);

            //set photo
            PHOTOS.add(pics.get(i).getDownloadUrl());
        }

        //for (Map.Entry<String, Pair<String, String>> entry : followingInfo.entrySet()) {
        //    Toast.makeText(getActivity(), "key: " + entry.getKey().toString() + " value: " + entry.getValue().first + " , " + entry.getValue().second, Toast.LENGTH_SHORT).show();
        //}

        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
    }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

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
                }

            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "onDataChange: Retrieve user info from database");
                // retrieve user info from the database
                // retrieve images from the user
                getFollowing(mAuth.getCurrentUser());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void goToProfile(String username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    // display message that user was not found
                    Toast.makeText(getActivity(), "User not found.", Toast.LENGTH_SHORT).show();
                }
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    if (singleSnapshot.exists()) {
                        Intent i = new Intent(getActivity(), ProfileActivity.class);
                        String user_id = singleSnapshot.child("user_id").getValue().toString();
                        i.putExtra("USER_ID", user_id);
                        startActivity(i);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

}
