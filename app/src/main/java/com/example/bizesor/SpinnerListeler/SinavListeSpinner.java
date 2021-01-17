package com.example.bizesor.SpinnerListeler;

import com.example.bizesor.Modeller.ModelSinav;

import java.util.ArrayList;

public class SinavListeSpinner {
   public ArrayList<ModelSinav> modelSinavArrayList;
    //Burada gösterilecek olanları bir arraylist oluşturup spinnerı kullandığımız yerde adapter içine atacağız
    public void initList(){
        modelSinavArrayList =new ArrayList<>();
        modelSinavArrayList.add(new ModelSinav("LGS"));
        modelSinavArrayList.add(new ModelSinav("KPSS"));
        modelSinavArrayList.add(new ModelSinav("DGS"));
        modelSinavArrayList.add(new ModelSinav("YKS(AYT/TYT"));
    }
}
