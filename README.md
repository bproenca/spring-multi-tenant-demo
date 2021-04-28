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

for i in `seq 1 200`; do curl -H "X-TenantID: ufcg" http://localhost:8080/nls; done
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

CREATE OR REPLACE PROCEDURE MY_PROC 
(
  TENANT IN VARCHAR2 
, VALOR IN DATE 
) AS 
parametro VARCHAR2(100);
BEGIN
    SELECT value into parametro FROM nls_session_parameters WHERE parameter = 'NLS_DATE_FORMAT';
    syn_log(TENANT, valor);
    syn_log(TENANT, to_date(VALOR, 'DD/MM/YYYY'));
    syn_log(TENANT, parametro);
    syn_log(TENANT, parametro || to_char(to_date(VALOR, 'DD/MM/YYYY'),'DD/MM/YYYY'));
END MY_PROC;

CREATE TABLE ABCD 
(
  TENANT VARCHAR2(20) 
, VALOR DATE 
, PARAMETRO VARCHAR2(200) 
);


CREATE OR REPLACE TRIGGER TRIGGER_ABCD 
BEFORE INSERT ON ABCD FOR EACH ROW
DECLARE
PARAM_NLS VARCHAR2(100);
BEGIN
    SELECT value into PARAM_NLS FROM nls_database_parameters WHERE parameter = 'NLS_DATE_FORMAT'; 
    :new.PARAMETRO := PARAM_NLS;
END;

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