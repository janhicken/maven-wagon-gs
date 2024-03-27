package(default_testonly = 1)

load("@maven_wagon_gs_maven//:defs.bzl", "maven_artifacts")
load("@rules_jvm_external//:defs.bzl", "artifact")
load("@rules_jvm_external//:specs.bzl", "parse")

java_test(
    name = "GSWagonTest",
    size = "small",
    srcs = [":GSWagonTest.java"],
    data = ["//src/test:logging.properties"],
    jvm_flags = [
        "-Djava.util.logging.config.file=$(execpath //src/test:logging.properties)",
    ],
    runtime_deps = [
        "@maven_wagon_gs_maven//:org_slf4j_slf4j_jdk14",
    ],
    deps = [
        "//src/main/java/io/github/janhicken/maven/wagon/gs:GSWagon",
        "@maven_wagon_gs_maven//:com_google_apis_google_api_services_storage",
        "@maven_wagon_gs_maven//:com_google_cloud_google_cloud_nio",
        "@maven_wagon_gs_maven//:com_google_cloud_google_cloud_storage",
        "@maven_wagon_gs_maven//:org_apache_maven_wagon_wagon_provider_api",
        "@maven_wagon_gs_maven//:org_apache_maven_wagon_wagon_provider_test",
    ],
)

# Depends on the full transitive clojure of artifacts.
# This causes all dependencies to be listed in IntelliJ's external libraries, which is useful
# for debugging and browsing the libraries' code
java_binary(
    name = "Dummy",
    srcs = [":Dummy.java"],
    deps = [
        artifact(
            jar_coord,
            repository_name = "maven_wagon_gs_maven",
        )
        for jar_coord in maven_artifacts
        if parse.parse_maven_coordinate(jar_coord).get("packaging", "jar") == "jar" and
           "classifier" not in parse.parse_maven_coordinate(jar_coord)
    ],
)
