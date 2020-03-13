package com.bijak.techno.oganlopian.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.model.HistoryModel;

import java.util.List;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.MyViewHolder> {
    private List<HistoryModel> historyModels;
    public HistoryListAdapter(List<HistoryModel> modelelist){this.historyModels = modelelist;}
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_history,parent,false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HistoryModel md = historyModels.get(position);
        holder.nama.setText("Pasien : "+md.getNamaLengkap());
        holder.tglbooking.setText(md.getTanggalBoking());
        holder.tglprocess.setText(md.getTanggalProses());
        holder.petugas.setText(md.getPetugase());
        holder.petugas.setCompoundDrawablesWithIntrinsicBounds(md.getAvatar(),null,null,null);
        holder.keluhan.setText("Keluhan : "+md.getKeluhan());
        //holder.durasi.setText("Durasi Layanan : "+md.getDurasi());
        holder.ratinge.setRating(Float.parseFloat(md.getPenilaian()));
        holder.jampis.setText(md.getJamPis());
        /*if(md.getJenisKelamin().equals("Perempuan")){
            holder.profile.setImageResource(R.drawable.circled_user_female_48px);
        }else{
            holder.profile.setImageResource(R.drawable.circled_user_male_48px);
        }*/
    }

    @Override
    public int getItemCount() {
        return historyModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView profile;
        public RatingBar ratinge;
        public TextView nama,tglbooking,tglprocess,petugas,keluhan,durasi,jampis;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.namapasien);
            //profile = itemView.findViewById(R.id.avatare);
            tglbooking = itemView.findViewById(R.id.tglbooking);
            keluhan = itemView.findViewById(R.id.patient_keluhan);
            tglprocess = itemView.findViewById(R.id.tglprocess);
            petugas = itemView.findViewById(R.id.petugase);
            durasi = itemView.findViewById(R.id.durasi);
            ratinge = itemView.findViewById(R.id.penilaian);
            jampis = itemView.findViewById(R.id.jmpis);
        }
    }
}
