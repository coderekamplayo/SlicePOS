package com.amplayo.slicepos.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "menu_items")
public class MenuItem {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String remoteId;

    private String name;

    private String type; // e.g., "pizza", "topping"

    private String size;

    private double price;

    public MenuItem(String remoteId, String name, String type, String size, double price) {
        this.remoteId = remoteId;
        this.name = name;
        this.type = type;
        this.size = size;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
