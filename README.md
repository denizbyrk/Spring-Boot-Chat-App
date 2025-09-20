# TalkieTalk - Spring Boot Chat App

A real-time chat application built with Java and Spring Boot. Spring WebSocket is used for real-time updating, and used Thymeleaf for frontend-backend integration.

## Technologies

- **Backend**: Java, Spring Boot 3
- **Frontend**: Thymeleaf (HTML/CSS/JS)  
- **Database**: MySQL
- **Others**: Maven, Spring Security, Spring WebSocket

## Features

- Real-time chat using Spring WebSocket
- Account management
- Friend list management
- Simple message broadcasting to all connected users

## Installation & Running

- **Clone Repository**
  
   ```bash
   git clone https://github.com/denizbyrk/Spring-Boot-Chat-App.git
   cd Spring-Boot-Chat-App
   ```
- **Configure Database**
  
  ```
  spring.datasource.url=jdbc:mysql://localhost:3306/seafbank
  spring.datasource.username=root
  spring.datasource.password=yourpassword
  spring.jpa.hibernate.ddl-auto=update
  ```
- **Run**

  ```
  mvn spring-boot:run
  ```
