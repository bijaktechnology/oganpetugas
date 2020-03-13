package com.bijak.techno.oganlopian.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.model.ProfileModel;
import java.util.ArrayList;
import java.util.List;


public class AutoCompleteAdapter extends ArrayAdapter<ProfileModel> {
    ArrayList<ProfileModel> customers, tempCustomer, suggestions;

    public AutoCompleteAdapter(Context context, ArrayList<ProfileModel> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        this.customers = objects;
        this.tempCustomer = new ArrayList<ProfileModel>(objects);
        this.suggestions = new ArrayList<ProfileModel>(objects);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProfileModel customer = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_familylist, parent, false);
        }
        ImageView avatar = convertView.findViewById(R.id.profile_avatar);
        TextView nama_lengkap = convertView.findViewById(R.id.name);
        TextView nik = convertView.findViewById(R.id.nike);
        TextView hubungan = convertView.findViewById(R.id.statushubkel);
        nama_lengkap.setText(customer.getNamaLengkap());
        nik.setText(customer.getNIK());
        hubungan.setText(customer.getHubungan());
        if(customer.getJenisKelamin().equals("Perempuan")){
            avatar.setImageResource(R.drawable.circled_user_female_24px);
        }else{
            avatar.setImageResource(R.drawable.circled_user_male_24px);
        }
        return convertView;
    }


    @Override
    public Filter getFilter() {
        return myFilter;
    }

    Filter myFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            ProfileModel customer = (ProfileModel) resultValue;
            return customer.getNamaLengkap();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (ProfileModel people : tempCustomer) {
                    if (people.getNamaLengkap().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(people);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<ProfileModel> c = (ArrayList<ProfileModel>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (ProfileModel cust : c) {
                    add(cust);
                    notifyDataSetChanged();
                }
            }
        }
    };

}
