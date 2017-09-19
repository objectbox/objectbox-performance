package io.objectbox.performanceapp.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {SimpleEntity.class, SimpleEntityIndexed.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract SimpleEntityDao simpleEntityDao();

    public abstract SimpleEntityIndexedDao simpleEntityIndexedDao();

}
