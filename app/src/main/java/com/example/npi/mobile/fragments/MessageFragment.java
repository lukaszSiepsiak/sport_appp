package com.example.npi.mobile.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.npi.mobile.AssetsHelper;
import com.example.npi.mobile.ConversationActivity;
import com.example.npi.mobile.R;
import com.example.npi.mobile.adapters.ConversationsAdapter;
import com.example.npi.mobile.adapters.TrainersAdapter;
import com.example.npi.mobile.json.AccountData;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class MessageFragment extends ListFragment {

    private SharedPreferences sharedPreferences;
    private ConversationsAdapter conversationsAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        new UsersTask(getContext()).execute();
        return inflater.inflate(R.layout.fragment_message,container,false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        AccountData data = (AccountData)l.getAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), ConversationActivity.class);
        intent.putExtra("from", data.getAccount().getEmail());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sharedPreferences = context.getSharedPreferences("account", MODE_PRIVATE);
    }

    private class UsersTask extends AsyncTask<Void, Void, Void> {

        private String url;
        private Context context;
        private HttpEntity httpEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<AccountData[]> responseEntity;
        private String bearerToken;
        private AccountData[] accounts;

        public UsersTask(Context context) {
            this.context = context;
            this.url = AssetsHelper.getServerUrl(context);
        }

        @Override
        protected void onPreExecute() {
            bearerToken = sharedPreferences.getString("token", "null");
            headers = new HttpHeaders();
            headers.set("Authorization", bearerToken);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
            httpEntity = new HttpEntity(headers);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("AA:", bearerToken);
            responseEntity = restTemplate.exchange(this.url + "/message/emails", HttpMethod.GET, httpEntity, AccountData[].class);
            accounts = responseEntity.getBody();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            conversationsAdapter = new ConversationsAdapter(getContext(), Arrays.asList(accounts));
            setListAdapter(conversationsAdapter);
        }
    }
}
