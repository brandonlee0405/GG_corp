package com.brandonlee.instagram;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserInfoActivity extends AppCompatActivity {

    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    EditText etUsername;
    EditText etFullName;
    Button btnConfirm;

    DatabaseReference databaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        spinner = (Spinner) findViewById(R.id.spinner);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etFullName = (EditText) findViewById(R.id.etFullName);
        adapter = ArrayAdapter.createFromResource(this, R.array.theme_names, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser();
            }
        });
    }

    private void addUser() {
        String username = etUsername.getText().toString().trim();
        String fullname = etFullName.getText().toString().trim();
        String theme = spinner.getSelectedItem().toString();

        if(!TextUtils.isEmpty(username)) {
            String id = databaseUsers.push().getKey();

            User user = new User(id, username, fullname, theme);

            databaseUsers.child(id).setValue(user);

            Toast.makeText(this, "User added!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "You should enter a username", Toast.LENGTH_LONG).show();
        }
    }


}
