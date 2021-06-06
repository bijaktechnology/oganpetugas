package com.bijak.techno.oganlopian.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.model.LaporanModel;
import com.bijak.techno.oganlopian.model.ProfileModel;
import com.bijak.techno.oganlopian.util.Constants;

import java.util.List;

public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.MyViewHolder> {
    private List<LaporanModel> modelList;

    public LaporanAdapter(List<LaporanModel> modelelist){
        this.modelList = modelelist;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_laporanwarga,parent,false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LaporanModel md = modelList.get(position);
        holder.nama.setText(md.getNamaLengkap());
        Bitmap gbrlaporan = Constants.String2Bitmap(md.getGambarLaporan());

        holder.profile.setImageBitmap(gbrlaporan);
        holder.keterangan.setText(md.getJudul());
        holder.kategori.setText(md.getKategori());
        holder.alamat.setText(md.getAlamat());
        holder.tanggal.setText(md.getTanggal());
        holder.komen.setText(md.getComment());
        holder.suka.setText(md.getLikes());
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView profile,kemap,kefavorit,imgkategori;
        public TextView nama,alamat,tanggal,keterangan,suka,komen,kategori;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.namapelapor);
            profile = itemView.findViewById(R.id.imglaporan);
            tanggal = itemView.findViewById(R.id.tglaporan);
            keterangan = itemView.findViewById(R.id.judul);
            kategori =itemView.findViewById(R.id.kategori);
            alamat = itemView.findViewById(R.id.alamat);
            kemap = itemView.findViewById(R.id.lihatdimap);
            kefavorit = itemView.findViewById(R.id.bookmark);
            suka = itemView.findViewById(R.id.likes);
            komen = itemView.findViewById(R.id.comment);
        }
    }
}
