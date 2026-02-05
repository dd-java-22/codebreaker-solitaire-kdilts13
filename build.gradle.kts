/*
 *  Copyright 2026 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    jacoco
    id("org.openapi.generator") version "7.2.0"
}

val javaVersion = libs.versions.java.get()

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
}

/**
 * Add OpenAPI-generated sources to the main source set.
 *
 * We generate into build/generated/openapi and compile from there (recommended),
 * rather than generating into the project root or src/main/java directly.
 */
sourceSets {
    main {
        java {
            srcDir(layout.buildDirectory.dir("generated/openapi/src/main/java"))
        }
    }
}

dependencies {
    implementation(libs.retrofit.core)
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.11.0")

    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.gson)
    implementation("io.gsonfire:gson-fire:1.9.0")

    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("com.google.code.findbugs:jsr305:3.0.2")

    testImplementation(libs.junit.aggregator)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.platform)
}

openApiGenerate {
    inputSpec.set("$projectDir/openapi.yaml")
    generatorName.set("java")

    ignoreFileOverride.set("$projectDir/.openapi-generator-ignore")

    apiPackage.set("edu.cnm.deepdive.codebreaker.service")
    modelPackage.set("edu.cnm.deepdive.codebreaker.model")
    invokerPackage.set("edu.cnm.deepdive.codebreaker")

    // IMPORTANT: Do NOT set outputDir to projectDir. Gradle will try to snapshot the whole repo
    // (including .gradle locks) and fail. Generate into build/ instead.
    outputDir.set(layout.buildDirectory.dir("generated/openapi").get().asFile.absolutePath)

    configOptions.set(
        mapOf(
            "library" to "retrofit2",
            "useRecords" to "true",
            "generateBuilders" to "true",
            "dateLibrary" to "java8",
            "openApiNullable" to "false",
            "generateModelTests" to "true",
            "generateApiTests" to "true",
        )
    )
}

tasks.withType<JavaCompile> {
    options.release = javaVersion.toInt()
}

// Ensure codegen runs before compilation.
tasks.named("compileJava") {
    dependsOn(tasks.openApiGenerate)
}

tasks.javadoc {
    with(options as StandardJavadocDocletOptions) {
        links("https://docs.oracle.com/en/java/javase/${javaVersion}/docs/api/")
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events.addAll(setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.PASSED))
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}
