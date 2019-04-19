package com.example.npi.mobile.register.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.npi.mobile.AssetsHelper;
import com.example.npi.mobile.MainActivity;
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

public class UserRegister extends Activity {

    EditText userRegisterEmail,userRegisterPassword;
    Spinner userRegisterGender;
    String setGender;
    Button userRegisterButton;
    CheckBox checkBoxRules;
    AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        updateUI();
    }

    private void updateUI() {
        userRegisterEmail = findViewById(R.id.editTextUserRegisterEmail);
        userRegisterPassword = findViewById(R.id.editTextUserRegisterPassword);
        userRegisterGender = findViewById(R.id.spinnerUserRegisterSex);
        userRegisterButton= findViewById(R.id.registerUserButton);
        checkBoxRules = findViewById(R.id.checkBoxUserRules);

        String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";
        awesomeValidation.addValidation(UserRegister.this,R.id.editTextUserRegisterEmail,android.util.Patterns.EMAIL_ADDRESS,R.string.emailerr);
        awesomeValidation.addValidation(UserRegister.this,R.id.editTextUserRegisterPassword,regexPassword,R.string.passerr);
        awesomeValidation.addValidation(UserRegister.this,R.id.checkBoxUserRules,s ->  checkBoxRules.isSelected(),R.string.ruleserr);


        userRegisterButton.setOnClickListener(view -> {
            if (awesomeValidation.validate() && checkBoxRules.isChecked()){
                new UserRegisterTask(getApplicationContext()).execute();
            } else if (awesomeValidation.validate() && !checkBoxRules.isChecked()){
                Toast.makeText(UserRegister.this, "Musisz zaakceptować regulamin",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(UserRegister.this, "Dane zostały wprowadzone błędnie",Toast.LENGTH_SHORT).show();
            }

        });
    }

    private class UserRegisterTask extends AsyncTask<Void,Void,Void> {
        private String url;
        private HttpEntity userEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<User> userResponseEntity;
        private User user;
        private Context context;

        public UserRegisterTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            url = AssetsHelper.getServerUrl(context) + "/users";
            user = getUser();
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            userEntity = new HttpEntity(user,headers);
            userResponseEntity = restTemplate.exchange(url,HttpMethod.POST, userEntity, User.class);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (userResponseEntity.getStatusCode().equals(HttpStatus.CREATED)){
                Toast.makeText(getApplicationContext(), "Rejestracja przebiegła pomyślnie. Teraz możesz się zalogować", Toast.LENGTH_LONG).show();
                Intent userPage = new Intent(UserRegister.this, MainActivity.class);
                startActivity(userPage);
            }
        }

        private User getUser(){
            User user = new User();
            Account account = new Account();
            account.setEmail(userRegisterEmail.getText().toString());
            account.setPassword(userRegisterPassword.getText().toString());
            if(userRegisterGender.getSelectedItem().toString().equals("Mężczyzna")) {
                user.setGender("m");
            } else {
                user.setGender("f");
            }
            user.setAccount(account);
            return user;
        }

    }
}
