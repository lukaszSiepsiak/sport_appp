package com.example.npi.mobile.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.npi.mobile.AssetsHelper;
import com.example.npi.mobile.R;
import com.example.npi.mobile.adapters.TrainersAdapter;
import com.example.npi.mobile.adapters.VisitAdapter;
import com.example.npi.mobile.json.Trainer;
import com.example.npi.mobile.json.Visit;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class VisitFragment extends ListFragment {

    SharedPreferences sharedPreferences;
    private Visit[] visits;
    VisitAdapter visitAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_visit_list, container, false);

       new VisitTask(view.getContext()).execute();
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
    private class VisitTask extends AsyncTask<Void, Void, Void> {

        private String visitUrl;
        private HttpEntity visitEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<Visit[]> visitListEntity;
        private String bearerToken;
        private Context context;

        VisitTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            visitUrl = AssetsHelper.getServerUrl(context) + "/visit";
            bearerToken = sharedPreferences.getString("token", "null");
            headers = new HttpHeaders();
            headers.set("Authorization", bearerToken);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            visitEntity = new HttpEntity(headers);
            visitListEntity = restTemplate.exchange(visitUrl, HttpMethod.GET, visitEntity, Visit[].class);
            visits = visitListEntity.getBody();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            visitAdapter = new VisitAdapter(getContext(), Arrays.asList(visits));
            setListAdapter(visitAdapter);
        }
    }
}
