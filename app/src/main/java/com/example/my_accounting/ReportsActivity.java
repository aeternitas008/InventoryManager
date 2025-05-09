package com.example.my_accounting;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Data.SupabaseHandler;
import Model.Goods;

public class ReportsActivity extends BaseActivity {

    private TableLayout tableReports;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        setMainTitle(REPORTS);
        setWarehouseTitle();

        tableReports = findViewById(R.id.table_reports);
        generateAndDisplayReports();
    }

    private void generateAndDisplayReports() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String topSellingProductsReport = turnoverOfGoods();

            runOnUiThread(() -> {
                displayReportInTable(topSellingProductsReport);
            });
        });
    }

    public String turnoverOfGoods() {
        StringBuilder report;
        SupabaseHandler supabaseHandler = new SupabaseHandler();
        report = supabaseHandler.getTurnoverOfGoods(this);
        return report.toString();
    }

    private void displayReportInTable(String report) {
        String[] rows = report.split("\n");

        String[] headers = {"Товар", "Штрихкод", "Приход", "Расход", "Остаток"};

        TableRow headerRow = new TableRow(this);

        for (String head : headers) {
            TextView tv_head = new TextView(this);
            tv_head.setText(head);
            tv_head.setTextSize(16);
            tv_head.setPadding(10, 10, 10, 10);
            headerRow.addView(tv_head);
        }

        tableReports.addView(headerRow);

        for (String row : rows) {
            String[] columns = row.split("\t");

            if (columns.length < 5) continue; // Проверка на наличие достаточного количества данных

            String product = columns[0];
            String barcode = columns[1];
            int income = Integer.parseInt(columns[2].trim());
            int expense = Integer.parseInt(columns[3].trim());
            int balance = Integer.parseInt(columns[4].trim());

            TableRow tableRow = new TableRow(this);

            String[] data = {product, barcode, String.valueOf(income), String.valueOf(expense), String.valueOf(balance)};
            for (String datum : data) {
                TextView textView = new TextView(this);
                textView.setText(datum);
                textView.setPadding(3, 5, 3, 5);
                textView.setTextSize(14);
                textView.setBackground(getDrawable(R.drawable.table_cell_border));
                tableRow.addView(textView);
            }

            tableReports.addView(tableRow);
        }
    }
}
