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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.npi.mobile.AssetsHelper;
import com.example.npi.mobile.R;
import com.example.npi.mobile.json.Account;
import com.example.npi.mobile.json.SportObject;

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

public class ObjectDataPageFragment extends Fragment {
    SharedPreferences sharedPreferences;
    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView imageView;
    private EditText name;
    private EditText password;
    private EditText street;
    private EditText postalCode;
    private EditText email;
    private EditText description;
    private Spinner city;
    private Spinner region;
    private Spinner discipline;
    private Context context;
    private Button changeImageButton, deleteImageButton, updateSportObjectButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_object_data_page_activity, container, false);
        name = view.findViewById(R.id.editTextObjectName);
        street = view.findViewById(R.id.editTextObjectStreet);
        postalCode = view.findViewById(R.id.editTextObjectPostCode);
        email = view.findViewById(R.id.editTextObjectEmail);
        password = view.findViewById(R.id.editTextObjectPassword);
        description = view.findViewById(R.id.editTextObjectDescription);
        city = view.findViewById(R.id.spinnerObjectCity);
        region = view.findViewById(R.id.SpinnerObjectProvince);
        discipline = view.findViewById(R.id.spinnerObjectOffer);

        updateSportObjectButton = view.findViewById(R.id.changeTrainerDataButton);
        updateSportObjectButton.setOnClickListener(v -> new UpdateSportObjectDataTask(view.getContext()).execute());

        changeImageButton = (Button) view.findViewById(R.id.changeObjectDataPageImage);
        changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        deleteImageButton = (Button) view.findViewById(R.id.deleteObjectDataPageImage);
        deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView = (ImageView) getView().findViewById(R.id.imageViewObjectDataPage);
                imageView.setImageDrawable(null);
            }
        });

        new SportObjectDataTask(view.getContext()).execute();
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
            imageView = (ImageView) getView().findViewById(R.id.imageViewObjectDataPage);
            imageView.setImageURI(selectedImage);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sharedPreferences = context.getSharedPreferences("account", MODE_PRIVATE);
        this.context = context;
    }

    private class SportObjectDataTask extends AsyncTask<Void, Void, Void> {
        private String objectUrl;
        private HttpEntity objectEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<SportObject> objectResponseEntity;
        private String bearerToken;
        private Context context;

        SportObjectDataTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            objectUrl = AssetsHelper.getServerUrl(context) + "/facility";
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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            SportObject sportObject = objectResponseEntity.getBody();
            name.setText(sportObject.getName() != null ? name.getText().toString() : "");
            email.setText(sportObject.getAccount().getEmail());
            if (sportObject.getVoivodeship() != null) {
                region.setSelection(getIndex(region, sportObject.getVoivodeship()));
            }
            Log.d("AA", sportObject.getAccount().getProfileImageUrl());
            if (sportObject.getCity() != null) {
                city.setSelection(getIndex(city, sportObject.getCity()));
            }
            if (sportObject.getDiscipline() != null){
                discipline.setSelection(getIndex(discipline, sportObject.getDiscipline()));
            }
            if (sportObject.getName() != null) {
                name.setText(sportObject.getName());
            }
            if (sportObject.getStreet() != null){
                street.setText(sportObject.getStreet());
            }
            if (sportObject.getPostalCode() != null){
                postalCode.setText(sportObject.getPostalCode());
            }
            if (sportObject.getDescription() != null){
                description.setText(sportObject.getDescription());
            }
        }
    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return 0;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private String imageUrl;
        private String sportObjectUrl;
        private HttpEntity sportObjectEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<SportObject> sportObjectResponseEntity;
        private String bearerToken;
        private Context context;

        DownloadImageTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            sportObjectUrl = AssetsHelper.getServerUrl(context) + "/facility";
            imageUrl = AssetsHelper.getServerUrl(context) + "/api/image/";
            bearerToken = sharedPreferences.getString("token", "null");
            headers = new HttpHeaders();
            headers.set("Authorization", bearerToken);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        }

        protected Bitmap doInBackground(String... urls) {
            sportObjectEntity = new HttpEntity(headers);
            sportObjectResponseEntity = restTemplate.exchange(sportObjectUrl, HttpMethod.GET, sportObjectEntity,SportObject.class);
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(imageUrl + sportObjectResponseEntity.getBody().getAccount().getProfileImageUrl()).openStream();
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

    private class UpdateSportObjectDataTask extends AsyncTask<Void,Void,Void> {
        private String sportObjectUrl;
        private HttpEntity<SportObject> sportObjectEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<String> sportObjectResponseEntity;
        private String bearerToken;
        private SportObject sportObject;
        private Context context;

        UpdateSportObjectDataTask(Context context) {
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            sportObjectUrl = AssetsHelper.getServerUrl(context) + "/facility";
            sportObject = getSportObject();
            bearerToken = sharedPreferences.getString("token", "null");
            headers = new HttpHeaders();
            headers.set("Authorization", bearerToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

        }

        @Override
        protected Void doInBackground(Void... voids) {
            sportObjectEntity = new HttpEntity<>(sportObject,headers);
            sportObjectResponseEntity = restTemplate.exchange(sportObjectUrl, HttpMethod.PUT, sportObjectEntity, String.class);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(sportObjectResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
//                Toast.makeText(getContext(),"Dane zostały zaktualizowane",Toast.LENGTH_LONG).show();
            }
        }

        private SportObject getSportObject() {
            Account account = new Account();
            SportObject sportObject = new SportObject();
            sportObject.setName(name.getText().toString());
            sportObject.setCity(city.getSelectedItem().toString());
            sportObject.setVoivodeship(region.getSelectedItem().toString());
            sportObject.setDescription(description.getText().toString());
            sportObject.setStreet(street.getText().toString());
            sportObject.setPostalCode(postalCode.getText().toString());
            account.setEmail(email.getText().toString());
            account.setType("coach");
            this.sportObject.setAccount(account);
            return this.sportObject;
        }
    }

}