package com.example.my_accounting;

import static Utils.Util.APP_PREFERENCES;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Data.SupabaseHandler;
import Model.Clients;
import Model.SharedPreferencesManager;

public class RegistrationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }
    public void createAccount(View view) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            String email = ((EditText) findViewById(R.id.email)).getText().toString();
            String password = ((EditText) findViewById(R.id.password)).getText().toString();
            SupabaseHandler supabaseHandler = new SupabaseHandler();
            int id;
            Clients clients = new Clients(email, password);
            try {
                id = supabaseHandler.addClientToDB(clients);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            int finalSuccess = id;
            handler.post(() -> {
                if (finalSuccess > 0) {
                    Toast.makeText(getApplicationContext(), "Клиент успешно зарегестрирован", Toast.LENGTH_SHORT).show();
                    SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
                    sharedPreferencesManager.clearClientId();
                    goToActivity(LoginActivity.class);
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка при регистрации", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }
}