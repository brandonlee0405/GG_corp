package com.brandonlee.instagram;


import android.support.design.internal.BottomNavigationItemView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.brandonlee.instagram.Utils.BottomNavigationViewHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: starting.");

        setupBottomNavView();
    }


    /**
     * BottomNavView Setup
     */
    private void setupBottomNavView() {
        Log.d(TAG, "setupBottomNavView: setting up BottomNavView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavbar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
    }

}
