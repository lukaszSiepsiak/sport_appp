package com.example.npi.mobile.startPage.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.npi.mobile.AssetsHelper;
import com.example.npi.mobile.MainActivity;
import com.example.npi.mobile.R;
import com.example.npi.mobile.fragments.CalendarFragment;
import com.example.npi.mobile.fragments.FavoritesFragment;
import com.example.npi.mobile.fragments.MessageFragment;
import com.example.npi.mobile.fragments.ObjectsFragment;
import com.example.npi.mobile.fragments.TrainersFragment;
import com.example.npi.mobile.fragments.UserDataPageFragment;
import com.example.npi.mobile.fragments.UserHomePageFragment;
import com.example.npi.mobile.fragments.VisitFragment;
import com.example.npi.mobile.json.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;

public class UserStartPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private TextView data;
    private TextView email;
    private ImageView image;
    private View header;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_start_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.inflateHeaderView(R.layout.nav_header);
        data = header.findViewById(R.id.nav_header_data);
        email = header.findViewById(R.id.nav_header_email);
        image = header.findViewById(R.id.nav_header_image);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new UserDataPageFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_data_page);
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new UserDataPageFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_data_page);
        }
        new UserDataTask(getApplicationContext()).execute();

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.nav_home_page:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new UserHomePageFragment()).commit();
                break;
            case R.id.nav_data_page:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new UserDataPageFragment()).commit();
                break;
            case R.id.nav_message:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new MessageFragment()).commit();
                break;
            case R.id.nav_favorites:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new FavoritesFragment()).commit();
                break;
            case R.id.nav_trainers:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new TrainersFragment()).commit();
                break;
            case R.id.nav_objects:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new ObjectsFragment()).commit();
                break;
            case R.id.nav_meeting_history:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new VisitFragment()).commit();
                break;
            case R.id.nav_calendar:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout, new CalendarFragment()).commit();
                break;
            case R.id.nav_logout:
                sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
                editor=sharedPreferences.edit();
                editor.remove("token");
                editor.apply();
                Intent i = new Intent(UserStartPage.this, MainActivity.class);
                startActivity(i);
                finish();
                break;


        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private class UserDataTask extends AsyncTask<Void,Void,Void> {
        private String userUrl;
        private String imageUrl;
        private HttpEntity userEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<User> userResponseEntity;
        private String bearerToken;
        private Bitmap icon;
        private Context context;

        UserDataTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            userUrl = AssetsHelper.getServerUrl(context) + "/user";
            imageUrl = AssetsHelper.getServerUrl(context) + "/api/image/";
            sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
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
            icon = null;
            try {
                InputStream in = new java.net.URL(imageUrl + userResponseEntity.getBody().getAccount().getProfileImageUrl()).openStream();
                icon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            User user = userResponseEntity.getBody();
            if(user.getFirstName() != null ) {
                data.setText(user.getFirstName());
            }
            if(user.getLastName() != null) {
                data.setText(data.getText().toString() + " " + user.getLastName());
            }
            email.setText(user.getAccount().getEmail());
            if(icon != null) {
                image.setImageBitmap(icon);
            }
        }
    }

}
