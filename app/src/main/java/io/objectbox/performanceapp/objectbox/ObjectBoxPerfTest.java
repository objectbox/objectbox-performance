package io.objectbox.performanceapp.objectbox;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.performanceapp.PerfTest;
import io.objectbox.performanceapp.PerfTestRunner;
import io.objectbox.performanceapp.TestType;

public class ObjectBoxPerfTest extends PerfTest {
    private BoxStore store;

    private boolean versionLoggedOnce;
    private Box<SimpleEntity> box;
    private Box<SimpleEntityIndexed> boxIndexed;

    @Override
    public String name() {
        return "ObjectBox";
    }

    public void setUp(Context context, PerfTestRunner testRunner) {
        super.setUp(context, testRunner);
        store = MyObjectBox.builder().androidContext(context).build();
        store.close();
        store.deleteAllFiles();
        store = MyObjectBox.builder().androidContext(context).maxSizeInKByte(200 * 1024).build();
        box = store.boxFor(SimpleEntity.class);
        boxIndexed = store.boxFor(SimpleEntityIndexed.class);

        if (!versionLoggedOnce) {
            String versionNative = BoxStore.getVersionNative();
            String versionJava = BoxStore.getVersion();
            if (versionJava != null && versionJava.equals(versionJava)) {
                log("ObjectBox " + versionNative);
            } else {
                log("ObjectBox " + versionNative + " (Java: " + versionJava + ")");
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
            case TestType.QUERY_ID:
                runQueryById();
                break;
        }
    }

    public void runBatchPerfTest(boolean scalarsOnly) {
        List<SimpleEntity> list = new ArrayList<>(numberEntities);
        for (int i = 0; i < numberEntities; i++) {
            list.add(createEntity(scalarsOnly));
        }
        startBenchmark("insert");
        box.put(list);
        stopBenchmark();

        for (SimpleEntity entity : list) {
            if (scalarsOnly) {
                setRandomScalars(entity);
            } else {
                setRandomValues(entity);
            }
        }
        startBenchmark("update");
        box.put(list);
        stopBenchmark();

        startBenchmark("load");
        List<SimpleEntity> reloaded = box.getAll();
        stopBenchmark();

//        reloaded = null;
//        startBenchmark("load2");
//        reloaded = box.getAll2();
//        stopBenchmark();

        startBenchmark("access");
        accessAll(reloaded);
        stopBenchmark();

        startBenchmark("delete");
        box.removeAll();
        stopBenchmark();
    }

    protected void setRandomValues(SimpleEntity entity) {
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

    public SimpleEntity createEntity(boolean scalarsOnly) {
        SimpleEntity entity = new SimpleEntity();
        if (scalarsOnly) {
            setRandomScalars(entity);
        } else {
            setRandomValues(entity);
        }
        return entity;
    }

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

    public void runBatchPerfTestIndexed() {
        List<SimpleEntityIndexed> list = new ArrayList<>(numberEntities);
        for (int i = 0; i < numberEntities; i++) {
            list.add(createEntityIndexed());
        }
        startBenchmark("insert");
        boxIndexed.put(list);
        stopBenchmark();

        for (SimpleEntityIndexed entity : list) {
            setRandomValues(entity);
        }
        startBenchmark("update");
        boxIndexed.put(list);
        stopBenchmark();

        startBenchmark("load");
        List<SimpleEntityIndexed> reloaded = boxIndexed.getAll();
        stopBenchmark();

        startBenchmark("access");
        accessAllIndexed(reloaded);
        stopBenchmark();

        startBenchmark("delete");
        boxIndexed.removeAll();
        stopBenchmark();
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

    public SimpleEntityIndexed createEntityIndexed() {
        SimpleEntityIndexed entity = new SimpleEntityIndexed();
        setRandomValues(entity);
        return entity;
    }

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

    private void runQueryByString() {
        if (numberEntities > 10000) {
            log("Reduce number of entities to 10000 to avoid extremely long test runs");
            return;
        }
        List<SimpleEntity> entities = new ArrayList<>(numberEntities);
        for (int i = 0; i < numberEntities; i++) {
            entities.add(createEntity(false));
        }

        startBenchmark("insert");
        box.put(entities);
        stopBenchmark();

        final String[] stringsToLookup = new String[numberEntities];
        for (int i = 0; i < numberEntities; i++) {
            String text = "";
            while (text.length() < 2) {
                text = entities.get(random.nextInt(numberEntities)).getSimpleString();
            }
            stringsToLookup[i] = text;
        }

        startBenchmark("query");

        final int propertyId = box.getPropertyId(SimpleEntityProperties.SimpleString.dbName);
        long entitiesFound = 0;
        for (int i = 0; i < numberEntities; i++) {
            List<SimpleEntity> result = box.find(propertyId, stringsToLookup[i]);
            accessAll(result);
            entitiesFound += result.size();
        }
        stopBenchmark();
        log("Entities found: " + entitiesFound);
    }

    private void runQueryByStringIndexed() {
        List<SimpleEntityIndexed> entities = new ArrayList<>(numberEntities);
        for (int i = 0; i < numberEntities; i++) {
            entities.add(createEntityIndexed());
        }

        startBenchmark("insert");
        boxIndexed.put(entities);
        stopBenchmark();

        final String[] stringsToLookup = new String[numberEntities];
        for (int i = 0; i < numberEntities; i++) {
            String text = "";
            while (text.length() < 2) {
                text = entities.get(random.nextInt(numberEntities)).getSimpleString();
            }
            stringsToLookup[i] = text;
        }

        startBenchmark("query");
        final int propertyId = boxIndexed.getPropertyId(SimpleEntityIndexedProperties.SimpleString.dbName);
        long entitiesFound = 0;
        for (int i = 0; i < numberEntities; i++) {
            List<SimpleEntityIndexed> result = boxIndexed.find(propertyId, stringsToLookup[i]);
            accessAllIndexed(result);
            entitiesFound += result.size();
        }
        stopBenchmark();
        log("Entities found: " + entitiesFound);
    }

    private void runQueryById() {
        List<SimpleEntity> entities = new ArrayList<>(numberEntities);
        for (int i = 0; i < numberEntities; i++) {
            entities.add(createEntity(false));
        }

        startBenchmark("insert");
        box.put(entities);
        stopBenchmark();

        long[] idsToLookup = new long[numberEntities];
        for (int i = 0; i < numberEntities; i++) {
            idsToLookup[i] = 1 + random.nextInt(numberEntities);
        }

        startBenchmark("query");
        for (int i = 0; i < numberEntities; i++) {
            SimpleEntity entity = box.get(idsToLookup[i]);
            accessAll(entity);
        }
        stopBenchmark();
    }

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

    @Override
    public void tearDown() {
        store.close();
        store.deleteAllFiles();
    }

}
