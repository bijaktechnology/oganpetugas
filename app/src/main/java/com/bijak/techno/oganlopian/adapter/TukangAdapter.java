package com.bijak.techno.oganlopian.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.activity.TukangbookingActivity;
import com.bijak.techno.oganlopian.activity.TukangdetailActivity;
import com.bijak.techno.oganlopian.activity.TukangorderActivity;
import com.bijak.techno.oganlopian.model.TukangModel;
import com.bijak.techno.oganlopian.util.Constants;

import java.util.List;

public class TukangAdapter extends RecyclerView.Adapter<TukangAdapter.MyViewHolder> {
    private List<TukangModel> tukangModelList;
    public TukangAdapter(List<TukangModel> modelList){ this.tukangModelList = modelList;}
    @NonNull
    @Override
    public TukangAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tukang,parent,false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull TukangAdapter.MyViewHolder holder, int position) {
        TukangModel md = tukangModelList.get(position);
        holder.nama.setText(md.getNama_Tukang());
        holder.alamat.setText(md.getAlamat_Tukang());
        holder.harga.setText(md.getHarga_Jasa());
        holder.ratingBar.setRating(Float.parseFloat(md.getRating_Tukang()));
        Bitmap gbrlaporan = Constants.String2Bitmap(md.getAvatar());
        holder.profile.setImageBitmap(gbrlaporan);
        holder.tukangID.setText(md.getId_Tukang().toString());
        holder.viewprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TukangdetailActivity.class);
                intent.putExtra("id",md.getId_Tukang().toString());
                intent.putExtra("nama",holder.nama.getText());
                v.getContext().startActivity(intent);
            }
        });
        holder.btnorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TukangorderActivity.class);
                intent.putExtra("tukang_id",md.getId_Tukang());
                intent.putExtra("tukang_nama",holder.nama.getText());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tukangModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView profile;
        public Button viewprofile,btnorder;
        public TextView tukangID,nama,harga,alamat;
        public RatingBar ratingBar;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.nama_tukang);
            profile = itemView.findViewById(R.id.profile_avatar);
            alamat = itemView.findViewById(R.id.alamat_tukang);
            harga = itemView.findViewById(R.id.harga_jasa);
            ratingBar = itemView.findViewById(R.id.smallRatingBar);
            tukangID = itemView.findViewById(R.id.tukangid);
            viewprofile =(Button)itemView.findViewById(R.id.profile_tukang);

            btnorder =(Button)itemView.findViewById(R.id.btn_order);
        }
    }
}
