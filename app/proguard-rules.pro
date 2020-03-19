# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Obfuscate resources file names and content TODO aren't these on by default?
-adaptresourcefilenames
-adaptresourcefilecontents

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt

#-keepclassmembers class **.R$* {public static <fields>;}
#-keep class **.R$*

# Carbon
-keepclasseswithmembernames class * {
    native <methods>;
}

-keep class android.support.v8.renderscript.** { *; }

-dontwarn carbon.**
-dontwarn carbon.BR
-dontwarn carbon.internal**
-dontwarn java.lang.invoke**

-dontwarn android.databinding.**
-keep class android.databinding.** { *; }

# Canvas.save(int) removal in api 28 issue. I guess Carbon sdk uses this? TODO
-dontwarn com.caverock.androidsvg.**
-keep class com.caverock.androidsvg.**
-keep interface com.caverock.androidsvg.**

# EventBus
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}