package com.bijak.techno.oganlopian.data;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.activity.LaporanmapsActivity;
import com.bijak.techno.oganlopian.util.Constants;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class CustomMarkerAdapter implements GoogleMap.InfoWindowAdapter {
    private LaporanmapsActivity context;

    public CustomMarkerAdapter(LaporanmapsActivity context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker arg0) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.markerlap_layout, null);
        ImageView imageView = v.findViewById(R.id.badge);
        LatLng latLng = arg0.getPosition();
        TextView tvLat = v.findViewById(R.id.title);
        TextView tvLng = v.findViewById(R.id.snippet);
        tvLat.setText(arg0.getTitle());
        tvLng.setText(arg0.getSnippet());
        Bitmap bmp = Constants.String2Bitmap(arg0.getSnippet());
        imageView.setImageBitmap(bmp);
        return v;
    }
}
