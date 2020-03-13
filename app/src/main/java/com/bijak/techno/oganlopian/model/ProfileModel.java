package com.bijak.techno.oganlopian.model;

public class ProfileModel {
    String NamaLengkap,Alamat,Kecamatan,Kelurahan,RT,RW,NoHP,JenisKelamin,TempatLahir,TglLahir;
    String NIK,KK,Hubungan,NamaIbuKandung;
    public ProfileModel(){

    }
    public ProfileModel(String namaLengkap,String NiK,String HubungaN,String sex){
        this.NamaLengkap = namaLengkap;
        this.NIK =NiK;
        this.Hubungan = HubungaN;
        this.JenisKelamin = sex;
    }
    //Profile umum

    public void setNamaLengkap(String namaLengkap){ this.NamaLengkap = namaLengkap;}
    public void setAlamat(String alamat){ this.Alamat = alamat;}
    public void setKecamatan(String kecamatan){ this.Kecamatan = kecamatan;}
    public void setKelurahan(String kelurahan){ this.Kelurahan = kelurahan;}
    public void setRT(String rt){ this.RT = rt;}
    public void setRW(String rw){ this.RW = rw;}
    public void setNoHP(String noHP){ this.NoHP = noHP;}
    public void setJenisKelamin(String jenisKelamin){ this.JenisKelamin = jenisKelamin;}
    public void setTempatLahir(String tempatLahir){ this.TempatLahir = tempatLahir;}
    public void setTglLahir(String tglLahir){ this.TglLahir = tglLahir;}
    public void setNIK(String nik){ this.NIK = nik;}
    public void setKK(String kk){ this.KK = kk;}
    public void setHubungan(String hubungan){ this.Hubungan = hubungan;}
    public void setNamaIbuKandung(String namaIbuKandung){ this.NamaIbuKandung = namaIbuKandung;}

    public String getNamaLengkap(){ return  this.NamaLengkap;}
    public String getAlamat() { return  this.Alamat;}
    public String getKecamatan(){ return  this.Kecamatan;}
    public String getKelurahan(){ return this.Kelurahan;}
    public String getRT(){ return this.RT;}
    public String getRW(){ return this.RW;}
    public String getNoHP(){ return this.NoHP;}
    public String getJenisKelamin(){ return this.JenisKelamin;}
    public String getTempatLahir(){ return  this.TempatLahir;}
    public String getTglLahir(){ return  this.TglLahir;}
    public String getNIK(){ return this.NIK;}
    public String getKK(){ return this.KK;}
    public String getHubungan(){ return this.Hubungan;}
    public String getNamaIbuKandung(){ return this.NamaIbuKandung;}
}
