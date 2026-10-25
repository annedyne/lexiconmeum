.PHONY: test test-one run-local package release

test:
	./mvnw test

test-one:
	./mvnw -Dtest=$(TEST) test

run-local:
	./mvnw spring-boot:run -Dspring-boot.run.profiles=local

package:
	./mvnw clean package

# Usage: make release [NEXT=0.22.0] [FROM=tag]
release:
	scripts/release.sh $(if $(FROM),--from $(FROM)) $(NEXT)
