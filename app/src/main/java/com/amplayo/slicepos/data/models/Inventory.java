package com.amplayo.slicepos.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "inventory")
public class Inventory {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String remoteId; // Firestore doc ID

    private String name;

    private int quantity;

    public Inventory(String remoteId, String name, int quantity) {
        this.remoteId = remoteId;
        this.name = name;
        this.quantity = quantity;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
