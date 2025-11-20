plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25" apply false
	kotlin("plugin.jpa") version "1.9.25" apply false
	id("org.springframework.boot") version "3.5.7" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
	group = "com.zunza"
	version = "0.0.1-SNAPSHOT"


	repositories {
		mavenCentral()
	}
}

subprojects {
	apply(plugin = "kotlin")
	apply(plugin = "org.jetbrains.kotlin.plugin.spring")
	apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")

	kotlin {
		compilerOptions {
			freeCompilerArgs.addAll("-Xjsr305=strict")
			jvmToolchain(21)
		}
	}

	dependencies {
		implementation("org.jetbrains.kotlin:kotlin-reflect")
		implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
		implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
		implementation("io.github.oshai:kotlin-logging-jvm:7.0.7")

		testImplementation("org.springframework.boot:spring-boot-starter-test")
		testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3") // Kotest
		testImplementation("io.kotest:kotest-assertions-core-jvm:5.9.1")
		testImplementation("io.kotest:kotest-runner-junit5-jvm:5.9.1")
		testImplementation("io.mockk:mockk:1.14.2") // MockK
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}
