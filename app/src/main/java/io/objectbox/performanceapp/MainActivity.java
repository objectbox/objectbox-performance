package io.objectbox.performanceapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends Activity {
    TestType[] TYPES = {
            new TestType(TestType.BULK_OPERATIONS),
            new TestType(TestType.BULK_OPERATIONS_INDEXED),
            new TestType(TestType.LOOK_UP_STRING_INDEX),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.buttonRunTest).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, TYPES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.spinnerTestType)).setAdapter(adapter);
    }

    class TestType {
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

}
