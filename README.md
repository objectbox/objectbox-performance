How to get good results
-----------------------
* Tests perform differently when multiple products are selected: 
    Thus, for more representable results, you should only run a single product at a time.
* Go into air plane mode to avoid background apps doing sync over the network 
* Screen must be on at all times (plug device in)
* Beware of lazy loaded properties (e.g. live objects on Realm):
    loading objects seems very fast because no property data is actually loaded.
    Thus it makes more sense to also access properties (at least once) and add values for load+access.
* Also consider general notes for [benchmarking on Android](http://greenrobot.org/android/benchmarking-on-android/)