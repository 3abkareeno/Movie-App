package com.example.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.data.Trailer;
import com.example.android.movies.R;

import java.util.ArrayList;

public class TrailerAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<Trailer> trailers;

    public TrailerAdapter(Context context, ArrayList<Trailer> trailers) {
        this.context = context;
        this.trailers = trailers;
    }

    @Override
    public int getCount() {
        return trailers.size();
    }

    @Override
    public Object getItem(int position) {
        return trailers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.trailer_item, parent, false);
        }

        Trailer currentItem = (Trailer) getItem(position);
        TextView trailerTitle = (TextView) convertView.findViewById(R.id.trailer_title);
        trailerTitle.setText(currentItem.getName());

        return convertView;
    }
}
