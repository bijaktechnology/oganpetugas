package com.bijak.techno.oganlopian.model;

public class DestinasiModel {
    String Gambar,Judul,Deskripsi,Distance,DataID,KategoriID;
    String Kategori,Lokasi,NamaLokasi,NamaKategori,AlamatLokasi;
    double Latitud,Longitud;
    public DestinasiModel(){

    }
    public DestinasiModel(String judule,String keterangan,String jarak,String img,String datax){
        this.Judul = judule;
        this.Deskripsi = keterangan;
        this.Distance = jarak;
        this.Gambar = img;
        this.DataID = datax;
    }
    public void setDataID(String dataID){ this.DataID = dataID;}
    public void setGambar(String gambar){ this.Gambar = gambar;}
    public void setJudul(String judul){ this.Judul = judul;}
    public void setDeskripsi(String deskripsi){ this.Deskripsi = deskripsi;}
    public void setDistance(String distance){ this.Distance = distance;}

    public String getDataID(){ return  this.DataID;}
    public String getGambar(){ return  this.Gambar;}
    public String getJudul(){ return  this.Judul;}
    public String getDeskripsi(){ return this.Deskripsi;}
    public String getDistance(){ return this.Distance;}

    /*buat lokasi penting*/
    public void setKategoriID(String kategoriID){this.KategoriID = kategoriID;}
    public void setKategori(String kategori){ this.Kategori = kategori;}
    public void setLokasi(String lokasi){ this.Lokasi = lokasi;}
    public void setNamaLokasi(String namaLokasi){ this.NamaLokasi=namaLokasi;}
    public void setNamaKategori(String namaKategori){ this.NamaKategori = namaKategori;}
    public void setAlamatLokasi(String alamat){ this.AlamatLokasi = alamat;}
    public void setLatitud(double lat){ this.Latitud = lat;}
    public void setLongitud(double lng){ this.Longitud = lng;}

    public String getKategori(){ return  this.Kategori;}
    public String getLokasi(){ return  this.Lokasi;}
    public String getNamaLokasi(){ return  this.NamaLokasi;}
    public String getNamaKategori(){ return this.NamaKategori;}
    public String getAlamatLokasi(){ return this.AlamatLokasi;}
    public double getLatitud(){ return this.Latitud;}
    public double getLongitud(){ return this.Longitud;}

}
