package io.objectbox.performanceapp;

/**
 * Created by Markus on 01.10.2016.
 */
public class TestType {
    public static final String CRUD = "Basic operations (CRUD)";
    public static final String CRUD_SCALARS = "Basic operations (CRUD) - scalars";
    public static final String CRUD_INDEXED = "Basic operations (CRUD) - indexed";
    public static final String QUERY_STRING = "Query by string";
    public static final String QUERY_STRING_INDEXED = "Query by string - index";

    public static TestType[] ALL = {
            new TestType(CRUD, "crud"),
            new TestType(CRUD_SCALARS, "crud-scalars"),
            new TestType(CRUD_INDEXED, "crud-indexed"),
            new TestType(QUERY_STRING, "query-string"),
            new TestType(QUERY_STRING_INDEXED, "query-string-indexed"),
    };

    public final String name;
    public final String nameShort;

    public TestType(String name, String nameShort) {
        this.name = name;
        this.nameShort = nameShort;
    }

    @Override
    public String toString() {
        return name;
    }
}
