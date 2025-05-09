package com.example.my_accounting;

import static Utils.Helper.println;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Adapters.DocsAdapter;
import Adapters.GoodsAdapter;
import Data.SupabaseHandler;
import Model.Documents;
import Model.Goods;

public class DocsActivity extends BaseActivity implements DocsAdapter.OnDocumentClickListener {
    String[] types_docs = { INPUTS, OUTPUTS, INVENT, ALL};

    private List<Documents> docsList = new ArrayList<>();
    private DocsAdapter docsAdapter;

    @Override
    public void onDocumentClick(Documents doc) {
        // Здесь вы определяете, что происходит при нажатии на элемент, открыть новый экран с деталями документа
        Toast.makeText(this, "this document id:" + doc.getId(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, DetailsDocumentActivity.class);
        intent.putExtra("document_id", String.valueOf(doc.getId()));
        intent.putExtra("document_type", String.valueOf(doc.getTypeString()));
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        setMainTitle(DOCS);
        setWarehouseTitle();

        ImageView download, add, search;
        download = getImageView(R.id.button_download);
//        add = getImageView(R.id.button_add);
        search = getImageView(R.id.button_search);
        //@TODO продумать как лучше делать видимыми на определённых страницах
        setAllVisible(download, download, search);

        Spinner spinner = findViewById(R.id.spinner_docs);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, types_docs);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        spinner.setAdapter(adapter);

        Button butAddDocs = getButton(R.id.button_add_docs);
        butAddDocs.setOnClickListener(v -> {
            if (spinner.getSelectedItemPosition() == 1) {
                goToActivityWithExtra(AddDocumentActivity.class, DOC_OUTPUT);
            } else if (spinner.getSelectedItemPosition() == 2) {
                goToActivityWithExtra(AddDocumentActivity.class, DOC_INVENT);
            } else {
                goToActivityWithExtra(AddDocumentActivity.class, DOC_INPUT);
            }
        });

        RecyclerView rv_docs = findViewById(R.id.rv_docs);
        rv_docs.setLayoutManager(new LinearLayoutManager(this));

        // Создаем адаптер и устанавливаем его в RecyclerView
        docsAdapter = new DocsAdapter(this, docsList);
        rv_docs.setAdapter(docsAdapter);

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

    public void loadDocs(int type) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            SupabaseHandler supabaseHandler = new SupabaseHandler();
            try {
                List<Documents> newDocsList  = supabaseHandler.getListDocs(this, type);
                handler.post(() -> {
                    docsList.clear();
                    docsList.addAll(newDocsList);
                    docsAdapter.notifyDataSetChanged();
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateDisplayBasedOnSelection(int position) {
        switch (position) {
            case DOC_INPUT_ID:
                loadDocs(DOC_INPUT_ID);
                break;
            case DOC_OUTPUT_ID:
                loadDocs(DOC_OUTPUT_ID);
                break;
            case DOC_INVENT_ID:
                loadDocs(DOC_INVENT_ID);
                break;
            case DOC_ALL:
                loadDocs(DOC_ALL);
                break;
        }
    }
}