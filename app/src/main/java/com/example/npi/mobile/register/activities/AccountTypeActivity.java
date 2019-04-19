package com.example.npi.mobile.register.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.npi.mobile.MainActivity;
import com.example.npi.mobile.R;

public class AccountTypeActivity extends Activity {

    private Button buttonChooseAccountType;
    private RadioGroup accountType;
    private RadioButton userRadioButton;
    private RadioButton trainerRadioButton;
    private RadioButton objectRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_type);
        init();

    }

    private void init() {
        buttonChooseAccountType = findViewById(R.id.button_choose);
        accountType = findViewById(R.id.accountType);
        userRadioButton = findViewById(R.id.userRadioButton);
        trainerRadioButton = findViewById(R.id.trainerRadioButton);
        objectRadioButton = findViewById(R.id.objectRadioButton);
        buttonChooseAccountType.setOnClickListener(getChooseAccountTypeListener());
        accountType.setOnCheckedChangeListener(getOnTypeChangeListener());

    }


    private RadioGroup.OnCheckedChangeListener getOnTypeChangeListener() {
        return (radioGroup, i) -> {
            if(accountType.getCheckedRadioButtonId() != -1){
                buttonChooseAccountType.setEnabled(true);
            }
        };
    }
    private View.OnClickListener getChooseAccountTypeListener(){
        return view -> {
            if(userRadioButton.isChecked()){
                Intent intentU = new Intent(AccountTypeActivity.this,UserRegister.class);
                startActivity(intentU);
            }
            else if (trainerRadioButton.isChecked()){
                Intent intentT = new Intent(AccountTypeActivity.this,TrainerRegister.class);
                startActivity(intentT);
            }
            else if (objectRadioButton.isChecked()){
                Intent intentO = new Intent(AccountTypeActivity.this,ObjectRegister.class);
                startActivity(intentO);
            }
        };
    }
//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        savedInstanceState.putBoolean("myOption1", userRadioButton.isChecked());
//        savedInstanceState.putBoolean("myOption2", trainerRadioButton.isChecked());
//        savedInstanceState.putBoolean("myOption3", objectRadioButton.isChecked());
//    }
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        userRadioButton.setChecked(savedInstanceState.getBoolean("myOption1"));
//        trainerRadioButton.setChecked(savedInstanceState.getBoolean("myOption2"));
//        objectRadioButton.setChecked(savedInstanceState.getBoolean("myOption3"));
//    }

}
