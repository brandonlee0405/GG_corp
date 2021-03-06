package com.brandonlee.instagram.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.brandonlee.instagram.Database.User;
import com.brandonlee.instagram.ProfileActivity;
import com.brandonlee.instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BrandonLee on 2/6/18.
 */

public class SearchFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "SearchFragment";

    EditText etUsername;
    private List<User> mUserList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        etUsername = (EditText)view.findViewById(R.id.editText);
        mUserList = new ArrayList<>();

        view.findViewById(R.id.searchButton).setOnClickListener(this);

        return view;
    }

    private void search() {
        String username = etUsername.getText().toString().trim();
        mUserList.clear();

        if (username.isEmpty()) {
            etUsername.setError("Must type in a username.");
            etUsername.requestFocus();
            return;
        }

        // query database
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.searchButton) {
            search();
        }
    }
}
