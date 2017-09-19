/*
 * Copyright 2017 ObjectBox Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.objectbox.performanceapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
import io.objectbox.performanceapp.greendao.GreendaoPerfTest;
import io.objectbox.performanceapp.objectbox.ObjectBoxPerfTest;
import io.objectbox.performanceapp.realm.RealmPerfTest;
import io.objectbox.performanceapp.room.RoomPerfTest;

public class MainActivity extends Activity implements Callback {
    private TextView textViewResults;
    private Button buttonRun;
    private PerfTestRunner testRunner;

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
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                    }
                    currentFocus.clearFocus();
                }
                boolean objectBox = ((CheckBox) findViewById(R.id.checkBoxObjectBox)).isChecked();
                boolean realm = ((CheckBox) findViewById(R.id.checkBoxRealm)).isChecked();
                boolean greenDao = ((CheckBox) findViewById(R.id.checkBoxGreenDao)).isChecked();
                boolean room = ((CheckBox) findViewById(R.id.checkBoxRoom)).isChecked();
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
                runTests(type, runs, numberEntities, objectBox, realm, greenDao, room);
            }
        });
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TestType.ALL);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.spinnerTestType)).setAdapter(adapter);
        textViewResults = ((TextView) findViewById(R.id.textViewResults));
    }

    @Override
    protected void onDestroy() {
        if (testRunner != null) {
            testRunner.destroy();
        }
        testRunner = null;
        super.onDestroy();
    }

    private void runTests(TestType type, int runs, int numberEntities, boolean objectBox, boolean realm, boolean greenDao, boolean room) {
        textViewResults.setText("");
        List<PerfTest> tests = new ArrayList<>();
        if (objectBox) {
            tests.add(new ObjectBoxPerfTest());
        }
        if (realm) {
            tests.add(new RealmPerfTest());
        }
        if (greenDao) {
            tests.add(new GreendaoPerfTest());
        }
        if (room) {
            tests.add(new RoomPerfTest());
        }
        testRunner = new PerfTestRunner(this, this, textViewResults, runs, numberEntities);
        testRunner.run(type, tests);
    }

    @Override
    public void done() {
        testRunner = null;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buttonRun.setEnabled(true);
            }
        });
    }

}
