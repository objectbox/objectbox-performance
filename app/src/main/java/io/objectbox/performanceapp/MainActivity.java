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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.performanceapp.PerfTestRunner.Callback;
import io.objectbox.performanceapp.databinding.ActivityMainBinding;
import io.objectbox.performanceapp.greendao.GreendaoPerfTest;
import io.objectbox.performanceapp.objectbox.ObjectBoxPerfTest;
import io.objectbox.performanceapp.realm.RealmPerfTest;
import io.objectbox.performanceapp.room.RoomPerfTest;

public class MainActivity extends Activity implements Callback {

    private static final String PREF_TYPE = "io.objectbox.performance.type";
    private static final String PREF_RUNS = "io.objectbox.performance.runs";
    private static final String PREF_COUNT = "io.objectbox.performance.count";

    private ActivityMainBinding binding;
    private PerfTestRunner testRunner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonRunTest.setOnClickListener(view -> {
            binding.buttonRunTest.setEnabled(false);
            View currentFocus = MainActivity.this.getCurrentFocus();
            if (currentFocus != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
                currentFocus.clearFocus();
            }
            boolean objectBox = binding.checkBoxObjectBox.isChecked();
            boolean realm = binding.checkBoxRealm.isChecked();
            boolean greenDao = binding.checkBoxGreenDao.isChecked();
            boolean room = binding.checkBoxRoom.isChecked();
            TestType type = (TestType) binding.spinnerTestType.getSelectedItem();

            int runs = getIntegerFromEditTextOrZero(binding.editTextRuns);
            int numberEntities = getIntegerFromEditTextOrZero(binding.editTextNumberEntities);

            runTests(type, runs, numberEntities, objectBox, realm, greenDao, room);
        });

        ArrayAdapter adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                TestType.ALL
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTestType.setAdapter(adapter);

        // Restore type, runs and count or set defaults.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        binding.spinnerTestType
                .setSelection(prefs.getInt(PREF_TYPE, 1), false);
        binding.editTextRuns
                .setText(String.valueOf(prefs.getInt(PREF_RUNS, 1)));
        binding.editTextNumberEntities
                .setText(String.valueOf(prefs.getInt(PREF_COUNT, 100000)));
    }

    private int getIntegerFromEditTextOrZero(EditText editText) {
        try {
            return Integer.parseInt(editText.getText().toString());
        } catch (NumberFormatException e) {
            binding.textViewResults.append(e.getMessage() + "\n");
            return 0;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save type, runs and count.
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putInt(PREF_TYPE, binding.spinnerTestType.getSelectedItemPosition())
                .putInt(PREF_RUNS,
                        getIntegerFromEditTextOrZero(binding.editTextRuns))
                .putInt(PREF_COUNT,
                        getIntegerFromEditTextOrZero(binding.editTextNumberEntities))
                .apply();
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
        binding.textViewResults.setText("");
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
        testRunner = new PerfTestRunner(this, this, binding.textViewResults, runs, numberEntities);
        testRunner.run(type, tests);
    }

    @Override
    public void done() {
        testRunner = null;
        runOnUiThread(() -> binding.buttonRunTest.setEnabled(true));
    }

}
