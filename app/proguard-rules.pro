-keepattributes *Annotation*

-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn javax.annotation.**

-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

-keep class * implements retrofit2.CallAdapter { *; }
-keep class * implements retrofit2.Converter { *; }

-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

-keep class okhttp3.** { *; }
-dontwarn okhttp3.**