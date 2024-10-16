# Keep all ML Kit classes
-keep class com.google.mlkit.** { *; }

# Keep the generated model classes (if using custom models)
-keep class com.google.android.gms.vision.** { *; }
-keep class com.google.mlkit.common.** { *; }

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# -keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
# }

# Uncomment this to preserve the line number information for debugging stack traces.
# This can help during debugging when you're analyzing crash reports.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to hide the original source file name.
# -renamesourcefileattribute SourceFile             proguard-rules.pro
