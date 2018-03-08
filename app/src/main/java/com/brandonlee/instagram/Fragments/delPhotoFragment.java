package com.brandonlee.instagram.Fragments;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.brandonlee.instagram.R;

public class delPhotoFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_del_photo_fragment, container, false);

        Button btnDelete = (Button) view.findViewById(R.id.btnDelete);
        ImageView delImageView = (ImageView) view.findViewById(R.id.delImageView);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePhoto();
            }
        });


        return view;
    }

    private void deletePhoto() {
        //do something
    }

}
