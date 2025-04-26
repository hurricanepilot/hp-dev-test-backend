# HMCTS DTS Developer Technical Test - Backend

## Overview

An implementation of the DTS developer technical test backend using Java 21 and Spring Boot 3.

### Building/Running

This project provides the backend implementation for the Task rest service. It's expected to be fronted by the companion project, though it does include the swagger UI internal for API testing.

To build the project, clone the repository and then, in the project root, run:

```bash
mvn clean package
java -jar target/hmcts-dev-test-backend-0.0.1-SNAPSHOT.jar
```

This should build, test and package the Spring Boot application, followed by running it using the JDK.

alternatively, to run the project using the `run` goal of the `spring-boot` maven plugin, you can use:

```bash
mvn clean package spring-boot::run
```

Once running the service will be available on `http://localhost:8080`

> NOTE: The backend service is using an in-memory database by default, so any data created while running will be lost. 

### Documentation

Once running, the expectation is that it will be connected to by the companion frontend project. However, navigating to the root:

> `http://localhost:8080/`

Will display the RESTDocs documentation that was generated via the test suite.

The swagger UI has also been included for functional testing and is linked at the top of the RESTDocs documentation, or is available directly from `http://localhost:8080/swagger-ui/index.html`.
