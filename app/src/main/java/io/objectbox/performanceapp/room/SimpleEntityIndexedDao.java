package io.objectbox.performanceapp.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SimpleEntityIndexedDao {

    @Insert
    void insertInTx(List<SimpleEntityIndexed> entities);

    @Query("SELECT * FROM simpleentityindexed")
    List<SimpleEntityIndexed> loadAll();

    @Update
    void updateInTx(List<SimpleEntityIndexed> entities);

    @Delete
    void deleteInTx(List<SimpleEntityIndexed> entities);

    @Query("SELECT * from simpleentityindexed where simpleInt = :value")
    List<SimpleEntityIndexed> whereSimpleIntEq(int value);

    @Query("SELECT * FROM simpleentityindexed WHERE simpleString = :value")
    List<SimpleEntityIndexed> whereSimpleStringEq(String value);

}
