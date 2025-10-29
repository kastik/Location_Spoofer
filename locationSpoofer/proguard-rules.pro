#Removes Log.d calls
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** e(...);
}

# Keep all generated protobuf classes
-keep class com.google.protobuf.** { *; }
-keep class com.google.maps.** { *; }
-keep class com.google.api.** { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * extends com.google.protobuf.GeneratedMessageLite {
    <init>(...);
}