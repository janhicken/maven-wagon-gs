load("@aspect_rules_lint//format:defs.bzl", "format_multirun")
load("@maven_wagon_gs_maven//:defs.bzl", "maven_artifacts")
load("@rules_jvm_external//:defs.bzl", "artifact")
load("@rules_jvm_external//:specs.bzl", "parse")

# Depends on the full transitive clojure of artifacts.
# This causes all dependencies to be listed in IntelliJ's external libraries, which is useful
# for debugging and browsing the libraries' code
java_binary(
    name = "Dummy",
    srcs = [":Dummy.java"],
    main_class = "tools.Dummy",
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
    runtime_deps = ["@maven_wagon_gs_maven//:com_google_googlejavaformat_google_java_format"],
)

format_multirun(
    name = "format",
    java = ":java_fmt",
    shell = "@aspect_rules_lint//format:shfmt",
    yaml = "@aspect_rules_lint//format:yamlfmt",
)
