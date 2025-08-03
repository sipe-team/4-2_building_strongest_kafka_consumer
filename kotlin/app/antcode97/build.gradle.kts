plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
    kotlin("plugin.jpa") version "1.9.20"
}

allprojects {
    group = "team.sipe.antcode97"
    version = "0.0.1-SNAPSHOT"
    
    repositories {
        mavenCentral()
    }
}

// 루트 프로젝트에서 bootJar 비활성화
tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = false
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    
    java {
        sourceCompatibility = JavaVersion.VERSION_17
    }
    
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "17"
        }
    }
    
    tasks.withType<Test> {
        useJUnitPlatform()
    }
    
    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.kafka:spring-kafka")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.springframework.kafka:spring-kafka-test")
    }
    
    // 공통 설정
    tasks.getByName("bootJar") {
        enabled = true
    }
    
    tasks.getByName("jar") {
        enabled = false
    }
}