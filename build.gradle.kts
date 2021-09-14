import com.bmuschko.gradle.docker.tasks.image.Dockerfile

plugins {
    java
    groovy
    id("com.bmuschko.docker-java-application") version "6.6.1"
    id("org.springframework.boot") version "2.5.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.diffplug.spotless") version "5.12.4"
    id("pl.allegro.tech.build.axion-release") version "1.13.2"
    application
    jacoco
}

group = "com.example"
project.version = scmVersion.version

application {
    mainClass.set("com.example.HelloWorld")
}

repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
}
val lombokVersion = "1.18.20"
val jUnitVersion = "5.7.2"
val mockitoVersion = "3.12.4"
dependencies {
    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("ch.qos.logback:logback-classic")
    implementation("org.apache.kafka:kafka-clients")
    implementation("io.confluent:kafka-json-schema-serializer:6.2.0")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.amazonaws:aws-java-sdk-dynamodb:1.12.17")
    implementation("io.github.boostchicken:spring-data-dynamodb:5.2.1")
    implementation("org.springframework.kafka:spring-kafka:2.7.4")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.slf4j:slf4j-api:1.7.16")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:${jUnitVersion}")
    testImplementation("org.mockito:mockito-junit-jupiter:${mockitoVersion}")

    testImplementation("cglib:cglib:3.2.8")
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.3")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<Test>().configureEach {
        useJUnitPlatform()
    }
}

docker {
    javaApplication {
        baseImage.set("azul/zulu-openjdk-debian:11")
        images.set(setOf("api:latest"))
        ports.set(listOf(9012))
    }
}

tasks.named<Dockerfile>("dockerCreateDockerfile") {
    doFirst {
        copy {
            from("scripts/entrypoint.sh")
            into(file("$buildDir/docker"))
        }
    }
    instruction("COPY entrypoint.sh .")
    val originalInstructions = instructions.get().toMutableList()
    val fromInstructionIndex = originalInstructions
        .indexOfFirst { item -> item.keyword == Dockerfile.EntryPointInstruction.KEYWORD }
    originalInstructions.removeAt(fromInstructionIndex)
    val entrypoint = Dockerfile.EntryPointInstruction("./entrypoint.sh", application.mainClass.get())
    originalInstructions.add(entrypoint)
    instructions.set(originalInstructions)
}

tasks.jacocoTestReport {
    reports {
        csv.isEnabled = true
    }
}
