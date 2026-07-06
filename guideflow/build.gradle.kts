plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.vanniktech.publish)
    id("signing")
}

android {
    namespace = "com.rajjaviya.guideflow"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }

}

kotlin {
    jvmToolchain(11)
}

// ---------------------------------------------------------------------------
// Detekt
// ---------------------------------------------------------------------------
detekt {
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    autoCorrect = true
}

// ---------------------------------------------------------------------------
// KtLint
// ---------------------------------------------------------------------------
ktlint {
    version = "1.5.0"
    android = true
    verbose = true
    outputToConsole = true
}

// ---------------------------------------------------------------------------
// Maven publishing (Vanniktech)
// ---------------------------------------------------------------------------
mavenPublishing {
    coordinates(
        groupId = "io.github.javiyaraj",
        artifactId = "guideflow",
        version = "1.0.0",
    )
    
    pom {
        name.set("GuideFlow")
        description.set("A modern, lightweight, and incredibly flexible in-app tour and onboarding SDK for Android.")
        inceptionYear.set("2026")
        url.set("https://github.com/JAVIYARAJ/guide_flow_sdk")
        
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        
        developers {
            developer {
                id.set("rajjaviya")
                name.set("Raj Javiya")
                url.set("https://github.com/rajjaviya")
            }
        }
        
        scm {
            url.set("https://github.com/JAVIYARAJ/guide_flow_sdk")
            connection.set("scm:git:git://github.com/JAVIYARAJ/guide_flow_sdk.git")
            developerConnection.set("scm:git:ssh://git@github.com/JAVIYARAJ/guide_flow_sdk.git")
        }
    }
    
    // Explicitly enable GPG signing (fixes Missing signature errors)
    signAllPublications()
    
    // Publish to both Maven Central and GitHub Packages if environment variables are set
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
}

// ---------------------------------------------------------------------------
// GPG Signing Configuration
// ---------------------------------------------------------------------------
signing {
    val keyId = project.findProperty("signing.keyId")?.toString()?.removePrefix("0x")
    val password = project.findProperty("signing.password")?.toString()
    
    // Replace the literal "\n" characters from gradle.properties with actual line breaks
    val key = project.findProperty("signing.key")?.toString()?.replace("\\n", "\n")

    if (keyId != null && password != null && key != null) {
        useInMemoryPgpKeys(keyId, key, password)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.bundles.coroutines)

    testImplementation(libs.bundles.testing)
    androidTestImplementation(libs.bundles.android.test)
}
