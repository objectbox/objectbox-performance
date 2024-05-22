# ObjectBox Java Database Performance Benchmarks

This is an Android app to measure object persistence performance of
- [ObjectBox](/app/src/main/java/io/objectbox/performanceapp/objectbox)
- [Realm](/app/src/main/java/io/objectbox/performanceapp/realm)
- [SQLite using Room](/app/src/main/java/io/objectbox/performanceapp/room)
- [SQLite using greenDAO](/app/src/main/java/io/objectbox/performanceapp/greendao) (deprecated)

Results are printed on the UI and saved as tab-separated files (`.tvs`) that can be easily imported
into a spreadsheet. The files are located on external storage.

<img src="android-perf-screenshot.png" height="540"/>

## How to get good results

* Tests perform differently when multiple databases are selected: 
    For comparable results, run only a single database at a time.
* Put the test device into air plane mode to avoid background apps doing sync over the network. 
* Screen must be on at all times (e.g. plug the device in).
* Beware of lazy loaded data (e.g. properties on live objects of Realm):
    loading objects may seem very fast because no data is actually loaded.
    For better comparison it may be necessary to access data (at least once) and combine load and access time to get actual read time.
* We also have written some general notes on [benchmarking on Android](https://greenrobot.org/android/benchmarking-on-android/).