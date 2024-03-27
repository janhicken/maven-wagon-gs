load("@aspect_rules_lint//format:defs.bzl", "format_multirun")

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
)
