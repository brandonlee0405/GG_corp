package com.brandonlee.instagram.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brandonlee.instagram.Database.Photo;
import com.brandonlee.instagram.Database.User;
import com.brandonlee.instagram.Database.UserAccountSettings;
import com.brandonlee.instagram.Database.UserSettings;
import com.brandonlee.instagram.ProfileSettings;
import com.brandonlee.instagram.R;
import com.brandonlee.instagram.Utils.FirebaseMethods;
import com.brandonlee.instagram.Utils.GridImageAdapter;
import com.brandonlee.instagram.Utils.GridViewImageHelper;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by BrandonLee on 2/6/18.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ProfileFragment";

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

        view.findViewById(R.id.btnEditProfile).setOnClickListener(this);

        initImageLoader();

        //setupGridView();
        //gridImage();
        //mGridView.setAdapter(new ImageAdpaterGridView(context));

        gridImage();

        setupFirebaseAuth();


        return view;
    }


    private void gridImage() {
        ArrayList<String> imgUrls = new ArrayList<>();
        imgUrls.add("https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_192653_?alt=media&token=9aaaa744-3f29-481c-a162-10388c644686");
        imgUrls.add("https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_211314_?alt=media&token=9e7a376c-48c6-4402-956e-4bd32c1d6c3a");
        imgUrls.add("https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_223323_?alt=media&token=4b1f3887-368b-4838-8c36-1226f8973993");
        imgUrls.add("https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_223323_?alt=media&token=4b1f3887-368b-4838-8c36-1226f8973993");
        imgUrls.add("https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_223323_?alt=media&token=4b1f3887-368b-4838-8c36-1226f8973993");
        imgUrls.add("https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_192653_?alt=media&token=9aaaa744-3f29-481c-a162-10388c644686");
        imgUrls.add("https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_192653_?alt=media&token=9aaaa744-3f29-481c-a162-10388c644686");

        setupImageGridView(imgUrls);
    }

    private void setupImageGridView(ArrayList<String> imgUrls) {
        GridViewImageHelper helper =  new GridViewImageHelper(context,R.layout.layout_grid_imageview,"",imgUrls);
        mGridView.setAdapter(helper);
    }

    /*
    private void setupGridView() {
        Log.d(TAG, "setupGridView: setting up the grid view");

        final ArrayList<Photo> photos = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference
                .child(getString(R.string.dbname_User_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    photos.add(singleSnapshot.getValue(Photo.class));
                }

                int gridWidth = getResources().getDisplayMetrics().widthPixels;
                int imageWidth = gridWidth/NUM_GRID_COLUMNS;

                mGridView.setColumnWidth(imageWidth);

                ArrayList<String> imgURLS = new ArrayList<String>();
                for (int i = 0; i < photos.size(); ++i) {
                    imgURLS.add(photos.get(i).getImage_path());
                }

                GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, "", imgURLS);
                mGridView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    */

    /*
    public class ImageAdpaterGridView extends BaseAdapter {
        private Context mContext;

        public ImageAdpaterGridView (Context c) {
            mContext = c;
        }

        public int getCount() {
            return imgUrls.size();
        }

        public String getItem(int position) {
            return imgUrls.get(position);
        }


        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView mImageView;

            if (convertView == null) {
                mImageView = new ImageView(mContext);
                mImageView.setLayoutParams(new GridView.LayoutParams(480, 480));
                mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mImageView.setPadding(8,8,8,8);
            }
            else {
                mImageView = (ImageView) convertView;
            }
            //String url = getItem(position);
            String url = imgUrls.get(position);
            Picasso.with(mContext).load(url).fit().centerCrop().into(mImageView);
            mImageView.setRotation((float) 90.0);
            return mImageView;
        }
    };
    */

    /*
    private void gridImage() {
        imgUrls = new String[]{
                "https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_192653_?alt=media&token=9aaaa744-3f29-481c-a162-10388c644686",
                "https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_211314_?alt=media&token=9e7a376c-48c6-4402-956e-4bd32c1d6c3a",
                "https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_223323_?alt=media&token=4b1f3887-368b-4838-8c36-1226f8973993",
                "https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_223323_?alt=media&token=4b1f3887-368b-4838-8c36-1226f8973993",
                "https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_223323_?alt=media&token=4b1f3887-368b-4838-8c36-1226f8973993",
                "https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_223323_?alt=media&token=4b1f3887-368b-4838-8c36-1226f8973993",
                "https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_223323_?alt=media&token=4b1f3887-368b-4838-8c36-1226f8973993",
                "https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_223323_?alt=media&token=4b1f3887-368b-4838-8c36-1226f8973993"
        };

    }
    */

    /*
    private void gridImage() {
        imgUrls = new ArrayList ();
        imgUrls.add("https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_192653_?alt=media&token=9aaaa744-3f29-481c-a162-10388c644686");
        imgUrls.add("https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_211314_?alt=media&token=9e7a376c-48c6-4402-956e-4bd32c1d6c3a");
        imgUrls.add("https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_223323_?alt=media&token=4b1f3887-368b-4838-8c36-1226f8973993");
        imgUrls.add("https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_223323_?alt=media&token=4b1f3887-368b-4838-8c36-1226f8973993");
        imgUrls.add("https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_223323_?alt=media&token=4b1f3887-368b-4838-8c36-1226f8973993");
        imgUrls.add("https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_192653_?alt=media&token=9aaaa744-3f29-481c-a162-10388c644686");
        imgUrls.add("https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/Users%2F6jDKySv4Y0ak2N8NaTJxoQF7iF73%2FJPEG_180305_192653_?alt=media&token=9aaaa744-3f29-481c-a162-10388c644686");
    }
    */


    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void setProfileWidgets(UserSettings userSettings) {
        Log.d(TAG, "setProfileWidgets: setting widgets with data from firebase");

        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");

        // Got to do the profile photo
        mFullname.setText(settings.getDisplay_name());
        mDescription.setText(settings.getDescription());
        mPosts.setText(String.valueOf(settings.getPosts()));
        mFollowing.setText(String.valueOf(settings.getFollowing()));
        mFollowers.setText(String.valueOf(settings.getFollowers()));

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
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());


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
