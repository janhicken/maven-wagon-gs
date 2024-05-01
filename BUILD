load("@rules_jvm_external//:defs.bzl", "java_export")

PROJECT_VERSION = "1.7.11-SNAPSHOT"

java_export(
    name = "maven",
    maven_coordinates = "io.github.janhicken:maven-wagon-gs:%s" % PROJECT_VERSION,
    pom_template = ":pom_template.xml",
    visibility = ["//visibility:public"],
    runtime_deps = ["//src/main/java/io/github/janhicken/maven/wagon/gs:GSWagon"],
)
