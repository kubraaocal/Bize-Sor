package com.example.bizesor.SpinnerListeler;

import android.util.Log;

import com.example.bizesor.Modeller.ModelSinav;

import java.util.ArrayList;

public class DersListeSpinner {
    public ArrayList<ModelSinav> dersItemArrayList;
    public void initlist(String sinav){
        switch (sinav){
            case "LGS":
                dersItemArrayList=new ArrayList<>();
                dersItemArrayList.add(new ModelSinav("Matematik LGS"));
                dersItemArrayList.add(new ModelSinav("Türkçe LGS"));
                dersItemArrayList.add(new ModelSinav("İnkılap Tarihi"));
                dersItemArrayList.add(new ModelSinav("Fen Bilgisi"));
                dersItemArrayList.add(new ModelSinav("İngilizce"));
                dersItemArrayList.add(new ModelSinav("Din Kültürü ve Ahlak Bilgisi"));
                break;
            case "KPSS":
                dersItemArrayList=new ArrayList<>();
                dersItemArrayList.add(new ModelSinav("Matematik KPSS"));
                dersItemArrayList.add(new ModelSinav("Geometri KPSS"));
                dersItemArrayList.add(new ModelSinav("Türkçe KPSS"));
                dersItemArrayList.add(new ModelSinav("Vatandaşlık"));
                dersItemArrayList.add(new ModelSinav("Tarih KPSS"));
                dersItemArrayList.add(new ModelSinav("Coğrafya KPSS"));
                break;
            case "YKS(AYT/TYT)":
                dersItemArrayList=new ArrayList<>();
                dersItemArrayList.add(new ModelSinav("Edebiyat"));
                dersItemArrayList.add(new ModelSinav("Matematik"));
                dersItemArrayList.add(new ModelSinav("Türkçe"));
                dersItemArrayList.add(new ModelSinav("Geometri"));
                dersItemArrayList.add(new ModelSinav("Fizik"));
                dersItemArrayList.add(new ModelSinav("Kimya"));
                dersItemArrayList.add(new ModelSinav("Biyoloji"));
                dersItemArrayList.add(new ModelSinav("Tarih"));
                dersItemArrayList.add(new ModelSinav("Coğrafya"));
                dersItemArrayList.add(new ModelSinav("Felsefe"));
                dersItemArrayList.add(new ModelSinav("Din Kültürü"));
                break;
            case "DGS":
                dersItemArrayList=new ArrayList<>();
                dersItemArrayList.add(new ModelSinav("Türkçe DGS"));
                dersItemArrayList.add(new ModelSinav("Matematik DGS"));
                dersItemArrayList.add(new ModelSinav("Geometri DGS"));
            default:
                Log.i("Hata","Bir şeyler ters gidiyor");
        }
    }
}
