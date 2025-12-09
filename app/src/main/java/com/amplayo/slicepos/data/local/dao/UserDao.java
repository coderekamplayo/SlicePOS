package com.amplayo.slicepos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.amplayo.slicepos.data.models.User;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(User user);

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    User getByUid(String uid);

    @Query("DELETE FROM users")
    void clear();
}
