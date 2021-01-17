package com.example.bizesor.Modeller;

public class ModelYorum {
    String yorumId,kullaniciId,kullaniciAd,saat,yorum,kullaniciResmi;

    public ModelYorum() {

    }

    public ModelYorum(String yorumId, String kullaniciId, String kullaniciAd, String saat, String yorum, String kullaniciResmi) {
        this.yorumId = yorumId;
        this.kullaniciId = kullaniciId;
        this.kullaniciAd = kullaniciAd;
        this.saat = saat;
        this.yorum = yorum;
        this.kullaniciResmi = kullaniciResmi;
    }

    public String getYorumId() {
        return yorumId;
    }

    public void setYorumId(String yorumId) {
        this.yorumId = yorumId;
    }

    public String getKullaniciId() {
        return kullaniciId;
    }

    public void setKullaniciId(String kullaniciId) {
        this.kullaniciId = kullaniciId;
    }

    public String getKullaniciAd() {
        return kullaniciAd;
    }

    public void setKullaniciAd(String kullaniciAd) {
        this.kullaniciAd = kullaniciAd;
    }

    public String getSaat() {
        return saat;
    }

    public void setSaat(String saat) {
        this.saat = saat;
    }

    public String getYorum() {
        return yorum;
    }

    public void setYorum(String yorum) {
        this.yorum = yorum;
    }

    public String getKullaniciResimi() {
        return kullaniciResmi;
    }

    public void setKullaniciResimi(String kullaniciResimi) {
        this.kullaniciResmi = kullaniciResimi;
    }
}
