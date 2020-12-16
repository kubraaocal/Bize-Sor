package com.example.bizesor;

import android.content.Context;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SinavAdapter extends ArrayAdapter<SinavItem> {
    //Burada SinavItem ı adapter içinde liste halinde gösterme işlemlerini yaptık

    public SinavAdapter(Context context,ArrayList<SinavItem> sinavList){
        super(context,0,sinavList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    private View initView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.sinav_spinner_row,parent,false);
        }
        TextView txtSinav=convertView.findViewById(R.id.txt_sinav);
        SinavItem sinavItem=getItem(position);
        if(sinavItem!=null) {
            txtSinav.setText(sinavItem.getSinavIsmi());
        }
        return convertView;
    }
}
