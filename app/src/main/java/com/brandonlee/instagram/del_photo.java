package com.brandonlee.instagram;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class del_photo extends AppCompatActivity {

    private String imgURL;
    private Button btnBack;

    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_del_photo);


        Button btnDelete = (Button) findViewById(R.id.btnDelete);
        ImageView delImageView = (ImageView) findViewById(R.id.delImageView);

        Bundle extra = getIntent().getExtras();
        imgURL = extra.getString("imgURL");
        Picasso.with(del_photo.this).load(imgURL).into(delImageView);
        delImageView.setRotation((float) 90.0);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePhoto();
            }
        });

    }

    private void deletePhoto() {
        // Delete from Storage
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(imgURL);
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(del_photo.this, "Photo Deleted!", Toast.LENGTH_LONG).show();
            }

            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        // Delete from databse
        
    }
}
