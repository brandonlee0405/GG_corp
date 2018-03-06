package com.brandonlee.instagram;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.brandonlee.instagram.Fragments.ProfileFragment;
import com.brandonlee.instagram.Utils.ViewProfileFragment;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: Did it come here?");

        init();
    }

    private void init() {
        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.calling_activity))) {
            if(intent.hasExtra(getString(R.string.intent_user))) {
                ViewProfileFragment fragment = new ViewProfileFragment();
                Bundle args = new Bundle();
                args.putParcelable(getString(R.string.intent_user), intent.getParcelableExtra(getString(R.string.intent_user)));
                fragment.setArguments(args);
                Log.d(TAG, "init: bundle: " + fragment.getArguments());

                Log.d(TAG, "init: Inflate fragment");
                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.relLayout1, fragment);
                transaction.addToBackStack(getString(R.string.view_profile_fragment));
                transaction.commit();
            }
            else{
                Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            ProfileFragment fragment = new ProfileFragment();
            android.support.v4.app.FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack("profile_fragment");
            transaction.commit();
        }
    }
}
