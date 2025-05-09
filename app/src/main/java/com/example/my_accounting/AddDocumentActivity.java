package com.example.my_accounting;

import static Utils.Helper.println;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Adapters.ItemBlocksAdapter;
import Data.SupabaseHandler;
import Model.Documents;
import Model.Goods;
import Model.ItemsDocuments;
import Model.SharedPreferencesManager;

public class AddDocumentActivity extends BaseActivity implements ItemBlocksAdapter.OnBarcodeScanListener {

    public static final int REQUEST_CODE_SCAN = 100;
    private static final String[] modes = {DOC_INPUT, DOC_OUTPUT, DOC_INVENT};

    private HashMap<String, Integer> mapIdGoods = new HashMap<>();
    private RecyclerView recyclerView;
    private ItemBlocksAdapter itemsAdapter;
    private List<ItemsDocuments> itemsList = new ArrayList<>();
    private EditText currentBarcodeEditText;
    private TextView currentNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_document);

        setMainTitle(CREATE);
        setWarehouseTitle();
        setupSpinner();
        handleIntent(getIntent());
        setupBackButton();
        setupRecyclerView();
        setupAddFieldButton();
    }

    private void setupSpinner() {
        Spinner spinner = findViewById(R.id.spinner_docs);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            Spinner spinner = findViewById(R.id.spinner_docs);
            String source = intent.getStringExtra(SOURCE);
            if (DOC_INPUT.equals(source)) {
                spinner.setSelection(DOC_INPUT_ID);
            } else if (DOC_OUTPUT.equals(source)) {
                spinner.setSelection(DOC_OUTPUT_ID);
            } else if (DOC_INVENT.equals(source)) {
                spinner.setSelection(DOC_INVENT_ID);
            }
        }
    }

    private void setupBackButton() {
        ImageView imageView = getImageView(R.id.iv_back);
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(AddDocumentActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.rv_item_blocks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemsAdapter = new ItemBlocksAdapter(this, itemsList, this);
        recyclerView.setAdapter(itemsAdapter);
    }

    private void setupAddFieldButton() {
        Button buttonAddField = getButton(R.id.button_add_field);
        buttonAddField.setOnClickListener(view -> addNewInputBlock());
    }
 
    private void addNewInputBlock() {
        ItemsDocuments newItem = new ItemsDocuments();
        itemsList.add(newItem);
        itemsAdapter.notifyItemInserted(itemsList.size() - 1);
    }

    @Override
    public void onBarcodeScanRequested(EditText editText, TextView textView) {
        this.currentBarcodeEditText = editText;
        this.currentNameTextView = textView;
        Intent intent = new Intent(this, ScannerActivity.class);
        intent.putExtra("action", "getBarcode");
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK && data != null) {
            String scannedBarcode = data.getStringExtra("barcode");
            if (currentBarcodeEditText != null) {
                currentBarcodeEditText.setText(scannedBarcode);
                if (!scannedBarcode.isEmpty()) {
                    getGood(scannedBarcode, good -> {
                        if (good != null) {
                            currentNameTextView.setText(good.getTitle());
                            currentBarcodeEditText.setBackgroundColor(ContextCompat.getColor(AddDocumentActivity.this, R.color.white));
                            mapIdGoods.put(scannedBarcode, good.getId());

                            // Найти элемент в itemsList и обновить заголовок
                            for (ItemsDocuments item : itemsList) {
                                if (item.getBarcodeGood().equals(scannedBarcode)) {
                                    item.setTitleGood(good.getTitle());
                                    item.setIdGood(good.getId());
                                    break;
                                }
                            }

                        } else {
                            currentBarcodeEditText.setBackgroundColor(ContextCompat.getColor(AddDocumentActivity.this, R.color.red));
                            currentNameTextView.setText("");
                        }
                        resetCurrentFields();
                    }, getBaseContext());
                }
            }
        }
    }

    private void resetCurrentFields() {
        currentBarcodeEditText = null;
        currentNameTextView = null;
    }

    public interface GoodsCallback {
        void onGoodReceived(Goods good);
    }

    public static void getGood(String scannedBarcode, GoodsCallback callback, Context context) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Goods good;
            SupabaseHandler supabaseHandler = new SupabaseHandler();
            try {
                good = supabaseHandler.getGoodByBarcode(context, scannedBarcode);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            handler.post(() -> callback.onGoodReceived(good));
        });
    }

    private List<ItemsDocuments> getValuesFromBlocks() {
        return new ArrayList<>(itemsList);
    }

    private boolean validateInputs() {
        for (ItemsDocuments item : itemsList) {
            if (item.getBarcodeGood().isEmpty()) {
                Toast.makeText(this, "Заполните все поля с штрих-кодами", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (item.getQuantity() <= 0) {
                Toast.makeText(this, "Заполните все поля с количеством", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (item.getTitleGood().equals("")) {
                Toast.makeText(this, "Проверьте штрихкоды, товаров нет в базе", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public void addDocToDB(View view) {
        if (!validateInputs()) {
            return;
        }

        SharedPreferencesManager preferencesManager = new SharedPreferencesManager(this);
        int currentWarehouseId = preferencesManager.getWarehouseId();

        Spinner spinner = findViewById(R.id.spinner_docs);
        int mode = spinner.getSelectedItemPosition();
        Documents doc = new Documents(mode, currentWarehouseId);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        SupabaseHandler supabaseHandler = new SupabaseHandler();
        executor.execute(() -> {
            int id;
            int success;
            try {
                id = supabaseHandler.addDocument(doc);
                doc.setId(id);
                success = supabaseHandler.insertItemsDocument(getValuesFromBlocks(), doc);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            handler.post(() -> {
                if (success > 0) {
                    Toast.makeText(getApplicationContext(), "Документ добавлен", Toast.LENGTH_SHORT).show();
                    finish();
                    goToActivity(MainActivity.class);
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка, документ не добавлен", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
