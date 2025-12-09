package com.amplayo.slicepos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.amplayo.slicepos.data.models.SyncQueue;
import java.util.List;

@Dao
public interface SyncQueueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(SyncQueue s);

    @Update
    int update(SyncQueue s);

    @Query("SELECT * FROM sync_queue WHERE synced = 0")
    List<SyncQueue> getPending();

    @Query("UPDATE sync_queue SET synced = 1 WHERE id = :id")
    void markSynced(long id);
}
