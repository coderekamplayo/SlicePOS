package com.amplayo.slicepos.data.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "orders")
public class Order {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String remoteId;

    private String status; // "open", "paid", "completed"

    private double total;

    private long createdAt;

    private String cashierUid;

    // Empty constructor required for Room and manual instantiation
    public Order() {
    }

    @Ignore
    public Order(String remoteId, String status, double total, long createdAt, String cashierUid) {
        this.remoteId = remoteId;
        this.status = status;
        this.total = total;
        this.createdAt = createdAt;
        this.cashierUid = cashierUid;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getCashierUid() {
        return cashierUid;
    }

    public void setCashierUid(String cashierUid) {
        this.cashierUid = cashierUid;
    }
}
