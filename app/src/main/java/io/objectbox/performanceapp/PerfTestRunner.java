package io.objectbox.performanceapp;

import android.app.Activity;
import android.os.Environment;
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
                if (scrollViewResults != null) {
                    scrollViewResults.fullScroll(ScrollView.FOCUS_DOWN);
                }
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
        Benchmark benchmark = createBenchmark(type, test);
        test.setBenchmark(benchmark);
        log("Starting tests with " + numberEntities + " entities at " + new Date());
        for (int i = 1; i <= runs; i++) {
            log("\n" + test.name() + " " + type + " (" + i + "/" + runs + ")\n" +
                    "------------------------------");
            test.setUp(activity, this);
            test.run(type);
            test.tearDown();
            benchmark.commit();
            if (destroyed) {
                break;
            }
        }
        log("\nTests done at " + new Date());
    }

    protected Benchmark createBenchmark(TestType type, PerfTest test) {
        String name = test.name() + "-" + type.nameShort + ".tsv";
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
