package com.example.my_accounting;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Adapters.ItemsAdapter;
import Data.SupabaseHandler;
import Model.ItemsDocuments;

public class DetailsDocumentActivity extends BaseActivity {

    private List<ItemsDocuments> itemsList = new ArrayList<>();
    ItemsAdapter itemsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_document);

        Intent intent = getIntent();
        int idDocument = Integer.parseInt(intent.getStringExtra("document_id"));
        String typeDocument = intent.getStringExtra("document_type");

        String titlePage ="Док. №" + idDocument + "(" + typeDocument + ")";
        //@TODO получить номер документа и справить название
        setMainTitle(titlePage);
        setWarehouseTitle();

        RecyclerView rv_items_document = findViewById(R.id.rv_items_document);
        rv_items_document.setLayoutManager(new LinearLayoutManager(this));

        // Создаем адаптер и устанавливаем его в RecyclerView
        itemsAdapter = new ItemsAdapter(this, itemsList);
        rv_items_document.setAdapter(itemsAdapter);

        loadItems(idDocument);
    }

        public void loadItems(int idDocument) {

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                SupabaseHandler supabaseHandler = new SupabaseHandler();
                try {
                    itemsList = supabaseHandler.getListItems(this, idDocument);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                handler.post(() -> {
                    itemsAdapter.setItemsList(itemsList);
                    itemsAdapter.notifyDataSetChanged();
                });
            });
        }
    }