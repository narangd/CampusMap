# Add project specific ProGuard rules here.
# By default_info, the flags in this file are appended to flags specified
# in C:\Andoird_SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep public class com.google.android.gms.**
-dontwarn com.google.android.gms.**
-dontwarn com.nhn.android.maps.**
-dontwarn com.example.campusmap.**