dependencies {
    implementation(project(":module-domain"))

    implementation("org.springframework.boot:spring-boot-starter-data-redis")
}

tasks.named("bootJar") {
    enabled = false
}

tasks.named("jar") {
    enabled = true
}
