package com.bijak.techno.oganlopian.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.activity.LaporandetailActivity;

/**
 * Created by HP on 5/11/2016.
 */

public class CustomAndroidGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private final String[] string;
    private final int[] Imageid;
    private final String[] kategoriid;

    public CustomAndroidGridViewAdapter(Context c,String[] string,int[] Imageid,String[] kategoriid ) {
        mContext = c;
        this.Imageid = Imageid;
        this.string = string;
        this.kategoriid = kategoriid;
    }

    @Override
    public int getCount() {
        return string.length;
    }

    @Override
    public Object getItem(int p) {
        return null;
    }

    @Override
    public long getItemId(int p) {
        return 0;
    }

    @Override
    public View getView(int p, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = new View(mContext);
            grid = inflater.inflate(R.layout.item_gridprofile, null);
            TextView textView = grid.findViewById(R.id.gridview_text);
            TextView kategoriID = grid.findViewById(R.id.kategoriid);
            ImageView imageView = grid.findViewById(R.id.gridview_image);
            textView.setText(string[p]);
            imageView.setImageResource(Imageid[p]);
            kategoriID.setText(kategoriid[p]);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, LaporandetailActivity.class);
                    i.putExtra("id", String.valueOf(Imageid[p]));
                    i.putExtra("text", string[p]);
                    i.putExtra("kategori_id",kategoriid[p]);
                    mContext.startActivity(i);
                }
            });
        } else {
            grid = convertView;
        }

        return grid;
    }
}
