package io.objectbox.performanceapp;

import android.content.Context;

import java.util.Random;

/**
 * Created by Markus on 01.10.2016.
 */
public abstract class PerfTest {

    protected Random random;
    protected Context context;
    protected PerfTestRunner testRunner;
    protected int numberEntities;
    protected Benchmark benchmark;

    public void setUp(Context context, PerfTestRunner testRunner) {
        random = new Random();
        this.context = context.getApplicationContext();
        this.testRunner = testRunner;
    }

    public void tearDown() {
    }

    protected void log(String text) {
        testRunner.log(text);
    }

    public abstract String name();

    public abstract void run(TestType type);

    public void setNumberEntities(int numberEntities) {
        this.numberEntities = numberEntities;
    }

    public void setBenchmark(Benchmark benchmark) {
        this.benchmark = benchmark;
    }

    protected void startBenchmark(String name) {
        benchmark.start(name);
    }

    protected void stopBenchmark() {
        log(benchmark.stop());
    }

    public String randomString() {
        return RandomValues.createRandomString(random, 0, 100);
    }

    public byte[] randomBytes() {
        int length = random.nextInt(100);
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return bytes;
    }

    public void allTestsComplete() {
    }
}
