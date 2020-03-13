package com.bijak.techno.oganlopian.data;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bijak.techno.oganlopian.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.sql.Array;
import java.util.Arrays;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity context;

    public CustomInfoWindowAdapter(Activity context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker arg0) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.marker_layout, null);
        ImageView imageView = v.findViewById(R.id.badge);
        LatLng latLng = arg0.getPosition();
        TextView tvLat = v.findViewById(R.id.title);
        TextView tvLng = v.findViewById(R.id.snippet);
        RatingBar ratingBar = v.findViewById(R.id.penilaian);

        tvLat.setText("Nama : " + arg0.getTitle());
        if(arg0.getSnippet()!=null) {
            /*if(arg0.getSnippet().substring(0,2).equals("xx")){
                tvLng.setText(arg0.getSnippet().substring(3));
            }else {
                tvLng.setText("Lokasi : " + arg0.getSnippet());
            }*/
            switch (arg0.getSnippet().substring(0,2)){
                case "xx":
                    tvLng.setText(arg0.getSnippet().substring(3));
                    break;
                case "R:":
                    String[] arr = arg0.getSnippet().split(":");
                    if(arr.length>1){
                        if(!arr[1].contains("null")) {
                            float d = Float.valueOf(arr[1]);
                            ratingBar.setRating(d);
                        }
                    }
                    break;
                default:
                    tvLng.setText("Lokasi : " + arg0.getSnippet());
                    break;
            }
        }
        String[] arr = arg0.getTitle().split(" ");
        switch (arr[0]) {
            case "Bidan":
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bidan));
                break;
            case "Dokter":
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_doctor));
                break;
            case "Petugas":
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_ambulance));
                break;
            default:
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_profile53));
                break;
        }

        return v;
    }
}
