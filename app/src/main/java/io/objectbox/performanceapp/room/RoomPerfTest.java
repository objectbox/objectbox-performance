package io.objectbox.performanceapp.room;

import io.objectbox.performanceapp.PerfTest;
import io.objectbox.performanceapp.TestType;

public class RoomPerfTest extends PerfTest {
    @Override
    public String name() {
        return "Room";
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
}
