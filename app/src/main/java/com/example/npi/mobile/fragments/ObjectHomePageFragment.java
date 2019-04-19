package com.example.npi.mobile.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.npi.mobile.AssetsHelper;
import com.example.npi.mobile.ConversationActivity;
import com.example.npi.mobile.R;
import com.example.npi.mobile.json.SportObject;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;

import static android.content.Context.MODE_PRIVATE;

public class ObjectHomePageFragment extends Fragment {
    SharedPreferences sharedPreferences;
    private TextView sportObjectName, sportObjectCity, sportObjectProvince;
    private TextView postalCode;
    private TextView sportObjectEmail;
    private TextView sportObjectDescription;
    private ImageView imageView;
    private Button sendMessageButton;
    private FloatingActionButton floatCalendar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_object_home_page_activity,container,false);
        sportObjectName = view.findViewById(R.id.textViewObjectName);
        sportObjectCity = view.findViewById(R.id.textViewObjectCity);
        sportObjectProvince = view.findViewById(R.id.textViewObjectProvince);
        sportObjectEmail = view.findViewById(R.id.textViewObjectEmail);
        sportObjectDescription = view.findViewById(R.id.textViewObjectDescription);
        imageView = view.findViewById(R.id.imageViewObjectDataPage);
        sendMessageButton = view.findViewById(R.id.sendObjectMessage);
        floatCalendar = view.findViewById(R.id.floatObjectCalendar);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ConversationActivity.class);
                startActivity(intent);
            }
        });

        floatCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.relativeLayoutObjectHomePage,new CalendarFragment()).commit();
            }
        });
        new ObjectDataTask(view.getContext()).execute();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sharedPreferences = context.getSharedPreferences("account", MODE_PRIVATE);
    }


    private class ObjectDataTask extends AsyncTask<Void,Void,Void> {
        private String objectUrl;
        private String imageUrl;
        private HttpEntity objectEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<SportObject> objectResponseEntity;
        private String bearerToken;
        private Context context;
        private Bitmap image;

        ObjectDataTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            objectUrl = AssetsHelper.getServerUrl(context) + "/facility";
            imageUrl = AssetsHelper.getServerUrl(context) + "/api/image/";
            bearerToken = sharedPreferences.getString("token", "null");
            headers = new HttpHeaders();
            headers.set("Authorization", bearerToken);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            objectEntity = new HttpEntity(headers);
            objectResponseEntity = restTemplate.exchange(objectUrl, HttpMethod.GET, objectEntity, SportObject.class);
            image = null;
            try {
                InputStream in = new java.net.URL(imageUrl + objectResponseEntity.getBody().getAccount().getProfileImageUrl()).openStream();
                image = BitmapFactory.decodeStream(in);
            }catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            SportObject sportObject = objectResponseEntity.getBody();
            if (sportObject.getName() != null){
                sportObjectName.setText(sportObject.getName());
            }
            if (sportObject.getVoivodeship() != null){
                sportObjectProvince.setText(sportObject.getVoivodeship());
            }
            if (sportObject.getCity() != null){
                sportObjectCity.setText(sportObject.getCity());
            }
            if (sportObject.getAccount().getEmail() != null){
                sportObjectEmail.setText(sportObject.getAccount().getEmail());
            }
            if (image != null){
                imageView.setImageBitmap(image);
            }

        }

    }
}
