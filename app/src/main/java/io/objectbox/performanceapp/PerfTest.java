package io.objectbox.performanceapp;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Markus on 01.10.2016.
 */
public abstract class PerfTest {

    protected final Context context;

    PerfTest(Context context) {
        this.context = context.getApplicationContext();
    }

    protected Benchmark getBenchmark(String name) {
        return new Benchmark(getBenchFile(name));
    }

    protected File getBenchFile(String name) {
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
        Log.d("PERF", text);
    }

    public abstract void runBatchPerfTest();
}
