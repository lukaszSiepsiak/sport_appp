package com.example.npi.mobile.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.npi.mobile.AssetsHelper;
import com.example.npi.mobile.R;
import com.example.npi.mobile.adapters.SportObjectAdapter;
import com.example.npi.mobile.json.SportObject;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class ObjectsFragment extends ListFragment implements AdapterView.OnItemClickListener {

    SharedPreferences sharedPreferences;
    private SportObject[] sportObjectList;
    SportObjectAdapter sportObjectAdapter;
    private EditText searchEditText;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.object_fragment_list,container,false);
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
                sportObjectAdapter.getFilter().filter(text);
            }
        });
        new ObjectTask(view.getContext()).execute();
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Toast.makeText(getActivity(), "Item: " + position,Toast.LENGTH_SHORT).show();
    }

    private class ObjectTask extends AsyncTask<Void,Void,Void>{

        private String sportObjectUrl;
        private HttpEntity sportObjectEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<SportObject[]> sportObjectListEntity;
        private String bearerToken;
        private Context context;

    ObjectTask(Context context){ this.context = context;}

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        sportObjectUrl = AssetsHelper.getServerUrl(context) + "/facility";
        bearerToken = sharedPreferences.getString("token",null);
        headers = new HttpHeaders();
        headers.set("Authorization",bearerToken);
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
    }

    @Override
    protected Void doInBackground(Void... voids) {
        sportObjectEntity = new HttpEntity(headers);
        sportObjectListEntity = restTemplate.exchange(sportObjectUrl,HttpMethod.GET,sportObjectEntity,SportObject[].class);
        sportObjectList = sportObjectListEntity.getBody();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        sportObjectAdapter = new SportObjectAdapter(getContext(), Arrays.asList(sportObjectList));
        setListAdapter(sportObjectAdapter);
    }
}

}
