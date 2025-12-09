package com.amplayo.slicepos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.amplayo.slicepos.data.models.Order;
import java.util.List;

@Dao
public interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Order order);

    @Update
    int update(Order order);

    @Query("SELECT * FROM orders WHERE status = 'open' ORDER BY createdAt DESC")
    List<Order> getOpenOrders();

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    Order getById(long orderId);

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    List<Order> getAll();
}
