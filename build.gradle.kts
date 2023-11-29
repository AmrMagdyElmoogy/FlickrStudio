// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false
}
buildscript {
    dependencies {
        val nav_version = "2.7.5"
        classpath(kotlin("gradle-plugin", version = "1.9.20"))
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
    }
}
true // Needed to make the Suppress annotation work for the plugins block