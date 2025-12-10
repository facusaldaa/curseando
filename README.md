# Curseando - Online Course Platform

A full-stack course platform built with Spring Boot backend and Angular frontend, featuring course catalog with filtering and enrollment functionality.

## Overview

Curseando is a web application that allows users to browse available courses, filter them by difficulty level, and enroll in courses. The platform includes:

- **Course Catalog**: Browse all available courses with filtering by difficulty
- **Course Details**: View detailed information about each course
- **Enrollment**: Enroll in courses with validation and real-time availability updates

## Architecture

- **Backend**: Spring Boot 3.2.0 (Java 17) with PostgreSQL
- **Frontend**: Angular 19.1.1 with Tailwind CSS
- **Database**: PostgreSQL 15 (Alpine)
- **Containerization**: Docker and Docker Compose

## Prerequisites

- Docker and Docker Compose installed on your system
- (Optional) Java 17 and Node.js 18+ for local development

## Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd curseando
```

2. Start all services with Docker Compose:
```bash
docker-compose up -d
```

This will start:
- PostgreSQL database on port 5433 (default, configurable)
- Spring Boot backend on port 8081 (default, configurable)
- Angular frontend on port 4200 (default, configurable)

The services will automatically:
- Create the database schema
- Seed initial course data
- Build and start all containers

## Running

### Start Services
```bash
docker-compose up -d
```

This command will:
- Pull/build required Docker images
- Start PostgreSQL, backend, and frontend services
- Wait for database to be healthy before starting backend
- Wait for backend to be healthy before starting frontend

### Stop Services
```bash
docker-compose down
```

To also remove volumes (database data):
```bash
docker-compose down -v
```

### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
```

### Rebuild Images
```bash
# Rebuild all services
docker-compose build --no-cache

# Rebuild and restart
docker-compose up -d --build
```

### Check Service Status
```bash
docker-compose ps
```

## Accessing the Application

Once all services are running, access:

- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8081/api
- **Swagger UI**: http://localhost:8081/swagger-ui/index.html#/
- **API Docs (OpenAPI JSON)**: http://localhost:8081/api-docs (raw OpenAPI specification - used by Swagger UI)

**Note**: Ports can be customized by setting environment variables before running `docker-compose up`.

## API Endpoints

### Courses

- `GET /api/courses` - Get all courses
- `GET /api/courses?difficulty={level}` - Filter courses by difficulty (BEGINNER, INTERMEDIATE, ADVANCED)
- `GET /api/courses/{id}` - Get course by ID

### Enrollments

- `POST /api/enrollments` - Enroll in a course
  - Request body:
    ```json
    {
      "courseId": 1,
      "fullName": "John Doe",
      "email": "john.doe@example.com"
    }
    ```

## Configuration

The application uses Docker Compose with default values. You can customize ports and database settings by setting environment variables before running `docker-compose up`:

```bash
# Database
export POSTGRES_DB=curseando
export POSTGRES_USER=postgres
export POSTGRES_PASSWORD=postgres
export POSTGRES_PORT=5433

# Backend
export BACKEND_PORT=8081

# Frontend
export FRONTEND_PORT=4200

# Then start services
docker-compose up -d
```

**Default values** (used if not set):
- `POSTGRES_DB`: curseando
- `POSTGRES_USER`: postgres
- `POSTGRES_PASSWORD`: postgres
- `POSTGRES_PORT`: 5433
- `BACKEND_PORT`: 8081
- `FRONTEND_PORT`: 4200

**Note**: The application works out of the box with these defaults. No `.env` file is required.

## Project Structure

```
curseando/
├── backend/                 # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/curseando/
│   │   │   │   ├── controller/    # REST controllers
│   │   │   │   ├── service/       # Business logic
│   │   │   │   ├── repository/    # Data access
│   │   │   │   ├── model/         # JPA entities
│   │   │   │   ├── dto/           # Data Transfer Objects
│   │   │   │   ├── exception/      # Exception handling
│   │   │   │   └── config/        # Configuration
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   └── Dockerfile
├── frontend/               # Angular application
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/              # Core services
│   │   │   ├── shared/            # Shared models
│   │   │   └── features/          # Feature modules
│   │   ├── assets/
│   │   └── environments/
│   └── Dockerfile
├── docker-compose.yml      # Docker Compose configuration
└── README.md
```

## Features

### Course Catalog
- Display all available courses in a responsive grid layout
- Filter courses by difficulty level (Beginner, Intermediate, Advanced)
- Show course availability (spots remaining)
- Navigate to course details

### Course Details
- View complete course information
- See enrollment statistics
- Enroll in courses (if spots available)
- Visual indication when course is full

### Enrollment
- Form validation (name and email)
- Duplicate enrollment prevention
- Real-time availability updates
- Success/error messaging

## Database Schema

### Courses Table
- id (BIGSERIAL PRIMARY KEY)
- title (VARCHAR(200))
- instructor (VARCHAR(100))
- duration (VARCHAR(50))
- difficulty (ENUM: BEGINNER, INTERMEDIATE, ADVANCED)
- description (TEXT)
- max_capacity (INTEGER)
- enrolled_count (INTEGER)

### Students Table
- id (BIGSERIAL PRIMARY KEY)
- full_name (VARCHAR(200))
- email (VARCHAR(255) UNIQUE)

### Enrollments Table
- id (BIGSERIAL PRIMARY KEY)
- course_id (BIGINT FOREIGN KEY)
- student_id (BIGINT FOREIGN KEY)
- enrollment_date (TIMESTAMP)
- UNIQUE constraint on (course_id, student_id)

## Seed Data

The application automatically seeds 5 courses on startup:
- Introduction to Java Programming (Beginner, 15/50 enrolled)
- Advanced Spring Boot Development (Intermediate, 28/30 enrolled - almost full)
- Microservices Architecture with Docker (Advanced, 8/25 enrolled)
- Web Development with Angular (Beginner, 38/40 enrolled - almost full)
- Full-Stack Development with Spring Boot and React (Intermediate, 12/35 enrolled)

## Troubleshooting

### Backend won't start
- Check database connection: Ensure PostgreSQL is healthy
  ```bash
  docker-compose ps postgres
  docker-compose logs postgres
  ```
- View backend logs: `docker-compose logs backend`
- Verify database is ready: Backend waits for PostgreSQL health check

### Frontend can't connect to backend
- Verify backend is running: `docker-compose ps`
- Check backend health: `curl http://localhost:8081/actuator/health`
- View backend logs: `docker-compose logs backend`
- Check CORS configuration in backend

### Database connection issues
- Check PostgreSQL is healthy: `docker-compose ps postgres`
- Verify connection string uses service name: `postgres:5432` (internal Docker network)
- View database logs: `docker-compose logs postgres`
- Restart database: `docker-compose restart postgres`

### Port conflicts
- Check what's using ports:
  ```bash
  # Linux/Mac
  lsof -i :8081
  lsof -i :4200
  lsof -i :5433
  
  # Windows
  netstat -ano | findstr :8081
  ```
- Change ports by setting environment variables before `docker-compose up`:
  ```bash
  export BACKEND_PORT=8082
  export FRONTEND_PORT=4201
  export POSTGRES_PORT=5434
  docker-compose up -d
  ```

## Development

### Backend Development
```bash
cd backend
# Requires Java 17 and Maven
mvn spring-boot:run
```

**Note**: Ensure PostgreSQL is running (via Docker Compose) or configure a local PostgreSQL instance in `application.properties`.

### Frontend Development
```bash
cd frontend
npm install
npm start
```

## Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## Technologies Used

### Backend
- Spring Boot 3.2.0
- Java 17
- Spring Data JPA
- PostgreSQL Driver (runtime)
- Springdoc OpenAPI 2.3.0 (Swagger UI)
- Jakarta Validation
- Spring Boot Actuator (health checks)

### Frontend
- Angular 19.1.1
- TypeScript 5.7.3
- Tailwind CSS 3.4.0
- RxJS 7.8.1
- Angular Signals (built-in)

### DevOps
- Docker
- Docker Compose
- Nginx (for frontend serving)

## License

This project is part of a technical assessment.

## Contact

For questions or issues, please refer to the project documentation or contact the development team.

