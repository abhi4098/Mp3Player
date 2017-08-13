package com.example.abhinandansharma.mp3.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.abhinandansharma.mp3.R;
import com.example.abhinandansharma.mp3.model.TypeModel;


import java.util.ArrayList;

/**
 * Created by Abhinandan on 6/6/17.
 */

public class TypeAdapter extends BaseAdapter {
    private ArrayList<TypeModel> typeList;
    private static LayoutInflater inflater = null;


    public TypeAdapter(Context c, ArrayList<TypeModel> typeList){

        this.typeList=typeList;
        inflater= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return typeList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
         return typeList.get(position).getID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       TypeViewHolder typeViewHolder ;
        View view = convertView;
        if (convertView == null) {

            view = inflater.inflate(R.layout.type, parent, false);
            typeViewHolder = new TypeViewHolder();
            typeViewHolder.llType = (LinearLayout) view.findViewById(R.id.ll_type);
            typeViewHolder.name = (TextView) view.findViewById(R.id.type_title);


            view.setTag(typeViewHolder);
        }
        else
        {
            typeViewHolder = (TypeViewHolder)view.getTag();
        }
        TypeModel typeModel = typeList.get(position);
        typeViewHolder.name.setText(typeModel.getName());
        return  view;
    }

    static class TypeViewHolder {
        LinearLayout llType;
        TextView name;


    }


}
