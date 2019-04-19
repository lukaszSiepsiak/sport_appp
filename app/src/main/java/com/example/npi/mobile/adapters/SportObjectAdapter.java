package com.example.npi.mobile.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.npi.mobile.AssetsHelper;
import com.example.npi.mobile.R;
import com.example.npi.mobile.json.SportObject;
import com.example.npi.mobile.json.Trainer;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SportObjectAdapter extends BaseAdapter implements Filterable {

    Context context;
    LayoutInflater inflater;
    private List<SportObject> sportObjectList;
    private List<SportObject> filteredList;
    private String imageUrl;

    public SportObjectAdapter(Context context, List<SportObject> sportObjectList){
        this.context = context;
        this.sportObjectList = sportObjectList;
        this.filteredList = sportObjectList;
        this.imageUrl = AssetsHelper.getServerUrl(context) + "/api/image/";
        inflater = LayoutInflater.from(context);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<SportObject> list = new ArrayList<>();
                if (charSequence.length() == 0) {
                    list.addAll(sportObjectList);
                } else {
                    list.clear();
                    for (SportObject sportObject : sportObjectList) {
                        if (sportObject.getName().toLowerCase().contains(charSequence)
                                || sportObject.getCity().toLowerCase().contains(charSequence)) {
                            list.add(sportObject);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.count = list.size();
                results.values = list;
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (List<SportObject>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public SportObject getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        if (view == null) {
            view = inflater.inflate(R.layout.object_list_item, null);
        }
        TextView name = view.findViewById(R.id.listItemSportObjectName);
        TextView city = view.findViewById(R.id.listItemSportObjectCity);
        ImageView image = view.findViewById(R.id.listItemSportObjectImage);
        SportObject current = filteredList.get(position);
        name.setText(current.getName());
        city.setText(current.getCity());
        if(current.getIcon() != null) {
            image.setImageBitmap(current.getIcon());
        } else {
            new DownloadIconTask(imageUrl, image, current).execute();
        }
        return view;
    }

    private class DownloadIconTask extends AsyncTask<Void,Void,Void> {
        private String url;
        private ImageView image;
        private SportObject sportObject;
        private Bitmap icon;

        public DownloadIconTask(String url, ImageView image, SportObject sportObject) {
            this.url = url;
            this.image = image;
            this.sportObject = sportObject;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            icon = null;
            try {
                InputStream inputStream = new java.net.URL(url + sportObject.getAccount().getProfileImageUrl()).openStream();
                icon = BitmapFactory.decodeStream(inputStream);
                sportObject.setIcon(icon);
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            image.setImageBitmap(icon);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }
}
