package com.example.bizesor.Adapterlar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bizesor.Modeller.ModelSinav;
import com.example.bizesor.R;

import java.util.ArrayList;

public class SinavAdapter extends ArrayAdapter<ModelSinav> {
    //Burada ModelSinav ı adapter içinde liste halinde gösterme işlemlerini yaptık

    public SinavAdapter(Context context,ArrayList<ModelSinav> sinavList){
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
        ModelSinav modelSinav =getItem(position);
        if(modelSinav !=null) {
            txtSinav.setText(modelSinav.getSinavIsmi());
        }
        return convertView;
    }
}
