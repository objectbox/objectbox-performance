package io.objectbox.performanceapp;

import android.content.Context;
import android.database.Cursor;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.performanceapp.greendao.DaoMaster;
import io.objectbox.performanceapp.greendao.DaoMaster.DevOpenHelper;
import io.objectbox.performanceapp.greendao.DaoSession;
import io.objectbox.performanceapp.greendao.SimpleEntityNotNull;
import io.objectbox.performanceapp.greendao.SimpleEntityNotNullDao;
import io.objectbox.performanceapp.greendao.SimpleEntityNotNullIndexed;
import io.objectbox.performanceapp.greendao.SimpleEntityNotNullIndexedDao;
import io.objectbox.performanceapp.greendao.SimpleEntityNotNullIndexedDao.Properties;

/**
 * Created by Markus on 01.10.2016.
 */

public class GreendaoPerfTest extends PerfTest {
    public static final String DB_NAME = "sqlite-greendao";
    private Database db;
    private DaoSession daoSession;
    private SimpleEntityNotNullDao dao;

    private boolean versionLoggedOnce;
    private SimpleEntityNotNullIndexedDao daoIndexed;

    @Override
    public String name() {
        return "greenDAO";
    }

    public void setUp(Context context, PerfTestRunner testRunner) {
        super.setUp(context, testRunner);
        db = new DevOpenHelper(context, DB_NAME).getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        dao = daoSession.getSimpleEntityNotNullDao();
        daoIndexed = daoSession.getSimpleEntityNotNullIndexedDao();

        if (!versionLoggedOnce) {
            Cursor cursor = db.rawQuery("select sqlite_version() AS sqlite_version", null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        log("SQLite version " + cursor.getString(0));
                    }
                } finally {
                    cursor.close();
                }
            }
            versionLoggedOnce = true;
        }
    }

    @Override
    public void run(TestType type) {
        switch (type.name) {
            case TestType.BULK_OPERATIONS:
                runBatchPerfTest();
                break;
            case TestType.BULK_OPERATIONS_INDEXED:
                runBatchPerfTestIndexed();
                break;
            case TestType.LOOK_UP_STRING:
                runLookupString();
                break;
        }
    }

    public void runBatchPerfTest() {
        List<SimpleEntityNotNull> list = new ArrayList<>(numberEntities);
        for (int i = 0; i < numberEntities; i++) {
            list.add(createEntity((long) i));
        }
        startBenchmark("insert");
        dao.insertInTx(list);
        stopBenchmark();

        for (SimpleEntityNotNull entity : list) {
            changeForUpdate(entity);
        }
        startBenchmark("update");
        dao.updateInTx(list);
        stopBenchmark();

        startBenchmark("load");
        List<SimpleEntityNotNull> reloaded = dao.loadAll();
        stopBenchmark();

        startBenchmark("access");
        accessAll(reloaded);
        stopBenchmark();

        startBenchmark("delete");
        dao.deleteAll();
        stopBenchmark();
    }

    protected void changeForUpdate(SimpleEntityNotNull entity) {
        entity.setSimpleInt(random.nextInt());
        entity.setSimpleLong(random.nextLong());
        entity.setSimpleBoolean(random.nextBoolean());
        entity.setSimpleDouble(random.nextDouble());
        entity.setSimpleFloat(random.nextFloat());
        entity.setSimpleString("Another " + entity.getSimpleString());
    }

    public static SimpleEntityNotNull createEntity(Long key) {
        SimpleEntityNotNull entity = new SimpleEntityNotNull();
        if (key != null) {
            entity.setId(key);
        }
        entity.setSimpleBoolean(true);
        entity.setSimpleByte(Byte.MAX_VALUE);
        entity.setSimpleShort(Short.MAX_VALUE);
        entity.setSimpleInt(Integer.MAX_VALUE);
        entity.setSimpleLong(Long.MAX_VALUE);
        entity.setSimpleFloat(Float.MAX_VALUE);
        entity.setSimpleDouble(Double.MAX_VALUE);
        entity.setSimpleString("greenrobot greenDAO");
        byte[] bytes = {42, -17, 23, 0, 127, -128};
        entity.setSimpleByteArray(bytes);
        return entity;
    }

    protected void accessAll(List<SimpleEntityNotNull> list) {
        for (SimpleEntityNotNull entity : list) {
            entity.getId();
            entity.getSimpleBoolean();
            entity.getSimpleByte();
            entity.getSimpleShort();
            entity.getSimpleInt();
            entity.getSimpleLong();
            entity.getSimpleFloat();
            entity.getSimpleDouble();
            entity.getSimpleString();
            entity.getSimpleByteArray();
        }
    }


    public void runBatchPerfTestIndexed() {
        List<SimpleEntityNotNullIndexed> list = new ArrayList<>(numberEntities);
        for (int i = 0; i < numberEntities; i++) {
            list.add(createEntityIndexed((long) i));
        }
        startBenchmark("insert");
        daoIndexed.insertInTx(list);
        stopBenchmark();

        for (SimpleEntityNotNullIndexed entity : list) {
            changeForUpdateIndexed(entity);
        }
        startBenchmark("update");
        daoIndexed.updateInTx(list);
        stopBenchmark();

        startBenchmark("load");
        List<SimpleEntityNotNullIndexed> reloaded = daoIndexed.loadAll();
        stopBenchmark();

        startBenchmark("access");
        accessAllIndexed(reloaded);
        stopBenchmark();

        startBenchmark("delete");
        daoIndexed.deleteAll();
        stopBenchmark();
    }

    protected void changeForUpdateIndexed(SimpleEntityNotNullIndexed entity) {
        entity.setSimpleInt(random.nextInt());
        entity.setSimpleLong(random.nextLong());
        entity.setSimpleBoolean(random.nextBoolean());
        entity.setSimpleDouble(random.nextDouble());
        entity.setSimpleFloat(random.nextFloat());
        entity.setSimpleString("Another " + entity.getSimpleString());
    }

    public static SimpleEntityNotNullIndexed createEntityIndexed(Long key) {
        SimpleEntityNotNullIndexed entity = new SimpleEntityNotNullIndexed();
        if (key != null) {
            entity.setId(key);
        }
        entity.setSimpleBoolean(true);
        entity.setSimpleByte(Byte.MAX_VALUE);
        entity.setSimpleShort(Short.MAX_VALUE);
        entity.setSimpleInt(Integer.MAX_VALUE);
        entity.setSimpleLong(Long.MAX_VALUE);
        entity.setSimpleFloat(Float.MAX_VALUE);
        entity.setSimpleDouble(Double.MAX_VALUE);
        entity.setSimpleString("greenrobot greenDAO");
        byte[] bytes = {42, -17, 23, 0, 127, -128};
        entity.setSimpleByteArray(bytes);
        return entity;
    }

    protected void accessAllIndexed(List<SimpleEntityNotNullIndexed> list) {
        for (SimpleEntityNotNullIndexed entity : list) {
            entity.getId();
            entity.getSimpleBoolean();
            entity.getSimpleByte();
            entity.getSimpleShort();
            entity.getSimpleInt();
            entity.getSimpleLong();
            entity.getSimpleFloat();
            entity.getSimpleDouble();
            entity.getSimpleString();
            entity.getSimpleByteArray();
        }
    }

    private void runLookupString() {
        List<SimpleEntityNotNullIndexed> entities = new ArrayList<>(numberEntities);
        for (int i = 0; i < numberEntities; i++) {
            entities.add(createEntityIndexed((long) i));
        }

        startBenchmark("insert");
        daoIndexed.insertInTx(entities);
        stopBenchmark();

        String[] stringsToLookup = new String[numberEntities];
        for (int i = 0; i < numberEntities; i++) {
            stringsToLookup[i] = entities.get(random.nextInt(numberEntities)).getSimpleString();
        }

        startBenchmark("lookup-indexed");
        Query<SimpleEntityNotNullIndexed> query = daoIndexed.queryBuilder().where(Properties.SimpleString.eq(null)).build();
        db.beginTransaction();
        for (int i = 0; i < numberEntities; i++) {
            query.setParameter(0, stringsToLookup[i]);
            SimpleEntityNotNullIndexed entity = query.unique();
            // assertEquals(stringsToLookup[i], entity.getSimpleString());
        }
        db.endTransaction();
        stopBenchmark();
    }


    @Override
    public void tearDown() {
        daoSession.getDatabase().close();
        boolean deleted = context.deleteDatabase(DB_NAME);
        log("DB deleted: " + deleted);
    }

}
