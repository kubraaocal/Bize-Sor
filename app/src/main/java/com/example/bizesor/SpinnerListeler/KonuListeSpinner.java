package com.example.bizesor.SpinnerListeler;

import com.example.bizesor.Modeller.ModelSinav;

import java.util.ArrayList;

public class KonuListeSpinner {
    public ArrayList<ModelSinav> konuItemArrayList=new ArrayList<>();
    public void initlist(String ders){
        switch (ders){
            case "Matematik LGS":
                konuItemArrayList.removeAll(konuItemArrayList);
                konuItemArrayList.add(new ModelSinav("Basit Sayılar"));
                konuItemArrayList.add(new ModelSinav("Fonksiyonlar"));
                konuItemArrayList.add(new ModelSinav("Türev"));
                konuItemArrayList.add(new ModelSinav("İntegral"));
                break;
            case "Türkçe LGS":
                konuItemArrayList.removeAll(konuItemArrayList);
                konuItemArrayList.add(new ModelSinav("Noktalama İşaretleri"));
                konuItemArrayList.add(new ModelSinav("Paragraf"));
                konuItemArrayList.add(new ModelSinav("Cümle Yapısı"));
                konuItemArrayList.add(new ModelSinav("Yüklem"));
                break;
            case "İnkılap Tarihi":
                konuItemArrayList.removeAll(konuItemArrayList);
                konuItemArrayList.add(new ModelSinav("Atatürk"));
                konuItemArrayList.add(new ModelSinav("İnkılaplar"));
                konuItemArrayList.add(new ModelSinav("Atatürkçülük"));
                konuItemArrayList.add(new ModelSinav("Türkiye"));
                break;
            case "Fen Bilgisi":
                konuItemArrayList.removeAll(konuItemArrayList);
                konuItemArrayList.add(new ModelSinav("Hücre"));
                konuItemArrayList.add(new ModelSinav("Solunum"));
                konuItemArrayList.add(new ModelSinav("Fotosentez"));
                konuItemArrayList.add(new ModelSinav("Boşaltım"));
                break;
            case "İngilizce":
                konuItemArrayList.removeAll(konuItemArrayList);
                konuItemArrayList.add(new ModelSinav("Come"));
                konuItemArrayList.add(new ModelSinav("On"));
                konuItemArrayList.add(new ModelSinav("Girl"));
                konuItemArrayList.add(new ModelSinav("Win"));
                break;
            case "Din Kültürü ve Ahlak Bilgisi":
                konuItemArrayList.removeAll(konuItemArrayList);
                konuItemArrayList.add(new ModelSinav("Namaz"));
                konuItemArrayList.add(new ModelSinav("Zekat"));
                konuItemArrayList.add(new ModelSinav("Hac"));
                konuItemArrayList.add(new ModelSinav("Oruç"));
                break;


        }
    }
}
