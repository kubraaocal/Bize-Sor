package com.example.bizesor;

import android.util.Log;

import java.util.ArrayList;

public class ListeSpinner {
    ArrayList<SinavItem> sinavItemArrayList;
    //Burada gösterilecek olanları bir arraylist oluşturup spinnerı kullandığımız yerde adapter içine atacağız
    public void initList(){
        sinavItemArrayList=new ArrayList<>();
        sinavItemArrayList.add(new SinavItem("LGS"));
        sinavItemArrayList.add(new SinavItem("KPSS"));
        sinavItemArrayList.add(new SinavItem("DGS"));
        sinavItemArrayList.add(new SinavItem("TYT"));
        sinavItemArrayList.add(new SinavItem("AYT"));
        Log.i("Liste",sinavItemArrayList.toString());
    }
}
