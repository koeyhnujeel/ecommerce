allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-tx")
    implementation("org.hibernate.orm:hibernate-core")
}

tasks.named("bootJar") {
    enabled = false
}

tasks.named("jar") {
    enabled = true
}