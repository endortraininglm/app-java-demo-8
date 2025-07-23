# Java 8 Demo Application with Known Vulnerabilities

This is a sample Java 8 project built with Maven 3.2.5 that intentionally includes dependencies with known vulnerabilities. The purpose of this project is to demonstrate how Software Composition Analysis (SCA) tools can detect these vulnerabilities.

## Included Vulnerabilities

This project includes the following vulnerable dependencies:

1. **Log4j 2.14.1** - Vulnerable to Log4Shell (CVE-2021-44228)
2. **Spring Framework 4.3.19.RELEASE** - Multiple vulnerabilities
3. **Apache Commons Collections 3.2.1** - Deserialization vulnerabilities (CVE-2015-7501)
4. **Struts 2.3.30** - Multiple vulnerabilities including OGNL injection
5. **Jackson Databind 2.9.9** - Deserialization vulnerabilities

## Project Structure

This is a standard Maven project with the following structure:

```
app-java-demo-8/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── App.java
│   │   └── resources/
│   │       └── log4j2.xml
```

## Building the Project

To build the project, use Maven:

```bash
mvn clean package
```

## Running the Application

After building, you can run the application with:

```bash
java -jar target/app-java-demo-8-1.0-SNAPSHOT.jar
```

## Disclaimer

This project is for educational and testing purposes only. It intentionally contains security vulnerabilities and should not be used in a production environment.
