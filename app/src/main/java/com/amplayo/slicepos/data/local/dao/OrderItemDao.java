package com.amplayo.slicepos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.amplayo.slicepos.data.models.OrderItem;
import java.util.List;

@Dao
public interface OrderItemDao {
    @Insert
    void insert(OrderItem item);

    @Delete
    void delete(OrderItem item);

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    List<OrderItem> getByOrderId(long orderId);
}
