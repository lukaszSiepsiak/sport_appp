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
import com.example.npi.mobile.json.Trainer;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;

import static android.content.Context.MODE_PRIVATE;

public class TrainerHomePageFragment extends Fragment {
    private static int RESULT_LOAD_IMAGE = 1;
    SharedPreferences sharedPreferences;
    private ImageView imageView;
    private Button sendMessageButton;
    private TextView firstName;
    private TextView lastName;
    private TextView email;
    private TextView region;
    private TextView city;
    private FloatingActionButton floatCalendar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trainer_home_page_activity, container, false);
        firstName = view.findViewById(R.id.textViewTrainerRegisterFirstName);
        lastName = view.findViewById(R.id.textViewTrainerRegisterLastName);
        email = view.findViewById(R.id.textViewTrainerRegisterEmail);
        region = view.findViewById(R.id.textViewTrainerRegion);
        city = view.findViewById(R.id.textViewTrainerCity);
        imageView = view.findViewById(R.id.imageViewTrainerDataPage);
        sendMessageButton = view.findViewById(R.id.sendTrainerMessage);
        floatCalendar = view.findViewById(R.id.floatTrainerCalendar);

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
                getFragmentManager().beginTransaction().replace(R.id.relativeLayoutTrainerHomePage,new CalendarFragment()).commit();
            }
        });
        new TrainerDataTask(view.getContext()).execute();
        return view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sharedPreferences = context.getSharedPreferences("account", MODE_PRIVATE);
    }

    private class TrainerDataTask extends AsyncTask<Void,Void,Void> {
        private String coachUrl;
        private HttpEntity userEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<Trainer> userResponseEntity;
        private String bearerToken;
        private Bitmap image;
        private String imageUrl;
        private Context context;

        TrainerDataTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            coachUrl = AssetsHelper.getServerUrl(context) + "/coach";
            imageUrl = AssetsHelper.getServerUrl(context) + "/api/image/";
            bearerToken = sharedPreferences.getString("token", "null");
            headers = new HttpHeaders();
            headers.set("Authorization", bearerToken);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            userEntity = new HttpEntity(headers);
            userResponseEntity = restTemplate.exchange(coachUrl, HttpMethod.GET,userEntity,Trainer.class);
            image = null;
            try {
                InputStream in = new java.net.URL(imageUrl + userResponseEntity.getBody().getAccount().getProfileImageUrl()).openStream();
                image = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Trainer trainer = userResponseEntity.getBody();
            if(trainer.getFirstName() != null) {
                firstName.setText(trainer.getFirstName());
            }
            if(trainer.getLastName() != null) {
                lastName.setText(trainer.getLastName());
            }
            if(trainer.getVoivodeship() != null) {
                region.setText(trainer.getVoivodeship());
            }
            if(trainer.getCity() != null) {
                city.setText(trainer.getCity());
            }
            if(trainer.getAccount().getEmail() != null) {
                email.setText(trainer.getAccount().getEmail());
            }
            if(image != null) {
                imageView.setImageBitmap(image);
            }
        }
    }
}