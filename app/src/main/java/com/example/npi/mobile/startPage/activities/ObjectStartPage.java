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
import com.example.npi.mobile.fragments.ObjectDataPageFragment;
import com.example.npi.mobile.fragments.ObjectHomePageFragment;
import com.example.npi.mobile.fragments.ObjectsFragment;
import com.example.npi.mobile.fragments.PriceListFragment;
import com.example.npi.mobile.fragments.ScheduleFragment;
import com.example.npi.mobile.fragments.TrainersFragment;
import com.example.npi.mobile.json.SportObject;
import com.example.npi.mobile.json.Trainer;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;

public class ObjectStartPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private DrawerLayout drawerLayout;
    private TextView data, email;
    private ImageView image;
    private NavigationView navigationView;
    private View header;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.object_start_page);
        //ustawienie paska na górze do rozwijania menu
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        header = navigationView.inflateHeaderView(R.layout.nav_header);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        data = header.findViewById(R.id.nav_header_data);
        email = header.findViewById(R.id.nav_header_email);
        image = header.findViewById(R.id.nav_header_image);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new ObjectHomePageFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_home_page);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new ObjectHomePageFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home_page);
        }
        new ObjectDataTask(getApplicationContext()).execute();

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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new ObjectHomePageFragment()).commit();
                break;
            case R.id.nav_data_page:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new ObjectDataPageFragment()).commit();
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
            case R.id.nav_price_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new PriceListFragment()).commit();
                break;
            case R.id.nav_schedule:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,new ScheduleFragment()).commit();
                break;
            case R.id.nav_calendar:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout, new CalendarFragment()).commit();
                break;
            case R.id.nav_logout:
                sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
                editor=sharedPreferences.edit();
                editor.remove("token");
                editor.apply();
                Intent i = new Intent(ObjectStartPage.this, MainActivity.class);
                startActivity(i);
                finish();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private class ObjectDataTask extends AsyncTask<Void,Void,Void> {
        private String objectUrl;
        private String imageUrl;
        private HttpEntity objectEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<SportObject> sportObjectResponseEntity;
        private String bearerToken;
        private Bitmap icon;
        private Context context;

        public ObjectDataTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            objectUrl = AssetsHelper.getServerUrl(context) + "/facility";
            imageUrl = AssetsHelper.getServerUrl(context) + "/api/image/";
            sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
            bearerToken = sharedPreferences.getString("token", "null");
            headers = new HttpHeaders();
            headers.set("Authorization", bearerToken);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());           }

        @Override
        protected Void doInBackground(Void... voids) {
            objectEntity = new HttpEntity(headers);
            sportObjectResponseEntity = restTemplate.exchange(objectUrl, HttpMethod.GET,objectEntity,SportObject.class);
            icon = null;
            try {
                InputStream in = new java.net.URL(imageUrl + sportObjectResponseEntity.getBody().getAccount().getProfileImageUrl()).openStream();
                icon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;        }

        @Override
        protected void onPostExecute(Void aVoid) {
            SportObject sportObject = sportObjectResponseEntity.getBody();
            if(sportObject.getName() != null ) {
                data.setText(sportObject.getName());
            }
            email.setText(sportObject.getAccount().getEmail());
            if(icon != null) {
                image.setImageBitmap(icon);
            }
        }
    }
}
