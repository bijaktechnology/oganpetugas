package com.bijak.techno.oganlopian.model;

public class Umume {
    private String jksName;
    private String jksID,bidan_id,userID,waktuBooking,nikPasien,namaPasien,keluhanPasien,layananPasien;
    private String booking_id, catatan_petugas,keterangan,penilaian;
    private double LatiTuded,LongiTuded;
    public Umume(){
        super();
    }
    public Umume(String ID, String Nama){
        this.jksID = ID;
        this.jksName = Nama;
    }
    public void setJksID(String jksid){
        this.jksID=jksid;
    }
    public  String getJksID(){
        return this.jksID;
    }
    public void setJksName(String jksNama){
        this.jksName=jksNama;
    }
    public String getJksName(){
        return this.jksName;
    }
    public void setBooking_id(String bookingId){this.booking_id=bookingId;}
    public void setCatatan_petugas(String catatanPetugas){ this.catatan_petugas=catatanPetugas;}
    public void setBidan_id(String bidanId){this.bidan_id=bidanId;}
    public void setKeterangan(String ket){this.keterangan=ket;}
    public void setPenilaian(String penilaianx){this.penilaian=penilaianx;}
    public String getBidan_id(){ return  this.bidan_id;}
    public String getBooking_id(){ return this.booking_id;}
    public String getCatatan_petugas(){ return this.catatan_petugas;}
    public String getKeterangan(){ return this.keterangan;}
    public String getPenilaian(){ return  this.penilaian;}
    @Override
    public String toString() {
        return jksName;
    }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Umume){
            Umume c = (Umume )obj;
            return c.getJksName().equals(jksName) && c.getJksID() == jksID;
        }

        return false;
    }
    //booking
    public void setUserID(String user_id){this.userID = user_id; }
    public void setWaktuBooking(String waktuBooking1){ this.waktuBooking = waktuBooking1;}
    public void setLatiTuded(double lat){ this.LatiTuded = lat; }
    public void setLongiTuded(double lng){ this.LongiTuded = lng;}
    public void setNikPasien(String nik){ this.nikPasien = nik;}
    public void setNamaPasien(String nama){ this.namaPasien = nama;}
    public void setKeluhanPasien(String keluhan){ this.keluhanPasien = keluhan; }
    public void setLayananPasien(String layanan){ this.layananPasien = layanan; }
    public String getUserID(){ return  this.userID;}
    public String getWaktuBooking(){ return this.waktuBooking;}
    public String getNikPasien(){ return  this.nikPasien;}
    public String getNamaPasien(){ return this.namaPasien;}
    public String getKeluhanPasien(){ return this.keluhanPasien;}
    public String getLayananPasien(){ return this.layananPasien;}
    public double getLatiTuded(){ return this.LatiTuded;}
    public double getLongiTuded(){ return this.LongiTuded;}
    //
    private String PetugasID,Latid,Longid,Catatan;
    public void setPetugasID(String petugasID){this.PetugasID=petugasID;}
    public void setLatid(String lati){this.Latid = lati;}
    public void setLongid(String longi){ this.Longid = longi;}
    public void setCatatan(String catatan){ this.Catatan = catatan;}
    public String getCatatan(){ return this.Catatan;}
    public String getPetugasID(){ return this.PetugasID;}
    public String getLatid(){ return this.Latid;}
    public String getLongid(){ return this.Longid;}
}
