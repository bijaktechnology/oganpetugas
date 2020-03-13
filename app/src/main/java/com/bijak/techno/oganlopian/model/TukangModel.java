package com.bijak.techno.oganlopian.model;

public class TukangModel {
    private String Id_Tukang,Nama_Tukang,Alamat_Tukang,Harga_Jasa,Avatars;
    private String Rating_Tukang;

    public void setId_Tukang(String tukang_id){ this.Id_Tukang = tukang_id; }
    public void setNama_Tukang(String nama_Tukang){ this.Nama_Tukang=nama_Tukang;}
    public void setAlamat_Tukang(String alamat_Tukang){this.Alamat_Tukang=alamat_Tukang;}
    public void setHarga_Jasa(String harga_Jasa){ this.Harga_Jasa = harga_Jasa;}
    public void setRating_Tukang(String rating_Tukang){ this.Rating_Tukang= rating_Tukang;}
    public void setAvatar(String avatar){this.Avatars = avatar;}
    public String getAvatar(){ return this.Avatars;}
    public String getId_Tukang(){ return this.Id_Tukang;}
    public String getNama_Tukang(){ return this.Nama_Tukang;}
    public String getAlamat_Tukang(){ return this.Alamat_Tukang;}
    public String getHarga_Jasa(){ return this.Harga_Jasa;}
    public String getRating_Tukang(){ return this.Rating_Tukang;}
}
