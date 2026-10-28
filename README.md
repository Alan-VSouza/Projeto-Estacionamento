![NavbarGit](frontend/src/assets/images/NavbarGit.png)

# 🚗 Parking Management System

A complete parking management project built with **Spring Boot** and an integrated front-end. It handles vehicle registration, entry and exit control, reports and user authentication.

---

## ✨ Features

### Back-end
- Vehicle registration (plate, model, color)
- Entry and exit logging with automatic calculation of parking time
- Daily reports (PDF/CSV)
- JWT authentication for secure access

### Front-end
- Responsive web interface (React)
- User login and registration
- Dashboard with real-time metrics
- Forms for vehicle registration and payments

---

## 🔧 Tech stack

### Back-end
- Java 21
- Spring Boot 3.2
- Spring Security
- Spring Data JPA
- SQLite (H2 for tests)
- Lombok
- OpenAPI (Swagger)

### Front-end
- React 18
- Axios (API integration)
- Bootstrap 5

### Testing
- Mockito
- Pitest (mutation testing)

---

## 🚀 Getting started

### Requirements
- Java 21
- Node.js 18+

### Back-end

```bash
git clone https://github.com/Alan-VSouza/Projeto-Estacionamento.git
cd Projeto-Estacionamento/backend
```

Set the key used to sign JWT tokens (a long random string with at least 32 characters). It is not stored in the repository:

```bash
export JWT_SECRET_KEY="replace-with-a-long-random-string"   # Windows (PowerShell): $env:JWT_SECRET_KEY="..."
./mvnw spring-boot:run
```

You can also run the `DemoAuthAppApplication` class from your IDE, with the environment variable set. The SQLite database (`database.db`) is created automatically on the first run.

### Front-end

```bash
cd Projeto-Estacionamento/frontend
npm install
npm start
```

Open `http://localhost:3000`.

## 🔒 Authentication

Authentication is protected by JWT, using these endpoints:
- **Register:** POST `/api/auth/register`
- **Login:** POST `/api/auth/login`

Create an account on the Register page and sign in to access the features.

---

## 📊 Quality

![Code coverage](https://img.shields.io/badge/coverage-100%25-brightgreen)
![Mutants killed](https://img.shields.io/badge/mutants-99%25-brightgreen)
![Build status](https://img.shields.io/badge/build-passing-brightgreen)

---

## 📄 API documentation

Open `http://localhost:8080/swagger-ui.html` after starting the back-end.
