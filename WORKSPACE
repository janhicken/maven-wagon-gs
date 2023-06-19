workspace(name = "maven_wagon_gs")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

RULES_JVM_EXTERNAL_TAG = "5.3"

http_archive(
    name = "rules_jvm_external",
    sha256 = "d31e369b854322ca5098ea12c69d7175ded971435e55c18dd9dd5f29cc5249ac",
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    url = "https://github.com/bazelbuild/rules_jvm_external/releases/download/%s/rules_jvm_external-%s.tar.gz" % (RULES_JVM_EXTERNAL_TAG, RULES_JVM_EXTERNAL_TAG),
)

load("@rules_jvm_external//:repositories.bzl", "rules_jvm_external_deps")

rules_jvm_external_deps()

load("@rules_jvm_external//:setup.bzl", "rules_jvm_external_setup")

rules_jvm_external_setup()

load("@rules_jvm_external//:defs.bzl", "maven_install")

MAVEN_WAGON_VERSION = "3.5.3"

SLF4J_VERSION = "2.0.7"

maven_install(
    artifacts = [
        # Maven Wagon
        "org.apache.maven.wagon:wagon-provider-api:%s" % MAVEN_WAGON_VERSION,
        "org.apache.maven.wagon:wagon-provider-test:%s" % MAVEN_WAGON_VERSION,

        # Google Cloud Storage
        "com.google.cloud:google-cloud-storage:2.23.0",
        "com.google.cloud:google-cloud-nio:0.126.18",

        # Logging
        "org.slf4j:slf4j-api:%s" % SLF4J_VERSION,
        "org.slf4j:slf4j-jdk14:%s" % SLF4J_VERSION,

        # Dev tools
        "com.google.googlejavaformat:google-java-format:1.17.0",
    ],
    excluded_artifacts = [
        "com.google.collections:google-collections",
    ],
    fetch_sources = True,
    repositories = ["https://repo1.maven.org/maven2"],
)
