package Model;

public class Clients {
    private int id;
    private String email;
    private String password;
    private String token;
    private String phone;

    private int currentWarehouseId;

    public Clients(int id) {
        this.id = id;
    }

    public Clients(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getCurrentWarehouseId() {
        return currentWarehouseId;
    }

    public void setCurrentWarehouseId(int currentWarehouseId) {
        this.currentWarehouseId = currentWarehouseId;
    }
}
