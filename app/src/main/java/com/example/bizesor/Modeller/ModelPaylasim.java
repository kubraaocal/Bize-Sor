package com.example.bizesor.Modeller;

public class ModelPaylasim {
    private String kullaniciId,paylasimId, kullaniciAdSoyad, resim, ders, paylasimYazi, puan, paylasimResim, tarihSaat, pLikes,pYorum,pSoru;

    public ModelPaylasim() {

    }

    public ModelPaylasim(String kullaniciId, String paylasimId, String kullaniciAdSoyad, String resim, String ders, String paylasimYazi, String puan, String paylasimResim, String tarihSaat, String pLikes, String pYorum, String pSoru) {
        this.kullaniciId = kullaniciId;
        this.paylasimId = paylasimId;
        this.kullaniciAdSoyad = kullaniciAdSoyad;
        this.resim = resim;
        this.ders = ders;
        this.paylasimYazi = paylasimYazi;
        this.puan = puan;
        this.paylasimResim = paylasimResim;
        this.tarihSaat = tarihSaat;
        this.pLikes = pLikes;
        this.pYorum = pYorum;
        this.pSoru = pSoru;
    }

    public String getKullaniciId() {
        return kullaniciId;
    }

    public void setKullaniciId(String kullaniciId) {
        this.kullaniciId = kullaniciId;
    }

    public String getPaylasimId() {
        return paylasimId;
    }

    public void setPaylasimId(String paylasimId) {
        this.paylasimId = paylasimId;
    }

    public String getKullaniciAdSoyad() {
        return kullaniciAdSoyad;
    }

    public void setKullaniciAdSoyad(String kullaniciAdSoyad) {
        this.kullaniciAdSoyad = kullaniciAdSoyad;
    }

    public String getResim() {
        return resim;
    }

    public void setResim(String resim) {
        this.resim = resim;
    }

    public String getDers() {
        return ders;
    }

    public void setDers(String ders) {
        this.ders = ders;
    }

    public String getPaylasimYazi() {
        return paylasimYazi;
    }

    public void setPaylasimYazi(String paylasimYazi) {
        this.paylasimYazi = paylasimYazi;
    }

    public String getPuan() {
        return puan;
    }

    public void setPuan(String puan) {
        this.puan = puan;
    }

    public String getPaylasimResim() {
        return paylasimResim;
    }

    public void setPaylasimResim(String paylasimResim) {
        this.paylasimResim = paylasimResim;
    }

    public String getTarihSaat() {
        return tarihSaat;
    }

    public void setTarihSaat(String tarihSaat) {
        this.tarihSaat = tarihSaat;
    }

    public String getpLikes() {
        return pLikes;
    }

    public void setpLikes(String pLikes) {
        this.pLikes = pLikes;
    }

    public String getpYorum() {
        return pYorum;
    }

    public void setpYorum(String pYorum) {
        this.pYorum = pYorum;
    }

    public String getpSoru() {
        return pSoru;
    }

    public void setpSoru(String pSoru) {
        this.pSoru = pSoru;
    }
}
