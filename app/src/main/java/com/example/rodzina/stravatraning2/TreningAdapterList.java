package com.example.rodzina.stravatraning2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.example.rodzina.stravatraning2.R.layout.row_layout;

/**
 * Created by Rodzina on 16.11.2016.
 */

public class TreningAdapterList extends ArrayAdapter {

    static class DataHand{
        ImageView image;
        TextView nazwa;
        TextView opis;
        TextView czas;

    }
    List list = new ArrayList<>();
    public TreningAdapterList(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public void add(Object object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public void clear() {

        super.clear();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        DataHand handler ;
        if (v ==null){

            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(row_layout,parent,false);
            handler = new DataHand();
            handler.image = (ImageView) v.findViewById(R.id.bike_icon);
            handler.nazwa = (TextView) v.findViewById(R.id.trening_nazwa);
            handler.opis = (TextView) v.findViewById(R.id.trening_opis);
            handler.czas = (TextView) v.findViewById(R.id.trening_czas);
            v.setTag(handler);
        }
        else{
            handler= (DataHand) v.getTag();

        }
        Trening trening;
        trening = (Trening) this.getItem(position);
        handler.image.setImageResource(R.mipmap.list_icon_bike);
        handler.nazwa.setText(trening.getNazwa());
        handler.opis.setText(trening.getOpis());
        handler.czas.setText(String.valueOf(trening.getCzas())+" minut");
        //return super.getView(position, convertView, parent);
        return v;
    }
}
