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
import android.app.ActivityManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Markus on 01.10.2016.
 */

public class PerfTestRunner {

    interface Callback {
        void done();
    }

    private final Activity activity;
    private final Callback callback;
    private final TextView textViewResults;
    private final int runs;
    private final int numberEntities;
    private ScrollView scrollViewResults;

    boolean running;
    boolean destroyed;

    public PerfTestRunner(Activity activity, Callback callback, TextView textViewResults, int runs, int numberEntities) {
        this.activity = activity;
        this.callback = callback;
        this.textViewResults = textViewResults;
        if (textViewResults.getParent() instanceof ScrollView) {
            scrollViewResults = (ScrollView) textViewResults.getParent();
        }
        this.runs = runs;
        this.numberEntities = numberEntities;
    }

    public void run(final TestType type, final List<PerfTest> tests) {
        if (running) {
            throw new IllegalStateException("Already running");
        }
        running = true;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (PerfTest test : tests) {
                        if (!destroyed) {
                            try {
                                PerfTestRunner.this.run(type, test);
                            } catch (Exception e) {
                                logError("Aborted because of " + e.getMessage());
                                Log.e("PERF", "Error while running tests", e);
                            }
                        }
                    }
                } finally {
                    running = false;
                    callback.done();
                }
            }
        });
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    public void destroy() {
        destroyed = true;
    }

    public void log(final String text) {
        log(text, false);
    }

    public void logError(final String text) {
        log(text, true);
    }

    private void log(final String text, final boolean error) {
        Log.d("PERF", text);
        final CountDownLatch joinLatch = new CountDownLatch(1);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (error) {
                    Spannable errorSpan = new SpannableString(text.concat("\n"));
                    errorSpan.setSpan(new ForegroundColorSpan(Color.RED), 0, errorSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textViewResults.append(errorSpan);
                } else {
                    textViewResults.append(text.concat("\n"));
                }
                // post so just appended text is visible
                if (scrollViewResults != null) {
                    textViewResults.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollViewResults.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
                textViewResults.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        joinLatch.countDown();

                    }
                }, 20);
            }
        });
        try {
            boolean ok = joinLatch.await(10, TimeUnit.SECONDS);
            if (!ok) {
                throw new RuntimeException("Not joined");
            }
            // Give UI time to settle (> 1 frame)
            Thread.sleep(20);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void run(TestType type, PerfTest test) {
        printDeviceInfo();

        test.setNumberEntities(numberEntities);
        Benchmark benchmark = createBenchmark(type, test, numberEntities);
        test.setBenchmark(benchmark);
        log("\nStarting tests with " + numberEntities + " entities at " + new Date());
        for (int i = 1; i <= runs; i++) {
            log("\n" + test.name() + " " + type + " (" + i + "/" + runs + ")\n" +
                    "------------------------------");
            test.setUp(activity, this);

            RuntimeException exDuringRun = null;
            try {
                test.run(type);
            } catch (RuntimeException ex) {
                exDuringRun = ex;
            }

            RuntimeException exDuringTearDown = null;
            try {
                test.tearDown();
            } catch (RuntimeException ex) {
                exDuringTearDown = ex;
            }
            if (exDuringRun != null) {
                throw exDuringRun;
            } else if (exDuringTearDown != null) {
                throw exDuringTearDown;
            }
            benchmark.commit();
            if (destroyed) {
                break;
            }
        }
        test.allTestsComplete();
        log("\nTests done at " + new Date());
    }

    private void printDeviceInfo() {
        log("Model: " + Build.MANUFACTURER + " " + Build.MODEL
                + ", Android " + Build.VERSION.RELEASE);

        ActivityManager activityManager =
                (ActivityManager) activity.getSystemService(Activity.ACTIVITY_SERVICE);
        int memoryClassMb = activityManager.getMemoryClass();
        int largeMemoryClassMb = activityManager.getLargeMemoryClass();
        log("MemoryClass: " + memoryClassMb + " MB");
        log("LargeMemoryClass: " + largeMemoryClassMb + " MB");
    }

    protected Benchmark createBenchmark(TestType type, PerfTest test, int numberEntities) {
        String name = test.name() + "-" + type.nameShort + "-" + numberEntities + ".tsv";
        File dir = Environment.getExternalStorageDirectory();
        File file = new File(dir, name);
        if (dir == null || !dir.canWrite()) {
            File appFile = new File(activity.getFilesDir(), name);
            Log.i("PERF", "Using file " + appFile.getAbsolutePath() + " because " + file.getAbsolutePath() +
                    " is not writable - please grant the storage permission to the app");
            file = appFile;
        }
        return new Benchmark(file);
    }
}
