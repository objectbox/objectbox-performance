package io.objectbox.performanceapp;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

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

    boolean running;
    boolean destroyed;

    public PerfTestRunner(Activity activity, Callback callback, TextView textViewResults, int runs, int numberEntities) {
        this.activity = activity;
        this.callback = callback;
        this.textViewResults = textViewResults;
        this.runs = runs;
        this.numberEntities = numberEntities;
    }

    public void run(final TestType type, final List<PerfTest> tests) {
        if (running) {
            throw new IllegalStateException("Already running");
        }
        running = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (PerfTest test : tests) {
                        if (!destroyed) {
                            PerfTestRunner.this.run(type, test);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    running = false;
                    callback.done();
                }
            }
        }).start();
    }

    public void destroy() {
        destroyed = true;
    }

    public void log(final String text) {
        Log.d("PERF", text);
        final CountDownLatch joinLatch = new CountDownLatch(1);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewResults.append(text.concat("\n"));
                joinLatch.countDown();
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
        test.setNumberEntities(numberEntities);
        for (int i = 1; i <= runs; i++) {
            log("\nStarting " + test.name() + " " + type + " (" + i + "/" + runs + ")\n" +
                    "------------------------------");
            test.setUp(activity, this);
            test.run(type);
            test.tearDown();
        }
    }
}
