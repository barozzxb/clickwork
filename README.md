# ClickWork API

A robust backend API for job management, recruitment, and application, built with **Spring Boot** and **JPA**, supporting MySQL and H2 databases, JWT authentication, and real-time notifications via WebSocket. The system features modular architecture for applicant, employer, and admin roles, with RESTful endpoints and email integration.

> For full API reference, data models, and system architecture, see the source code in `vn.clickwork.controller.api.v1.*` and `vn.clickwork.model.dto.*`.

---

## Features

- **RESTful API** for job, account, and application management
- **Spring Boot** for rapid backend development
- **JPA/Hibernate** ORM for database access
- **JWT Authentication** for secure endpoints
- **WebSocket** for real-time notifications
- **Email Service** for verification and support
- **Role-based access**: Applicant, Employer, Admin
- **Docker** support for easy deployment
- **Modular, extensible architecture**

---

## Project Structure

```text
src/
  main/
    java/vn/clickwork/
      controller/api/v1/
        admin/         # Admin API endpoints
        applicant/     # Applicant API endpoints
        employer/      # Employer API endpoints
        auth/          # Authentication endpoints
        common/        # Common endpoints (jobs, search, etc.)
      entity/          # JPA entities (Account, Job, Applicant, Employer, ...)
      model/
        dto/           # Data Transfer Objects
        request/       # Request models
        response/      # Response models
      repository/      # Spring Data JPA repositories
      service/         # Service interfaces & implementations
      config/          # Security, Web, and CORS configuration
      util/            # Utility classes (JWT, OTP, Password, ...)
      filter/          # JWT filter
    resources/
      application.properties  # Main configuration
      static/                 # Static resources
  test/
    java/vn/clickwork/        # Unit tests
uploads/
  avatar/    # User avatars
  cvs/       # Uploaded CVs
```

---

## Technology Stack
- Java 21
- Spring Boot 3.4.4
- Spring Data JPA, Hibernate
- Spring Security (JWT)
- MySQL, H2
- Lombok
- Maven
- Docker (optional)

---

## Getting Started

1. **Clone the repository:**
   ```bash
   git clone <repo-url>
   cd clickwork
   ```
2. **Configure the database:**
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://<host>:<port>/<db_name>
   spring.datasource.username=<username>
   spring.datasource.password=<password>
   ```
   Or use H2 for local development.
3. **Configure email (optional):**
   Update SMTP settings in `application.properties`.
4. **Build the project:**
   ```bash
   ./mvnw clean install
   ```
5. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   Default port: `9000`

---

## Docker Deployment (optional)
```bash
docker build -t clickwork .
docker run -p 9000:9000 clickwork
```

---

## Documentation
- API endpoints: see `vn.clickwork.controller.api.v1.*`
- Data models: see `vn.clickwork.model.dto.*`
- System architecture: see source code structure above

---

## Contributing
Pull requests and issues are welcome!

---

## License
Academic, non-commercial use only. 