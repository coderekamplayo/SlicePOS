package com.amplayo.slicepos.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_items")
public class OrderItem {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long orderId;

    private String name;

    private String config; // JSON string for toppings/crust

    private int quantity;

    private double unitPrice;

    private double lineTotal;

    // Empty constructor for Room and general use
    public OrderItem() {
    }

    public OrderItem(long orderId, String name, String config, int quantity, double unitPrice, double lineTotal) {
        this.orderId = orderId;
        this.name = name;
        this.config = config;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(double lineTotal) {
        this.lineTotal = lineTotal;
    }
}
