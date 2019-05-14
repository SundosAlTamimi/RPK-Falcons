package com.example.restposkitchen.model;

public class Orders {

    private String dateIn;
    private String orderNumber;///
    private int orderType;///
    private String itemCode;
    private String itemName;
    private int quantity;
    private double price;
    private int posNumber; //  ///
    private String kitchenNumber;
    private int tableNumber;
    private String section;
    private String isUpdated;
    private String note; //
    private String done;
    private String screenNo;

    private int companyNo;
    private int companyYear;

    public Orders() {
    }

    // for data from server
    public Orders(String dateIn, String orderNumber, int orderType, String itemCode, String itemName, int quantity, double price, int posNumber, int tableNumber, String section, String isUpdated, String done, String note) {
        this.dateIn = dateIn;
        this.orderNumber = orderNumber;
        this.orderType = orderType;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.posNumber = posNumber;
        this.tableNumber = tableNumber;
        this.section = section;
        this.isUpdated = isUpdated;
        this.done = done;
        this.note = note;
    }

//[{"QTY":2,"NOTE":"","PRICE":10,"ITEMCODE":"1111","ITEMNAME":"milk","SCREENNO":1,"ISUPDATE":0}]

    public Orders(int quantity, String note, double price, String itemCode, String itemName, String screenNo, String isUpdated) {
        this.quantity = quantity;
        this.note = note;
        this.price = price;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.screenNo = screenNo;
        this.isUpdated = isUpdated;
    }

    public String getDateIn() {
        return dateIn;
    }

    public void setDateIn(String dateIn) {
        this.dateIn = dateIn;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getKitchenNumber() {
        return kitchenNumber;
    }

    public void setKitchenNumber(String kitchenNumber) {
        this.kitchenNumber = kitchenNumber;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getIsUpdated() {
        return isUpdated;
    }

    public void setIsUpdated(String isUpdated) {
        this.isUpdated = isUpdated;
    }

    public String getDone() {
        return done;
    }

    public void setDone(String done) {
        this.done = done;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getPosNumber() {
        return posNumber;
    }

    public void setPosNumber(int posNumber) {
        this.posNumber = posNumber;
    }

    public int getCompanyNo() {
        return companyNo;
    }

    public void setCompanyNo(int companyNo) {
        this.companyNo = companyNo;
    }

    public int getCompanyYear() {
        return companyYear;
    }

    public void setCompanyYear(int companyYear) {
        this.companyYear = companyYear;
    }

    public String getScreenNo() {
        return screenNo;
    }

    public void setScreenNo(String screenNo) {
        this.screenNo = screenNo;
    }
}
