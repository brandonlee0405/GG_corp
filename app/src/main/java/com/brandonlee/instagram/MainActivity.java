package com.brandonlee.instagram;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.brandonlee.instagram.Fragments.AlertFragment;
import com.brandonlee.instagram.Fragments.CameraFragment;
import com.brandonlee.instagram.Fragments.HomeFragment;
import com.brandonlee.instagram.Fragments.ProfileFragment;
import com.brandonlee.instagram.Fragments.SearchFragment;
import com.brandonlee.instagram.Utils.BottomNavigationViewHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //private SectionsPageAdapter mSectionsPageAdapter;
    //private ViewPager mViewPager;

    private BottomNavigationViewEx.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.ic_house:
                    goHouse();
                    return true;
                case R.id.ic_search:
                    goSearch();
                    return true;
                case R.id.ic_circle:
                    goCamera();
                    return true;
                case R.id.ic_alert:
                    goAlert();
                    return true;
                case R.id.ic_android:
                    goProfile();
                    return true;
            }
            return false;
        }
    };

    private void goHouse() {
        HomeFragment fragment = new HomeFragment();
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container,fragment);
        ft.commit();
    }

    private void goSearch() {
        SearchFragment fragment = new SearchFragment();
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container,fragment);
        ft.commit();
    }

    private void goAlert() {
        AlertFragment fragment = new AlertFragment();
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container,fragment);
        ft.commit();
    }

    private void goProfile() {
        ProfileFragment fragment = new ProfileFragment();
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container,fragment);
        ft.commit();
    }

    private void goCamera() {
        CameraFragment fragment = new CameraFragment();
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container,fragment);
        ft.commit();
    }

    public void goToProfile() {
        goProfile();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: starting.");

        setupBottomNavView();

        /*
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        */

    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new SearchFragment());
        adapter.addFragment(new CameraFragment());
        adapter.addFragment(new AlertFragment());
        adapter.addFragment(new ProfileFragment());

        viewPager.setAdapter(adapter);
    }



    /**
     * BottomNavView Setup
     */
    private void setupBottomNavView() {
        Log.d(TAG, "setupBottomNavView: setting up BottomNavView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavbar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        goHouse();
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
