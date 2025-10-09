#Removes Log.d calls
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** e(...);
}