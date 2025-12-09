package com.amplayo.slicepos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.amplayo.slicepos.data.models.MenuItem;
import java.util.List;

@Dao
public interface MenuItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(MenuItem m);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<MenuItem> items);

    @Query("SELECT * FROM menu_items WHERE type = :type")
    List<MenuItem> getByType(String type);

    @Query("DELETE FROM menu_items")
    void clear();
}
