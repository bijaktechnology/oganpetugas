package com.bijak.techno.oganlopian.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.activity.LokasipentingActivity;
import com.bijak.techno.oganlopian.activity.MenuActivity;
import com.bijak.techno.oganlopian.model.MenuModel;

import java.util.List;

public class MenuDepanAdapter extends RecyclerView.Adapter<MenuDepanAdapter.MyViewHolder> {
    private List<MenuModel> menuModelList;
    private Context mContext;
    public MenuDepanAdapter(Context context,List<MenuModel> menuModels){
        this.menuModelList=menuModels;
        this.mContext = context;
    }
    @NonNull
    @Override
    public MenuDepanAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menumain,parent,false);
        return new MenuDepanAdapter.MyViewHolder(itemList);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MenuModel md = menuModelList.get(position);
        holder.avatar.setImageResource(md.getIcone());
        holder.judul.setText(md.getMenu_name());
        holder.menu_id.setText(md.getMenu_id());
        /*holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext,md.getMenu_name(),Toast.LENGTH_LONG).show();

            }
        });*/
    }
    @Override
    public int getItemCount() {
        return menuModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView judul,menu_id;
        private CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.book_img_id);
            judul = itemView.findViewById(R.id.book_title_id);
            menu_id = itemView.findViewById(R.id.menu_id);
            cardView = itemView.findViewById(R.id.cardview_id);
        }
    }
}
