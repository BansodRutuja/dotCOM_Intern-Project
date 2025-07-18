# Java Registration Web Application with PDF Report

## Introduction
This is a Java web-based CRUD application that allows users to register, view, update, and delete student records. The application also provides a feature to generate a PDF report of all registered users.

##  Features

- Register a new student
- View all registered students
- Update existing student details
- Delete a student record
- Generate a PDF report of all records with serial numbers
- Clean and responsive UI

###  Technologies Used
- **Java (JDK 17+)**
- **MySQL**
- **HTML, CSS**
- **OpenPDF + Flying Saucer (PDF generation)**
- **JDBC**
- **VS Code Editor**

### Prerequisites
- **Version**: JDK 8 or above
- **MySQL 5.7 or 8.0**
- **Required JAR Files** : 1.*mysql-connector-j-9.3.0.jar* ,
                           2.*openpdf-1.2.7.jar* ,
                           3.*core-renderer-R8.jar* ,
                           4.*flying-saucer-pdf-openpdf-9.1.18.jar* 

###  Setup Instructions
1.Clone the project and open it in your IDE or file system.
2.Import MySQL Database:
    CREATE DATABASE registration_db;
    USE registration_db;

   CREATE TABLE registration (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50),
    middle_name VARCHAR(50),
    last_name VARCHAR(50),
    dob VARCHAR(20),
    gender VARCHAR(10),
    education VARCHAR(50),
    contact VARCHAR(15),
    address VARCHAR(255),
    username VARCHAR(50),
    password VARCHAR(50)
  );
  
3.Update credentials in Database.java:
private static final String USER = "your_mysql_username";
private static final String PASSWORD = "your_mysql_password";

4.Compile Java File :
*javac -cp ".;lib/*" -d build server/*.java*

5.Run The Application :
*java -cp ".;lib/*;build" server.Main*

## Author 
- Rutuja Purushottam Bansod
- B.E. IT, 3rd Year
- Internship Project (2025)
