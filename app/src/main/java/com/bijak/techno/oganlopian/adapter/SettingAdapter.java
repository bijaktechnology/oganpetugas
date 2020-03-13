package com.bijak.techno.oganlopian.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.model.SettingModel;

import java.util.List;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.MyViewHolder> {
    private List<SettingModel> menulist;

    public SettingAdapter(List<SettingModel> menulist){
         this.menulist=menulist;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_setting,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SettingModel model = menulist.get(position);
        holder.mnuTitle.setText(model.getMenuTitle());
    }

    @Override
    public int getItemCount() {
        return menulist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mnuTitle;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mnuTitle = itemView.findViewById(R.id.item_settings_txt);
        }
    }
}
