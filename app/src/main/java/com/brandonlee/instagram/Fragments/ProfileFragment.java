package com.brandonlee.instagram.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.brandonlee.instagram.Database.LoginActivity;
import com.brandonlee.instagram.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by BrandonLee on 2/6/18.
 */

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button btnLogout = (Button)view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent redirect = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(redirect);
            }
        });

        return view;

    }



}
