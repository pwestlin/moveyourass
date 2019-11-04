import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.0.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    kotlin("jvm") version "1.3.50"
    kotlin("plugin.spring") version "1.3.50"
}

group = "nu.westlin.moveyourass"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val developmentOnly: Configuration by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
}

dependencies {
    implementation("org.springframework.fu:spring-fu-kofu:0.2.2.BUILD-SNAPSHOT")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-mustache")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.data:spring-data-r2dbc:1.0.0.RC1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.r2dbc:r2dbc-h2")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.mockk:mockk:1.9.3")
}

dependencyManagement {
    imports {
        mavenBom("io.r2dbc:r2dbc-bom:Arabba-RC2")
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.spring.io/milestone")
    maven("https://repo.spring.io/snapshot")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=enable")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()

    include("**/*Test.class")

    addTestListener(object : TestListener {
        override fun beforeTest(p0: TestDescriptor?) = Unit
        override fun beforeSuite(p0: TestDescriptor?) = Unit
        override fun afterTest(desc: TestDescriptor, result: TestResult) = Unit
        override fun afterSuite(desc: TestDescriptor, result: TestResult) {
            printResults(desc, result)
        }
    })
}

fun printResults(desc: TestDescriptor, result: TestResult) {
    val output = "${desc.name} results: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} successes, ${result.failedTestCount} failures, ${result.skippedTestCount} skipped)"
    val startItem = "|  "
    val endItem = "  |"
    val repeatLength = startItem.length + output.length + endItem.length
    println("\n" + ("-".repeat(repeatLength)) + "\n" + startItem + output + endItem + "\n" + ("-".repeat(repeatLength)))
}

configurations.all {
    exclude(module = "jakarta.validation-api")
    exclude(module = "hibernate-validator")
}