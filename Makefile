SHELL := /usr/bin/env bash

.DEFAULT_TARGET: all
.PHONY: all
all:
	bazel build //...

.PHONY: lint
lint:
	bazel run //:java_fmt -- --dry-run --set-exit-if-changed @<(find $$PWD/src -type f -name *.java)

.PHONY: fmt
fmt:
	bazel run //:java_fmt -- --replace @<(find $$PWD/src -type f -name *.java)

.PHONY: test
test:
	bazel test //... --test_output=errors

.PHONY: release
release:
	scripts/release.sh
	git push --follow-tags --atomic

.PHONY: ci
ci: lint test

.PHONY: outdated
outdated:
	bazel run @maven//:outdated

.PHONY: pin
pin:
	bazel run @unpinned_maven//:pin
