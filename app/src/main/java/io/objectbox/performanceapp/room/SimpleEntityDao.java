package io.objectbox.performanceapp.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;

@Dao
public interface SimpleEntityDao {

    @Insert
    void insertAll(SimpleEntity... entities);

    @Delete
    void delete(SimpleEntity entity);

}
