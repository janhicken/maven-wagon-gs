load("@rules_jvm_external//:defs.bzl", "java_export")

PROJECT_VERSION = "1.7.9"

java_export(
    name = "maven",
    maven_coordinates = "io.github.janhicken:maven-wagon-gs:%s" % PROJECT_VERSION,
    pom_template = ":pom_template.xml",
    visibility = ["//visibility:public"],
    runtime_deps = ["//src/main/java/io/github/janhicken/maven/wagon/gs:GSWagon"],
)

java_binary(
    name = "java_fmt",
    jvm_flags = [
        # Extra flags according to https://github.com/google/google-java-format/#intellij-jre-config
        "--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
    ],
    main_class = "com.google.googlejavaformat.java.Main",
    runtime_deps = ["@maven//:com_google_googlejavaformat_google_java_format"],
)
