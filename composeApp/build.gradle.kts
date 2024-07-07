import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // kotlin
            implementation(libs.kotlinx.coroutines.android)

            // androidx
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.work.runtime.ktx)

            // others
            implementation(libs.jsoup)

            // androidx media3
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.exoplayer.dash)
            implementation(libs.androidx.media3.exoplayer.hls)
            implementation(libs.androidx.media3.exoplayer.rtsp)
            implementation(libs.androidx.media3.exoplayer.smoothstreaming)
            implementation(libs.androidx.media3.datasource.okhttp)
            implementation(libs.androidx.media3.datasource.rtmp)
            implementation(libs.androidx.media3.ui)

            // squareup
            implementation(libs.squareup.okio)
            implementation(libs.squareup.okhttp3.okhttp)
            implementation(libs.squareup.okhttp3.logging)

            // ktor
            implementation(libs.ktor.client.okhttp)

            // local
            //implementation(project(":composetrack"))
        }

        iosMain.dependencies {
            // ktor
            implementation(libs.ktor.client.darwin)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // kotlin
            implementation(libs.kotlin.stdlib)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)

            // androidx
            implementation(libs.androidx.annotation)
            implementation(libs.androidx.lifecycle.runtime)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.swiperefreshlayout)

            // coil
            implementation(libs.coil)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            // Kotlin Inject
            implementation(libs.kotlinInject.runtime.kmp)

            // compose
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.compose.material3)
            implementation(libs.androidx.paging.common)
            implementation(libs.androidx.constraintlayout.compose)
            //implementation(libs.compose.markdown)

            // navigation
            implementation(libs.androidx.navigation.compose)

            // google
            implementation(libs.google.flexbox)

            // ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.encoding)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            // others
            implementation(libs.napier)

            // local
            implementation(project(":subsamplingimage:library"))
        }
    }

    configureCommonMainKsp()
}

android {
    namespace = "com.hym.zhankumultiplatform"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.hym.zhankumultiplatform"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)

        // debug
        //debugImplementation(project(":logcollector"))
        debugImplementation(libs.squareup.leakcanary.android)

        // debug compose
        debugImplementation(libs.androidx.compose.ui.tooling)
        debugImplementation(libs.androidx.compose.ui.test.manifest)

        // test
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)

        // test compose
        androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    }
}

dependencies {
    // 1. Configure code generation into the common source set
    kspCommonMainMetadata(libs.kotlinInject.compiler)

    // 2. Configure code generation into each KMP target source set
    add("kspAndroid", libs.kotlinInject.compiler)
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
    add("kspIosSimulatorArm64", libs.kotlinInject.compiler)
}

fun KotlinMultiplatformExtension.configureCommonMainKsp() {
    /*
    sourceSets.named("commonMain").configure {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }
    */

    project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
        if (name != "kspCommonMainKotlinMetadata") {
            dependsOn("kspCommonMainKotlinMetadata")
        }
    }
}

ksp {
    arg("me.tatarka.inject.dumpGraph", "true")
}
