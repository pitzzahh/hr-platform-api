# HR Platform API

A **SpringBoot RESTful API** for managing HR data such as employees, salaries, audits, and user roles/permissions.
Designed with clean architecture, modular packages, and standardized API responses for smooth integration.

---

## ✨ Features

* 👤 **Employee Management** — CRUD operations, employment history, positions, civil status.
* 💰 **Salary Management** — Salary data and salary grade tracking.
* 📜 **Audit Logging** — Track key user and system actions.
* 🔐 **Authentication & Authorization** — User accounts, roles, API-Key authentication, and fine-grained permissions.
* ⚡ **Robust Infrastructure** — Exception handling, pagination, mapping utilities, and more.

---

## 📁 Project Structure

```
~/hrplatformapi
├── audit/             
│   ├── controller     
│   ├── dto           
│   ├── model          
│   ├── repository    
│   └── service
│
├── employee/
│   ├── controller     
│   ├── dto           
│   ├── model          
│   ├── repository    
│   └── service
│
├── salary/           
│   ├── controller     
│   ├── dto           
│   ├── model          
│   ├── repository    
│   └── service
│
├── user/             
│   ├── controller     
│   ├── dto           
│   ├── model          
│   ├── repository    
│   └── service
│
├── utils/            
└── exception/         
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

| Resource     | Endpoint                                     | Methods                | Description                              |
|--------------|----------------------------------------------|------------------------|------------------------------------------|
| Employee     | `/api/v1/employees`                          | GET, POST, PUT, DELETE | Manage employees (CRUD, info, positions) |
| Salary Grade | `/api/v1/salary-grades`                      | GET, POST, PUT, DELETE | Manage salary grade definitions (1–33)   |
| Salary Data  | `/api/v1/salary-grades/{salaryGradeId}/data` | GET, POST, PUT, DELETE | Manage salary step data (1–8 per grade)  |
| Audit        | `/api/v1/audit`                              | GET                    | View audit logs                          |
| User         | `/api/v1/users`                              | GET, POST, PUT, DELETE | Manage users, roles, and permissions     |
| Roles        | `/api/v1/roles`                              | POST                   | User login and token generation          |
| Permission   | `/api/v1/permissions`                        | POST                   | Manage user permissions                  |
 | 

👉 Check the controller classes for full request/response examples.

---

## 📖 API Documentation

Interactive API documentation is available at:

```
/docs
```

After starting the application, visit this endpoint in your browser to explore and test all available endpoints using Scalar.

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

This project is licensed under the [MIT License](LICENSE).
