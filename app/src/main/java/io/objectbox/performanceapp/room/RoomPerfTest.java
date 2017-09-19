package io.objectbox.performanceapp.room;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.database.Cursor;

import io.objectbox.performanceapp.PerfTest;
import io.objectbox.performanceapp.PerfTestRunner;
import io.objectbox.performanceapp.TestType;

public class RoomPerfTest extends PerfTest {

    public static final String DB_NAME = "sqlite-room";

    private AppDatabase db;
    private boolean versionLoggedOnce;

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
//            case TestType.CRUD:
//                runBatchPerfTest(false);
//                break;
//            case TestType.CRUD_SCALARS:
//                runBatchPerfTest(true);
//                break;
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

    @Override
    public void tearDown() {
        super.tearDown();
        db.close();
        boolean deleted = context.deleteDatabase(DB_NAME);
        log("DB deleted: " + deleted);
    }
}
