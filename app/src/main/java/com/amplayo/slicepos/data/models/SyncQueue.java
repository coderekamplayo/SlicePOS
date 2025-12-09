package com.amplayo.slicepos.data.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.util.Map;

@Entity(tableName = "sync_queue")
public class SyncQueue {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String type; // e.g., "ORDER_CREATE"

    private String remoteId;

    private boolean synced;

    @Ignore
    private Map<String, Object> payload;

    public SyncQueue(String type, String remoteId, boolean synced) {
        this.type = type;
        this.remoteId = remoteId;
        this.synced = synced;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}
