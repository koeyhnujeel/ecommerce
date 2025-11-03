dependencies {
    implementation(project(":module-apis"))
    implementation(project(":module-domain"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}

tasks.named("bootJar") {
    enabled = false
}

tasks.named("jar") {
    enabled = true
}
