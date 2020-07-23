import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.provideDelegate

// Provides dependencies that can be used throughout the project build.gradle files

// https://github.com/JetBrains/kotlin/blob/master/ChangeLog.md
// https://github.com/JetBrains/kotlin/releases/latest
// https://plugins.jetbrains.com/plugin/6954-kotlin
// https://kotlinlang.org/docs/reference/whatsnew13.html
// https://blog.jetbrains.com/kotlin/2019/11/kotlin-1-3-60-released/
// https://blog.jetbrains.com/kotlin/2020/03/kotlin-1-3-70-released/
// TODO: Update corresponding buildSrc/build.gradle.kts value when updating this version!
private const val KOTLIN_VERSION = "1.3.72"
private const val NAVIGATION_VERSION = "2.3.0"

/**
 * Provides the source of truth for version/configuration information to any gradle build file (project and app module build.gradle.kts)
 */
object Config {
    // https://github.com/JLLeitschuh/ktlint-gradle/blob/master/CHANGELOG.md
    // https://github.com/JLLeitschuh/ktlint-gradle/releases
    const val KTLINT_GRADLE_VERSION = "9.2.1"

    // https://github.com/pinterest/ktlint/blob/master/CHANGELOG.md
    // https://github.com/pinterest/ktlint/releases
    const val KTLINT_VERSION = "0.36.0"

    // 1. Execute `./gradlew jacocoTestReport` OR `./gradlew jacocoTestInternalDebugUnitTestReport`
    // 2. Execute `open app/build/jacoco/jacocoHtml/index.html` or the `Open Jacoco Report` AS Run Configuration
    // http://www.jacoco.org/jacoco/trunk/doc/
    // https://github.com/jacoco/jacoco/releases
    const val JACOCO_VERSION = "0.8.5"

    /**
     * Called from root project buildscript block in the project root build.gradle.kts
     */
    object BuildScriptPlugins {
        // https://developer.android.com/studio/releases/gradle-plugin
        // https://developer.android.com/studio/releases/gradle-plugin#3-3-0
        // https://developer.android.com/studio/preview/features/new-android-plugin.html
        // https://androidstudio.googleblog.com/2019/07/android-studio-342-available.html
        // https://androidstudio.googleblog.com/2019/11/android-studio-352-available.html
        // https://developer.android.com/studio/releases/gradle-plugin#3-6-0
        // https://developer.android.com/studio/releases/gradle-plugin#4-0-0
        // TODO: Update corresponding buildSrc/build.gradle.kts value when updating this version!
        const val ANDROID_GRADLE = "com.android.tools.build:gradle:4.0.0"
        const val KOTLIN_GRADLE = "org.jetbrains.kotlin:kotlin-gradle-plugin:$KOTLIN_VERSION"

        // Gradle version plugin; use dependencyUpdates task to view third party dependency updates via `./gradlew dependencyUpdates` or AS Gradle -> [project]] -> Tasks -> help -> dependencyUpdates
        // https://github.com/ben-manes/gradle-versions-plugin/releases
        const val GRADLE_VERSIONS = "com.github.ben-manes:gradle-versions-plugin:0.28.0"

        // https://github.com/arturdm/jacoco-android-gradle-plugin/releases
        // const val JACOCO_ANDROID = "com.dicedmelon.gradle:jacoco-android:0.1.4"
        // As the dicedmelon plugin doesn't support gradle 6 yet, using the hiya ported plugin. See https://github.com/arturdm/jacoco-android-gradle-plugin/pull/75#issuecomment-565222643
        const val JACOCO_ANDROID = "com.hiya:jacoco-android:0.2"
        const val NAVIGATION_SAFE_ARGS_GRADLE = "androidx.navigation:navigation-safe-args-gradle-plugin:$NAVIGATION_VERSION"
    }

    /**
     * Called in non-root project modules via id()[org.gradle.kotlin.dsl.PluginDependenciesSpecScope.id]
     * or kotlin()[org.gradle.kotlin.dsl.kotlin] (the PluginDependenciesSpec.kotlin extension function) in a build.gradle.kts
     */
    object ApplyPlugins {
        const val ANDROID_APPLICATION = "com.android.application"
        const val ANDROID_LIBRARY = "com.android.library"
        const val GRADLE_VERSIONS = "com.github.ben-manes.versions"
        const val KT_LINT = "org.jlleitschuh.gradle.ktlint"

        // const val JACOCO_ANDROID = "jacoco-android"
        // As the dicedmelon plugin doesn't support gradle 6 yet, using the hiya ported plugin. See https://github.com/arturdm/jacoco-android-gradle-plugin/pull/75#issuecomment-565222643
        const val JACOCO_ANDROID = "com.hiya.jacoco-android"
        const val NAVIGATION_SAFE_ARGS_KOTLIN = "androidx.navigation.safeargs.kotlin"

        object Kotlin {
            const val ANDROID = "android"
            const val ANDROID_EXTENSIONS = "android.extensions"
            const val KAPT = "kapt"
        }
    }

    // What each version represents - https://medium.com/androiddevelopers/picking-your-compilesdkversion-minsdkversion-targetsdkversion-a098a0341ebd
    object AndroidSdkVersions {
        const val COMPILE_SDK = 29

        // https://developer.android.com/studio/releases/build-tools
        const val BUILD_TOOLS = "29.0.3"
        const val MIN_SDK = 23

        // https://developer.android.com/about/versions/10/behavior-changes-10
        const val TARGET_SDK = 29
    }

    // Gradle versions plugin configuration: https://github.com/ben-manes/gradle-versions-plugin#revisions
    fun isNonStable(version: String): Boolean {
        val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(version)
        return isStable.not()
    }
}

/**
 * Dependency Version definitions with links to source (if open source)/release notes. Defines the version in one place for multiple dependencies that use the same version.
 * Use [DependencyHandler] extension functions below that provide logical groupings of dependencies including appropriate configuration accessors.
 */
private object Libraries {
    //// AndroidX
    // All androidx dependencies version table: https://developer.android.com/jetpack/androidx/versions#version-table
    // https://developer.android.com/jetpack/androidx/releases/core
    // https://developer.android.com/kotlin/ktx#core-packages
    // https://developer.android.com/jetpack/androidx/releases/
    // https://developer.android.com/kotlin/ktx

    // https://developer.android.com/jetpack/androidx/releases/core
    const val CORE_KTX = "androidx.core:core-ktx:1.3.0"

    // Lifecycle
    // https://developer.android.com/jetpack/androidx/releases/lifecycle
    private const val LIFECYCLE_VERSION = "2.2.0"
    const val LIFECYCLE_LIVEDATA_KTX = "androidx.lifecycle:lifecycle-livedata-ktx:$LIFECYCLE_VERSION"
    const val LIFECYCLE_VIEWMODEL_KTX = "androidx.lifecycle:lifecycle-viewmodel-ktx:$LIFECYCLE_VERSION"
    const val LIFECYCLE_COMMON_JAVA8 = "androidx.lifecycle:lifecycle-common-java8:$LIFECYCLE_VERSION"

    // https://developer.android.com/jetpack/androidx/releases/appcompat
    const val APP_COMPAT = "androidx.appcompat:appcompat:1.1.0"

    // https://androidstudio.googleblog.com/2019/05/constraintlayout-200-beta-1.html
    // https://androidstudio.googleblog.com/2019/06/constraintlayout-200-beta-2.html
    // https://androidstudio.googleblog.com/2019/10/constraintlayout-200-beta-3.html
    // https://androidstudio.googleblog.com/2019/12/constraintlayout-200-beta-4.html
    // https://developer.android.com/jetpack/androidx/releases/constraintlayout
    const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:2.0.0-beta8"

    // Navigation
    // https://developer.android.com/jetpack/androidx/releases/navigation
    const val NAVIGATION_FRAGMENT_KTX = "androidx.navigation:navigation-fragment-ktx:$NAVIGATION_VERSION"
    const val NAVIGATION_UI_KTX = "androidx.navigation:navigation-ui-ktx:$NAVIGATION_VERSION"

    // https://security.googleblog.com/2020/02/data-encryption-on-android-with-jetpack.html
    // https://developer.android.com/topic/security/data
    // https://developer.android.com/jetpack/androidx/releases/security
    const val SECURITY_CRYPTO = "androidx.security:security-crypto:1.0.0-rc02"

    //// Material
    // https://github.com/material-components/material-components-android/releases
    const val MATERIAL = "com.google.android.material:material:1.1.0"

    //// Kotlin
    const val KOTLIN_STDLIB_JDK7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$KOTLIN_VERSION"
    const val KOTLIN_REFLECT = "org.jetbrains.kotlin:kotlin-reflect:$KOTLIN_VERSION"

    //// Coroutines + Flow
    // https://kotlinlang.org/docs/reference/coroutines/flow.html
    // https://github.com/Kotlin/kotlinx.coroutines/blob/master/CHANGES.md
    // https://github.com/Kotlin/kotlinx.coroutines/releases
    private const val KOTLIN_COROUTINES_VERSION = "1.3.7"
    const val KOTLINX_COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$KOTLIN_COROUTINES_VERSION"
    const val KOTLINX_COROUTINES_ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$KOTLIN_COROUTINES_VERSION"

    //// Koin
    // https://github.com/InsertKoinIO/koin/blob/master/CHANGELOG.md
    // https://github.com/InsertKoinIO/koin/releases
    private const val KOIN_VERSION = "2.1.6"
    const val KOIN_ANDROID = "org.koin:koin-android:$KOIN_VERSION"
    const val KOIN_ANDROIDX_SCOPE = "org.koin:koin-androidx-scope:$KOIN_VERSION"
    const val KOIN_ANDROIDX_VIEWMODEL = "org.koin:koin-androidx-viewmodel:$KOIN_VERSION"

    //// Retrofit
    // javadoc: https://square.github.io/retrofit/2.x/retrofit/
    // https://github.com/square/retrofit/blob/master/CHANGELOG.md
    // https://github.com/square/retrofit/releases
    private const val RETROFIT_VERSION = "2.9.0"
    const val RETROFIT = "com.squareup.retrofit2:retrofit:$RETROFIT_VERSION"
    const val RETROFIT_SCALARS_CONVERTER = "com.squareup.retrofit2:converter-scalars:$RETROFIT_VERSION"
    const val RETROFIT_MOSHI_CONVERTER = "com.squareup.retrofit2:converter-moshi:$RETROFIT_VERSION"

    //// Moshi
    // https://github.com/square/moshi/blob/master/CHANGELOG.md
    // https://github.com/square/moshi/releases
    private const val MOSHI_VERSION = "1.9.3"
    const val MOSHI_KOTLIN = "com.squareup.moshi:moshi-kotlin:$MOSHI_VERSION"
    const val MOSHI_KOTLIN_CODEGEN = "com.squareup.moshi:moshi-kotlin-codegen:$MOSHI_VERSION"

    //// UI
    // NOTE: groupie-databinding is deprecated. groupie-viewbinding is the replacement and should be used with both viewbinding and databinding. See https://github.com/lisawray/groupie#note
    // https://github.com/lisawray/groupie/releases
    private const val GROUPIE_VERSION = "2.8.0"
    const val GROUPIE = "com.xwray:groupie:$GROUPIE_VERSION"
    const val GROUPIE_KOTLIN_ANDROID_EXTENSIONS = "com.xwray:groupie-kotlin-android-extensions:$GROUPIE_VERSION"
    const val GROUPIE_VIEWBINDING = "com.xwray:groupie-viewbinding:$GROUPIE_VERSION"

    //// Utility
    // Blog: https://proandroiddev.com/livedata-with-single-events-2395dea972a8
    // https://github.com/hadilq/LiveEvent/releases
    const val LIVE_EVENT = "com.github.hadilq.liveevent:liveevent:1.2.0"

    // https://github.com/JakeWharton/timber/blob/master/CHANGELOG.md
    // https://github.com/JakeWharton/timber/releases
    const val TIMBER = "com.jakewharton.timber:timber:4.7.1"

    // https://github.com/JakeWharton/ProcessPhoenix/blob/master/CHANGELOG.md
    // https://github.com/JakeWharton/ProcessPhoenix/releases
    const val PROCESS_PHOENIX = "com.jakewharton:process-phoenix:2.0.0"

    // Commons codec - used for base64 operations (no android framework requirement)
    // https://github.com/apache/commons-codec/blob/master/RELEASE-NOTES.txt
    // https://github.com/apache/commons-codec/releases
    const val COMMONS_CODEC = "commons-codec:commons-codec:1.14"

    // https://square.github.io/leakcanary/changelog/
    // https://github.com/square/leakcanary/releases
    // Just use on debugImplementation builds
    // TODO: Keep an eye on plumber-android possible future usage in release builds: https://square.github.io/leakcanary/changelog/#plumber-android-is-a-new-artifact-that-fixes-known-android-leaks
    const val LEAK_CANARY = "com.squareup.leakcanary:leakcanary-android:2.4"

    // Chucker
    // https://medium.com/@cortinico/introducing-chucker-18f13a51b35d
    // https://github.com/ChuckerTeam/chucker/blob/develop/CHANGELOG.md
    // https://github.com/ChuckerTeam/chucker/releases
    private const val CHUCKER_VERSION = "3.2.0"
    const val CHUCKER = "com.github.ChuckerTeam.Chucker:library:$CHUCKER_VERSION"
    const val CHUCKER_NO_OP = "com.github.ChuckerTeam.Chucker:library-no-op:$CHUCKER_VERSION"

    // https://github.com/amitshekhariitbhu/Android-Debug-Database/blob/master/CHANGELOG.md
    // https://github.com/amitshekhariitbhu/Android-Debug-Database/releases
    const val DEBUG_DATABASE = "com.amitshekhar.android:debug-db:1.0.6"
}

/**
 * test and/or androidTest specific dependencies.
 * Use [DependencyHandler] extension functions below that provide logical groupings of dependencies including appropriate configuration accessors.
 */
private object TestLibraries {
    // https://github.com/junit-team/junit4/releases
    // https://github.com/junit-team/junit4/blob/master/doc/ReleaseNotes4.13.md
    const val JUNIT = "junit:junit:4.13"

    // main site: https://google.github.io/truth/
    // comparison to other assertion libs: https://google.github.io/truth/comparison
    // benefits: https://google.github.io/truth/benefits
    // javadocs: https://truth.dev/api/1.0/
    // https://github.com/google/truth/releases
    const val TRUTH = "com.google.truth:truth:1.0.1"

    // https://github.com/nhaarman/mockito-kotlin/wiki/Mocking-and-verifying
    // https://github.com/nhaarman/mockito-kotlin/releases
    const val MOCKITO_KOTLIN = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.0"

    //// AndroidX - testing
    // https://developer.android.com/jetpack/androidx/releases/arch
    const val ARCH_CORE_TESTING = "androidx.arch.core:core-testing:2.1.0"
    const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:3.2.0"
}

//// Dependency Groups - to be used inside dependencies {} block instead of declaring all necessary lines for a particular dependency
fun DependencyHandler.kotlinDependencies() {
    implementation(Libraries.KOTLIN_STDLIB_JDK7)
    implementation(Libraries.KOTLIN_REFLECT)
}

fun DependencyHandler.coroutineDependencies() {
    implementation(Libraries.KOTLINX_COROUTINES_CORE)
    implementation(Libraries.KOTLINX_COROUTINES_ANDROID)
}

fun DependencyHandler.koinAppDependencies() {
    implementation(Libraries.KOIN_ANDROID)
    implementation(Libraries.KOIN_ANDROIDX_SCOPE)
    implementation(Libraries.KOIN_ANDROIDX_VIEWMODEL)
}
fun DependencyHandler.koinDataDependencies() {
    implementation(Libraries.KOIN_ANDROID)
    implementation(Libraries.KOIN_ANDROIDX_SCOPE)
}
fun DependencyHandler.retrofitDependencies() {
    implementation(Libraries.RETROFIT)
    implementation(Libraries.RETROFIT_SCALARS_CONVERTER)
    implementation(Libraries.RETROFIT_MOSHI_CONVERTER)
}

fun DependencyHandler.moshiDependencies() {
    api(Libraries.MOSHI_KOTLIN)
    kapt(Libraries.MOSHI_KOTLIN_CODEGEN)
}

fun DependencyHandler.appCompatDependencies() {
    implementation(Libraries.APP_COMPAT)
}
fun DependencyHandler.constraintLayoutDependencies() {
    implementation(Libraries.CONSTRAINT_LAYOUT)
}

fun DependencyHandler.lifecycleDependencies() {
    implementation(Libraries.LIFECYCLE_LIVEDATA_KTX)
    implementation(Libraries.LIFECYCLE_VIEWMODEL_KTX)
    implementation(Libraries.LIFECYCLE_COMMON_JAVA8)
}
fun DependencyHandler.navigationDependencies() {
    implementation(Libraries.NAVIGATION_FRAGMENT_KTX)
    implementation(Libraries.NAVIGATION_UI_KTX)
}

fun DependencyHandler.materialDependencies() {
    implementation(Libraries.MATERIAL)
}

fun DependencyHandler.coreKtxDependencies() {
    implementation(Libraries.CORE_KTX)
}

fun DependencyHandler.securityCryptoDependencies() {
    implementation(Libraries.SECURITY_CRYPTO)
}

fun DependencyHandler.groupieDependencies() {
    implementation(Libraries.GROUPIE)
    implementation(Libraries.GROUPIE_KOTLIN_ANDROID_EXTENSIONS)
    implementation(Libraries.GROUPIE_VIEWBINDING)
}

fun DependencyHandler.timberDependencies() {
    implementation(Libraries.TIMBER)
}
fun DependencyHandler.processPhoenixDependencies() {
    implementation(Libraries.PROCESS_PHOENIX)
}
fun DependencyHandler.liveEventDependencies() {
    implementation(Libraries.LIVE_EVENT)
}

fun DependencyHandler.commonsCodecDependencies() {
    implementation(Libraries.COMMONS_CODEC)
}

fun DependencyHandler.leakCanaryDependencies() {
    debugImplementation(Libraries.LEAK_CANARY) // note the debugImplementation usage (no releaseImplementation) - intentionally bypass adding to internalRelease build unless QA has a reason to need it
}

fun DependencyHandler.chuckerDependencies(devConfigurations: List<Configuration>, productionConfiguration: Configuration) {
    // Only add dependency for dev configurations in the list
    devConfigurations.forEach { devConfiguration: Configuration ->
        add(devConfiguration.name, Libraries.CHUCKER)
    }
    // Production configuration is a no-op
    add(productionConfiguration.name, Libraries.CHUCKER_NO_OP) // note the releaseImplementation no-op
}

fun DependencyHandler.debugDatabaseDependencies(devConfigurations: List<Configuration>) {
    // Only add dependency for dev configurations in the list
    devConfigurations.forEach { devConfiguration: Configuration ->
        add(devConfiguration.name, Libraries.DEBUG_DATABASE)
    }
}

// Test specific dependency groups
fun DependencyHandler.junitDependencies() {
    testImplementation(TestLibraries.JUNIT)
}

fun DependencyHandler.mockitoKotlinDependencies() {
    testImplementation(TestLibraries.MOCKITO_KOTLIN)
}

fun DependencyHandler.truthDependencies() {
    testImplementation(TestLibraries.TRUTH)
}

fun DependencyHandler.archCoreTestingDependencies() {
    testImplementation(TestLibraries.ARCH_CORE_TESTING)
}

fun DependencyHandler.espressoDependencies() {
    androidTestImplementation(TestLibraries.ESPRESSO_CORE)
}
