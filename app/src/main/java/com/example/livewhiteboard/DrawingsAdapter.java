package com.example.livewhiteboard;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DrawingsAdapter extends BaseAdapter {

    private ArrayList<DrawingModel> drawings;
    private Context context;

    public DrawingsAdapter(Context context, ArrayList<DrawingModel> drawings) {
        this.context = context;
        this.drawings = drawings;

    }

    @Override
    public int getCount() {
        return drawings.size();
    }

    @Override
    public DrawingModel getItem(int position) {
        return drawings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.drawing_list_item, parent, false);
        }

        TextView tvDrawingItem = convertView.findViewById(R.id.tvDrawingItem);

        final DrawingModel drawing = getItem(position);

        tvDrawingItem.setText(drawing.getName()+" "+drawing.getId());

        return convertView;
    }

}
