plugins {
    id("java")
}

group = "com.dibimbing.tixsplit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.seleniumhq.selenium:selenium-java:4.25.0")
    implementation("io.github.bonigarcia:webdrivermanager:5.9.2")
    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("org.apache.logging.log4j:log4j-core:2.23.1")
    implementation("io.rest-assured:rest-assured:5.5.0")
    implementation("com.google.code.gson:gson:2.11.0")

    testImplementation("org.testng:testng:7.10.2")
}

tasks.test {
    useTestNG {
        val suiteFile = (project.findProperty("suiteFile") as String?) ?: "src/test/resources/testng.xml"
        suites(suiteFile)
        (project.findProperty("threadCount") as String?)?.toIntOrNull()?.let {
            threadCount = it
        }
    }

    maxParallelForks = 1
    systemProperty("env", System.getProperty("env", "staging"))

    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = false
    }
}

