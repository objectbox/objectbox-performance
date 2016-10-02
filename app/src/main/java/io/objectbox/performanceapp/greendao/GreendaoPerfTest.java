package io.objectbox.performanceapp.greendao;

import android.content.Context;
import android.database.Cursor;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.performanceapp.PerfTest;
import io.objectbox.performanceapp.PerfTestRunner;
import io.objectbox.performanceapp.TestType;
import io.objectbox.performanceapp.greendao.DaoMaster.DevOpenHelper;
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
        boolean deleted = context.deleteDatabase(DB_NAME);
        if (deleted) {
            log("DB existed before start - deleted");
        }
        db = new DevOpenHelper(context, DB_NAME).getWritableDb();
        daoSession = new DaoMaster(db).newSession(IdentityScopeType.None);
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
            case TestType.CRUD:
                runBatchPerfTest(false);
                break;
            case TestType.UPDATE_SCALARS:
                runBatchPerfTest(true);
                break;
            case TestType.CRUD_INDEXED:
                runBatchPerfTestIndexed();
                break;
            case TestType.LOOK_UP_STRING:
                runLookupString();
                break;
        }
    }

    public void runBatchPerfTest(boolean updateScalarsOnly) {
        List<SimpleEntityNotNull> list = new ArrayList<>(numberEntities);
        for (int i = 0; i < numberEntities; i++) {
            list.add(createEntity((long) i));
        }
        startBenchmark("insert");
        dao.insertInTx(list);
        stopBenchmark();

        for (SimpleEntityNotNull entity : list) {
            if (updateScalarsOnly) {
                setRandomScalars(entity);
            } else {
                setRandomValues(entity);
            }
        }
        startBenchmark("update");
        dao.updateInTx(list);
        stopBenchmark();
        if(updateScalarsOnly) {
            return;
        }

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

    protected void setRandomValues(SimpleEntityNotNull entity) {
        setRandomScalars(entity);
        entity.setSimpleString(randomString());
        entity.setSimpleByteArray(randomBytes());
    }

    private void setRandomScalars(SimpleEntityNotNull entity) {
        entity.setSimpleBoolean(random.nextBoolean());
        entity.setSimpleByte((byte) random.nextInt());
        entity.setSimpleShort((short) random.nextInt());
        entity.setSimpleInt(random.nextInt());
        entity.setSimpleLong(random.nextLong());
        entity.setSimpleDouble(random.nextDouble());
        entity.setSimpleFloat(random.nextFloat());
    }

    public SimpleEntityNotNull createEntity(Long key) {
        SimpleEntityNotNull entity = new SimpleEntityNotNull();
        if (key != null) {
            entity.setId(key);
        }
        setRandomValues(entity);
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
            setRandomValues(entity);
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

    protected void setRandomValues(SimpleEntityNotNullIndexed entity) {
        entity.setSimpleBoolean(random.nextBoolean());
        entity.setSimpleByte((byte) random.nextInt());
        entity.setSimpleShort((short) random.nextInt());
        entity.setSimpleInt(random.nextInt());
        entity.setSimpleLong(random.nextLong());
        entity.setSimpleDouble(random.nextDouble());
        entity.setSimpleFloat(random.nextFloat());
        entity.setSimpleString(randomString());
        entity.setSimpleByteArray(randomBytes());
    }

    public SimpleEntityNotNullIndexed createEntityIndexed(Long key) {
        SimpleEntityNotNullIndexed entity = new SimpleEntityNotNullIndexed();
        if (key != null) {
            entity.setId(key);
        }
        setRandomValues(entity);
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
            String text = "";
            while (text.length() < 2) {
                text = entities.get(random.nextInt(numberEntities)).getSimpleString();
            }
            stringsToLookup[i] = text;
        }

        startBenchmark("lookup-indexed");
        Query<SimpleEntityNotNullIndexed> query = daoIndexed.queryBuilder().where(Properties.SimpleString.eq(null)).build();
        db.beginTransaction();
        for (int i = 0; i < numberEntities; i++) {
            query.setParameter(0, stringsToLookup[i]);
            List<SimpleEntityNotNullIndexed> result = query.list();
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
