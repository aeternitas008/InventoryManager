package Data;

import static Utils.Util.*;

import java.sql.*;
import java.util.ArrayList;

import Model.Clients;
import Model.Documents;
import Model.Goods;
import Model.ItemsDocuments;
import Model.Warehouses;

public abstract class BaseDatabaseHandler {

    protected Connection getDBConnection() throws SQLException {
        try {
            Class.forName(DB_DRIVER);
            return DriverManager.getConnection(DB_CONNECTION);
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
    }

    protected void setInsertParameters(PreparedStatement preparedStatement, Object object) throws SQLException {
        if (object instanceof Clients) {
            Clients client = (Clients) object;
            preparedStatement.setString(1, client.getEmail().toLowerCase());
            preparedStatement.setString(2, client.getPassword());
        } else if (object instanceof Warehouses) {
            Warehouses warehouse = (Warehouses) object;
            preparedStatement.setString(1, warehouse.getTitle());
            preparedStatement.setInt(2, warehouse.getIdClient());
        } else if (object instanceof Goods) {
            Goods good = (Goods) object;
            preparedStatement.setString(1, good.getTitle());
            preparedStatement.setString(2, good.getBarcode());
            preparedStatement.setInt(3, good.getIdWarehouse());
            if (good.getPrice() != 0) preparedStatement.setInt(4, good.getPrice());
            if (good.getPrice() != 0 && !good.getDescription().equals("")) {
                preparedStatement.setString(5, good.getDescription());
            } else if (!good.getDescription().equals("")) {
                preparedStatement.setString(4, good.getDescription());
            }
        } else if (object instanceof Documents) {
            Documents doc = (Documents) object;
            preparedStatement.setInt(1, doc.getId_warehouse());
            preparedStatement.setInt(2, doc.getType());
        }
    }

    protected void setSelectParameters(PreparedStatement preparedStatement, Object object) throws SQLException {
        if (object instanceof Clients) {
            Clients client = (Clients) object;
            if (client.getId() > 0) {
                preparedStatement.setInt(1, client.getId());
            } else {
                preparedStatement.setString(1, client.getEmail());
                preparedStatement.setString(2, client.getPassword());
            }
        } else if (object instanceof Warehouses) {
            Warehouses warehouse = (Warehouses) object;
            preparedStatement.setInt(1, warehouse.getId());
        } else if (object instanceof Goods) {
            Goods good = (Goods) object;
            preparedStatement.setString(1, good.getTitle());
            preparedStatement.setInt(2, good.getIdWarehouse());
        }
    }

    protected void setUpdateParameters(PreparedStatement preparedStatement, Object object) throws SQLException {
        if (object instanceof Clients) {
            Clients client = (Clients) object;
            preparedStatement.setInt(1, client.getCurrentWarehouseId());
            preparedStatement.setInt(2, client.getId());
        }
    }

    protected void setDeleteParameters(PreparedStatement preparedStatement, Object object) throws SQLException {
        if (object instanceof Goods) {
            Goods good = (Goods) object;
            preparedStatement.setInt(1, good.getId());
        }
    }

    protected Object getObjectFromResultSet(ResultSet resultSet, Object object) throws SQLException {
        if (resultSet.next()) {
            if (object instanceof Clients) {
                Clients client = (Clients) object;
                if (client.getId() == 0) {
                    client.setId(resultSet.getInt(CLIENTS_ID));
                }
                client.setToken(resultSet.getString(CLIENTS_TOKEN));
                client.setCurrentWarehouseId(resultSet.getInt(CLIENTS_CURRENT_WAREHOUSE_ID));
                return client;
            } else if (object instanceof Warehouses) {
                Warehouses warehouse = (Warehouses) object;
                warehouse.setId(resultSet.getInt(WAREHOUSE_ID));
                warehouse.setTitle(resultSet.getString(WAREHOUSE_TITLE));
                warehouse.setId_client(resultSet.getInt(WAREHOUSE_ID_CLIENT));
                return warehouse;
            } else if (object instanceof Goods) {
                Goods good = (Goods) object;
                good.setId(resultSet.getInt(GOODS_ID));
                good.setTitle(resultSet.getString(GOODS_TITLE));
                good.setBarcode(resultSet.getString(GOODS_BARCODE));
                good.setIdWarehouse(resultSet.getInt(GOODS_WAREHOUSE_ID));
                return good;
            }
        }
        return null;
    }

    public Object selectObjectFromDB(Object object, String query) throws SQLException {
        try (Connection dbConnection = getDBConnection();
             PreparedStatement preparedStatement = dbConnection.prepareStatement(query)) {
            setSelectParameters(preparedStatement, object);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return getObjectFromResultSet(resultSet, object);
            }
        }
    }

    public ArrayList<Goods> getGoodsListFromResultSet(ResultSet resultSet) throws SQLException {
        ArrayList<Goods> list_goods = new ArrayList<>();
        while (resultSet.next()) {
            Goods good = new Goods(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getInt(4));
            list_goods.add(good);
        }
        return list_goods;
    }

    public ArrayList<ItemsDocuments> getItemsListFromResultSet(ResultSet resultSet) throws SQLException {
        ArrayList<ItemsDocuments> listItems = new ArrayList<>();
        while (resultSet.next()) {
            ItemsDocuments item = new ItemsDocuments(resultSet.getString(1), resultSet.getString(2), resultSet.getInt(3));
            listItems.add(item);
        }
        return listItems;
    }

    public ArrayList<Documents> getDocsListFromResultSet(ResultSet resultSet) throws SQLException {
        ArrayList<Documents> list_docs = new ArrayList<>();
        while (resultSet.next()) {
            Documents doc = new Documents(resultSet.getInt(1), resultSet.getDate(2), resultSet.getInt(3));
            list_docs.add(doc);
        }
        return list_docs;
    }

    public ArrayList<Warehouses> getWarehousesListFromResultSet(ResultSet resultSet) throws SQLException {
        ArrayList<Warehouses> list_warehouses = new ArrayList<>();
        while (resultSet.next()) {
            Warehouses warehouse = new Warehouses(resultSet.getInt(1), resultSet.getString(2));
            list_warehouses.add(warehouse);
        }
        return list_warehouses;
    }

    public ArrayList<Warehouses> selectWarehousesFromDB(String query, int id_client) throws SQLException {
        try (Connection dbConnection = getDBConnection();
             PreparedStatement preparedStatement = dbConnection.prepareStatement(query)) {
            preparedStatement.setInt(1, id_client);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return getWarehousesListFromResultSet(resultSet);
            }
        }
    }

        public ArrayList<Goods> selectGoodsFromDB(String query, int id_warehouse) throws SQLException {
            try (Connection dbConnection = getDBConnection();
                 PreparedStatement preparedStatement = dbConnection.prepareStatement(query)) {
                preparedStatement.setInt(1, id_warehouse);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return getGoodsListFromResultSet(resultSet);
                }
            }
        }

        public ArrayList<ItemsDocuments> selectItemsFromDB(String query, int id_document) throws SQLException {
            try (Connection dbConnection = getDBConnection();
                 PreparedStatement preparedStatement = dbConnection.prepareStatement(query)) {
                preparedStatement.setInt(1, id_document);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return getItemsListFromResultSet(resultSet);
                }
            }
        }

        public ArrayList<Documents> selectDocsFromDB(String query, int id_warehouse) throws SQLException {
            try (Connection dbConnection = getDBConnection();
                 PreparedStatement preparedStatement = dbConnection.prepareStatement(query)) {
                preparedStatement.setInt(1, id_warehouse);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return getDocsListFromResultSet(resultSet);
                }
            }
        }

        public Goods selectGoodByBarcode(String query, int id_warehouse, String barcode) throws SQLException {
            try (Connection dbConnection = getDBConnection();
                 PreparedStatement preparedStatement = dbConnection.prepareStatement(query)) {
                preparedStatement.setInt(1, id_warehouse);
                preparedStatement.setString(2, barcode);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return new Goods(resultSet.getInt(1), resultSet.getString(2), barcode, id_warehouse);
                    }
                    return null;
                }
            }
        }

        public int insertObjectToDB(Object object, String query) throws SQLException {
            try (Connection dbConnection = getDBConnection();
                 PreparedStatement preparedStatement = dbConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                setInsertParameters(preparedStatement, object);
                preparedStatement.executeUpdate();
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1);
                    }
                }
            }
            return -1;
        }

        public boolean updateClients(Clients client, String query) throws SQLException {
            try (Connection dbConnection = getDBConnection();
                 PreparedStatement preparedStatement = dbConnection.prepareStatement(query)) {
                setUpdateParameters(preparedStatement, client);
                return preparedStatement.executeUpdate() > 0;
            }
        }

        public boolean deleteFromDB(Goods good, String query) throws SQLException {
            try (Connection dbConnection = getDBConnection();
                PreparedStatement preparedStatement = dbConnection.prepareStatement(query)) {
                setDeleteParameters(preparedStatement, good);
                return preparedStatement.executeUpdate() > 0;
            }
        }

    }
