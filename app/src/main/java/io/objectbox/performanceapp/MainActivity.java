package io.objectbox.performanceapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    TestType[] TYPES = {
            new TestType(TestType.BULK_OPERATIONS),
            new TestType(TestType.BULK_OPERATIONS_INDEXED),
            new TestType(TestType.LOOK_UP_STRING_INDEX),
    };
    private TextView textViewResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.buttonRunTest).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean objectBox = ((CheckBox) findViewById(R.id.checkBoxObjectBox)).isChecked();
                boolean realm = ((CheckBox) findViewById(R.id.checkBoxRealm)).isChecked();
                boolean sqlite = ((CheckBox) findViewById(R.id.checkBoxSQLite)).isChecked();
                TestType type = (TestType) ((Spinner) findViewById(R.id.spinnerTestType)).getSelectedItem();
                runTests(type, objectBox, realm, sqlite);
            }
        });
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, TYPES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.spinnerTestType)).setAdapter(adapter);
        textViewResults = ((TextView) findViewById(R.id.textViewResults));
    }

    private void runTests(TestType type, boolean objectBox, boolean realm, boolean sqlite) {
        textViewResults.setText("");
        List<PerfTest> tests = new ArrayList<>();
        switch (type.name) {
            case TestType.BULK_OPERATIONS:
                if (sqlite) {
                    GreendaoPerfTest test = new GreendaoPerfTest();
                    tests.add(test);
                    test.setTextViewLogger(textViewResults);
                    test.setUp(this);
                    test.runBatchPerfTest();
                    test.tearDown();
                }
                break;
            default:
                textViewResults.append("Not implemented\n");
        }
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
