package com.example.my_accounting;

import static Utils.Util.WAREHOUSE_BASE_TITLE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Data.SupabaseHandler;
import Model.Clients;
import Model.SharedPreferencesManager;
import Model.Warehouses;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        int id = sharedPreferencesManager.getClientId();

        Button button_login = getButton(R.id.login);

        if(id > 0) {
            //@ToDo как ?
            goToMain(id);
        }

        button_login.setOnClickListener(view -> goToMain(0));
        // @TODO сделать прогресс бар для активации кнопки логин и вывода ошибки если меньше символов в пароле или нправильный логин
    }

    public void goToMain(int id) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Clients client;
            SupabaseHandler supabaseHandler = new SupabaseHandler();
            try {
                if (id > 0) {
                    client = supabaseHandler.getClientFromId(this, id);
                } else {
                    //@TODO нужны предпроверки для введённых данных
                    String email = (getEditText(R.id.email)).getText().toString().toLowerCase();
                    String password = (getEditText(R.id.password)).getText().toString();

                    client = supabaseHandler.getClientFromEmail(this, email, password);
                }
                if (client != null) {
                    SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
                    Warehouses warehouse = supabaseHandler.getCurrentWarehouse(client.getCurrentWarehouseId());
                    if (warehouse == null) {
                        try {
                            warehouse = supabaseHandler.createWarehouse(this, WAREHOUSE_BASE_TITLE);
                            //@TODO insert current_warehouse in clients
                            supabaseHandler.updateCurrentWarehouseId(this);
                        } catch (SQLException e) {
                            Toast.makeText(this, "Склад не удалось создать", Toast.LENGTH_SHORT).show();
                            throw new RuntimeException(e);
                        }
                    }
                    sharedPreferencesManager.saveTitleWarehouse(warehouse.getTitle());
                    sharedPreferencesManager.saveWarehouseId(warehouse.getId());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            handler.post(() -> {
                if (client != null) {
                    Toast.makeText(getApplicationContext(), "Успешно", Toast.LENGTH_SHORT).show();
                    goToActivity(MainActivity.class);
                } else {
                    if (id > 0) {
                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
                        sharedPreferencesManager.clearClientId();
                    } else {
                        Toast.makeText(getApplicationContext(), "Некорректные данные", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

    }

    public void goToRegistration(View view) {
        Intent intent = new Intent(view.getContext(), RegistrationActivity.class);
        view.getContext().startActivity(intent);
    }
}