dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
}

tasks.named("bootJar") {
    enabled = false
}

tasks.named("jar") {
    enabled = true
}
