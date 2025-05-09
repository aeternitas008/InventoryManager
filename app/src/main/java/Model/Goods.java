package Model;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Goods {
    private int id;
    private String title;
    private String description;
    private int id_warehouse;

    private String barcode;
    private int price;

    public Goods() {
    }

    public Goods(String title, String barcode, int id_warehouse) {
        this.title = title;
        this.barcode = barcode;
        this.id_warehouse = id_warehouse;
    }

    public Goods(String title, String barcode, int id_warehouse, int price) {
        this.title = title;
        this.barcode = barcode;
        this.id_warehouse = id_warehouse;
        this.price = price;
    }

    public Goods(String title, String barcode, int id_warehouse, String description) {
        this.title = title;
        this.barcode = barcode;
        this.id_warehouse = id_warehouse;
        this.description = description;
    }

    public Goods(String title, String barcode, int id_warehouse, String description, int price) {
        this.title = title;
        this.barcode = barcode;
        this.id_warehouse = id_warehouse;
        this.description = description;
        this.price = price;
    }

    public Goods(int id, String title, String barcode, int id_warehouse) {
        this.id = id;
        this.title = title;
        this.barcode = barcode;
        this.id_warehouse = id_warehouse;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }


    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIdWarehouse() {
        return id_warehouse;
    }

    public void setIdWarehouse(int id_warehouse) {
        this.id_warehouse = id_warehouse;
    }

}
