package com.example.npi.mobile.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.npi.mobile.AssetsHelper;
import com.example.npi.mobile.R;
import com.example.npi.mobile.adapters.TrainersAdapter;
import com.example.npi.mobile.json.Trainer;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class TrainersFragment extends ListFragment {

    SharedPreferences sharedPreferences;
    private Trainer[] trainerList;
    TrainersAdapter coachAdapter;
    private EditText searchEditText;
    ListView listView;

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {



    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trainers_list, container, false);
        searchEditText = view.findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = searchEditText.getText().toString().toLowerCase(Locale.getDefault());
                coachAdapter.getFilter().filter(text);
            }
        });


        new TrainersTask(view.getContext()).execute();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sharedPreferences = context.getSharedPreferences("account", MODE_PRIVATE);
    }

    private class TrainersTask extends AsyncTask<Void, Void, Void> {

        private String coachesUrl;
        private HttpEntity trainerEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<Trainer[]> coachListEntity;
        private String bearerToken;
        private Context context;

        TrainersTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            coachesUrl = AssetsHelper.getServerUrl(context) + "/coaches";
            bearerToken = sharedPreferences.getString("token", "null");
            headers = new HttpHeaders();
            headers.set("Authorization", bearerToken);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            trainerEntity = new HttpEntity(headers);
            coachListEntity = restTemplate.exchange(coachesUrl, HttpMethod.GET, trainerEntity, Trainer[].class);
            trainerList = coachListEntity.getBody();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            coachAdapter = new TrainersAdapter(getContext(), Arrays.asList(trainerList));
            setListAdapter(coachAdapter);
        }
    }
}
