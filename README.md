# IssueTrackinatorAPI
This repository contains the REST API for our issue tracker. We developed it using Java, with [Spring Boot](https://spring.io/projects/spring-boot) framework, and also JPA for persistance. Also, we used [Maven](https://maven.apache.org/) as project manager.
## Usage
First, compile the project using Maven
```
mvn clean install
```
If there's no errors, then you can start the tomcat server with:
```
mvn spring-boot:run
```
By default, API will be hosted on *localhost:8080*

## Documentation
You can load documentation for the REST API using Swagger Editor, and loading our [swagger.yaml](swagger.yaml) file with all methods.


## Collaborators
 * Rubén Presa Martín (ruben.presa@est.fib.upc.edu)
 * Hèctor Morales Carnicé (hector.morales.carnice@est.fib.upc.edu)
 * Marc Revelles Segalés (marc.revelles@est.fib.upc.edu)
 * Jesús Mercadal Mir (jesus.mercadal@est.fib.upc.edu)