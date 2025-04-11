# ===== CONSERVAR ATRIBUTOS PARA DEPURACIÓN =====
-keepattributes SourceFile, LineNumberTable, Signature, Exceptions
-renamesourcefileattribute SourceFile

# ===== DAGGER HILT =====
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponentManagerHolder { *; }

# ===== ROOM DATABASE =====
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class * implements androidx.room.migration.AutoMigrationSpec { *; }

# ===== JAVAX (PROCESAMIENTO DE ANOTACIONES) =====
-keep class javax.annotation.processing.** { *; }
-keep class javax.lang.model.** { *; }
-keep class javax.tools.** { *; }

# ===== JAVAPOET (USADO POR ROOM/DAGGER) =====
-keep class com.squareup.javapoet.** { *; }

# ===== GOOGLE AUTO-SERVICE =====
-keep class com.google.auto.service.** { *; }
-keep @com.google.auto.service.AutoService class * { *; }

# ===== RETROFIT/GSON =====
-keep class com.google.gson.** { *; }
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# ===== FIREBASE =====
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**