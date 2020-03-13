package com.bijak.techno.oganlopian.model;

import android.graphics.drawable.Drawable;

public class HistoryModel extends ProfileModel {
    String Keluhan,TanggalBoking,Petugase,TanggalProses,Durasi,Penilaian,JamPis;
    Drawable avatar;
    public HistoryModel(){

    }

    public void setKeluhan(String keluhan){ this.Keluhan=keluhan;}
    public void setTanggalBoking(String tglbooking){ this.TanggalBoking = tglbooking;}
    public void setTanggalProses(String tglprocess){ this.TanggalProses = tglprocess;}
    public void setDurasi(String durasi){ this.Durasi = durasi;}
    public void setPetugase(String petugas){ this.Petugase = petugas;}
    public void setPenilaian(String penilaian){ this.Penilaian = penilaian;}
    public void setJamPis(String jamPis){ this.JamPis = jamPis;}
    public void setAvatar(Drawable img){ this.avatar = img;}
    public String getKeluhan(){ return  this.Keluhan;}
    public String getTanggalBoking(){ return this.TanggalBoking;}
    public String getPetugase(){ return this.Petugase;}
    public String getTanggalProses(){ return  this.TanggalProses;}
    public String getDurasi(){ return this.Durasi;}
    public String getPenilaian(){ return  this.Penilaian;}
    public String getJamPis(){ return  this.JamPis;}
    public Drawable getAvatar(){return  this.avatar;}
}
