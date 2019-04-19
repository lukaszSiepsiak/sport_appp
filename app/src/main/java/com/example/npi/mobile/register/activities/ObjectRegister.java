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
import com.example.npi.mobile.MultiSpinner;
import com.example.npi.mobile.R;
import com.example.npi.mobile.json.Account;
import com.example.npi.mobile.json.SportObject;
import com.example.npi.mobile.json.Trainer;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class ObjectRegister extends Activity {

    EditText objectRegisterName,objectRegisterStreet, objectRegisterZipCode, objectRegisterEmail, objectRegisterPassword, objectRegisterDescription;
    Spinner objectRegisterCity,objectRegisterProvince, objectRegisterDiscipline;
    MultiSpinner objectRegisterOffer;
    Button objectRegisterButton;
    CheckBox checkBoxRules;
    AwesomeValidation awesomeValidation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_register);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        updateUI();
    }

    private void updateUI() {
        objectRegisterName = (EditText)findViewById(R.id.editTextObjectName);
        objectRegisterDescription = (EditText)findViewById(R.id.editTextObjectDescription) ;
        objectRegisterCity = (Spinner) findViewById(R.id.spinnerObjectCity);
        objectRegisterDiscipline = (Spinner)findViewById(R.id.spinnerObjectOffer);
        objectRegisterStreet = (EditText)findViewById(R.id.editTextObjectStreet);
        objectRegisterProvince = (Spinner)findViewById(R.id.spinnerObjectProvince);
        objectRegisterZipCode = (EditText)findViewById(R.id.editTextObjectPostCode);
        objectRegisterEmail = (EditText)findViewById(R.id.editTextObjectEmail);
        objectRegisterPassword = (EditText)findViewById(R.id.editTextObjectPassword);
        objectRegisterButton = (Button)findViewById(R.id.registerObjectButton);
        checkBoxRules = (CheckBox)findViewById(R.id.checkBoxObjectRegisterRules);
        String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";
        awesomeValidation.addValidation(ObjectRegister.this, R.id.editTextObjectName, "[A-Z][a-zA-Z\\s]+", R.string.nameerr);
        awesomeValidation.addValidation(ObjectRegister.this,R.id.spinnerObjectCity,"^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$",R.string.cityerr);
        awesomeValidation.addValidation(ObjectRegister.this,R.id.spinnerObjectProvince,"^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$,",R.string.provinceerr);
        awesomeValidation.addValidation(ObjectRegister.this,R.id.editTextObjectStreet,"^([\\w\\s\\W]+[\\w\\W]?)\\s([\\d\\-\\\\\\/\\w]*)?",R.string.streeterr);
        awesomeValidation.addValidation(ObjectRegister.this, R.id.editTextObjectEmail, android.util.Patterns.EMAIL_ADDRESS, R.string.emailerr);
        awesomeValidation.addValidation(ObjectRegister.this, R.id.editTextObjectPassword, regexPassword, R.string.passerr);
        awesomeValidation.addValidation(ObjectRegister.this,R.id.editTextObjectPostCode,"^[0-9]{2}(?:-[0-9]{3}) (?:[a-zA-Z\\s]+)$", R.string.err_zipcode);
        objectRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (awesomeValidation.validate() && checkBoxRules.isChecked() ){
                    new SportObjectRegisterTask(getApplicationContext()).execute();
                }
                else if (awesomeValidation.validate() && !checkBoxRules.isChecked()){

                    Toast.makeText(ObjectRegister.this,"Musisz zaakceptować regulamin",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ObjectRegister.this, "Dane zostały wprowadzone błędnie", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private class SportObjectRegisterTask extends AsyncTask<Void,Void,Void>{
        private String url;
        private HttpEntity sportObjectEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<SportObject> sportObjectResponseEntity;
        private SportObject sportObject;
        private Context context;

        public SportObjectRegisterTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            url = AssetsHelper.getServerUrl(context) + "/trainer";
            sportObject = getSportObject();
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            sportObjectEntity = new HttpEntity(sportObject,headers);
            sportObjectResponseEntity = restTemplate.exchange(url,HttpMethod.POST,sportObjectEntity, SportObject.class);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (sportObjectResponseEntity.getStatusCode().equals(HttpStatus.CREATED)){
                Toast.makeText(getApplicationContext(),"Rejestracja przebiegła pomyślnie. Teraz możesz się zalogować", Toast.LENGTH_LONG).show();
                Intent sportObjectPage = new Intent(ObjectRegister.this, MainActivity.class);
                startActivity(sportObjectPage);
            }
        }


        private SportObject getSportObject() {
            SportObject sportObject = new SportObject();
            Account account = new Account();
            account.setEmail(objectRegisterEmail.getText().toString());
            account.setPassword(objectRegisterPassword.getText().toString());
            sportObject.setName(objectRegisterName.getText().toString());
            sportObject.setStreet(objectRegisterStreet.getText().toString());
            sportObject.setPostalCode(objectRegisterZipCode.getText().toString());
            sportObject.setDescription(objectRegisterDescription.getText().toString());
            sportObject.setCity(objectRegisterCity.getSelectedItem().toString());
            sportObject.setVoivodeship(objectRegisterProvince.getSelectedItem().toString());
//            sportObject.setDiscipline(objectRegisterDiscipline.getSelectedItem().toString());
            sportObject.setObjectOffer(objectRegisterOffer.getItems());

            return sportObject;
        }
    }


}
