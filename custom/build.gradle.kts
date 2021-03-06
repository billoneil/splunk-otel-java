plugins {
  java
  id("com.github.johnrengelman.shadow") version "6.0.0"
}

val versions: Map<String, String> by extra

dependencies {
  implementation("io.opentelemetry:opentelemetry-sdk:${versions["opentelemetry"]}")
  implementation("io.opentelemetry:opentelemetry-exporter-jaeger-thrift:${versions["opentelemetry"]}")
  implementation("io.opentelemetry.javaagent:opentelemetry-javaagent-spi:${versions["opentelemetryJavaagent"]}")
}

tasks {
  compileJava {
    options.release.set(8)
  }

  shadowJar {
    mergeServiceFiles()

    exclude("**/module-info.class")

// Prevents conflict with other SLF4J instances. Important for premain.
    relocate("org.slf4j", "io.opentelemetry.javaagent.slf4j")
    // rewrite dependencies calling Logger.getLogger
    relocate("java.util.logging.Logger", "io.opentelemetry.javaagent.bootstrap.PatchLogger")

    // prevents conflict with library instrumentation
    relocate("io.opentelemetry.instrumentation.api", "io.opentelemetry.javaagent.shaded.instrumentation.api")

    // relocate OpenTelemetry API
    relocate("io.opentelemetry.api", "io.opentelemetry.javaagent.shaded.io.opentelemetry.api")
    relocate("io.opentelemetry.context", "io.opentelemetry.javaagent.shaded.io.opentelemetry.context")

    // relocate OpenTelemetry API dependency
    relocate("io.grpc", "io.opentelemetry.javaagent.shaded.io.grpc")
  }
}