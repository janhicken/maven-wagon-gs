load("@rules_java//java:defs.bzl", "java_library")

java_library(
    name = "GSWagon",
    srcs = [":GSWagon.java"],
    resources = ["//src/main/resources/META-INF"],
    visibility = ["//visibility:public"],
    deps = [
        ":MimeMapper",
        "@maven_wagon_gs_maven//:com_google_cloud_google_cloud_storage",
        "@maven_wagon_gs_maven//:org_apache_maven_wagon_wagon_provider_api",
    ],
)

java_library(
    name = "MimeMapper",
    srcs = [":MimeMapper.java"],
    resources = ["//src/main/resources/io/github/janhicken/maven/wagon/gs:mime.properties"],
)
