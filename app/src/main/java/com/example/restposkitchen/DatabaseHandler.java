package com.example.restposkitchen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.restposkitchen.model.KitchenSettingsModel;
import com.example.restposkitchen.model.Orders;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "KitchenDatabase";
    private static final int DATABASE_VERSION = 8;
    static SQLiteDatabase database;

    // ==================================================================================================================
    private static final String SETTINGS_KITCHEN_TABLE = "KITCHEN_SETTINGS";

    private static final String COMPANY_NO = "COMPANY_NO";
    private static final String COMPANY_YEAR = "COMPANY_YEAR";
    private static final String SETTINGS_POS = "SETTINGS_POS";
    private static final String SCREEN_NO = "SCREEN_NO";
    private static final String TIMER_DURATION = "TIMER_DURATION";
    private static final String IP_ADDRESS = "IP_ADDRESS";
    private static final String STAGE_TYPE = "STAGE_TYPE";
    private static final String STAGE_NO = "STAGE_NO";
    private static final String URL = "URL";


    // ==================================================================================================================
    private static final String BINDING_ORDERS_TABLE = "BINDING_ORDERS";

    private static final String BINDING_ORDER_NO = "ORDER_NO";

    // ==================================================================================================================
    private static final String ORDERS_TABLE = "ORDERS";

    private static final String DATE_IN = "DATE_IN";
    private static final String CASH_NO = "CASH_NO";
    private static final String ORDER_NO = "ORDER_NO";
    private static final String ORDER_TYPE = "ORDER_TYPE";
    private static final String ITEM_CODE = "ITEM_CODE";
    private static final String ITEM_NAME = "ITEM_NAME";
    private static final String QUANTITY = "QUANTITY";
    private static final String PRICE = "PRICE";
    private static final String POSNO = "POS_NO";
    private static final String TABLE_NO = "TABLE_NO";
    private static final String SECTION = "SECTION";
    private static final String IS_UPDATED = "IS_UPDATED";
    private static final String DONE = "DONE";
    private static final String NOTE = "NOTE";
    private static final String STGNO = "STGNO";


    // ==================================================================================================================
    private static final String SOCKET_TABLE = "SOCKET";

    private static final String SOCKET_DATE_IN = "DATE_IN";
    private static final String SOCKET_CASH_NO = "CASH_NO";
    private static final String SOCKET_ORDER_NO = "ORDER_NO";
    private static final String SOCKET_ORDER_TYPE = "ORDER_TYPE";
    private static final String SOCKET_ITEM_CODE = "ITEM_CODE";
    private static final String SOCKET_ITEM_NAME = "ITEM_NAME";
    private static final String SOCKET_QUANTITY = "QUANTITY";
    private static final String SOCKET_PRICE = "PRICE";
    private static final String SOCKET_POSNO = "POS_NO";
    private static final String SOCKET_TABLE_NO = "TABLE_NO";
    private static final String SOCKET_SECTION = "SECTION";
    private static final String SOCKET_IS_UPDATED = "IS_UPDATED";
    private static final String SOCKET_DONE = "DONE";
    private static final String SOCKET_NOTE = "NOTE";
    private static final String SOCKET_STGNO = "STGNO";

    // ==================================================================================================================


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        Log.e("database", " 1 ");
//        this.database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        Log.e("database", " 2 ");

//        this.database = db;
//        database = this.getWritableDatabase();
        String CREATE_TABLE_SETTINGS = "CREATE TABLE IF NOT EXISTS " + SETTINGS_KITCHEN_TABLE + "("
                + COMPANY_NO + " TEXT,"
                + COMPANY_YEAR + " TEXT,"
                + SETTINGS_POS + " TEXT,"
                + SCREEN_NO + " TEXT,"
                + TIMER_DURATION + " TEXT,"
                + IP_ADDRESS + " TEXT,"
                + STAGE_NO + " TEXT,"
                + URL + " TEXT,"
                + STAGE_TYPE + " TEXT"+")";
        db.execSQL(CREATE_TABLE_SETTINGS);

        String CREATE_BINDING_ORDERS = "CREATE TABLE IF NOT EXISTS " + BINDING_ORDERS_TABLE + "(" + BINDING_ORDER_NO + " TEXT)";
        db.execSQL(CREATE_BINDING_ORDERS);

        String CREATE_TABLE_ORDERS = "CREATE TABLE IF NOT EXISTS " + ORDERS_TABLE + "("
                + DATE_IN + " TEXT,"
                + CASH_NO + " TEXT,"
                + ORDER_NO + " TEXT,"
                + ORDER_TYPE + " INTEGER,"
                + ITEM_CODE + " TEXT,"
                + ITEM_NAME + " TEXT,"
                + QUANTITY + " INTEGER,"
                + PRICE + " REAL,"
                + POSNO + " INTEGER,"
                + TABLE_NO + " INTEGER,"
                + SECTION + " TEXT,"
                + IS_UPDATED + " TEXT,"
                + DONE + " TEXT,"
                + NOTE + " TEXT,"
                + STGNO + " INTEGER" + ")";
        db.execSQL(CREATE_TABLE_ORDERS);

        String CREATE_TABLE_SOCKET_ORDERS = "CREATE TABLE IF NOT EXISTS " + SOCKET_TABLE + "("
                + SOCKET_DATE_IN + " TEXT,"
                + SOCKET_CASH_NO + " TEXT,"
                + SOCKET_ORDER_NO + " TEXT,"
                + SOCKET_ORDER_TYPE + " INTEGER,"
                + SOCKET_ITEM_CODE + " TEXT,"
                + SOCKET_ITEM_NAME + " TEXT,"
                + SOCKET_QUANTITY + " INTEGER,"
                + SOCKET_PRICE + " REAL,"
                + SOCKET_POSNO + " INTEGER,"
                + SOCKET_TABLE_NO + " INTEGER,"
                + SOCKET_SECTION + " TEXT,"
                + SOCKET_IS_UPDATED + " TEXT,"
                + SOCKET_DONE + " TEXT,"
                + SOCKET_NOTE + " TEXT,"
                + SOCKET_STGNO + " INTEGER" + ")";
        db.execSQL(CREATE_TABLE_SOCKET_ORDERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        this.database = db;

    }

//    @Override
//    public SQLiteDatabase getWritableDatabase() {
//        SQLiteDatabase db;
//        if (database != null) {
//            db = database;
//        } else {
//            db = super.getWritableDatabase();
//        }
//        return db;
//    }

    public void addkitchenSettings() {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COMPANY_NO, KitchenSettingsModel.COMPANY_NO);
        values.put(COMPANY_YEAR, KitchenSettingsModel.COMPANY_YEAR);
        values.put(SETTINGS_POS, KitchenSettingsModel.POS_NO);
        values.put(SCREEN_NO, KitchenSettingsModel.SCREEN_NO);
        values.put(TIMER_DURATION, KitchenSettingsModel.TIMER_DURATION);
        values.put(IP_ADDRESS, KitchenSettingsModel.IP_OF_RECEIVER);
        values.put(STAGE_NO, KitchenSettingsModel.STAGE_NO);
        values.put(URL, KitchenSettingsModel.URL);
        values.put(STAGE_TYPE, KitchenSettingsModel.STAGE_TYPE);

        database.insert(SETTINGS_KITCHEN_TABLE, null, values);
    }

    public void getkitchenSettings() {
        database = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + SETTINGS_KITCHEN_TABLE;
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            KitchenSettingsModel.COMPANY_NO = (cursor.getString(0));
            KitchenSettingsModel.COMPANY_YEAR = (cursor.getString(1));
            KitchenSettingsModel.POS_NO = (cursor.getString(2));
            KitchenSettingsModel.SCREEN_NO = (cursor.getString(3));
            KitchenSettingsModel.TIMER_DURATION = (cursor.getString(4));
            KitchenSettingsModel.IP_OF_RECEIVER = (cursor.getString(5));
            KitchenSettingsModel.STAGE_NO = (cursor.getString(6));
            KitchenSettingsModel.URL = (cursor.getString(7));
            KitchenSettingsModel.STAGE_TYPE= (cursor.getString(8));

        }

    }

    public void deletekitchenSettings() {
        database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + SETTINGS_KITCHEN_TABLE);
    }

    // ********************************************************************************************************
    public void addBindingOrders(String bindingOrder) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(BINDING_ORDER_NO, bindingOrder);

        database.insert(BINDING_ORDERS_TABLE, null, values);
    }

    public List<String> getBindingOrders() {
        List<String> bindingOrderList = new ArrayList<>();
        database = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + BINDING_ORDERS_TABLE;
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                bindingOrderList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        return bindingOrderList;
    }

    public void deleteFromBindingList(String orderNumber) {
        database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + BINDING_ORDERS_TABLE + " WHERE ORDER_NO = '" + orderNumber + "'");
    }

    public void deleteAllBindingOrders() {
        database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + BINDING_ORDERS_TABLE + ";");
        database.close();
    }

    // ********************************************************************************************************

    public void addOrders(Orders orders) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DATE_IN, orders.getDateIn());
        values.put(CASH_NO, orders.getCashNumber());
        values.put(ORDER_NO, orders.getOrderNumber());
        values.put(ORDER_TYPE, orders.getOrderType());
        values.put(ITEM_CODE, orders.getItemCode());
        values.put(ITEM_NAME, orders.getItemName());
        values.put(QUANTITY, orders.getQuantity());
        values.put(PRICE, orders.getPrice());
        values.put(POSNO, orders.getPosNumber());
        values.put(TABLE_NO, orders.getTableNumber());
        values.put(SECTION, orders.getSection());
        values.put(IS_UPDATED, orders.getIsUpdated());
        values.put(DONE, orders.getDone());
        values.put(NOTE, orders.getNote());
        values.put(STGNO, orders.getStageNo());

        database.insert(ORDERS_TABLE, null, values);
        database.close();

    }

    public List<Orders> getOrders() {
        List<Orders> ordersList = new ArrayList<>();
        database = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + ORDERS_TABLE;
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Orders orders = new Orders();
                orders.setDateIn(cursor.getString(0));
                orders.setCashNumber(cursor.getString(1));
                orders.setOrderNumber(cursor.getString(2));
                orders.setOrderType(cursor.getInt(3));
                orders.setItemCode(cursor.getString(4));
                orders.setItemName(cursor.getString(5));
                orders.setQuantity(cursor.getInt(6));
                orders.setPrice(cursor.getDouble(7));
                orders.setPosNumber(cursor.getInt(8));
                orders.setTableNumber(cursor.getInt(9));
                orders.setSection(cursor.getString(10));
                orders.setIsUpdated(cursor.getString(11));
//                orders.setDone(cursor.getString(12));
                orders.setNote(cursor.getString(13));
                orders.setStageNo(cursor.getInt(14));

                ordersList.add(orders);
            } while (cursor.moveToNext());
        }

        return ordersList;
    }

    public List<Orders> getOrderBy(String orderNumber){
        List<Orders> ordersList = new ArrayList<>();
        database = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + ORDERS_TABLE + " WHERE ORDER_NO = '" + orderNumber + "';";
        Log.e("db", selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Orders orders = new Orders();
                orders.setDateIn(cursor.getString(0));
                orders.setCashNumber(cursor.getString(1));
                orders.setOrderNumber(cursor.getString(2));
                orders.setOrderType(cursor.getInt(3));
                orders.setItemCode(cursor.getString(4));
                orders.setItemName(cursor.getString(5));
                orders.setQuantity(cursor.getInt(6));
                orders.setPrice(cursor.getDouble(7));
                orders.setPosNumber(cursor.getInt(8));
                orders.setTableNumber(cursor.getInt(9));
                orders.setSection(cursor.getString(10));
                orders.setIsUpdated(cursor.getString(11));
//                orders.setDone(cursor.getString(12));
                orders.setNote(cursor.getString(13));
                orders.setStageNo(cursor.getInt(14));

                ordersList.add(orders);
            } while (cursor.moveToNext());
        }

        return ordersList;
    }

    public void updateOrders() {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IS_UPDATED, 1);
        database.update(ORDERS_TABLE, values, "IS_UPDATED = ?", new String[]{String.valueOf(0)});
    }

    public void deleteFromOrders(String orderNumber) {
        database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + ORDERS_TABLE + " WHERE ORDER_NO = '" + orderNumber + "';");
        database.close();

    }

    public void deleteAllOrders() {
        database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + ORDERS_TABLE + ";");
        database.close();
    }

    // ********************************************************************************************************

    public void addOrdersBySocket(Orders orders) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SOCKET_DATE_IN, orders.getDateIn());
        values.put(SOCKET_CASH_NO, orders.getCashNumber());
        values.put(SOCKET_ORDER_NO, orders.getOrderNumber());
        values.put(SOCKET_ORDER_TYPE, orders.getOrderType());
        values.put(SOCKET_ITEM_CODE, orders.getItemCode());
        values.put(SOCKET_ITEM_NAME, orders.getItemName());
        values.put(SOCKET_QUANTITY, orders.getQuantity());
        values.put(SOCKET_PRICE, orders.getPrice());
        values.put(SOCKET_POSNO, orders.getPosNumber());
        values.put(SOCKET_TABLE_NO, orders.getTableNumber());
        values.put(SOCKET_SECTION, orders.getSection());
        values.put(SOCKET_IS_UPDATED, orders.getIsUpdated());
        values.put(SOCKET_DONE, orders.getDone());
        values.put(SOCKET_NOTE, orders.getNote());
        values.put(SOCKET_STGNO, orders.getStageNo());

        database.insert(SOCKET_TABLE, null, values);
        database.close();
    }

    public List<Orders> getOrdersFromSocket() {
        List<Orders> ordersList = new ArrayList<>();
        database = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + SOCKET_TABLE;
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Orders orders = new Orders();
                orders.setDateIn(cursor.getString(0));
                orders.setCashNumber(cursor.getString(1));
                orders.setOrderNumber(cursor.getString(2));
                orders.setOrderType(cursor.getInt(3));
                orders.setItemCode(cursor.getString(4));
                orders.setItemName(cursor.getString(5));
                orders.setQuantity(cursor.getInt(6));
                orders.setPrice(cursor.getDouble(7));
                orders.setPosNumber(cursor.getInt(8));
                orders.setTableNumber(cursor.getInt(9));
                orders.setSection(cursor.getString(10));
                orders.setIsUpdated(cursor.getString(11));
//                orders.setDone(cursor.getString(12));
                orders.setNote(cursor.getString(13));
                orders.setStageNo(cursor.getInt(14));

                ordersList.add(orders);
            } while (cursor.moveToNext());
        }

        return ordersList;
    }

    public void updateOrdersInSocket() {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IS_UPDATED, 1);
        database.update(SOCKET_TABLE, values, "IS_UPDATED = ?", new String[]{String.valueOf(0)});
    }

    public void deleteFromSocketAndCloud(String orderNumber) {
        database = this.getWritableDatabase();
        String sqldelete = "Delete from SOCKET where ORDER_NO = '" + orderNumber + "';";
        String sqldelete1 = "Delete from ORDERS where  ORDER_NO = '" + orderNumber + "';";
        database.execSQL(sqldelete);
        database.execSQL(sqldelete1);
        database.close();
    }

    public void deleteAllFromSocketOrders() {
        database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + SOCKET_TABLE + ";");
        database.close();

    }
}
