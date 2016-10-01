package io.objectbox.performanceapp;

import android.content.Context;
import android.os.Environment;
import android.widget.TextView;

import java.io.File;
import java.util.Random;

/**
 * Created by Markus on 01.10.2016.
 */
public abstract class PerfTest {

    protected final int runs = 8;
    protected int count = 10000;
    protected final Random random;

    protected Context context;
    private PerfTestRunner testRunner;

    PerfTest() {
        random = new Random();
    }

    public void setUp(Context context, PerfTestRunner testRunner) {
        this.context = context.getApplicationContext();
        this.testRunner = testRunner;
    }

    public void tearDown() {
    }

    protected Benchmark getBenchmark(String name) {
        return new Benchmark(getBenchFile(name));
    }

    protected File getBenchFile(String name) {
        name += ".tsv";
        File dir = Environment.getExternalStorageDirectory();
        File file = new File(dir, name);
        if (dir == null || !dir.canWrite()) {
            File appFile = new File(context.getFilesDir(), name);
            log("Using file " + appFile.getAbsolutePath() + ", (cannot write to " + file.getAbsolutePath() + ")");
            file = appFile;
        }
        return file;
    }

    protected void log(String text) {
        testRunner.log(text);
    }

    public abstract String name();

    public abstract void run(TestType type);
}
