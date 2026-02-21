#  Survey System Pro (Java Swing + MySQL)

A professional desktop application to create, manage, and take surveys. This project uses **Java Swing** for the GUI and **MySQL** for data persistence, following a clean **DAO (Data Access Object)** architecture.

---

##  Features
* **Survey Management:** Create and Delete surveys easily.
* **Question Builder:** Add or remove questions from specific surveys.
* **Live Survey:** Users can fill out surveys with real-time response recording.
* **Modern UI:** Built with a user-friendly and responsive layout.
* **Database Integration:** Full CRUD operations using JDBC.

---

##  Tech Stack
* **Language:** Java (JDK 17+)
* **GUI Framework:** Java Swing
* **Database:** MySQL 8.0
* **Driver:** MySQL Connector/J

---

##  Getting Started

### 1. Prerequisites
* Install **MySQL Server**.
* Install **Java Development Kit (JDK)**.
* MySQL Connector `.jar` file added to the project libraries.

### 2. Database Setup
Run the following SQL commands in your MySQL Workbench or Shell:

```sql
CREATE DATABASE survey_system;
USE survey_system;

CREATE TABLE surveys (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL
);

CREATE TABLE questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    survey_id INT,
    question_text TEXT,
    FOREIGN KEY (survey_id) REFERENCES surveys(id) ON DELETE CASCADE
);

CREATE TABLE responses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT,
    response_text TEXT,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);
