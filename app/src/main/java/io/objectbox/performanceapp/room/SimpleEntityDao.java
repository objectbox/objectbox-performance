package io.objectbox.performanceapp.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface SimpleEntityDao {

    @Insert
    void insertInTx(List<SimpleEntity> entities);

    @Query("SELECT * FROM simpleentity")
    List<SimpleEntity> loadAll();

    @Update
    void updateInTx(List<SimpleEntity> entities);

    @Delete
    void deleteInTx(List<SimpleEntity> entities);

}
