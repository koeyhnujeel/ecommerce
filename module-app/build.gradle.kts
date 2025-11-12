dependencies {
    implementation(project(":module-domain"))
    implementation(project(":module-infra"))
    implementation(project(":module-common"))
    implementation(project(":module-auth"))
    implementation(project(":module-apis"))
    implementation(project(":module-apis:customer-api"))
    implementation(project(":module-apis:partner-api"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.named("bootJar") {
    enabled = true
}

tasks.named("jar") {
    enabled = false
}
