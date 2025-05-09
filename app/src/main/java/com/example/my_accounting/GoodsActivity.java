package com.example.my_accounting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Adapters.GoodsAdapter;
import Data.SupabaseHandler;
import Model.Goods;

public class GoodsActivity extends BaseActivity implements GoodsAdapter.OnDeleteClickListener{

    private List<Goods> goodsList = new ArrayList<>();
    private GoodsAdapter goodsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods);

        setMainTitle(GOODS);
        setWarehouseTitle();

        ImageView download, add, search;
        download = getImageView(R.id.button_download);
        add = getImageView(R.id.button_add);
        search = getImageView(R.id.button_search);
        //@TODO продумать как лучше делать видимыми на определённых страницах
        setAllVisible(download, add, search);

        add.setOnClickListener(v -> {
            goToActivityWithExtra(AddActivity.class, GOODS);
        });

        RecyclerView rv_goods = findViewById(R.id.rv_goods);
        rv_goods.setLayoutManager(new LinearLayoutManager(this));

        goodsAdapter = new GoodsAdapter(this, goodsList);
        rv_goods.setAdapter(goodsAdapter);

        loadGoods();
    }

    public void loadGoods() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            SupabaseHandler supabaseHandler = new SupabaseHandler();
            try {
                goodsList = supabaseHandler.getListGoods(this);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            handler.post(() -> {
                goodsAdapter.setGoodsList(goodsList);
                goodsAdapter.notifyDataSetChanged();
            });
        });
    }


    private void deleteGood(Goods good, int position) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            SupabaseHandler supabaseHandler = new SupabaseHandler();
            try {
                supabaseHandler.deleteGood(good); // Метод удаления товара по ID
            } catch (SQLException e) {
                handler.post(() -> {
                    String errorMessage = "Ошибка удаления товара: " + e.getMessage();
                    if (e.getMessage().contains("violates foreign key constraint")) {
                        errorMessage = "Ошибка удаления товара: товар связан с другими записями.";
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                });
                e.printStackTrace(); // Логируйте ошибку
                return;
            }

            handler.post(() -> {
                goodsList.remove(position);
                goodsAdapter.notifyItemRemoved(position);
            });
        });
    }

    @Override
    public void onDeleteClick(Goods good, int position) {
        deleteGood(good, position);
    }
}