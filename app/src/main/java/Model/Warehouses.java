package Model;

public class Warehouses {
    private int id;
    private String title;
    private int id_client;

    public Warehouses() {
    }

    public Warehouses(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Warehouses(String title, int id_client) {
        this.title = title;
        this.id_client = id_client;
    }

    public Warehouses(int currentWarehouseId) {
        this.id = currentWarehouseId;
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

    public void setTitle(String name) {
        this.title = name;
    }

    public int getIdClient() {
        return id_client;
    }

    public void setId_client(int id_client) {
        this.id_client = id_client;
    }
}
