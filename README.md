[![forthebadge](https://forthebadge.com/images/badges/made-with-typescript.svg)](https://forthebadge.com)[![forthebadge](https://forthebadge.com/images/badges/made-with-java.svg)](https://forthebadge.com)  [![forthebadge](https://forthebadge.com/images/badges/uses-css.svg)](https://forthebadge.com)

# Yoga-App

This project contains the Yoga-app backend and frontend.
This application provide Yoga lesson organized in sessions whose registered users can participate.

## Technologies
- TS 14
- CSS 3
- Angular 14.2
- Java 11
- Spring-Boot 2.6.1
- MySQL 8.4

## Authors

Code: @Cengiz

Testing: @pozasl

## Licensing

&copy; NumDev

## Development environment

For convenience a devcontainer with all needed dependencies is provided.

Otherwise you'll need to install the following dependencies:

### Backend

You need to install:
- JDK 11
- Maven 3.6
- A MySQL Server 8.4 with a dedicated user and database
- An extra `test` database with a `test` account for testing purpose

Install MySQL server and create a dedicated user whith all rights to a dedicated database for the application

```bash
mysql -u root -p
```

```sql
CREATE DATABASE `yogadb`;
CREATE USER `yogauser`@`%` IDENTIFIED BY "SECRET_PASSWORD_HERE";
GRANT ALL PRIVILEGES ON `yogadb`.* TO `yogauser`@`%`;
FLUSH PRIVILEGES;
```

Then from this project's root folder, initialize the table in the application database with the application's user

```bash
mysql -u yogauser -p
```

```sql
USE `yogadb`;
source ressources/sql/script.sql
```

Once the database is ready, go to the `back` folder and run commandline:
```bash
mvn install
```

Then set the database connection parameters as environment variables according to the mysql server settings and created user credentials

```bash
MYSQL_SERVER_HOST="localhost"
MYSQL_SERVER_PORT="3306"
MYSQL_DATABASE="yogadb"
MYSQL_USER="yogauser"
MYSQL_PASSWORD="SECRET_PASSWORD_HERE"
```

Finally, to run the backend localy, just type:
 ```bash
mvn spring-boot:run
```

The backend server will be listening on http://localhost:8080

### Frontend
You need to install
- NodeJS 16
- Npm

Then from the `front` folder run :

```bash
npm instal
```

To launch the frontend localy, just run :

```bash
npm run start
```
and navigate to `http://127.0.0.1:4200/`

## Testing the application

### Testing the Backend

Go to the `back` folder

To test the backend, especialy with integration tests, you'll need a testing database:
```bash
mysql -u root -p
```

```sql
CREATE DATABASE `test`;
CREATE USER `test`@`%` IDENTIFIED BY "test";
GRANT ALL PRIVILEGES ON `test`.* TO `test`@`%`;
FLUSH PRIVILEGES;
```

To run the backend's Unit Tests:
```bash
mvn clean test
```

An html report is created in `target/site/jacoco/index.html` with the code coverage

To run the the backend Unit Tests with Integration Tests:
```bash
mvn clean verify
```
### Testing the Frontend

Go to the `front` folder

To run the frontend's Unit Tests only:

```bash
npm run test:unit
```

To run the frontend's Integration Tests only:
```bash
npm run test:integration
```

To run the frontend's Integration and Unit Tests together:
```bash
npm run test
```

For the code coverage
```bash
npm run test:coverage
```
### End-to-end testing the whole app

Go to the `front` folder

Initialize cypress e2e test to compile the app bundle

```bash
npm run e2e
```

To run all the e2e tests suites, close the cypress window and in a `new terminal` in the `front` folder run:
```bash
npm run cypress:run
```

For the code coverage run:
```bash
npm run e2e:coverage
```

/!\ if you run the test suites one by one from the cypress window, the code coverage report will only consider the last e2e test suite ran and ignore the others.









