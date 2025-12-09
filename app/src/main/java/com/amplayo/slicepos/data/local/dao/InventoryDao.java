package com.amplayo.slicepos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.amplayo.slicepos.data.models.Inventory;

@Dao
public interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Inventory i);

    @Query("UPDATE inventory SET quantity = quantity - :deduct WHERE name = :ingredient AND quantity >= :deduct")
    void deductStock(String ingredient, int deduct);

    @Query("SELECT * FROM inventory WHERE name = :name LIMIT 1")
    Inventory getByName(String name);

    @Query("DELETE FROM inventory")
    void clear();
}
