package Data;

//import io.supabase.SupabaseClient;
//
//String supabaseUrl = "https://your-project.supabase.co";
//        String supabaseKey = "your-anon-key";
//        SupabaseClient client = SupabaseClient.create(supabaseUrl, supabaseKey);


import static Utils.Helper.println;

import android.content.Context;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Clients;
import Model.Documents;
import Model.Goods;
import Model.ItemsDocuments;
import Model.SharedPreferencesManager;
import Model.Warehouses;

public class SupabaseHandler extends BaseDatabaseHandler {

    public Clients getClientFromId(Context context, int id) throws SQLException {
        String token = null;
        String queryGetClientFromId = "SELECT id, token, current_warehouse_id FROM clients where id = ?";
        Clients client =  new Clients(id) ;
        Object object = selectObjectFromDB(client, queryGetClientFromId);
        client = (Clients) object;
        return client;
    }

    public Clients getClientFromEmail(Context context, String email, String password) throws SQLException {
        String queryGetClientFromEmail = "SELECT id, token, current_warehouse_id FROM clients WHERE email = ? AND password = ?";
        Clients client = new Clients(email, password);
        Object object = selectObjectFromDB(client, queryGetClientFromEmail);
        Clients clients = (Clients) object;
        if (clients != null) {
            SharedPreferencesManager preferencesManager = new SharedPreferencesManager(context);
            preferencesManager.saveClientId(client.getId());
            preferencesManager.saveToken(clients.getToken());
        } else {
            //@TODO Вывести ошибку что нет такого пользователя  или неправильный пароль? ? два запроса в бд?
            //@TODO уточнить почему выводит много информации в лог jdbs у gpt
            System.out.println("Пользователь с такими данными не найден");
        }
        return clients;
    }

    public int addClientToDB(Clients client) throws SQLException {
        String queryAddClient = "INSERT INTO clients (email, password) VALUES (?, ?) RETURNING id";
        int id = insertObjectToDB(client, queryAddClient);
        return id;
    }

    public int addGood(Goods good) throws SQLException {
        String queryAddGood = "insert into goods (title, barcode, id_warehouse) values (?, ?, ?) returning id";
        int id = insertObjectToDB(good, queryAddGood);
        return id;
    }

    public int addGoodWithPrice(Goods good) throws SQLException {
        String queryAddGood = "insert into goods (title, barcode, id_warehouse, price) values (?, ?, ?, ?) returning id";
        int id = insertObjectToDB(good, queryAddGood);
        return id;
    }

    public int addGoodWithDescription(Goods good) throws SQLException {
        String queryAddGood = "insert into goods (title, barcode, id_warehouse, description) values (?, ?, ?, ?) returning id";
        int id = insertObjectToDB(good, queryAddGood);
        return id;
    }

    public int addGoodWithPriceAndDescription(Goods good) throws SQLException {
        String queryAddGood = "insert into goods (title, barcode, id_warehouse, price, description) values (?, ?, ?, ?, ?) returning id";
        int id = insertObjectToDB(good, queryAddGood);
        return id;
    }

    public boolean deleteGood(Goods good) throws SQLException {
        String queryAddGood = "delete from goods where id = ?";
        boolean success = deleteFromDB(good, queryAddGood);
        return success;
    }

    public int addDocument(Documents doc) throws SQLException {
        String queryAddDocument = "insert into documents (id_warehouse, type_document) values (?, ?) returning id";
        int id = insertObjectToDB(doc, queryAddDocument);
        return id;
    }

    public int addWarehouse(Warehouses warehouse) throws SQLException {
        String queryAddWarehouse = "insert into warehouses (title, id_client) values (?, ?) returning id";
        int id = insertObjectToDB(warehouse, queryAddWarehouse);
        return id;
    }

    public ResultSet selectFromTable(String table) throws SQLException {
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        String querySelect = "select * from ?";
        //@TODO можно использовать хранимые процедуры чтобы упростить задачи, так же можно сделать запрос из разных баз на основе переменной имени таблицы
        ResultSet result = null;
        try {
            dbConnection = getDBConnection();
            preparedStatement = dbConnection.prepareStatement(querySelect);
            preparedStatement.setString(1, table);
            result = preparedStatement.executeQuery();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (dbConnection != null) {
                dbConnection.close();
            }
        }
        return result;
    }

    public Warehouses createWarehouse(Context context, String title) throws SQLException {
        Warehouses warehouse = new Warehouses();
        warehouse.setTitle(title);
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
        warehouse.setId_client(sharedPreferencesManager.getClientId());
        int id = addWarehouse(warehouse);
        warehouse.setId(id);
        sharedPreferencesManager.saveWarehouseId(warehouse.getId());
        sharedPreferencesManager.saveTitleWarehouse(warehouse.getTitle());
        return warehouse;
    }

    public boolean updateCurrentWarehouseId(Context context) throws SQLException {
        boolean success;
        String queryUpdate = "update clients set current_warehouse_id = ? where id = ?";
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
        Clients client = new Clients(sharedPreferencesManager.getClientId());
        println(String.valueOf(sharedPreferencesManager.getWarehouseId()));
        client.setCurrentWarehouseId(sharedPreferencesManager.getWarehouseId());
        success = updateClients(client, queryUpdate);
        return success;
    }

    public Warehouses getCurrentWarehouse(int id_warehouse) throws SQLException {
        Warehouses warehouse_new = null;
        if (id_warehouse != 0) {
            Warehouses warehouse = new Warehouses(id_warehouse);
            String querySelectWarehouseFromId = "select * from warehouses where id = ?";
            Object object = selectObjectFromDB(warehouse, querySelectWarehouseFromId);
            if (object != null) {
                warehouse_new = (Warehouses) object;
            }
        }
        return warehouse_new;
    }

    public List<Warehouses> getListWarehouses(Context context) throws SQLException {
        ArrayList<Warehouses> list_warehouses;
        String query = "Select * from warehouses where id_client = ?";
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
        list_warehouses = selectWarehousesFromDB(query, sharedPreferencesManager.getClientId());
        return list_warehouses;
    }

    public int insertItemsDocument(List<ItemsDocuments> listItems, Documents doc) throws SQLException {
        int success = 0;
        String query = "INSERT INTO document_items (id_document, id_good, count) VALUES (?, ?, ?)";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getDBConnection();
            connection.setAutoCommit(false); // Начало транзакции

            preparedStatement = connection.prepareStatement(query);

            println("Запись!");
            println(String.valueOf(doc.getId()));

            for (ItemsDocuments item : listItems) {
                println(String.valueOf(item.getIdGood()));
                preparedStatement.setLong(1, doc.getId()); // Предполагается, что в классе Documents есть метод getId(), возвращает long
                preparedStatement.setLong(2, item.getIdGood()); // Предполагается, что в классе ItemsDocuments есть метод getIdGood(), возвращает long
                preparedStatement.setLong(3, item.getQuantity()); // Предполагается, что в классе ItemsDocuments есть метод getQuantity(), возвращает long
                preparedStatement.addBatch();
            }

            int[] results = preparedStatement.executeBatch();
            connection.commit(); // Завершение транзакции

            for (int result : results) {
                if (result >= 0) {
                    success++;
                }
            }
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback(); // Откат транзакции в случае ошибки
            }
            e.printStackTrace();
            throw new SQLException("Error inserting items into document_items", e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return success;
    }


    public List<Goods> getListGoods(Context context) throws SQLException {
        ArrayList<Goods> list_goods;
        String query = "Select id, title, barcode, id_warehouse from goods where id_warehouse = ?";
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
        list_goods = selectGoodsFromDB(query, sharedPreferencesManager.getWarehouseId());
        return list_goods;
    }

    public List<ItemsDocuments> getListItems(Context context, int id_document) throws SQLException {
        ArrayList<ItemsDocuments> listItems;
        String query = "Select title, barcode, count from document_items di join goods g on di.id_good = g.id where id_document = ?";
        listItems = selectItemsFromDB(query, id_document);
        return listItems;
    }

    public List<Documents> getListDocs(Context context, int type) throws SQLException {
        ArrayList<Documents> list_docs;
        String query = "SELECT id, created_at, type_document FROM documents WHERE id_warehouse = ?";

        if (type != 3) {
            query += " AND type_document = " + type;
        }

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
        list_docs = selectDocsFromDB(query, sharedPreferencesManager.getWarehouseId());
        return list_docs;
    }

    public Goods getGoodByBarcode(Context context, String barcode) throws SQLException {
        Goods good;
        String query = "Select id, title from goods where id_warehouse = ? and barcode = ?";
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
        good = selectGoodByBarcode(query, sharedPreferencesManager.getWarehouseId(), barcode);
        return good;
    }

    public StringBuilder getTurnoverOfGoods(Context context) {
        StringBuilder report = new StringBuilder();
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
        int idWarehouse = sharedPreferencesManager.getWarehouseId();
        String query = "select * from calculate_turnover_of_goods(?)";

        try (Connection conn = getDBConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setInt(1, idWarehouse);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String title = rs.getString("title");
                String barcode = rs.getString("barcode");
                int totalIncomeQuantity = rs.getInt("total_income_quantity");
                int totalOutcomeQuantity = rs.getInt("total_outcome_quantity");
                int currentBalance = rs.getInt("current_balance");

                // Добавляем строку в таблицу
                String tableRow = String.format("%s\t%s\t%d\t%d\t%d\n", title, barcode, totalIncomeQuantity, totalOutcomeQuantity, currentBalance);
                report.append(tableRow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            report.append("Ошибка при получении данных.");
        }
        return report;
    }
}
