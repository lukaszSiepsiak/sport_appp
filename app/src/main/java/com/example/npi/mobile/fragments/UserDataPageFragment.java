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

public class UserDataPageFragment extends Fragment {
    SharedPreferences sharedPreferences;
    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView imageView;
    private Button changeImageButton, deleteImageButton, updateUserButton;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private Spinner region;
    private Spinner gender;
    private Spinner city;
    private EditText password;
    private Context context;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_data_page_activity,container,false);
        firstName = view.findViewById(R.id.editTextUserFirstName);
        lastName = view.findViewById(R.id.editTextUserLastName);
        email = view.findViewById(R.id.editTextUserRegisterEmail);
        password = view.findViewById(R.id.editTextUserRegisterPassword);
        region = view.findViewById(R.id.spinnerUserProvince);
        gender = view.findViewById(R.id.spinnerUserRegisterSex);
        city = view.findViewById(R.id.spinnerUserCity);
        imageView = view.findViewById(R.id.imageViewUserDataPage);
        updateUserButton = view.findViewById(R.id.changeUserDataButton);
        updateUserButton.setOnClickListener( v -> new UpdateUserDataTask(view.getContext()).execute());
        changeImageButton = view.findViewById(R.id.changeObjectDataPageImage);
        changeImageButton.setOnClickListener(view1 -> {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, RESULT_LOAD_IMAGE);
        });

        deleteImageButton = view.findViewById(R.id.deleteObjectDataPageImage);
        deleteImageButton.setOnClickListener(view12 -> {
            imageView = view.findViewById(R.id.imageViewObjectDataPage);
            imageView.setImageDrawable(null);
        });
        new UserDataTask(view.getContext()).execute();
        new DownloadImageTask(view.getContext()).execute();
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null ){
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
//            imageView = getView().findViewById(R.id.imageViewUserDataPage);
//            imageView.setImageURI(selectedImage);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sharedPreferences = context.getSharedPreferences("account", MODE_PRIVATE);
        this.context = context;
    }

/*    private void insertRegionsIntoSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.my_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        region.setAdapter(adapter);
    }*/

    private class UserDataTask extends AsyncTask<Void,Void,Void> {
        private String userUrl;
        private HttpEntity userEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<User> userResponseEntity;
        private String bearerToken;
        private Context context;

        UserDataTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            userUrl = AssetsHelper.getServerUrl(context) + "/user";
            bearerToken = sharedPreferences.getString("token", "null");
            headers = new HttpHeaders();
            headers.set("Authorization", bearerToken);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            userEntity = new HttpEntity(headers);
            userResponseEntity = restTemplate.exchange(userUrl, HttpMethod.GET,userEntity,User.class);
            //jesli chce wyciagnac z bazy tablice userow to biore userRespnseEntity = restTemplate.exchange(userUrl,HttpMethod.GET,userEntity,User[].class
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            User user = userResponseEntity.getBody();
            firstName.setText(user.getFirstName() != null ? firstName.getText().toString() : "");
            lastName.setText(user.getLastName() != null ? lastName.getText().toString() : "");
            email.setText(user.getAccount().getEmail());
            if(user.getVoivodeship() != null ) {
                region.setSelection(getIndex(region,user.getVoivodeship()));
            }
            if(user.getGender() != null) {
                gender.setSelection(getIndex(gender,user.getGender().equals("m") ? "Mężczyzna" : "Kobieta"));
            }
            if(user.getCity() != null) {
                city.setSelection(getIndex(city,user.getCity()));
            }
            if(user.getFirstName() != null) {
                firstName.setText(user.getFirstName());
            }
            if(user.getLastName() != null) {
                lastName.setText(user.getLastName());
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
        private String userUrl;
        private HttpEntity userEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<User> userResponseEntity;
        private String bearerToken;
        private Context context;

        DownloadImageTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            userUrl = AssetsHelper.getServerUrl(context) + "/user";
            imageUrl = AssetsHelper.getServerUrl(context) + "/api/image/";
            bearerToken = sharedPreferences.getString("token", "null");
            headers = new HttpHeaders();
            headers.set("Authorization", bearerToken);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        }

        protected Bitmap doInBackground(String... urls) {
            userEntity = new HttpEntity(headers);
            userResponseEntity = restTemplate.exchange(userUrl, HttpMethod.GET,userEntity,User.class);
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(imageUrl + userResponseEntity.getBody().getAccount().getProfileImageUrl()).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if(result != null) {
                imageView.setImageBitmap(result);
            }
        }
    }

    private class UpdateUserDataTask extends AsyncTask<Void,Void,Void> {
        private String userUrl;
        private HttpEntity<User> userEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<String> userResponseEntity;
        private String bearerToken;
        private User user;
        private Context context;

        UpdateUserDataTask(Context context) {
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            userUrl = AssetsHelper.getServerUrl(context) + "/user";
            user = getUser();
            bearerToken = sharedPreferences.getString("token", "null");
            headers = new HttpHeaders();
            headers.set("Authorization", bearerToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

        }

        @Override
        protected Void doInBackground(Void... voids) {
            userEntity = new HttpEntity<>(user,headers);
            userResponseEntity = restTemplate.exchange(userUrl, HttpMethod.PUT,userEntity, String.class);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(userResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
//                Toast.makeText(getContext(),"Dane zostały zaktualizowane",Toast.LENGTH_LONG).show();
            }
        }

        private User getUser() {
            Account account = new Account();
            User user = new User();
            user.setFirstName(firstName.getText().toString());
            user.setLastName(lastName.getText().toString());
            user.setCity(city.getSelectedItem().toString());
            user.setVoivodeship(region.getSelectedItem().toString());
            if(gender.getSelectedItem().toString().equals("Mężczyzna")) {
                user.setGender("m");
            } else {
                user.setGender("f");
            }
            account.setEmail(email.getText().toString());
            account.setType("user");
            user.setAccount(account);
            return user;
        }
    }

}
