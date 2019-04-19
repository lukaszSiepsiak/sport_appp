package com.example.npi.mobile.register.activities;

        import android.app.Activity;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.res.TypedArray;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v7.app.AlertDialog;
        import android.util.AttributeSet;
        import android.util.Log;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.CheckBox;
        import android.widget.EditText;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.basgeekball.awesomevalidation.AwesomeValidation;
        import com.basgeekball.awesomevalidation.ValidationStyle;
        import com.example.npi.mobile.AssetsHelper;
        import com.example.npi.mobile.MainActivity;
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

        import java.util.Arrays;
        import java.util.Collections;
        import java.util.List;
        import com.example.npi.mobile.MultiSpinner;


public class TrainerRegister extends Activity {

    EditText trainerRegisterName, trainerRegisterSurname, trainerRegisterEmail, trainerRegisterPassword;
    Button trainerRegisterButton;
    Spinner trainerRegisterGender, trainerRegisterAge, trainerRegisterCity, trainerRegisterProvince;
    MultiSpinner trainerRegisterDiscipline;
    CheckBox checkBoxRules;
    AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_register);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        updateUI();
    }

    private void updateUI() {
        trainerRegisterName = findViewById(R.id.editTextTrainerRegisterName);
        trainerRegisterSurname = findViewById(R.id.editTextTrainerRegisterSurname);
        trainerRegisterEmail = findViewById(R.id.editTextTrainerRegisterEmail);
        trainerRegisterPassword = findViewById(R.id.editTextTrainerRegisterPassword);
        trainerRegisterButton = findViewById(R.id.registerTrainerButton);
        trainerRegisterDiscipline = findViewById(R.id.spinnerTrainerRegisterDiscipline);
        trainerRegisterGender = findViewById(R.id.spinnerTrainerRegisterSex);
        trainerRegisterAge = findViewById(R.id.spinnerTrainerRegisterAge);
        trainerRegisterCity = findViewById(R.id.spinnerTrainerRegisterCity);
        trainerRegisterProvince = findViewById(R.id.spinnerTrainerRegisterProvince);
        checkBoxRules = findViewById(R.id.checkBoxTrainerRules);
        String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";
                awesomeValidation.addValidation(TrainerRegister.this, R.id.editTextTrainerRegisterName, "[A-Z][a-zA-Z\\s]+", R.string.nameerr);
        awesomeValidation.addValidation(TrainerRegister.this, R.id.editTextTrainerRegisterSurname, "[A-Z][a-zA-Z\\s]+", R.string.surnameerr);
        awesomeValidation.addValidation(TrainerRegister.this, R.id.editTextTrainerRegisterEmail, android.util.Patterns.EMAIL_ADDRESS, R.string.emailerr);
        awesomeValidation.addValidation(TrainerRegister.this, R.id.editTextTrainerRegisterPassword, regexPassword, R.string.passerr);

        trainerRegisterButton.setOnClickListener(view -> {
            if (awesomeValidation.validate() && checkBoxRules.isChecked()) {
                new TrainerRegisterTask(getApplicationContext()).execute();
            } else if (awesomeValidation.validate() && !checkBoxRules.isChecked()) {
                Toast.makeText(TrainerRegister.this, "Musisz zaakceptować regulamin", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(TrainerRegister.this, "Dane zostały wprowadzone błędnie", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private class TrainerRegisterTask extends AsyncTask<Void, Void, Void> {
        private String url;
        private HttpEntity trainerEntity;
        private HttpHeaders headers;
        private RestTemplate restTemplate;
        private ResponseEntity<Trainer> trainerResponseEntity;
        private Trainer trainer;
        private Context context;
        List<String> discplineList;


        public TrainerRegisterTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            url = AssetsHelper.getServerUrl(context) + "/coach";
            trainer = getTrainer();
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            trainerEntity = new HttpEntity(trainer, headers);
            trainerResponseEntity = restTemplate.exchange(url, HttpMethod.POST, trainerEntity, Trainer.class);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (trainerResponseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
                Toast.makeText(getApplicationContext(), "Rejestracja przebiegła pomyślnie. Teraz możesz się zalogować", Toast.LENGTH_LONG).show();
                Intent trainerPage = new Intent(TrainerRegister.this, MainActivity.class);
                startActivity(trainerPage);
            }
        }

        private Trainer getTrainer() {
            Trainer trainer = new Trainer();
            Account account = new Account();
            account.setEmail(trainerRegisterEmail.getText().toString());
            account.setPassword(trainerRegisterPassword.getText().toString());
            trainer.setCity(trainerRegisterCity.getSelectedItem().toString());
            trainer.setAge(trainerRegisterAge.getSelectedItem().toString());
            trainer.setVoivodeship(trainerRegisterProvince.getSelectedItem().toString());
            trainer.setFirstName(trainerRegisterName.getText().toString());
            trainer.setLastName(trainerRegisterSurname.getText().toString());
            if (trainerRegisterGender.getSelectedItem().toString().equals("Mężczyzna")) {
                trainer.setGender("m");
            } else {
                trainer.setGender("f");
            }
            trainer.setSports(trainerRegisterDiscipline.getItems());
            trainer.setAccount(account);

            return trainer;
        }
    }
}

