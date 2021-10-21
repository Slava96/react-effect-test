import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

val ktorVersion: String = "1.6.0"

plugins {
    kotlin("multiplatform") version "1.5.10"
    application
}

group = "me.dbtcuser"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlin/p/kotlin/kotlin-js-wrappers") }
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
        withJava()
    }

    js {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }

    sourceSets {
        val commonMain by getting {

        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-html-builder:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        val kotlinWrappersVersion = "0.0.1-pre.213-kotlin-1.5.10"

        val jsMain by getting {
            dependencies {
                implementation(project.dependencies.enforcedPlatform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:$kotlinWrappersVersion"))
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom")
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

application {
    mainClassName = "ServerKt"
}

tasks.getByName<KotlinWebpack>("jsBrowserProductionWebpack") {
    outputFileName = "js.js"
}

tasks.getByName<Jar>("jvmJar") {
    val taskName = if(project.hasProperty("isProduction")) "jsBrowserProductionWebpack" else "jsBrowserDevelopmentWebpack"
    val webpackTask = tasks.getByName<KotlinWebpack>(taskName)
    dependsOn(webpackTask)
    from(webpackTask.destinationDirectory)
}

tasks.getByName<JavaExec>("run") {
    dependsOn(tasks.getByName<Jar>("jvmJar"))
    classpath(tasks.getByName<Jar>("jvmJar"))
}