package com.example.my_accounting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Data.SupabaseHandler;
import Model.Goods;
import Model.SharedPreferencesManager;
import Model.Warehouses;

public class AddActivity extends BaseActivity {

    static final int GOOD_ID = 0;
    static final int WAREHOUSE_ID = 1;
    String[] modes = {GOOD, WAREHOUSE};

    private static final int REQUEST_CODE_SCAN = 100;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        setMainTitle(CREATE);
        setWarehouseTitle();
        setupSpinner();
        handleIntent(getIntent());
        setupBackButton();
        setupBarcodeScanner();
    }

    private void setupSpinner() {
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateDisplayBasedOnSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            String source = intent.getStringExtra(SOURCE);
            Spinner spinner = findViewById(R.id.spinner);
            if (WAREHOUSES.equals(source)) {
                spinner.setSelection(WAREHOUSE_ID);
            } else if (GOODS.equals(source)) {
                spinner.setSelection(GOOD_ID);
            }
        }
    }

    private void setupBackButton() {
        ImageView imageView = getImageView(R.id.iv_back);
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(AddActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupBarcodeScanner() {
        EditText fieldBarcode = getEditText(R.id.field_barcode);
        fieldBarcode.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Drawable drawableEnd = fieldBarcode.getCompoundDrawablesRelative()[2];
                if (drawableEnd != null && event.getRawX() >= (fieldBarcode.getRight() - drawableEnd.getBounds().width())) {
                    Intent intent = new Intent(AddActivity.this, ScannerActivity.class);
                    intent.putExtra("action", "getBarcode");
                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                    return true;
                }
            }
            return false;
        });
    }

    private void updateDisplayBasedOnSelection(int position) {
        EditText[] editTextArray = {
                getEditText(R.id.field_title), getEditText(R.id.field_barcode),
                getEditText(R.id.field_description), getEditText(R.id.field_cost),
        };
        for (EditText editText : editTextArray) {
            editText.setVisibility(View.GONE);
        }
        switch (position) {
            case WAREHOUSE_ID:
                editTextArray[0].setVisibility(View.VISIBLE);
                break;
            case GOOD_ID:
                for (int i = 0; i <= 3; i++) {
                    editTextArray[i].setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK && data != null) {
            String barcode = data.getStringExtra("barcode");
            EditText fieldBarcode = getEditText(R.id.field_barcode);
            fieldBarcode.setText(barcode);
        }
    }

    public void addToDb(View view) {
        Spinner spinner = findViewById(R.id.spinner);
        int mode = spinner.getSelectedItemPosition();
        if (mode == GOOD_ID) {
            addGoodToDB();
        } else if (mode == WAREHOUSE_ID) {
            addWarehouseToDB();
        }
    }

    private void addGoodToDB() {
        String title = getTextFromEditText(R.id.field_title);
        String barcode = getTextFromEditText(R.id.field_barcode);
        String description = getTextFromEditText(R.id.field_description);
        int price = 0;
        if(!getTextFromEditText(R.id.field_cost).equals(""))
            price = Integer.parseInt(getTextFromEditText(R.id.field_cost));

        if (title.isEmpty() || barcode.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Заполните все обязательные поля (*)", Toast.LENGTH_SHORT).show();
            return; // Если какое-то из обязательных полей не заполнено, прерываем выполнение метода
        }

        int currentWarehouseId = new SharedPreferencesManager(this).getWarehouseId();
        Goods good = new Goods(title, barcode, currentWarehouseId, description, price);
        if (price != 0) {
            if (!description.equals("")) {
                addEntityToDB(() -> new SupabaseHandler().addGoodWithPriceAndDescription(good), "Товар добавлен", "Ошибка, товар не добавлен");
            } else {
                addEntityToDB(() -> new SupabaseHandler().addGoodWithPrice(good), "Товар добавлен", "Ошибка, товар не добавлен");
            }
        } else if (!description.equals("")) {
            addEntityToDB(() -> new SupabaseHandler().addGoodWithDescription(good), "Товар добавлен", "Ошибка, товар не добавлен");
        } else {
            addEntityToDB(() -> new SupabaseHandler().addGood(good), "Товар добавлен", "Ошибка, товар не добавлен");
        }
    }

    private void addWarehouseToDB() {
        String title = getTextFromEditText(R.id.field_title);
        if (title.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Заполните все обязательные поля", Toast.LENGTH_SHORT).show();
            return; // Если какое-то из обязательных полей не заполнено, прерываем выполнение метода
        }
        int clientId = new SharedPreferencesManager(this).getClientId();
        Warehouses warehouse = new Warehouses(title, clientId);
        addEntityToDB(() -> new SupabaseHandler().addWarehouse(warehouse), "Склад добавлен", "Ошибка, склад не добавлен");
    }

    private void addEntityToDB(DatabaseAction action, String successMessage, String failureMessage) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            int id;
            try {
                id = action.perform();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            int finalSuccess = id;
            handler.post(() -> {
                if (finalSuccess > 0) {
                    Toast.makeText(getApplicationContext(), successMessage, Toast.LENGTH_SHORT).show();
                    goToActivity(AddActivity.class);
                } else {
                    Toast.makeText(getApplicationContext(), failureMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private interface DatabaseAction {
        int perform() throws SQLException;
    }
}
