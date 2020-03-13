package com.bijak.techno.oganlopian.data;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bijak.techno.oganlopian.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.lang.reflect.Array;

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Context context;
    public InfoWindowAdapter(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }


    @Override
    public View getInfoContents(Marker arg0) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v =  inflater.inflate(R.layout.marker_layout, null);
        ImageView imageView = v.findViewById(R.id.badge);
        LatLng latLng = arg0.getPosition();
        TextView tvLat = v.findViewById(R.id.title);
        TextView tvLng = v.findViewById(R.id.snippet);
        tvLat.setText("Nama : " + arg0.getTitle());
        tvLng.setText("Lokasi : "+ arg0.getSnippet());
        String[] arr = arg0.getTitle().split(" ");
        switch (arr[0]){
            case "Bidan":imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bidan));break;
            case "Dokter":imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_doctor));break;
            case "Petugas":imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_ambulance));break;
            default:imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_profile53));break;
        }

        return v;
    }


    public void onInfoWindowClick(Marker arg0){
        Toast.makeText(context,arg0.getTitle(), Toast.LENGTH_LONG).show();
        //Intent tangani =new Intent(context, NotificationsActivity.class);
        //tangani.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //context.startActivity(tangani);
    }
}

