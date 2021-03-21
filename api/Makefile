SHELL = /bin/bash
VERSION := $(shell git rev-parse --short HEAD)

.PHONY: test
test:
	@clojure -M:test

release: test
	@mkdir classes || rm -rf classes/*
	@clojure -e "(compile 'core)"
	@clojure -M:uberdeps --main-class core
	mv target/api.jar target/app-$(VERSION).jar

run:
	java -jar target/app-$(VERSION).jar
