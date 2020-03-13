package com.bijak.techno.oganlopian.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.model.ProfileModel;

import java.util.List;

public class FamilyListAdapter extends RecyclerView.Adapter<FamilyListAdapter.MyViewHolder> {
    private List<ProfileModel> modelList;
    public FamilyListAdapter(List<ProfileModel> modelelist){
        this.modelList = modelelist;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_familylist,parent,false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            ProfileModel md = modelList.get(position);
            holder.nama.setText(md.getNamaLengkap());
            holder.nik.setText(md.getNIK());
            holder.status_hubungan.setText(md.getHubungan());
            if(md.getJenisKelamin().equals("Perempuan")){
                holder.profile.setImageResource(R.drawable.circled_user_female_48px);
            }else{
                holder.profile.setImageResource(R.drawable.circled_user_male_48px);
            }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyViewHolder extends ViewHolder {
        public ImageView profile;
        public TextView nama,nik,status_hubungan;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.name);
            profile = itemView.findViewById(R.id.profile_avatar);
            nik = itemView.findViewById(R.id.nike);
            status_hubungan = itemView.findViewById(R.id.statushubkel);
        }
    }
}
