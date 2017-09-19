package io.objectbox.performanceapp.room;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.performanceapp.PerfTest;
import io.objectbox.performanceapp.PerfTestRunner;
import io.objectbox.performanceapp.TestType;

public class RoomPerfTest extends PerfTest {

    public static final String DB_NAME = "sqlite-room";

    private boolean versionLoggedOnce;
    private AppDatabase db;
    private SimpleEntityDao dao;
    private SimpleEntityIndexedDao daoIndexed;

    @Override
    public String name() {
        return "Room";
    }

    @Override
    public void setUp(Context context, PerfTestRunner testRunner) {
        super.setUp(context, testRunner);
        boolean deleted = context.deleteDatabase(DB_NAME);
        if (deleted) {
            log("DB existed before start - deleted");
        }
        db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME)
                .build();
        dao = db.simpleEntityDao();
        daoIndexed = db.simpleEntityIndexedDao();

        if (!versionLoggedOnce) {
            Cursor cursor = db.query("select sqlite_version() AS sqlite_version", null);
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
            case TestType.CRUD_SCALARS:
                runBatchPerfTest(true);
                break;
//            case TestType.CRUD_INDEXED:
//                runBatchPerfTestIndexed();
//                break;
//            case TestType.QUERY_STRING:
//                runQueryByString();
//                break;
//            case TestType.QUERY_STRING_INDEXED:
//                runQueryByStringIndexed();
//                break;
//            case TestType.QUERY_INTEGER:
//                runQueryByInteger();
//                break;
//            case TestType.QUERY_INTEGER_INDEXED:
//                runQueryByIntegerIndexed();
//                break;
//            case TestType.QUERY_ID:
//                runQueryById(false, false);
//                break;
//            case TestType.QUERY_ID_RANDOM:
//                runQueryById(true, false);
//                break;
        }
    }

    private void runBatchPerfTest(boolean scalarsOnly) {
        List<SimpleEntity> list = new ArrayList<>(numberEntities);
        for (int i = 0; i < numberEntities; i++) {
            list.add(createEntity((long) i, scalarsOnly));
        }
        startBenchmark("insert");
        dao.insertInTx(list);
        stopBenchmark();

        for (SimpleEntity entity : list) {
            if (scalarsOnly) {
                setRandomScalars(entity);
            } else {
                setRandomValues(entity);
            }
        }
        startBenchmark("update");
        dao.updateInTx(list);
        stopBenchmark();

        //noinspection UnusedAssignment
        list = null;

        startBenchmark("load");
        List<SimpleEntity> reloaded = dao.loadAll();
        stopBenchmark();

        startBenchmark("access");
        accessAll(reloaded);
        stopBenchmark();

        startBenchmark("delete");
        dao.deleteInTx(reloaded);
        stopBenchmark();
    }

    @Override
    public void tearDown() {
        super.tearDown();
        db.close();
        boolean deleted = context.deleteDatabase(DB_NAME);
        log("DB deleted: " + deleted);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected void accessAll(List<SimpleEntity> list) {
        for (SimpleEntity entity : list) {
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

    private void setRandomValues(SimpleEntity entity) {
        setRandomScalars(entity);
        entity.setSimpleString(randomString());
        entity.setSimpleByteArray(randomBytes());
    }

    private void setRandomScalars(SimpleEntity entity) {
        entity.setSimpleBoolean(random.nextBoolean());
        entity.setSimpleByte((byte) random.nextInt());
        entity.setSimpleShort((short) random.nextInt());
        entity.setSimpleInt(random.nextInt());
        entity.setSimpleLong(random.nextLong());
        entity.setSimpleDouble(random.nextDouble());
        entity.setSimpleFloat(random.nextFloat());
    }

    private SimpleEntity createEntity(Long key, boolean scalarsOnly) {
        SimpleEntity entity = new SimpleEntity();
        if (key != null) {
            entity.setId(key);
        }
        if (scalarsOnly) {
            setRandomScalars(entity);
        } else {
            setRandomValues(entity);
        }
        return entity;
    }
}
