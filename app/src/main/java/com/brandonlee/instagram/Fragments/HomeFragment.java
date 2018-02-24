package com.brandonlee.instagram.Fragments;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.brandonlee.instagram.ProfileActivity;
import com.brandonlee.instagram.R;

import org.w3c.dom.Text;

/**
 * Created by BrandonLee on 2/6/18.
 */

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    // testing data
    int[] PROFILE_PICS = {R.drawable.sample_profile_pic, R.drawable.sample_profile_pic_2, R.drawable.sample_profile_pic_3};
    int[] PHOTOS = {R.drawable.sample_pic, R.drawable.sample_pic_2, R.drawable.sample_pic_3};
    String[] NAMES = {"Tiff94", "Arthurrr", "Crazy_Sydney"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ListView listView = (ListView)view.findViewById(R.id.listView);

        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);

        return view;
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return PROFILE_PICS.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.customlayout,null);
            ImageView profile_pic = (ImageView)view.findViewById(R.id.imageView_profilepic);
            TextView name = (TextView)view.findViewById(R.id.textView_name);
            ImageView photo = (ImageView)view.findViewById(R.id.imageView_photo);

            profile_pic.setImageResource(PROFILE_PICS[i]);
            name.setText(NAMES[i]);
            photo.setImageResource(PHOTOS[i]);

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), ProfileActivity.class));
                }
            });

            return view;
        }
    }

}
