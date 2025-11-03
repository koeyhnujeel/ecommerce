dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}

tasks.named("bootJar") {
    enabled = false
}

tasks.named("jar") {
    enabled = true
}
