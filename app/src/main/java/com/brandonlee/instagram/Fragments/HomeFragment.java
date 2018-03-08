package com.brandonlee.instagram.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
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
import com.brandonlee.instagram.Database.UserAccountSettings;
import com.brandonlee.instagram.Database.UserSettings;
import com.brandonlee.instagram.ProfileActivity;
import com.brandonlee.instagram.R;
import com.brandonlee.instagram.Utils.FirebaseMethods;
import com.brandonlee.instagram.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;

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

        mFirebaseMethods = new FirebaseMethods(getActivity());

        listView = (ListView)view.findViewById(R.id.listView);

        //CustomAdapter customAdapter = new CustomAdapter();
        //listView.setAdapter(customAdapter);

        initImageLoader();

        setupFirebaseAuth();

        return view;
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return TEST_PROFILE_PICS.length;
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
            UniversalImageLoader.setImage(PROFILE_PICS.get(i), profile_pic, null, "");
            UniversalImageLoader.setImage(PHOTOS.get(i), photo, null, "");
            name.setText(NAMES.get(i));


            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), ProfileActivity.class);
                    String user_id = followingInfo.get(name.getText().toString()).first;
                    i.putExtra("USER_ID", user_id);
                    startActivity(i);
                }
            });

            return view;
        }
    }

    private void getFollowing(FirebaseUser user) {
        Log.d(TAG, "setProfileWidgets: setting widgets with data from firebase");

        Toast.makeText(getActivity(), "userid: " + user.getUid(), Toast.LENGTH_SHORT).show();

        // query database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByKey()
                .equalTo(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    // display message that user was not found
                    Toast.makeText(getActivity(), "not found.", Toast.LENGTH_SHORT).show();
                }

                // get userId's of following
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    if (singleSnapshot.exists()) {
                        // go to user's profile
                        User user = singleSnapshot.getValue(User.class);
                        String string = singleSnapshot.child("following").getValue().toString();
                        string = string.replace("{", "");
                        string = string.replace("}", "");
                        string = string.replace(",", "");
                        string = string.replace("=1", "");
                        string = string.replaceAll("[0-9a-zA-Z]+=0", "");
                        String[] following = string.split("\\s+");
                        for (int i = 0; i < following.length; i++) {
                            //Toast.makeText(getActivity(), following[i], Toast.LENGTH_SHORT).show();
                        }
                        getFollowingInfo(following, followingInfo,  0);
                    }

                }


                // retrieve pics from following


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void getFollowingInfo(final String[] following, final HashMap<String, Pair<String, String>> followingInfo, final int index) {
        // get following usernames and profile pics
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            Query query = reference
                    .child(getString(R.string.dbname_user_account_settings))
                    .orderByKey()
                    .equalTo(following[index]);
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

                            String username =  singleSnapshot.child("username").getValue().toString();
                            String profile_pic = singleSnapshot.child("profile_photo").getValue().toString();
                            Pair<String, String> pair = new Pair<>(username, profile_pic);

                            followingInfo.put(following[index], pair);
                        }

                    }

                    if (index < following.length - 1) {
                        getFollowingInfo(following, followingInfo, index + 1);
                    }
                    else {
                        getPics(following, followingInfo, new ArrayList<Pic>(), 0);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

    private void getPics(final String[] following, final HashMap<String, Pair<String, String>> followingInfo, final ArrayList<Pic> pics, final int index) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference
                .child(getString(R.string.dbname_user_photos))
                .orderByKey()
                .equalTo(following[index]);
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
                                Pic pic = new Pic();
                                pic.setOwner(ss.child("user_id").getValue().toString());
                                pic.setTimestamp(ss.child("time_created").getValue().toString());
                                pic.setDownloadUrl(ss.child("image_path").getValue().toString());
                                pics.add(pic);
                            }
                        }
                    }
                }

                if (index < following.length - 1) {
                    getPics(following, followingInfo, pics, index + 1);
                }
                else {
                    // done querying
                    Toast.makeText(getActivity(), "ready to display pics", Toast.LENGTH_SHORT).show();
                    displayPics(followingInfo, pics);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayPics(HashMap<String, Pair<String, String>> followingInfo, ArrayList<Pic> pics) {
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

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

}
