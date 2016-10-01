package io.objectbox.performanceapp;

/**
 * Created by Markus on 01.10.2016.
 */
public class TestType {
    public static final String BULK_OPERATIONS = "Bulk operations (CRUD)";
    public static final String BULK_OPERATIONS_INDEXED = "Bulk operations (CRUD) - indexed";
    public static final String LOOK_UP_STRING_INDEX = "Look up string using index";

    String name;

    public TestType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
