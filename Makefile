# See https://github.com/google/google-java-format#jdk-16
export MAVEN_OPTS := $(MAVEN_OPTS) \
	--add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED \
	--add-exports jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED \
	--add-exports jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED \
	--add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED \
	--add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED

.PHONY: build fmt test release ci

build:
	mvn $(MVN_FLAGS) compile

fmt:
	mvn $(MVN_FLAGS) spotless:apply

test:
	mvn $(MVN_FLAGS) test

release:
	mvn $(MVN_FLAGS) release:prepare
	mvn $(MVN_FLAGS) release:perform

ci: test
