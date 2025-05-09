package com.example.my_accounting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.SQLException;

import Model.SharedPreferencesManager;
import Utils.Helper;

public class BaseActivity extends AppCompatActivity {

//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    // @TODO надо это куда то пристроить, например в strings? и можно уменьшить разнообразие
    static final String GOODS = "Товары";
    static final String WAREHOUSES = "Склады";
    static final String SCANNER = "Сканер";
    static final String DOCS = "Документы";
    static final String DOC = "Документ";
    static final String MAIN_MENU = "Главное меню";
    static final String REPORTS = "Отчёты";
    static final String INPUTS = "Приходы";
    static final String OUTPUTS = "Расходы";
    static final String ALL = "Все документы";
    static final String INVENT = "Инвентаризация";
    static final String CREATE = "Создание";
    static final String SOURCE= "source";

    static final String GOOD = "Товар";
    static final String WAREHOUSE = "Склад";
    static final String DOC_INPUT = "Приход";
    static final String DOC_OUTPUT = "Расход";
    static final String DOC_INVENT = "Инвентаризация";


    static final int DOC_INPUT_ID = 0,  DOC_OUTPUT_ID = 1, DOC_INVENT_ID = 2, DOC_ALL = 3;

    protected void goToActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    protected void setMainTitle(String title) {
        TextView tv_title = getTextView(R.id.title_page);
        tv_title.setText(title);
    }


    protected void setWarehouseTitle() {
        TextView tv_warehouse = getTextView(R.id.tv_warehouse);
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        tv_warehouse.setText(sharedPreferencesManager.getTitleWarehouse());
    }

    protected void goToActivityWithExtra(Class<?> cls, String source) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(SOURCE, source);
        startActivity(intent);
    }

    protected Button getButton(int id) {
        Button button = (Button) findViewById(id);
        return button;
    }

    protected EditText getEditText(int id) {
        EditText et = (EditText) findViewById(id);
        return et;
    }

    protected TextView getTextView(int id) {
        TextView tv = (TextView) findViewById(id);
        return tv;
    }

    protected ImageView getImageView(int id) {
        ImageView iv = (ImageView) findViewById(id);
        return iv;
    }

    protected void setAllVisible(ImageView download, ImageView add, ImageView search){
        download.setVisibility(View.VISIBLE);
        add.setVisibility(View.VISIBLE);
        search.setVisibility(View.VISIBLE);
    }

    public void openWarehouses(View view) {
        goToActivity(WarehousesActivity.class);
    }

    public void backToPrevious(View view) {
        getOnBackPressedDispatcher().onBackPressed();
    }

    public void goToAdd(@NonNull View view) {
        goToActivity(AddActivity.class);
    }


    String getTextFromEditText(int id) {
        return getEditText(id).getText().toString();
    }

}
