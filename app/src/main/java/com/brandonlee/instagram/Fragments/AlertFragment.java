package com.brandonlee.instagram.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brandonlee.instagram.R;

/**
 * Created by BrandonLee on 2/6/18.
 */

public class AlertFragment extends Fragment {
    private static final String TAG = "AlertFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alert, container, false);

        return view;
    }
}
