import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.proto
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("com.google.protobuf")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

sourceSets["main"].proto {
    srcDirs("src/main/proto", "src/main/proto/google")
}

dependencies {
    // Protobuf and gRPC
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation(libs.kotlinx.serialization.protobuf)
    implementation(libs.grpc.core)
    implementation(libs.grpc.context)
    implementation(libs.grpc.stub)
    implementation(libs.grpc.okhttp)
    implementation(libs.grpc.protobuf)
    //implementation(libs.proto.google.common.protos)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.24.0"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.61.0"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                maybeCreate("java")
            }
            task.plugins {
                id("grpc")
            }
        }
    }
}
//TODO Find what causes duplicates
tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}