dependencies {
    implementation(project(":core"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.security:spring-security-core") // 임시

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers:3.5.7") // Testcontainers
    testImplementation("org.testcontainers:junit-jupiter:1.21.3")
    testImplementation("org.testcontainers:postgresql:1.21.3")
    testImplementation("org.junit-pioneer:junit-pioneer:2.3.0") // 출력문(sout) 테스트
    testImplementation("com.tngtech.archunit:archunit-junit5:1.4.1") // 아키텍처 테스트

    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    runtimeOnly("org.postgresql:postgresql")
}

tasks.named("bootJar") {
    enabled = true
}

tasks.named("jar") {
    enabled = false
}