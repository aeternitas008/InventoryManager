package com.example.my_accounting;

import static Utils.Helper.println;
import static Utils.Util.WAREHOUSE_BASE_TITLE;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Adapters.DocsAdapter;
import Adapters.WarehouseAdapter;
import Data.SupabaseHandler;
import Model.Clients;
import Model.Documents;
import Model.SharedPreferencesManager;
import Model.Warehouses;

public class WarehousesActivity extends BaseActivity implements WarehouseAdapter.OnWarehouseClickListener {
    private List<Warehouses> warehousesList = new ArrayList<>();
    private WarehouseAdapter warehouseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouses);

        setMainTitle(WAREHOUSES);
        setWarehouseTitle();

        RelativeLayout relativeLayout = findViewById(R.id.pick_warehouse);
        relativeLayout.setClickable(false);

        RecyclerView rv_warehouses = findViewById(R.id.rv_warehouses);
        rv_warehouses.setLayoutManager(new LinearLayoutManager(this));

        ImageView add = getImageView(R.id.button_add);
        add.setVisibility(View.VISIBLE);

        add.setOnClickListener(v -> {
            goToActivityWithExtra(AddActivity.class, WAREHOUSES);
        });
        // Создаем адаптер и устанавливаем его в RecyclerView
        warehouseAdapter = new WarehouseAdapter(this, warehousesList);
        rv_warehouses.setAdapter(warehouseAdapter);
        // Загружаем список складов
        loadWarehouses();
    }

    public void loadWarehouses() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            SupabaseHandler supabaseHandler = new SupabaseHandler();
            try {
                warehousesList = supabaseHandler.getListWarehouses(this);
                System.out.println(warehousesList.size());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            handler.post(() -> {
                warehouseAdapter.setWarehousesList(warehousesList);
                warehouseAdapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    public void onWarehouseClick(Warehouses warehouse) {
        // Здесь вы определяете, что происходит при нажатии на элемент, открыть новый экран с деталями документа
        Toast.makeText(this, "Выбран склад: " + warehouse.getTitle(), Toast.LENGTH_SHORT).show();
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        sharedPreferencesManager.saveTitleWarehouse(warehouse.getTitle());
        sharedPreferencesManager.saveWarehouseId(warehouse.getId());
        setCurrentWarehouse();
        goToActivity(MainActivity.class);
        finish();
    }

    public void setCurrentWarehouse() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            SupabaseHandler supabaseHandler = new SupabaseHandler();
            try {
                supabaseHandler.updateCurrentWarehouseId(this);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

    }

}