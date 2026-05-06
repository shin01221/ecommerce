# E-Commerce Web Application

A Java-based e-commerce platform demonstrating backend architecture, request handling, authentication, and data management using MVC pattern.

## Technologies

- **Java 17** - Core language
- **Servlets/JSP** - MVC framework
- **MariaDB / MySQL** - Relational database
- **Redis** - Caching and rate limiting
- **JWT** - Stateless authentication
- **Sessions** - Stateful authentication
- **BCrypt** - Password hashing
- **Maven** - Build tool

## Architecture

```
com.ecommerce
├── controller/    # Servlets (Controller layer)
├── service/       # Business logic (Service layer)
├── dao/           # Database access (DAO layer)
├── model/         # POJO classes (User, Product, Review)
├── filter/        # AuthFilter and other filters
├── util/          # Helper utilities (JWT, RateLimiter)
└── config/        # Configuration classes (Database, Redis)
```

## Quick Start (Docker)

This is the recommended way to run the application.

```bash
cd ecommerce
docker compose up --build
```

Then open: `http://localhost:8080/ecommerce/`

To stop:

```bash
docker compose down
```

To stop and remove all data (volumes):

```bash
docker compose down -v
```

## Manual Setup (Without Docker)

### Prerequisites

Install these before starting:

### Java 17

```bash
sudo apt install openjdk-17-jdk
java -version
```

### Maven

```bash
sudo apt install maven
mvn -version
```

### MariaDB

```bash
sudo apt install mariadb-server
sudo systemctl start mariadb
sudo systemctl enable mariadb
```

### Redis

```bash
sudo apt install redis-server
sudo systemctl start redis-server
sudo systemctl enable redis-server
```

Verify Redis is running:

```bash
redis-cli ping
# Should return: PONG
```

## Setup

### 1. Database Setup

Create the database, tables, and seed data:

```bash
sudo mariadb < schema.sql
```

Verify the database was created:

```bash
sudo mariadb -e "USE ecommerce_db; SHOW TABLES;"
```

### 2. Configure Database

Edit `src/main/java/com/ecommerce/config/DatabaseConfig.java` to match your MariaDB credentials:

```java
private static final String USER = "root";
private static final String PASSWORD = "";  // Set your MariaDB root password here if you have one
```

**Note:** The `jdbc:mysql://` URL prefix works with MariaDB. The MySQL JDBC driver is fully compatible.

### 3. Configure Redis

Edit `src/main/java/com/ecommerce/config/RedisConfig.java` if your Redis is not on localhost:6379:

```java
private static final String HOST = "localhost";
private static final int PORT = 6379;
```

### 4. Build

```bash
cd ecommerce
mvn clean package
```

### 5. Run

```bash
mvn tomcat7:run
```

The application will start on port 8080.

### 6. Access

Open browser: `http://localhost:8080/ecommerce/`

## Quick Start (All in One - Manual)

```bash
# 1. Start MariaDB
sudo systemctl start mariadb

# 2. Start Redis
sudo systemctl start redis-server

# 3. Import database
sudo mariadb < schema.sql

# 4. Build and run
cd ecommerce
mvn clean package
mvn tomcat7:run
```

Then open: `http://localhost:8080/ecommerce/`

## Features

### Core
- Home page with products, user info, and reviews
- User registration and login (Sessions + JWT)
- Product management (add/delete for admins)
- Product reviews/feedback
- Redis caching for products
- Rate limiting via Redis
- AuthFilter for request validation

### Bonus
- Product details page
- Admin privileges (only admin can add/delete products)
- Separated layers (Controller/Service/DAO)
- Error handling (custom error page)
- Server-side input validation

## Default Admin

- Username: `admin`
- Password: `admin123`
