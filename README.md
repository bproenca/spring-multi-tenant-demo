Spring Boot Multi-tenant demo
-----------------------------
This repository contains the sources for the blogpost about Spring Boot multi-tenancy.
If you want to know more, go read my weblog ;-)

## Compiling
Run the following command to build the solution:

```
mvn clean compile package
```

## Simple tests

```
mvn spring-boot:run

curl -H "X-TenantID: ufcg" http://localhost:8080/date
curl -H "X-TenantID: ufpb" http://localhost:8080/nls
```


## Running the demo
You're going to need a database which has a structure like this:

MySQL:
```sql
CREATE TABLE orders (
    id int not null auto_increment,
    date datetime not null,
    primary key(id)
);
```
Oracle:
```sql
CREATE TABLE ORDERS 
(
  ID NUMBER NOT NULL 
, DATEX DATE NOT NULL 
, CONSTRAINT ORDERS_PK PRIMARY KEY 
  (
    ID 
  )
  ENABLE 
);
```

Create a new database per tenant and include a new properties file
in the tenants folder in the root of the solution.
The contents of this file looks like this:

```
name=<tenant id>
datasource.url=jdbc:mysql://localhost:3306/<tenant>
datasource.username=
datasource.password=
```

Next boot up the application using the following command and try it out using SOAPUI, Postman or Curl.

```
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

Enjoy!