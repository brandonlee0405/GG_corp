package com.brandonlee.instagram.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.brandonlee.instagram.ProfileSettings;
import com.brandonlee.instagram.R;
import com.squareup.picasso.Picasso;

/**
 * Created by BrandonLee on 2/6/18.
 */

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    Activity context;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getActivity();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Button btnEditProfile = (Button) context.findViewById(R.id.btnEditProfile);
        ImageView imgButton = (ImageView) context.findViewById(R.id.imageButton);
        String url = "https://firebasestorage.googleapis.com/v0/b/ggcorp-9ffb1.appspot.com/o/image%2FJPEG_180228_150836_?alt=media&token=eb1dd0ed-62ec-49dd-83b9-1e339904aefb";
        Picasso.with(getActivity().getApplicationContext()).load(url).into(imgButton);
        imgButton.setRotation((float) 90.0);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileSettings.class);
                Log.d(TAG, "onClick: Test the intent");
                startActivity(intent);
            }
        });
    }
}
