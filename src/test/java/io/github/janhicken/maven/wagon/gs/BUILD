package(default_testonly = 1)

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
