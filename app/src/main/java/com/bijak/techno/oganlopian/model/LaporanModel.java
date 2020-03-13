package com.bijak.techno.oganlopian.model;

public class LaporanModel extends UsersModel {
    String loc_id,longitud,latitud,petugas_id,judul,gambarLaporan,laporanID;
    String comment, tanggal,kategori,keterangan,likes,maps, favorite;

    public void setLoc_id(String id){this.loc_id=id;}
    public void setLongitud(String lng){ this.longitud=lng;}
    public void setLatitud(String lat){ this.latitud=lat;}
    public void setPetugas_id(String uid){ this.petugas_id=uid;}

    public String getLoc_id(){ return  this.loc_id;}
    public String getLongitud(){ return  this.longitud;}
    public String getLatitud(){ return  this.latitud;}
    public String getPetugas_id(){ return  this.petugas_id;}

    public LaporanModel(){
        super();
    }
    public LaporanModel(String id, String lng, String lat,String uid){
        this.loc_id = id;
        this.longitud = lng;
        this.latitud = lat;
        this.petugas_id = uid;
    }
    //laporan warga
    public void setLaporanID(String lapid){ this.laporanID=lapid;}
    public void setJudul(String title){ this.judul=title;}
    public void setComment(String cmt){this.comment=cmt;}
    public void setTanggal(String tgl){this.tanggal=tgl;}
    public void setKategori(String ktg){this.kategori=ktg;}
    public void setKeterangan(String ket){ this.keterangan=ket;}
    public void setLikes(String like){ this.likes=like;}
    public void setMaps(String map){ this.maps=map;}
    public void setGambarLaporan(String img){ this.gambarLaporan=img;}

    public String getComment(){ return this.comment;}
    public String getTanggal(){ return this.tanggal;}
    public String getKategori(){ return this.kategori;}
    public String getKeterangan(){ return this.keterangan;}
    public String getLikes(){ return this.likes;}
    public String getMaps(){ return this.maps;}
    public String getFavorite(){ return this.favorite;}
    public String getJudul(){ return this.judul;}
    public String getGambarLaporan(){return this.gambarLaporan;}
    public String getLaporanID(){ return this.laporanID;}
}
