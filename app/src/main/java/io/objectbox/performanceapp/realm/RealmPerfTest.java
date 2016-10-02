package io.objectbox.performanceapp.realm;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.performanceapp.PerfTest;
import io.objectbox.performanceapp.PerfTestRunner;
import io.objectbox.performanceapp.TestType;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class RealmPerfTest extends PerfTest {

    private boolean versionLoggedOnce;
    private Realm realm;

    @Override
    public String name() {
        return "Realm";
    }

    public void setUp(Context context, PerfTestRunner testRunner) {
        super.setUp(context, testRunner);
        Realm.init(context);
        realm = Realm.getDefaultInstance();

        RealmConfiguration configuration = realm.getConfiguration();
        realm.close();
        Realm.deleteRealm(configuration);
        realm = Realm.getDefaultInstance();

        if (!versionLoggedOnce) {
            //log("Realm " + ??);
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
            list.add(createEntity(i, scalarsOnly));
        }
        startBenchmark("insert");
        realm.beginTransaction();
        realm.insert(list);
        realm.commitTransaction();
        stopBenchmark();

        for (SimpleEntity entity : list) {
            if (scalarsOnly) {
                setRandomScalars(entity);
            } else {
                setRandomValues(entity);
            }
        }
        startBenchmark("update");
        realm.beginTransaction();
        realm.insertOrUpdate(list);
        realm.commitTransaction();
        stopBenchmark();

        startBenchmark("load");
        RealmResults<SimpleEntity> reloaded = realm.where(SimpleEntity.class).findAll();
        stopBenchmark();

        startBenchmark("access");
        accessAll(reloaded);
        stopBenchmark();

        startBenchmark("delete");
        realm.beginTransaction();
        reloaded.deleteAllFromRealm();
        realm.commitTransaction();
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

    public SimpleEntity createEntity(long id, boolean scalarsOnly) {
        SimpleEntity entity = new SimpleEntity();
        entity.setId(id);
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
            list.add(createEntityIndexed(i));
        }
        startBenchmark("insert");
        realm.beginTransaction();
        realm.insert(list);
        realm.commitTransaction();
        stopBenchmark();

        for (SimpleEntityIndexed entity : list) {
            setRandomValues(entity);
        }
        startBenchmark("update");
        realm.beginTransaction();
        realm.insertOrUpdate(list);
        realm.commitTransaction();
        stopBenchmark();

        log("Count: " + realm.where(SimpleEntity.class).count());

        startBenchmark("load");
        RealmResults<SimpleEntity> reloaded = realm.where(SimpleEntity.class).findAll();
        stopBenchmark();

        startBenchmark("access");
        accessAll(reloaded);
        stopBenchmark();

        startBenchmark("delete");
        realm.beginTransaction();
        reloaded.deleteAllFromRealm();
        realm.commitTransaction();
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

    public SimpleEntityIndexed createEntityIndexed(long id) {
        SimpleEntityIndexed entity = new SimpleEntityIndexed();
        entity.setId(id);
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
            entities.add(createEntity(i, false));
        }

        startBenchmark("insert");
        realm.beginTransaction();
        realm.insert(entities);
        realm.commitTransaction();
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
        long entitiesFound = 0;
        for (int i = 0; i < numberEntities; i++) {
            List<SimpleEntity> result = realm.where(SimpleEntity.class).equalTo("simpleString", stringsToLookup[i]).findAll();
            accessAll(result);
            entitiesFound += result.size();
        }
        stopBenchmark();
        log("Entities found: " + entitiesFound);
    }

    private void runQueryByStringIndexed() {
        List<SimpleEntityIndexed> entities = new ArrayList<>(numberEntities);
        for (int i = 0; i < numberEntities; i++) {
            entities.add(createEntityIndexed(i));
        }

        startBenchmark("insert");
        realm.beginTransaction();
        realm.insert(entities);
        realm.commitTransaction();
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
        long entitiesFound = 0;
        for (int i = 0; i < numberEntities; i++) {
            List<SimpleEntityIndexed> result = realm.where(SimpleEntityIndexed.class).equalTo("simpleString", stringsToLookup[i]).findAll();
            accessAllIndexed(result);
            entitiesFound += result.size();
        }
        stopBenchmark();
        log("Entities found: " + entitiesFound);
    }

    private void runQueryById() {
        List<SimpleEntity> entities = new ArrayList<>(numberEntities);
        for (int i = 0; i < numberEntities; i++) {
            entities.add(createEntity((long) i, false));
        }

        startBenchmark("insert");
        realm.beginTransaction();
        realm.insert(entities);
        realm.commitTransaction();
        stopBenchmark();

        long[] idsToLookup = new long[numberEntities];
        for (int i = 0; i < numberEntities; i++) {
            idsToLookup[i] = random.nextInt(numberEntities);
        }

        startBenchmark("query");
        for (int i = 0; i < numberEntities; i++) {
            SimpleEntity entity = realm.where(SimpleEntity.class).equalTo("id", i).findFirst();
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
        RealmConfiguration configuration = realm.getConfiguration();
        realm.close();
        Realm.deleteRealm(configuration);
    }

}
