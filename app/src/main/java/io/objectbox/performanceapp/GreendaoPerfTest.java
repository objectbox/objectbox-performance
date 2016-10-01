package io.objectbox.performanceapp;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.performanceapp.greendao.DaoMaster;
import io.objectbox.performanceapp.greendao.DaoMaster.DevOpenHelper;
import io.objectbox.performanceapp.greendao.DaoSession;
import io.objectbox.performanceapp.greendao.SimpleEntityNotNull;
import io.objectbox.performanceapp.greendao.SimpleEntityNotNullDao;

/**
 * Created by Markus on 01.10.2016.
 */

public class GreendaoPerfTest extends PerfTest {
    private DaoSession daoSession;
    private SimpleEntityNotNullDao dao;

    public void setUp(Context context, PerfTestRunner testRunner) {
        super.setUp(context, testRunner);
        Database db = new DevOpenHelper(context, "sqlite-greendao").getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        dao = daoSession.getSimpleEntityNotNullDao();
    }

    @Override
    public void run(TestType type) {
        switch (type.name) {
            case TestType.BULK_OPERATIONS:
                runBatchPerfTest();
                break;
        }
    }

    public void runBatchPerfTest() {
        List<SimpleEntityNotNull> list = new ArrayList<>(numberEntities);
        for (int i = 0; i < numberEntities; i++) {
            list.add(createEntity((long) i));
        }
        Benchmark benchmark = getBenchmark("greendao-batch");
        benchmark.start("insert");
        dao.insertInTx(list);
        log(benchmark.stop());

        for (SimpleEntityNotNull entity : list) {
            changeForUpdate(entity);
        }
        benchmark.start("update");
        dao.updateInTx(list);
        log(benchmark.stop());

        benchmark.start("load");
        List<SimpleEntityNotNull> reloaded = dao.loadAll();
        log(benchmark.stop());

        benchmark.start("access");
        accessAll(reloaded);
        log(benchmark.stop());

        benchmark.start("delete");
        dao.deleteAll();
        log(benchmark.stop());
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

    @Override
    public void tearDown() {
        daoSession.getDatabase().close();
    }

    @Override
    public String name() {
        return "greenDAO";
    }
}
