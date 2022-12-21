buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.21"
    id("com.github.kt3k.coveralls") version "2.8.4"
    `java-library`
    `maven-publish`
    idea
    jacoco
}

group = "com.mercateo"
version = "0.1.13-SNAPSHOT"

description = """json-schema"""

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

jacoco {
    toolVersion = "0.8.8"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(false)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.mercateo"
            artifactId = "json-schema"
            version = "0.1.13-SNAPSHOT"

            from(components["java"])

            pom {
                name.set("json-schema")
                //packaging.set("jar")
                // optionally artifactId can be defined here
                description.set("json-schema generator for the JVM.")
                url.set("https://github.com/mercateo/json-schema")

                scm {
                    connection.set("scm:git:git@github.com:Mercateo/json-schema.git")
                    developerConnection.set("scm:git:git@github.com:Mercateo/json-schema.git")
                    url.set("https://github.com/mercateo/json-schema")
                }

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("wuerla")
                        name.set("Andreas Würl")
                        email.set("andreas.wuerl@mercateo.com")
                    }
                }
            }
        }
    }
}

val jackson_version = "2.12.5"
val jmh_version = "1.33"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.googlecode.gentyref:gentyref:1.2.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:${jackson_version}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${jackson_version}")
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("javax.validation:validation-api:2.0.1.Final") {
    exclude("testng")
}
//    implementation(group: "org.jetbrains.kotlin", name: "kotlin-stdlib", version: kotlin_version)
//    implementation(group: "org.jetbrains.kotlin", name: "kotlin-reflect", version: kotlin_version)

    testImplementation("junit:junit:4.13")
    testImplementation("org.assertj:assertj-core:3.20.2")
    testImplementation("io.mockk:mockk:1.12.0")
    testImplementation("org.openjdk.jmh:jmh-core:${jmh_version}")
    testAnnotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:${jmh_version}")
    testImplementation("ch.qos.logback:logback-classic:1.2.5")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.hibernate:hibernate-validator:6.2.0.Final")
}
