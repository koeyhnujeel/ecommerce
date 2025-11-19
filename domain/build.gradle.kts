dependencies {
    implementation(project(":common"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-tx")
}

tasks.named("bootJar") {
    enabled = false
}

tasks.named("jar") {
    enabled = true
}
