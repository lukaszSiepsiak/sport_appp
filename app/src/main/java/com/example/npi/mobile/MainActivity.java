package com.example.npi.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.npi.mobile.json.Account;
import com.example.npi.mobile.register.activities.AccountTypeActivity;
import com.example.npi.mobile.register.activities.PasswordReminder;
import com.example.npi.mobile.startPage.activities.ObjectStartPage;
import com.example.npi.mobile.startPage.activities.TrainerStartPage;
import com.example.npi.mobile.startPage.activities.UserStartPage;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class MainActivity extends Activity {

    private EditText loginInput;
    private EditText passwordInput;
    private TextView passReminder;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginInput = findViewById(R.id.login);
        passwordInput = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);
        passReminder = findViewById(R.id.passReminder);
        sharedPreferences=getSharedPreferences("account",MODE_PRIVATE);
        if(sharedPreferences.contains("token")) {
            redirect();
            finish();
        }
        loginButton.setOnClickListener(view -> new LoginTask(getApplicationContext()).execute());
        registerButton.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, AccountTypeActivity.class);
            startActivity(i);
        });

        passReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PasswordReminder.class);
                startActivity(intent);
            }
        });

        if(savedInstanceState != null){
            loginInput.setText(savedInstanceState.getString("LOGIN"));
            passwordInput.setText(savedInstanceState.getString("PASSWORD"));
        }

    }

    private void redirect() {
        sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
        String accountType = sharedPreferences.getString("type", "");
        switch (accountType) {
            case "user":
                Intent userPage = new Intent(MainActivity.this, UserStartPage.class);
                startActivity(userPage);
                break;
            case "coach":
                Intent coachPage = new Intent(MainActivity.this, TrainerStartPage.class);
                startActivity(coachPage);
                break;
            case "object":
                Intent objectPage = new Intent(MainActivity.this, ObjectStartPage.class);
                startActivity(objectPage);
                break;
            default:
                break;
        }
    }

    private class LoginTask extends AsyncTask<Void,Void,Void> {

        private Account account;
        private HttpEntity<Account> entity;
        private HttpEntity accountEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<String> tokenEntity;
        private ResponseEntity<Account> accountResponseEntity;
        private String bearerToken;
        private String loginUrl;
        private String accountUrl;
        private Context context;

        LoginTask(Context context) {
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            loginUrl = AssetsHelper.getServerUrl(context) + "/login";
            accountUrl = AssetsHelper.getServerUrl(context) + "/account";
            account = new Account();
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            account.setEmail(loginInput.getText().toString());
            account.setPassword(passwordInput.getText().toString());
            entity = new HttpEntity<>(account,headers);
            try {
                tokenEntity = restTemplate.exchange(loginUrl, HttpMethod.POST, entity, String.class);
                saveTokenInMemory();
                headers.clear();
                headers.set("Authorization", bearerToken);
                accountEntity = new HttpEntity<>(headers);
                accountResponseEntity = restTemplate.exchange(accountUrl,HttpMethod.GET, accountEntity, Account.class);
                saveAccountTypeInMemory();
            } catch (HttpStatusCodeException e ) {
                Log.d("Error: ", e.getMessage());
            }
            return null;
        }

        private void saveAccountTypeInMemory() {
            sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
            editor=sharedPreferences.edit();
            editor.putString("type", accountResponseEntity.getBody().getType());
            editor.apply();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(tokenEntity != null && tokenEntity.getStatusCode().equals(HttpStatus.OK)) {
                redirect();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Niepoprawna nazwa użytkonika i/lub hasło!", Toast.LENGTH_LONG).show();
            }
        }

        private void saveTokenInMemory() {
            sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
            bearerToken = tokenEntity.getHeaders().getAuthorization();
            editor=sharedPreferences.edit();
            editor.putString("token", bearerToken);
            editor.commit();
        }
    }
}
