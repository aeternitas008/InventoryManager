package Utils;

public class Util {

    //SHARED_PREFERENCES UTILS
    public static final String APP_PREFERENCES = "mysettings";
    public static final String TOKEN_CLIENT = "TOKEN";
    public static final String ID_CLIENT = "ID_CLIENT";
    public static final String ID_WAREHOUSE = "ID_WAREHOUSE";


    // DATABASE UTILS
    public static final int DATABASE_VERSION = 1;

    public static final String DB_CONNECTION = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:5432/postgres?user=postgres.liojuqacujjtzrldtynr&password=Supabase26!";
    public static final String DB_DRIVER = "org.postgresql.Driver";


//    public static final String DATABASE_NAME = "goodsDB";
    public static final String TABLE_GOODS = "goods";
//    public static final String GOODS_ID = "id";
    public static final String GOODS_ID = "id";
    public static final String GOODS_TITLE = "title";
    public static final String GOODS_BARCODE = "barcode";
    public static final String GOODS_WAREHOUSE_ID = "warehouse_id";
    public static final String GOODS_DESCRIPTION = "description";

    public static final String TABLE_CLIENTS = "clients";


    public static final String CLIENTS_ID = "id";
    public static final String CLIENTS_EMAIL = "email";
    public static final String CLIENTS_PASSWORD = "password";
    public static final String CLIENTS_TOKEN = "token";
    public static final String CLIENTS_CURRENT_WAREHOUSE_ID = "current_warehouse_id";
    public static final String KEY_ANOTHER = "another";


    public static final String WAREHOUSE_ID = "id";
    public static final String WAREHOUSE_TITLE = "title";
    public static final String WAREHOUSE_ID_CLIENT = "id_client";
    public static final String WAREHOUSE_BASE_TITLE = "Основной";

}
