# HR Platform API

[![CI Pipeline](https://github.com/pitzzahh/hr-platform-api/actions/workflows/ci.yml/badge.svg)](https://github.com/pitzzahh/hr-platform-api/actions/workflows/ci.yml)
[![CI Pipeline with Qodana](https://github.com/pitzzahh/hr-platform-api/actions/workflows/qodana_code_quality.yml/badge.svg)](https://github.com/pitzzahh/hr-platform-api/actions/workflows/qodana_code_quality.yml)
[![CodeQL](https://github.com/pitzzahh/hr-platform-api/actions/workflows/github-code-scanning/codeql/badge.svg)](https://github.com/pitzzahh/hr-platform-api/actions/workflows/github-code-scanning/codeql)
[![Automatic Dependency Submission](https://github.com/pitzzahh/hr-platform-api/actions/workflows/dependency-graph/auto-submission/badge.svg)](https://github.com/pitzzahh/hr-platform-api/actions/workflows/dependency-graph/auto-submission)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

A **SpringBoot RESTful API** for managing HR data such as employees, salaries, audits, and user roles/permissions.
Designed with clean architecture, modular packages, and standardized API responses for smooth integration.

---

## ✨ Features

* 👤 **Employee Management** — CRUD operations, employment history, positions, civil status, ID documents.
* 💰 **Salary Management** — Salary data, salary grade, and salary overrides.
* 📜 **Audit Logging** — Track key user and system actions.
* 🔐 **Authentication & Authorization** — User accounts, roles, API-Key authentication, and fine-grained permissions.
* ⚡ **Robust Infrastructure** — Exception handling, pagination, mapping utilities, and more.

---

## 📁 Project Structure

```
~/hrplatformapi
├───audit
│   ├───controller
│   ├───dto
│   │   ├───request
│   │   └───response
│   ├───model
│   ├───repository
│   └───service
│       └───impl
├───config
├───employee
│   ├───controller
│   ├───dto
│   │   ├───request
│   │   └───response
│   ├───model
│   ├───repository
│   └───service
│       └───impl
├───exception
├───salary
│   ├───controller
│   ├───dto
│   │   ├───request
│   │   └───response
│   ├───model
│   ├───repository
│   └───service
│       └───impl
├───user
│   ├───model
│   └───repository
└───utils
├───annotations
├───enums
├───formatter
└───mappers
```

---

## ⚙️ Getting Started

### Prerequisites

* ☕ Java **17+**
* 🔧 Maven **3.6+**
* 🗄️ Database (e.g. PostgreSQL/MySQL — configurable)

### Build & Run

```bash
./mvnw clean install
./mvnw spring-boot:run
```

### Configuration

Edit your application settings in:

```
src/main/resources/application.yml
```

---

## 🔗 API Endpoints

| Resource                               | Endpoint                                                | Methods                | Description                                           |
|----------------------------------------|---------------------------------------------------------|------------------------|-------------------------------------------------------|
| Employee                               | `/api/v1/employees`                                     | GET, POST, PUT, DELETE | Manage employees (CRUD, info, positions)              |
| Employee (by User ID)                  | `/api/v1/employees/user/{userId}`                       | GET                    | Retrieve employee by user ID                          |
| Employment Information                 | `/api/v1/employees/{employeeId}/employment-information` | GET, POST, PUT, DELETE | Manage employment information for a specific employee |
| ID Documents                           | `/api/v1/id-documents`                                  | GET, POST, PUT, DELETE | Manage employee ID documents                          |
| Positions                              | `/api/v1/positions`                                     | GET, POST, PUT, DELETE | Manage employee positions                             |
| Workplaces                             | `/api/v1/workplaces`                                    | GET, POST, PUT, DELETE | Manage employee workplaces                            |
| Employment Information Salary Override | `/api/v1/employment-information-salary-overrides`       | GET, POST, PUT, DELETE | Manage salary overrides for employment information    |
| Salary Grade                           | `/api/v1/salary-grades`                                 | GET, POST, PUT, DELETE | Manage salary grade definitions (1–33)                |
| Salary Data                            | `/api/v1/salary-data`                                   | GET, POST, PUT, DELETE | Manage salary step data (1–8 per grade)               |
| Audit                                  | `/api/v1/audit`                                         | GET                    | View audit logs                                       |
| User                                   | `/api/v1/users`                                         | GET, POST, PUT, DELETE | Manage users, roles, and permissions                  |
| Roles                                  | `/api/v1/roles`                                         | POST                   | User login and token generation                       |
| Permission                             | `/api/v1/permissions`                                   | POST                   | Manage user permissions                               |

👉 Check the controller classes for full request/response examples.

---

## 📖 API Documentation

Interactive API documentation is available at:

```
/docs
```

After starting the application, visit this endpoint in your browser to explore and test all available endpoints using
Scalar.

---

## 🧪 Testing

Run the test suite:

```bash
./mvnw test
```

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!
For major changes, please open an issue first to discuss your ideas.

---

## 📜 License

This project is licensed under the [Apache License 2.0](LICENSE).
