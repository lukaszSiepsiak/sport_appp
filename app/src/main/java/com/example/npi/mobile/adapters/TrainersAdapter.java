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
import com.example.npi.mobile.fragments.CalendarFragment;
import com.example.npi.mobile.json.Trainer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TrainersAdapter extends BaseAdapter implements Filterable {

    Context context;
    LayoutInflater inflater;
    private List<Trainer> trainerList;
    private List<Trainer> filteredList;
    private String imageUrl;

    public TrainersAdapter(Context context, List<Trainer> trainerList){
        this.context = context;
        this.trainerList = trainerList;
        this.filteredList = trainerList;
        this.imageUrl = AssetsHelper.getServerUrl(context) + "/api/image/";
        inflater = LayoutInflater.from(context);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Trainer> list = new ArrayList<>();
                if (charSequence.length() == 0) {
                    list.addAll(trainerList);
                } else {
                    list.clear();
                    for (Trainer trainer : trainerList) {
                        if (trainer.getFirstName().toLowerCase().contains(charSequence)
                                || trainer.getLastName().toLowerCase().contains(charSequence) || trainer.getCity().toLowerCase().contains(charSequence)) {
                            list.add(trainer);
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
                filteredList = (List<Trainer>)filterResults.values;
                notifyDataSetChanged();

            }
        };
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Trainer getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        if (view == null) {
            view = inflater.inflate(R.layout.trainer_list_item, null);
        }
        TextView firstName = view.findViewById(R.id.firstName);
        TextView lastName = view.findViewById(R.id.lastName);
        TextView city = view.findViewById(R.id.city);
        ImageView image = view.findViewById(R.id.listItemCoachImage);
        Trainer current = filteredList.get(position);
        firstName.setText(current.getFirstName());
        lastName.setText(current.getLastName());
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
        private Trainer trainer;
        private Bitmap icon;

        public DownloadIconTask(String url, ImageView image, Trainer trainer) {
            this.url = url;
            this.image = image;
            this.trainer = trainer;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            icon = null;
            try {
                InputStream in = new java.net.URL(url + trainer.getAccount().getProfileImageUrl()).openStream();
                icon = BitmapFactory.decodeStream(in);
                trainer.setIcon(icon);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            image.setImageBitmap(icon);
        }
    }
}

