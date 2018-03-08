package com.brandonlee.instagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brandonlee.instagram.Database.LoginActivity;
import com.brandonlee.instagram.Database.Photo;
import com.brandonlee.instagram.Database.User;
import com.brandonlee.instagram.Database.UserAccountSettings;
import com.brandonlee.instagram.Database.UserSettings;
import com.brandonlee.instagram.Utils.FirebaseMethods;
import com.brandonlee.instagram.Utils.UniversalImageLoader;
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
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSettings extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "ProfileSettings";

    static final int REQUEST_IMAGE_CAPTURE = 1;

    FirebaseUser user;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    String mCurrentPhotoPath;
    String mCurrentPhotoName;
    String mPhotoTimeStamp;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    String mCurrentPhotoLink;

    private String userID;

    //EditProfile Widgets
    private EditText mDisplayName, mUsername, mDescription, mEmail;
    private CircleImageView mProfilePhoto;


    private UserSettings mUserSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        mDisplayName = (EditText)findViewById(R.id.etFullName);
        mUsername = (EditText)findViewById(R.id.etUsername);
        mDescription = (EditText)findViewById(R.id.userBio);
        mEmail = (EditText)findViewById(R.id.etEmail);
        mProfilePhoto = (CircleImageView) findViewById(R.id.profile_photo);

        mFirebaseMethods = new FirebaseMethods(ProfileSettings.this);

        initImageLoader();
        //setProfileImage();

        setupFirebaseAuth();

        findViewById(R.id.btnSignOut).setOnClickListener(this);
        findViewById(R.id.btnSubmit).setOnClickListener(this);

        mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    takeProfilePic();
                } catch (IOException e) {

                }
            }
        });


    }

    private void takeProfilePic() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {

            String authorities = this.getApplicationContext().getPackageName() + ".fileprovider";
            Uri photoURI = FileProvider.getUriForFile(this.getApplicationContext(), authorities, createImageFile());
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }

    }

    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "GG_Corps");
        if (!storageDir.exists())
            storageDir.mkdir();
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        /*
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        */
        mPhotoTimeStamp = timestamp;
        mCurrentPhotoName = imageFileName;
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == this.RESULT_OK) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //imageView.setImageBitmap(imageBitmap);
            //setReducedImageSize();
            Uri imageUri = Uri.parse(mCurrentPhotoPath);
            File file = new File(imageUri.getPath());
            Toast.makeText(this, file.toString(), Toast.LENGTH_LONG).show();
            try {
                InputStream ims = new FileInputStream(file);
                //imageView.setImageBitmap(BitmapFactory.decodeStream(ims));
                Bitmap imageBit =  (Bitmap) BitmapFactory.decodeStream(ims);
                //imageView.setImageBitmap(rotateImage(imageBit, 90));
                mProfilePhoto.setImageBitmap(rotateImageIfRequired(this.getApplicationContext(), imageBit, imageUri));
            } catch (FileNotFoundException e) {
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }

            MediaScannerConnection.scanFile(this.getApplicationContext(),
                    new String[]{imageUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {

                        }
                    });
            upLoadPicture(file, mCurrentPhotoName);
        }
    }

    private void upLoadPicture(File filePath,  String imageName) {
        // if there is a user, use the account name for the folder.  So the pictures are account bound
        String fName = "image/";
        if (user != null) {
            //Toast.makeText(getActivity(), user.getEmail(), Toast.LENGTH_LONG).show();
            fName = "Users/" + user.getUid() + "/";
            //Toast.makeText(getActivity(), user.getUid(), Toast.LENGTH_LONG).show();
        }
        Uri file = Uri.fromFile(filePath);
        StorageReference picRef = mStorageRef.child(fName + imageName);

        picRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //Toast.makeText(getActivity(), downloadUrl.toString(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "onSuccess: " + downloadUrl.toString());
                mCurrentPhotoLink = downloadUrl.toString();
                /*
                Intent intent = new Intent(getActivity(), ProfileFragment.class);
                intent.putExtra("Photo_Link", mCurrentPhotoLink);
                startActivity(intent);
                */
                String id = user.getUid();
                String newPhotoKey = myRef.child("User_Photo").push().getKey();
                Photo photo = new Photo();
                photo.setImage_path(mCurrentPhotoLink);
                photo.setPhoto_id(newPhotoKey);
                photo.setDate_created(getTimeStamp());
                photo.setUser_id(id);
                photo.setArchived("0");
                myRef.child("user_account_settings")
                        .child(FirebaseAuth.getInstance().getCurrentUser()
                                .getUid()).child("profile_photo").setValue(photo.getImage_path());
            }
        }) .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("Pacific/Canada"));
        return sdf.format(new Date());
    }


    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        //InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        /*
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
        */
        ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    private void setProfileWidgets(UserSettings userSettings) {
        Log.d(TAG, "setProfileWidgets: setting widgets with data from firebase " + userSettings.getUser().getEmail());

        mUserSettings = userSettings;
        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");
        mProfilePhoto.setRotation((float) -90.0);

        // Got to do the profile photo
        mDescription.setText(settings.getDescription());
        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mEmail.setText(userSettings.getUser().getEmail());

    }


    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(ProfileSettings.this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }


    /**
     * Retrieves the data contained in the widgets and write to the database
     * Before writing, check the username is unique
     */
    private void saveProfileSettings() {
        final String displayName = mDisplayName.getText().toString();
        final String username = mUsername.getText().toString();
        final String description = mDescription.getText().toString();
        final String email = mEmail.getText().toString();


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // User did change their username
                if(!mUserSettings.getUser().getUsername().equals(username)) {
                    checkIfUsernameExists(username);
                }

                // Update your fullname
                if(!mUserSettings.getSettings().getDisplay_name().equals(displayName)) {
                    mFirebaseMethods.updateUserAccountSettings(displayName, null);
                }

                // Update your description
                if(!mUserSettings.getSettings().getDescription().equals(description)) {
                    mFirebaseMethods.updateUserAccountSettings(null, description);
                }
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Checking if username exists in our databse
     * @param username
     */
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: Check if " + username + " already exists.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()) {
                    // Add the username
                    mFirebaseMethods.updateUsername(username);
                    Toast.makeText(ProfileSettings.this, "Saved Username", Toast.LENGTH_SHORT).show();
                }
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    if (singleSnapshot.exists()) {
                        Log.d(TAG, "checkIfUsernameExists: Found a match: " + singleSnapshot.getValue(User.class).getUsername());
                        Toast toast = Toast.makeText(ProfileSettings.this, "That username already exists.", Toast.LENGTH_SHORT);
                        TextView v = (TextView)toast.getView().findViewById(android.R.id.message);
                        v.setTextColor(Color.BLACK);
                        toast.show();
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
        switch(view.getId()) {
            case R.id.btnSignOut:
                Log.d(TAG, "onClick: Sign out the user");
                mAuth.signOut();
                ProfileSettings.this.finish();
                break;
                
            case R.id.btnSubmit:
                Log.d(TAG, "onClick: Submit updated info");
                saveProfileSettings();
                break;
        }
    }


    /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");


                    Intent intent = new Intent(ProfileSettings.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                // ...
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "onDataChange: Retrieve user info from database");
                // retrieve user info from the database
                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));
                // retrieve images from the user

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
