package Model;

import java.util.Date;

public class Documents {
    int id;
    int type;
    String typeString;
    int id_warehouse;

    Date created_at;

    public Documents(int type, int id_warehouse) {
        this.type = type;
        this.id_warehouse = id_warehouse;
        this.typeString = getStringType(type);
    }


    public Documents(int id, Date created_at, int type) {
        this.id = id;
        this.created_at = created_at;
        this.type = type;
        this.typeString = getStringType(type);
    }

    public String getTypeString() {
        return typeString;
    }

    public void setTypeString(String typeString) {
        this.typeString = typeString;
    }

    public int getId() {
        return id;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId_warehouse() {
        return id_warehouse;
    }

    public void setId_warehouse(int id_warehouse) {
        this.id_warehouse = id_warehouse;
    }


    public String getStringType(int id) {
        String string_type;
        switch (id) {
            case 0:
                string_type = "Приход";
                break;
            case 1:
                string_type = "Расход";
                break;
            case 2:
                string_type = "Инвентар.";
                break;
            default:
                string_type = "Ошибка";
                break;
        }
        return string_type;
    }
}
