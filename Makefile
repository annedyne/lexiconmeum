.PHONY: test test-one run-local package

test:
	./mvnw test

test-one:
	./mvnw -Dtest=$(TEST) test

run-local:
	./mvnw spring-boot:run -Dspring-boot.run.profiles=local

package:
	./mvnw clean package
