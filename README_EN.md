# Spring Security with JWT Authentication

A Spring Boot application demonstrating implementation of security and authentication using Spring Security and JWT (JSON Web Tokens).

## Features

- User registration and login with JWT authentication
- Password encryption using BCrypt
- Role-based authorization with Spring Security
- Customized access denied handling
- Swagger UI integration
- PostgreSQL database
- Exception handling

## Technologies

- Java 21
- Spring Boot 3.3.5
- Spring Security
- JSON Web Tokens (JWT)
- BCrypt
- PostgreSQL
- Gradle
- Swagger UI
- MapStruct
- Lombok

## Getting Started

### Prerequisites

- JDK 21
- Gradle
- PostgreSQL

### Database Configuration

Create a PostgreSQL database and update the application.yml file with your database configuration:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5444/springsecurity
    username: postgres
    password: postgres
```

### Running the Application

1. Clone the repository
```bash
git clone https://github.com/MFurkanTurkmen/springsecurity.git
```

2. Navigate to the project directory
```bash
cd springsecurity
```

3. Build the project
```bash
./gradlew build
```

4. Run the application
```bash
./gradlew bootRun
```

The application will start running at http://localhost:80

## API Documentation

After running the application, you can access the Swagger UI at:
```
http://localhost/swagger-ui/index.html
```

## API Endpoints

### Auth Controller

| Method | Url | Description |
| ------ | --- | ----------- |
| POST   | /auth/signup | Register a new user |
| POST   | /auth/login  | Login user |

### Book Controller

| Method | Url | Description | Required Role |
| ------ | --- | ----------- | ------------- |
| GET    | /books | Get all books | ROLE_USER |
| GET    | /books/{id} | Get book by id | ROLE_USER |
| POST   | /books | Create new book | ROLE_ADMIN |

## Security

The application uses JWT (JSON Web Tokens) for authentication. The JWT secret key is configured in the application.yml file:

```yaml
jwt:
  secret: "your-secret-key"
```

## Role-based Access Control

The application supports the following roles:
- ROLE_USER
- ROLE_MODERATOR
- ROLE_ADMIN

## Exception Handling

The application includes a global exception handler that handles:
- Authentication exceptions
- Validation exceptions
- General application exceptions

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request

## License

This project is licensed under the MIT License.

## Author

Muhammed Furkan Türkmen
- GitHub: [@MFurkanTurkmen](https://github.com/MFurkanTurkmen)