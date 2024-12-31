load("@bazel_tools//tools/jdk:default_java_toolchain.bzl", "default_java_toolchain")
load("@rules_jvm_external//:defs.bzl", "java_export")

PROJECT_VERSION = "2.0.0-SNAPSHOT"

java_export(
    name = "maven",
    maven_coordinates = "io.github.janhicken:maven-wagon-gs:%s" % PROJECT_VERSION,
    pom_template = ":pom_template.xml",
    visibility = ["//visibility:public"],
    runtime_deps = ["//src/main/java/io/github/janhicken/maven/wagon/gs:GSWagon"],
)

# ╔════════════════════════════════════════════════════════════════════════════╗
# ║                        Java Toolchain Configuration                        ║
# ╚════════════════════════════════════════════════════════════════════════════╝

package_group(
    name = "java_packages",
    packages = [
        "//src/main/java/...",
        "//src/test/java/...",
        "//tools",
    ],
)

java_package_configuration(
    name = "strict_linting",
    javacopts = [
        "-Werror",
        "-Xlint:all",
        "-Xlint:-classfile",
        "-Xlint:-serial",
        "-Xlint:-processing",
        "-Xlint:-try",
    ],
    packages = [":java_packages"],
)

JAVA_VERSION = "17"

default_java_toolchain(
    name = "java_toolchain",
    package_configuration = [":strict_linting"],
    source_version = JAVA_VERSION,
    target_version = JAVA_VERSION,
    visibility = ["//visibility:public"],
)
