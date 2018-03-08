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

import com.brandonlee.instagram.Fragments.HomeFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class del_photo extends AppCompatActivity {

    private String imgURL;
    private String photo_id;
    private Button btnBack;

    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_del_photo);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                imgURL = null;
            } else {
                imgURL = extras.getString("imgURL");
            }
        } else {
            imgURL = (String) savedInstanceState.getSerializable("imgURL");
        }

        if (imgURL == null) {
            Toast.makeText(this, "no img url found.", Toast.LENGTH_SHORT).show();
        } else {
            getPhotoId();
        }


        Button btnDelete = (Button) findViewById(R.id.btnDelete);
        Button btnArchive = (Button) findViewById(R.id.btnArchive);
        ImageView delImageView = (ImageView) findViewById(R.id.delImageView);

        Bundle extra = getIntent().getExtras();
        imgURL = extra.getString("imgURL");
        Picasso.with(del_photo.this).load(imgURL).into(delImageView);
        delImageView.setRotation((float) 90.0);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photo_id != null) {
                    deletePhoto();
                }
            }
        });

        btnArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photo_id != null) {
                    archivePhoto();
                }
            }
        });

    }

    private void getPhotoId() {
        // query database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference
                .child(getString(R.string.dbname_user_photos))
                .orderByKey()
                .equalTo(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    // display message that user was not found
                    //Toast.makeText(del_photo.this, "not found.", Toast.LENGTH_SHORT).show();
                }
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    if (singleSnapshot.exists()) {
                        for (DataSnapshot ss : singleSnapshot.getChildren()) {
                            if (ss.exists()) {
                                if (ss.child("image_path").getValue().toString().equals(imgURL)) {
                                    photo_id = ss.child("photo_id").getValue().toString();
                                    //Toast.makeText(del_photo.this, "found " + photo_id, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void deletePhoto() {
        // Delete from Storage
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(imgURL);
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Delete from databse
                FirebaseDatabase.getInstance().getReference()
                        .child("User_Photo")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(photo_id)
                        .removeValue();
                Toast.makeText(del_photo.this, "Photo Deleted!", Toast.LENGTH_LONG).show();
                finish();
            }

            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(del_photo.this, "Deletion Failed", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void archivePhoto() {
        database.child("User_Photo").child(user.getUid()).child(photo_id).child("archived").setValue("1").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(del_photo.this, "Photo Archived!", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
