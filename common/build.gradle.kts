dependencies {
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.20")
}

tasks.named("bootJar") {
    enabled = false
}

tasks.named("jar") {
    enabled = true
}
