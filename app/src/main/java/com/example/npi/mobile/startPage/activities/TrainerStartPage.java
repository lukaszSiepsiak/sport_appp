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
import com.example.npi.mobile.fragments.DisciplineListFragment;
import com.example.npi.mobile.fragments.FavoritesFragment;
import com.example.npi.mobile.fragments.MessageFragment;
import com.example.npi.mobile.fragments.ObjectsFragment;
import com.example.npi.mobile.fragments.TrainerDataPageFragment;
import com.example.npi.mobile.fragments.TrainerHomePageFragment;
import com.example.npi.mobile.fragments.TrainersFragment;
import com.example.npi.mobile.json.Trainer;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;

public class TrainerStartPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private TextView data, email;
    private ImageView image;
    private NavigationView navigationView;
    private View header;
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainer_start_page);
        //ustawienie paska na górze do rozwijania menu
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.inflateHeaderView(R.layout.nav_header);
        data = header.findViewById(R.id.nav_header_data);
        email = header.findViewById(R.id.nav_header_email);
        image = header.findViewById(R.id.nav_header_image);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new TrainerHomePageFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_home_page);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new TrainerHomePageFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home_page);
        }

        new TrainerDataTask(getApplicationContext()).execute();

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.nav_home_page:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new TrainerHomePageFragment()).commit();
                break;
            case R.id.nav_data_page:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new TrainerDataPageFragment()).commit();
                break;
            case R.id.nav_message:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new MessageFragment()).commit();
                break;
            case R.id.nav_favorites:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new FavoritesFragment()).commit();
                break;
            case R.id.nav_objects:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new ObjectsFragment()).commit();
                break;
            case R.id.nav_trainers:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new TrainersFragment()).commit();
                break;
            case R.id.nav_discipline_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new DisciplineListFragment()).commit();
                break;
            case R.id.nav_calendar:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout, new CalendarFragment()).commit();
                break;
            case R.id.nav_logout:
                sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
                editor=sharedPreferences.edit();
                editor.remove("token");
                editor.apply();
                Intent i = new Intent(TrainerStartPage.this, MainActivity.class);
                startActivity(i);
                finish();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private class TrainerDataTask extends AsyncTask<Void,Void,Void>{
        private String trainerUrl;
        private String imageUrl;
        private HttpEntity trainerEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<Trainer> trainerResponseEntity;
        private String bearerToken;
        private Bitmap icon;
        private Context context;

        public TrainerDataTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            trainerUrl = AssetsHelper.getServerUrl(context) + "/coach";
            imageUrl = AssetsHelper.getServerUrl(context) + "/api/image/";
            sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
            bearerToken = sharedPreferences.getString("token", "null");
            headers = new HttpHeaders();
            headers.set("Authorization", bearerToken);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());        }

        @Override
        protected Void doInBackground(Void... voids) {
            trainerEntity = new HttpEntity(headers);
            trainerResponseEntity = restTemplate.exchange(trainerUrl, HttpMethod.GET,trainerEntity,Trainer.class);
            icon = null;
            try {
                InputStream in = new java.net.URL(imageUrl + trainerResponseEntity.getBody().getAccount().getProfileImageUrl()).openStream();
                icon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Trainer trainer = trainerResponseEntity.getBody();
            if(trainer.getFirstName() != null ) {
                data.setText(trainer.getFirstName());
            }
            if(trainer.getLastName() != null) {
                data.setText(data.getText().toString() + " " + trainer.getLastName());
            }
            email.setText(trainer.getAccount().getEmail());
            if(icon != null) {
                image.setImageBitmap(icon);
            }
        }
    }
}
