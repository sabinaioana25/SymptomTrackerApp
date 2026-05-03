@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.jetbrainsCompose)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.sqlDelight)
}

kotlin {
  applyDefaultHierarchyTemplate()

  androidTarget {
    compilations.all {
      compileTaskProvider.configure {
        compilerOptions {
          jvmTarget.set(JvmTarget.JVM_17)
        }
      }
    }
    publishLibraryVariants("release", "debug")
  }

  compilerOptions {
    optIn.add("kotlin.uuid.ExperimentalUuidApi")
  }

  listOf(
    iosX64(),
    iosArm64(),
    iosSimulatorArm64()
  ).forEach {
    it.binaries.framework {
      baseName = "shared"
      isStatic = true
    }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(libs.atomic.fu)
      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.kotlinx.datetime)
      implementation(libs.sql.runtime)
      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(libs.compose.navigation.mp)
      implementation(compose.components.resources)
    }

    androidMain.dependencies {
      implementation(libs.sql.android.driver)
      implementation(compose.ui)
      implementation(compose.material3)
    }

    commonTest.dependencies {
      implementation(libs.io.kotest)
      implementation(libs.kotlin.test)
      implementation(libs.kotlinx.coroutines.test)
    }

    iosMain.dependencies {
      implementation(libs.sql.native.driver)
      implementation(compose.ui)
    }
  }
}

android {
  namespace = "com.example.recoverylogger"
  compileSdk = 36
  defaultConfig {
    minSdk = 24
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  buildTypes {
    debug {
      isMinifyEnabled = false
      isShrinkResources = false
    }
    release {
      isMinifyEnabled = false
      isShrinkResources = false
    }
  }

  lint {
    abortOnError = true
    targetSdk = 36
  }

  testOptions {
    targetSdk = 36
    animationsDisabled = true
    unitTests.all {
      it.useJUnitPlatform()
    }
  }
}

sqldelight {
  databases {
    create("AppDatabase") {
      packageName.set("com.example.recoverylogger.db")
    }
  }
}
