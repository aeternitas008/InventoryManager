package Model;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "my_preferences";
    private static final String TOKEN_KEY = "token";
    private static final String ID_CLIENT_KEY = "ID_CLIENT";
    private static final String ID_WAREHOUSE_KEY = "ID_WAREHOUSE";
    private static final String TITLE_WAREHOUSE_KEY = "TITLE_WAREHOUSE";

    private final SharedPreferences preferences;

    public void clearClientId() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(ID_CLIENT_KEY, 0);
        editor.apply();
    }

    public SharedPreferencesManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveTitleWarehouse(String title_warehouse) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TITLE_WAREHOUSE_KEY, title_warehouse);
        editor.apply();
    }

    public String getTitleWarehouse() {
        return preferences.getString(TITLE_WAREHOUSE_KEY, null);
    }

    public void saveToken(String token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    public String getToken() {
        return preferences.getString(TOKEN_KEY, null);
    }

    public void saveClientId(int clientId) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(ID_CLIENT_KEY, clientId);
        editor.apply();
    }

    public int getClientId() {
        return preferences.getInt(ID_CLIENT_KEY, -1);
    }

    public void saveWarehouseId(int warehouseId) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(ID_WAREHOUSE_KEY, warehouseId);
        editor.apply();
    }

    public int getWarehouseId() {
        return preferences.getInt(ID_WAREHOUSE_KEY, -1);
    }
}
