package com.brandonlee.instagram.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.brandonlee.instagram.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by adria on 3/7/2018.
 */

public class GridViewImageHelper extends ArrayAdapter<String> {

    Context context;
    LayoutInflater layoutInflater;
    int layoutRes;
    String mAppend;
    ArrayList<String> imageUrls;

    public GridViewImageHelper(Context context, int resource, String mAppend, ArrayList<String> imageUrls) {
        super(context, resource, imageUrls);
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.layoutRes = resource;
        this.mAppend = mAppend;
        this.imageUrls = imageUrls;
    }

    public static class Holder {
        ImageView imageView;
        ProgressBar progressBar;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutRes, parent, false);

            holder = new Holder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.gridImageView);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.gridImageProgressbar);

            convertView.setTag(holder);
        }
        else {
            holder = (Holder) convertView.getTag();
        }

        String imgURL = getItem(position);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mAppend + imgURL, holder.imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if (holder.progressBar != null) {
                    holder.progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (holder.progressBar != null) {
                    holder.progressBar.setVisibility(View.GONE);
                    holder.imageView.setRotation((float) 90.0);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (holder.progressBar != null) {
                    holder.progressBar.setVisibility(View.GONE);
                    holder.imageView.setRotation((float) 90.0);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (holder.progressBar != null) {
                    holder.progressBar.setVisibility(View.GONE);
                    holder.imageView.setRotation((float) 90.0);
                }
            }
        });

        return convertView;
    }
}
