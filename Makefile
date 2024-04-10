SHELL := /usr/bin/env bash

LOCAL_REPO_URL		= file://$(HOME)/.m2/repository
SONATYPE_REPO_URL	= https://oss.sonatype.org/service/local/staging/deploy/maven2

.DEFAULT_TARGET: all
.PHONY: all
all:
	bazel build //...

.PHONY: lint
lint:
	bazel run -- //tools:format.check 

.PHONY: fmt
fmt:
	bazel run -- //tools:format

.PHONY: test
test:
	bazel test //...

.PHONY: install
install:
	bazel run //:maven.publish --define maven_repo=$(LOCAL_REPO_URL)

.PHONY: deploy
deploy:
	bazel run //:maven.publish --define maven_repo=$(SONATYPE_REPO_URL) --define gpg_sign=True

.PHONY: release
release:
	scripts/release.sh
	git push --follow-tags --atomic

.PHONY: ci
ci: lint test

.PHONY: outdated
outdated:
	bazel run @maven_wagon_gs_maven//:outdated

.PHONY: pin
pin: maven_lock.json

maven_lock.json: MODULE.bazel
	bazel run @unpinned_maven_wagon_gs_maven//:pin
	chmod -x $@
