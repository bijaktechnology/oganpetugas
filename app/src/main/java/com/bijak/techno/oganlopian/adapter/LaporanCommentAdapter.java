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
import com.bijak.techno.oganlopian.util.Constants;

import java.util.List;

public class LaporanCommentAdapter extends RecyclerView.Adapter<LaporanCommentAdapter.MyViewHolder> {
    private List<LaporanModel> modelList;

    public LaporanCommentAdapter(List<LaporanModel> modelelist){
        this.modelList = modelelist;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_laporan_comment,parent,false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LaporanModel md = modelList.get(position);
        Bitmap gbrlaporan = Constants.String2Bitmap(md.getGambarLaporan());
        holder.avatar.setImageBitmap(gbrlaporan);
        holder.commentor.setText(md.getNamaLengkap());
        holder.comment_content.setText(md.getComment());
        holder.tanggal.setText(md.getTanggal());
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView commentor,tanggal,comment_content;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar =(ImageView) itemView.findViewById(R.id.item_report_comment_img_user);
            commentor =(TextView) itemView.findViewById(R.id.item_report_comment_txt_user);
            tanggal =(TextView)itemView.findViewById(R.id.item_report_comment_txt_time);
            comment_content = (TextView)itemView.findViewById(R.id.item_report_comment_txt_comment);
        }
    }
}
