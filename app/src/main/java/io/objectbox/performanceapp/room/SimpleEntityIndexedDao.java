package io.objectbox.performanceapp.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;

@Dao
public interface SimpleEntityIndexedDao {

    @Insert
    void insertAll(SimpleEntityIndexed... entities);

    @Delete
    void delete(SimpleEntityIndexed entity);

}
