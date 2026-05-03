# TinyPinyin (used for Chinese romanization)
-keep class com.github.promeg.pinyinhelper.** { *; }
-keep interface com.github.promeg.pinyinhelper.** { *; }
-dontwarn com.github.promeg.pinyinhelper.**

# ahocorasick (TinyPinyin dependency)
-keep class org.ahocorasick.** { *; }
-dontwarn org.ahocorasick.**

# Kuromoji (used for Japanese romanization)
-keep class com.atilika.kuromoji.** { *; }
-dontwarn com.atilika.kuromoji.**
