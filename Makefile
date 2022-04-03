.PHONY: build fmt test release ci

build:
	mvn $(MVN_FLAGS) compile

fmt:
	mvn $(MVN_FLAGS) spotless:apply

test:
	mvn $(MVN_FLAGS) test

release:
	mvn $(MVN_FLAGS) release:prepare
	git push --follow-tags --atomic
	mvn $(MVN_FLAGS) release:perform

ci: test
