package com.bijak.techno.oganlopian.model;

/**
 * Created by Zetro on 30/07/2017.
 */

public class UsersModel {
    String username,password,email,namalengkap,notelp,usertype,userrole,keycode,devicetoken,wilayahkerja,jamkerja;
    String avatar,statuspegawai,createdate,jksName,alamat,tgl_lahir,nama_ibu,nik,kartu_keluarga;
    Integer uid,dinasid,rsid,avatar2,jksID;

    public void setUid(Integer uid){this.uid=uid;}
    public void setUserName(String username){this.username=username;}
    public void setPassword(String password){ this.password=password;}
    public void setEmail(String email){this.email=email;}
    public void setNamaLengkap(String namalengkap){ this.namalengkap = namalengkap;}
    public void setNoTelp(String notelp){this.notelp= notelp;}
    public void setUserType(String usertype){ this.usertype = usertype;}
    public void setUserRole(String userrole){ this.userrole = userrole;}
    public void setKeyCode(String keycode){ this.keycode = keycode;}
    public void setDevicetoken(String devicetoken){ this.devicetoken = devicetoken;}
    public void setWilayahKerja(String wilayahkerja){ this.wilayahkerja = wilayahkerja;}
    public void setJamKerja(String jamkerja){ this.jamkerja = jamkerja;}
    public void setAvatar(String avatar){ this.avatar=avatar;}
    public void setStatusPegawai(String statusPegawai){ this.statuspegawai = statusPegawai;}
    public void setDinasId(Integer dinasid){ this.dinasid = dinasid;}
    public void setRsId(Integer rsId){ this.rsid = rsId;}
    public void setAvatar2(Integer avatar2){ this.avatar2 = avatar2;}
    public void setCreateDate(String createdate){ this.createdate = createdate;}
    public void setUsername(String usernamed){ this.username=usernamed;}
    public void setAlamat(String address){ this.alamat = address;}
    public void setNama_ibu(String namaibu){ this.nama_ibu=namaibu;}
    public void setTgl_lahir(String tgllahir){ this.tgl_lahir = tgllahir;}
    public void setNik(String ktp){ this.nik = ktp;}
    public void setKartu_keluarga(String nokk){ this.kartu_keluarga=nokk;}

    public Integer getUid(){ return uid;}
    public String getUserName(){ return  username;}
    public String getPassword(){ return  password;}
    public String getEmail() { return  email;}
    public String getNamaLengkap(){ return namalengkap;}
    public String getNoTelp(){ return  notelp;}
    public String getUserType(){ return usertype;}
    public String getUserRole(){ return userrole;}
    public String getKeyCode(){ return keycode;}
    public String getDeviceToken(){ return devicetoken;}
    public String getWilayahKerja(){ return wilayahkerja;}
    public String getJamKerja(){ return jamkerja;}
    public String getAvatar(){ return avatar;}
    public String getStatusPegawai(){ return statuspegawai;}
    public Integer getDinasId(){ return dinasid;}
    public Integer getRsId(){ return rsid;}
    public Integer getAvatar2(){ return avatar2;}
    public String getCreatedDate() { return createdate; }
    public String getUsername(){ return  username;}
    public String getAlamat(){ return  alamat;}
    public String getNama_ibu(){ return nama_ibu;}
    public String getTgl_lahir(){ return tgl_lahir;}
    public String getNik(){ return nik;}
    public String getKartu_keluarga(){ return kartu_keluarga;}

}
