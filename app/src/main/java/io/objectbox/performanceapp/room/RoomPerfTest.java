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
            case TestType.CRUD_INDEXED:
                runBatchPerfTestIndexed();
                break;
            case TestType.QUERY_STRING:
                runQueryByString();
                break;
            case TestType.QUERY_STRING_INDEXED:
                runQueryByStringIndexed();
                break;
            case TestType.QUERY_INTEGER:
                runQueryByInteger();
                break;
            case TestType.QUERY_INTEGER_INDEXED:
                runQueryByIntegerIndexed();
                break;
            case TestType.QUERY_ID:
                runQueryById(false);
                break;
            case TestType.QUERY_ID_RANDOM:
                runQueryById(true);
                break;
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

    private void runBatchPerfTestIndexed() {
        List<SimpleEntityIndexed> list = new ArrayList<>(numberEntities);
        for (int i = 0; i < numberEntities; i++) {
            list.add(createEntityIndexed((long) i));
        }
        startBenchmark("insert");
        daoIndexed.insertInTx(list);
        stopBenchmark();

        for (SimpleEntityIndexed entity : list) {
            setRandomValues(entity);
        }
        startBenchmark("update");
        daoIndexed.updateInTx(list);
        stopBenchmark();

        //noinspection UnusedAssignment
        list = null;

        startBenchmark("load");
        List<SimpleEntityIndexed> reloaded = daoIndexed.loadAll();
        stopBenchmark();

        startBenchmark("access");
        accessAllIndexed(reloaded);
        stopBenchmark();

        startBenchmark("delete");
        daoIndexed.deleteInTx(reloaded);
        stopBenchmark();
    }

    private void runQueryByString() {
        if (numberEntities > 10000) {
            log("Reduce number of entities to 10000 to avoid extremely long test runs");
            return;
        }
        List<SimpleEntity> entities = new ArrayList<>(numberEntities);
        for (int i = 0; i < numberEntities; i++) {
            entities.add(createEntity((long) i, false));
        }

        startBenchmark("insert");
        dao.insertInTx(entities);
        stopBenchmark();

        String[] stringsToLookup = new String[numberEntities];
        for (int i = 0; i < numberEntities; i++) {
            String text = "";
            while (text.length() < 2) {
                text = entities.get(random.nextInt(numberEntities)).getSimpleString();
            }
            stringsToLookup[i] = text;
        }

        long entitiesFound = 0;
        startBenchmark("query");
        db.beginTransaction();
        for (int i = 0; i < numberEntities; i++) {
            List<SimpleEntity> result = dao.whereSimpleStringEq(stringsToLookup[i]);
            accessAll(result);
            entitiesFound += result.size();
        }
        db.endTransaction();
        stopBenchmark();
        log("Entities found: " + entitiesFound);
    }

    private void runQueryByStringIndexed() {
        List<SimpleEntityIndexed> entities = new ArrayList<>(numberEntities);
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

        long entitiesFound = 0;
        startBenchmark("query");
        db.beginTransaction();
        for (int i = 0; i < numberEntities; i++) {
            List<SimpleEntityIndexed> result = daoIndexed.whereSimpleStringEq(stringsToLookup[i]);
            accessAllIndexed(result);
            entitiesFound += result.size();
        }
        db.endTransaction();
        stopBenchmark();
        log("Entities found: " + entitiesFound);
    }

    private void runQueryByInteger() {
        if (numberEntities > 10000) {
            log("Reduce number of entities to 10000 to avoid extremely long test runs");
            return;
        }
        List<SimpleEntity> entities = new ArrayList<>(numberEntities);
        for (int i = 0; i < numberEntities; i++) {
            entities.add(createEntity((long) i, false));
        }

        startBenchmark("insert");
        dao.insertInTx(entities);
        stopBenchmark();

        final int[] valuesToLookup = new int[numberEntities];
        for (int i = 0; i < numberEntities; i++) {
            valuesToLookup[i] = entities.get(random.nextInt(numberEntities)).getSimpleInt();
        }

        startBenchmark("query");
        long entitiesFound = 0;
        for (int i = 0; i < numberEntities; i++) {
            List<SimpleEntity> result = dao.whereSimpleIntEq(valuesToLookup[i]);
            accessAll(result);
            entitiesFound += result.size();
        }
        stopBenchmark();
        log("Entities found: " + entitiesFound);
        assertGreaterOrEqualToNumberOfEntities(entitiesFound);
    }

    private void runQueryByIntegerIndexed() {
        List<SimpleEntityIndexed> entities = new ArrayList<>(numberEntities);
        for (int i = 0; i < numberEntities; i++) {
            entities.add(createEntityIndexed((long) i));
        }

        startBenchmark("insert");
        daoIndexed.insertInTx(entities);
        stopBenchmark();

        final int[] valuesToLookup = new int[numberEntities];
        for (int i = 0; i < numberEntities; i++) {
            valuesToLookup[i] = entities.get(random.nextInt(numberEntities)).getSimpleInt();
        }

        startBenchmark("query");
        long entitiesFound = 0;
        for (int i = 0; i < numberEntities; i++) {
            List<SimpleEntityIndexed> result = daoIndexed.whereSimpleIntEq(valuesToLookup[i]);
            accessAllIndexed(result);
            entitiesFound += result.size();
        }
        stopBenchmark();
        log("Entities found: " + entitiesFound);
        assertGreaterOrEqualToNumberOfEntities(entitiesFound);
    }

    private void runQueryById(boolean randomIds) {
        List<SimpleEntity> entities = new ArrayList<>(numberEntities);
        for (int i = 0; i < numberEntities; i++) {
            entities.add(createEntity((long) i, false));
        }

        startBenchmark("insert");
        dao.insertInTx(entities);
        stopBenchmark();

        assertEntityCount(dao.count());

        long[] idsToLookup = new long[numberEntities];
        for (int i = 0; i < numberEntities; i++) {
            idsToLookup[i] = randomIds ? random.nextInt(numberEntities) : i;
        }

        startBenchmark("query");
        for (int i = 0; i < numberEntities; i++) {
            SimpleEntity entity = dao.load(idsToLookup[i]);
            accessAll(entity);
        }
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
    private void accessAll(SimpleEntity entity) {
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected void accessAllIndexed(List<SimpleEntityIndexed> list) {
        for (SimpleEntityIndexed entity : list) {
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

    protected void setRandomValues(SimpleEntityIndexed entity) {
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

    public SimpleEntityIndexed createEntityIndexed(Long key) {
        SimpleEntityIndexed entity = new SimpleEntityIndexed();
        if (key != null) {
            entity.setId(key);
        }
        setRandomValues(entity);
        return entity;
    }
}
