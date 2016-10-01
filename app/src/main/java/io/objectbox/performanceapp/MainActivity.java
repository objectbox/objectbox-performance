package io.objectbox.performanceapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.performanceapp.PerfTestRunner.Callback;

public class MainActivity extends Activity implements Callback {
    TestType[] TYPES = {
            new TestType(TestType.BULK_OPERATIONS),
            new TestType(TestType.BULK_OPERATIONS_INDEXED),
            new TestType(TestType.LOOK_UP_STRING_INDEX),
    };
    private TextView textViewResults;
    private Button buttonRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonRun = (Button) findViewById(R.id.buttonRunTest);
        buttonRun.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonRun.setEnabled(false);
                View currentFocus = MainActivity.this.getCurrentFocus();
                if (currentFocus != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
                boolean objectBox = ((CheckBox) findViewById(R.id.checkBoxObjectBox)).isChecked();
                boolean realm = ((CheckBox) findViewById(R.id.checkBoxRealm)).isChecked();
                boolean sqlite = ((CheckBox) findViewById(R.id.checkBoxSQLite)).isChecked();
                TestType type = (TestType) ((Spinner) findViewById(R.id.spinnerTestType)).getSelectedItem();

                int runs;
                int numberEntities;
                try {
                    runs = Integer.parseInt(((EditText) findViewById(R.id.editTextRuns)).getText().toString());
                    numberEntities =
                            Integer.parseInt(((EditText) findViewById(R.id.editTextNumberEntities)).getText().toString());
                } catch (NumberFormatException e) {
                    textViewResults.append(e.getMessage() + "\n");
                    return;
                }
                runTests(type, runs, numberEntities, objectBox, realm, sqlite);
            }
        });
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, TYPES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.spinnerTestType)).setAdapter(adapter);
        textViewResults = ((TextView) findViewById(R.id.textViewResults));
        textViewResults.setMovementMethod(new ScrollingMovementMethod());
    }

    private void runTests(TestType type, int runs, int numberEntities, boolean objectBox, boolean realm, boolean sqlite) {
        textViewResults.setText("");
        List<PerfTest> tests = new ArrayList<>();
        if (sqlite) {
            tests.add(new GreendaoPerfTest());
        }
        PerfTestRunner testRunner = new PerfTestRunner(this, this, textViewResults, runs, numberEntities);
        testRunner.run(type, tests);
    }

    @Override
    public void done() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buttonRun.setEnabled(true);
            }
        });
    }

}
