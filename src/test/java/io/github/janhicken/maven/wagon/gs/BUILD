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
        "@maven//:org_slf4j_slf4j_jdk14",
    ],
    deps = [
        "//src/main/java/io/github/janhicken/maven/wagon/gs:GSWagon",
        "//src/test/java/com/google/cloud/storage/contrib/nio/testing:FakeStorageRpc2",
        "@maven//:com_google_cloud_google_cloud_storage",
        "@maven//:org_apache_maven_wagon_wagon_provider_api",
        "@maven//:org_apache_maven_wagon_wagon_provider_test",
    ],
)
