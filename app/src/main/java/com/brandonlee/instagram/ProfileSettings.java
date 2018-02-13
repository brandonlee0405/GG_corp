package com.brandonlee.instagram;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.brandonlee.instagram.Database.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileSettings extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "ProfileSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        findViewById(R.id.btnSignOut).setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnSignOut:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}
