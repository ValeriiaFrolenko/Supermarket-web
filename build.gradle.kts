import io.github.cdimascio.dotenv.Dotenv

buildscript {
    repositories { mavenCentral() }
    dependencies {
        classpath("io.github.cdimascio:dotenv-java:3.2.0")
    }
}
        plugins {
            java
            id("org.springframework.boot") version "4.1.0"
            id("io.spring.dependency-management") version "1.1.7"
            id("org.jooq.jooq-codegen-gradle") version "3.21.6"
        }

group = "frolenko"
version = "0.0.1-SNAPSHOT"
description = "Supermarket-web"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    jooqCodegen("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testCompileOnly("org.projectlombok:lombok")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testAnnotationProcessor("org.projectlombok:lombok")
    implementation("org.flywaydb:flyway-core:12.10.0")
    implementation("org.flywaydb:flyway-database-postgresql:12.10.0")
    implementation("me.paulschwarz:springboot4-dotenv:5.1.0")
}

val dotenv = Dotenv.configure()
    .directory(rootDir.absolutePath)
    .load()

jooq {
    configuration {
        jdbc {
            driver = "org.postgresql.Driver"
            url = dotenv["DATABASE_URL"]
            user = dotenv["DATABASE_USERNAME"]
            password = dotenv["DATABASE_PASSWORD"]
        }
        generator {
            database {
                name = "org.jooq.meta.postgres.PostgresDatabase"
                inputSchema = "public"
            }
            target {
                packageName = "frolenko.generated"
                directory = "src/main/java"
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}