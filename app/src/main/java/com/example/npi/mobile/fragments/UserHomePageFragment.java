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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.npi.mobile.AssetsHelper;
import com.example.npi.mobile.R;
import com.example.npi.mobile.json.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;

import static android.content.Context.MODE_PRIVATE;

public class UserHomePageFragment extends Fragment {
    SharedPreferences sharedPreferences;
    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView imageView;
    private Button changeImageButton, deleteImageButton;
    private TextView firstName;
    private TextView lastName;
    private TextView region;
    private TextView city;
    private TextView email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_home_page_activity, container, false);
        imageView = view.findViewById(R.id.imageViewUserHomePage);
        firstName = view.findViewById(R.id.textViewUserFirstName);
        lastName = view.findViewById(R.id.textViewUserLastName);
        region = view.findViewById(R.id.textViewObjectProvince);
        city = view.findViewById(R.id.textViewObjectCity);
        email = view.findViewById(R.id.textViewUserEmail);
        new UserDataTask(view.getContext()).execute();
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
            imageView = getView().findViewById(R.id.imageViewObjectDataPage);
            imageView.setImageURI(selectedImage);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sharedPreferences = context.getSharedPreferences("account", MODE_PRIVATE);
    }
    private class UserDataTask extends AsyncTask<Void,Void,Void> {
        private String userUrl;
        private String imageUrl;
        private HttpEntity userEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<User> userResponseEntity;
        private String bearerToken;
        private Bitmap image;
        private Context context;

        UserDataTask(Context context) {
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

        @Override
        protected Void doInBackground(Void... voids) {
            userEntity = new HttpEntity(headers);
            userResponseEntity = restTemplate.exchange(userUrl, HttpMethod.GET,userEntity,User.class);
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
            User user = userResponseEntity.getBody();
            if(user.getFirstName() != null) {
                firstName.setText(user.getFirstName());
            }
            if(user.getLastName() != null) {
                lastName.setText(user.getLastName());
            }
            if(user.getVoivodeship() != null) {
                region.setText(user.getVoivodeship());
            }
            if(user.getCity() != null) {
                city.setText(user.getCity());
            }
            if(user.getAccount().getEmail() != null) {
                email.setText(user.getAccount().getEmail());
            }
            if(image != null) {
                imageView.setImageBitmap(image);
            }
        }
    }


}
