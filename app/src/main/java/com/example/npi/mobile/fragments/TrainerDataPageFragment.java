package com.example.npi.mobile.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.npi.mobile.AssetsHelper;
import com.example.npi.mobile.MultiSpinner;
import com.example.npi.mobile.R;
import com.example.npi.mobile.json.Account;
import com.example.npi.mobile.json.Trainer;
import com.example.npi.mobile.json.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;

import static android.content.Context.MODE_PRIVATE;

public class TrainerDataPageFragment extends Fragment {
    SharedPreferences sharedPreferences;
    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView imageView;
    private Button changeImageButton, deleteImageButton, updateTrainerButton;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText description;
    private Spinner region;
    private Spinner gender;
    private Spinner city;
    private Spinner age;
    private MultiSpinner sports;
    private EditText password;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trainer_data_page_activity, container, false);
        firstName = view.findViewById(R.id.editTextTrainerRegisterName);
        lastName = view.findViewById(R.id.editTextTrainerRegisterSurname);
        email = view.findViewById(R.id.editTextTrainerRegisterEmail);
        password = view.findViewById(R.id.editTextTrainerRegisterPassword);
        description = view.findViewById(R.id.editTextTrainerRegisterDescription);
        region = view.findViewById(R.id.spinnerTrainerProvince);
        gender = view.findViewById(R.id.spinnerTrainerRegisterSex);
        city = view.findViewById(R.id.spinnerTrainerCity);
        age = view.findViewById(R.id.spinnerTrainerRegisterAge);
        sports = view.findViewById(R.id.spinnerTrainerRegisterDiscipline);
        imageView = view.findViewById(R.id.imageViewTrainerDataPage);
        updateTrainerButton = view.findViewById(R.id.changeTrainerDataButton);
        updateTrainerButton.setOnClickListener(v -> new UpdateTrainerDataTask(view.getContext()).execute());

        changeImageButton = (Button) view.findViewById(R.id.changeObjectDataPageImage);
        changeImageButton.setOnClickListener(view1 -> {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, RESULT_LOAD_IMAGE);
        });

        deleteImageButton = view.findViewById(R.id.deleteObjectDataPageImage);
        deleteImageButton.setOnClickListener(view12 -> {
            imageView = getView().findViewById(R.id.imageViewObjectDataPage);
            imageView.setImageDrawable(null);
        });
        new TrainerDataTask(view.getContext()).execute();
        new DownloadImageTask(view.getContext()).execute();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            imageView = getView().findViewById(R.id.imageViewObjectDataPage);
            imageView.setImageURI(selectedImage);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sharedPreferences = context.getSharedPreferences("account", MODE_PRIVATE);
        this.context = context;
    }

    private class TrainerDataTask extends AsyncTask<Void, Void, Void> {
        private String trainerUrl;
        private HttpEntity trainerEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<Trainer> trainerResponseEntity;
        private String bearerToken;
        private Context context;

    public TrainerDataTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        trainerUrl = AssetsHelper.getServerUrl(context) + "/coach";
        bearerToken = sharedPreferences.getString("token", "null");
        headers = new HttpHeaders();
        headers.set("Authorization", bearerToken);
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
    }

    @Override
    protected Void doInBackground(Void... voids) {
        trainerEntity = new HttpEntity(headers);
        trainerResponseEntity = restTemplate.exchange(trainerUrl, HttpMethod.GET, trainerEntity, Trainer.class);
        //jesli chce wyciagnac z bazy tablice userow to biore userRespnseEntity = restTemplate.exchange(userUrl,HttpMethod.GET,userEntity,User[].class
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Trainer trainer = trainerResponseEntity.getBody();
        firstName.setText(trainer.getFirstName() != null ? firstName.getText().toString() : "");
        lastName.setText(trainer.getLastName() != null ? lastName.getText().toString() : "");
        email.setText(trainer.getAccount().getEmail());
        if (trainer.getVoivodeship() != null) {
            region.setSelection(getIndex(region, trainer.getVoivodeship()));
        }
        if (trainer.getGender() != null) {
            gender.setSelection(getIndex(gender, trainer.getGender().equals("m") ? "Mężczyzna" : "Kobieta"));
        }
        Log.d("AA", trainer.getAccount().getProfileImageUrl());
        if (trainer.getCity() != null) {
            city.setSelection(getIndex(city, trainer.getCity()));
        }
        if (trainer.getFirstName() != null) {
            firstName.setText(trainer.getFirstName());
        }
        if (trainer.getLastName() != null) {
            lastName.setText(trainer.getLastName());
        }
    }
}
    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private String imageUrl;
        private String trainerUrl;
        private HttpEntity trainerEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<Trainer> trainerResponseEntity;
        private String bearerToken;
        private Context context;

        DownloadImageTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            trainerUrl = AssetsHelper.getServerUrl(context) + "/coach";
            imageUrl = AssetsHelper.getServerUrl(context) + "/api/image/";
            bearerToken = sharedPreferences.getString("token", "null");
            headers = new HttpHeaders();
            headers.set("Authorization", bearerToken);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        }

        protected Bitmap doInBackground(String... urls) {
            trainerEntity = new HttpEntity(headers);
            trainerResponseEntity = restTemplate.exchange(trainerUrl, HttpMethod.GET,trainerEntity,Trainer.class);
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(imageUrl + trainerResponseEntity.getBody().getAccount().getProfileImageUrl()).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            if(result != null) {
                imageView.setImageBitmap(result);
            }
        }
    }

    private class UpdateTrainerDataTask extends AsyncTask<Void,Void,Void> {
        private String trainerUrl;
        private HttpEntity<Trainer> trainerEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<String> trainerResponseEntity;
        private String bearerToken;
        private Trainer trainer;
        private Context context;

        UpdateTrainerDataTask(Context context) {
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            trainerUrl = AssetsHelper.getServerUrl(context) + "/user";
            trainer = getTrainer();
            bearerToken = sharedPreferences.getString("token", "null");
            headers = new HttpHeaders();
            headers.set("Authorization", bearerToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

        }

        @Override
        protected Void doInBackground(Void... voids) {
            trainerEntity = new HttpEntity<>(trainer,headers);
            trainerResponseEntity = restTemplate.exchange(trainerUrl, HttpMethod.PUT,trainerEntity, String.class);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(trainerResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
//                Toast.makeText(getContext(),"Dane zostały zaktualizowane",Toast.LENGTH_LONG).show();
            }
        }

        private Trainer getTrainer() {
            Account account = new Account();
            Trainer trainer = new Trainer();
            trainer.setFirstName(firstName.getText().toString());
            trainer.setLastName(lastName.getText().toString());
            trainer.setCity(city.getSelectedItem().toString());
            trainer.setVoivodeship(region.getSelectedItem().toString());
            trainer.setAge(age.getSelectedItem().toString());
            trainer.setSports(sports.getItems());
//            trainer.setDescription(description.getText().toString());
            if(gender.getSelectedItem().toString().equals("Mężczyzna")) {
                trainer.setGender("m");
            } else {
                trainer.setGender("f");
            }
            account.setEmail(email.getText().toString());
            account.setType("coach");
            trainer.setAccount(account);
            return trainer;
        }
    }
}
