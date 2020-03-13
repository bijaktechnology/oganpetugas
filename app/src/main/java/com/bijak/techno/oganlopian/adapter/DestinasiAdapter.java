package com.bijak.techno.oganlopian.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.model.DestinasiModel;

import java.util.List;

public class DestinasiAdapter extends RecyclerView.Adapter<DestinasiAdapter.MyViewHolder> {
    private List<DestinasiModel> destinasiModels;
    public DestinasiAdapter(List<DestinasiModel> modelelist){this.destinasiModels = modelelist; }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_destinasi,parent,false);
        return new MyViewHolder(itemList);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DestinasiModel hm = destinasiModels.get(position);
        holder.Distance.setText(hm.getDistance());
        holder.Titel.setText(hm.getJudul());
        holder.Descripsi.setText(hm.getDeskripsi());
        final String encodedString = hm.getGambar();
        final String pureBase64Encoded = encodedString.substring(encodedString.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        holder.imageView.setImageBitmap(decodedBitmap);
        holder.Dataid.setText(hm.getDataID());
    }
    @Override
    public int getItemCount() {
        return destinasiModels.size();
    }
    public class MyViewHolder extends ViewHolder {
        public ImageView imageView;
        public TextView Titel,Descripsi,Distance,Dataid;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.item_tours_img);
            Titel = itemView.findViewById(R.id.item_tours_txt_title);
            Descripsi = itemView.findViewById(R.id.item_tours_txt_description);
            Distance = itemView.findViewById(R.id.item_tours_txt_distance);
            Dataid = itemView.findViewById(R.id.dataid);
        }
    }
}
