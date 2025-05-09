package com.example.my_accounting;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.sql.SQLException;

import Data.SupabaseHandler;
import Model.SharedPreferencesManager;
import Model.Warehouses;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setMainTitle(MAIN_MENU);
        setWarehouseTitle();

        Button button_goods, button_docs, button_scanner, button_reports, button_input, button_output;

        button_goods = getButton(R.id.button_goods);
        button_docs = getButton(R.id.button_docs);
        button_scanner = getButton(R.id.button_scanner);
        button_reports = getButton(R.id.button_reports);
        button_input = getButton(R.id.button_input);
        button_output = getButton(R.id.button_output);

        button_goods.setOnClickListener(view -> {
            goToActivity(GoodsActivity.class);
        });

        button_docs.setOnClickListener(view -> {
            goToActivity(DocsActivity.class);
        });

        button_scanner.setOnClickListener(view -> {
            goToActivity(ScannerActivity.class);
        });

        button_reports.setOnClickListener(view -> {
            goToActivity(ReportsActivity.class);
        });

        button_input.setOnClickListener(view -> {
            goToActivityWithExtra(AddDocumentActivity.class, DOC_INPUT);
        });

        button_output.setOnClickListener(view -> {
            goToActivityWithExtra(AddDocumentActivity.class, DOC_OUTPUT);
        });


    }
}
