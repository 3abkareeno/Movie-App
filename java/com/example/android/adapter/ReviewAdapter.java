package com.example.android.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.data.Review;
import com.example.android.movies.R;

import java.util.ArrayList;

public class ReviewAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<Review> reviews;

    public ReviewAdapter(Context context, ArrayList<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.review_item, parent, false);
        }

        Review currentItem = (Review) getItem(position);

        TextView reviewAuthor = (TextView) convertView.findViewById(R.id.review_author);
        reviewAuthor.setText(currentItem.getAuthor());

        TextView reviewContent = (TextView) convertView.findViewById(R.id.review_content);
        reviewContent.setText(currentItem.getContent());

        return convertView;
    }
}
