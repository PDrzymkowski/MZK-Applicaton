package com.example.piotr.mzkapi15;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity{


    String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        language = (String) getIntent().getSerializableExtra("Language");
        setInterface();

    }

    private void setInterface(){

        final EditText emailEditText = findViewById(R.id.emailEditText);
        final EditText passwordEditText = findViewById(R.id.passwordEditText);
        final EditText passwordAgainEditText = findViewById(R.id.passwordAgainEditText);
        Button goBackButton = findViewById(R.id.goBackBtn);
        Button registerButton = findViewById(R.id.registerBtn);

        if(language.equals("PL")){
            emailEditText.setHint(getResources().getString(R.string.Email));
            passwordEditText.setHint(getResources().getString(R.string.PasswordPL));
            passwordAgainEditText.setHint(getResources().getString(R.string.PasswordAgainPL));
            goBackButton.setText(getResources().getString(R.string.GoBackPL));
            registerButton.setText(getResources().getString(R.string.CreateAccountPL));

        }else if(language.equals("ENG")){
            emailEditText.setHint(getResources().getString(R.string.Email));
            passwordEditText.setHint(getResources().getString(R.string.PasswordENG));
            passwordAgainEditText.setHint(getResources().getString(R.string.PasswordAgainENG));
            goBackButton.setText(getResources().getString(R.string.GoBackENG));
            registerButton.setText(getResources().getString(R.string.CreateAccountENG));

        }

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(emailEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals("") ||
                        passwordAgainEditText.getText().toString().equals("")){
                    createToast(getResources().getString(R.string.FieldNotFilledErrENG), getResources().getString(R.string.FieldNotFilledErrPL));

                }else if(!passwordAgainEditText.getText().toString().equals(passwordEditText.getText().toString())){
                    createToast(getResources().getString(R.string.DifferentPasswordsErrENG), getResources().getString(R.string.DifferentPasswordsErrPL));
                    System.out.println(passwordAgainEditText.getText().toString());
                    System.out.println(passwordEditText.getText().toString());
                }else if(passwordEditText.getText().toString().length()<5){
                    createToast(getResources().getString(R.string.TooShortPasswordErrENG), getResources().getString(R.string.TooShortPasswordErrPL));
                }else{

                }

            }
        });

    }

    private void createToast(String resultENG, String resultPL){

        if(language.equals("PL")) Toast.makeText(getApplicationContext(), resultPL,
                Toast.LENGTH_LONG).show();
        else if(language.equals("ENG")) Toast.makeText(getApplicationContext(), resultENG,
                Toast.LENGTH_LONG).show();

    }




}
