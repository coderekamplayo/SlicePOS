package com.amplayo.slicepos.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.amplayo.slicepos.data.local.dao.InventoryDao;
import com.amplayo.slicepos.data.local.dao.MenuItemDao;
import com.amplayo.slicepos.data.local.dao.OrderDao;
import com.amplayo.slicepos.data.local.dao.OrderItemDao;
import com.amplayo.slicepos.data.local.dao.SyncQueueDao;
import com.amplayo.slicepos.data.local.dao.UserDao;
import com.amplayo.slicepos.data.models.Inventory;
import com.amplayo.slicepos.data.models.MenuItem;
import com.amplayo.slicepos.data.models.Order;
import com.amplayo.slicepos.data.models.OrderItem;
import com.amplayo.slicepos.data.models.SyncQueue;
import com.amplayo.slicepos.data.models.User;

@Database(entities = { User.class, MenuItem.class, Inventory.class, Order.class, OrderItem.class,
        SyncQueue.class }, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();

    public abstract MenuItemDao menuItemDao();

    public abstract InventoryDao inventoryDao();

    public abstract OrderDao orderDao();

    public abstract SyncQueueDao syncQueueDao();

    public abstract OrderItemDao orderItemDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "slice_pos.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
