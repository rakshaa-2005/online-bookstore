#  Online Bookstore

A full-stack online bookstore built with **Java + Spring MVC** (backend) and **HTML/CSS/JavaScript** (frontend), using **JDBC** for database access.

---

## 🛠 Tech Stack

| Layer      | Technology                          |
|------------|-------------------------------------|
| Frontend   | HTML5, CSS3, Vanilla JavaScript (SPA)|
| Backend    | Java 17, Spring Boot 3, Spring MVC  |
| Database   | H2 in-memory (swap for MySQL/PostgreSQL) |
| DB Access  | Spring JDBC (`JdbcTemplate`)        |
| Build      | Maven                               |

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+

### Run the Application

```bash
# Clone / unzip the project
cd online-bookstore

# Build and run
mvn spring-boot:run
```

Open your browser at **http://localhost:8080**

> The H2 console is available at **http://localhost:8080/h2-console**  
> JDBC URL: `jdbc:h2:mem:bookstoredb` | User: `sa` | Password: *(empty)*

---

## 📁 Project Structure

```
online-bookstore/
├── pom.xml
└── src/main/
    ├── java/com/bookstore/
    │   ├── BookstoreApplication.java          # Entry point
    │   ├── config/
    │   │   └── WebConfig.java                 # CORS + static resources
    │   ├── controller/
    │   │   ├── BookController.java            # GET /books, GET /books/{id}
    │   │   ├── CartController.java            # GET/POST /cart, DELETE /cart/{id}
    │   │   ├── OrderController.java           # POST /checkout
    │   │   └── UserController.java            # POST /auth/login, /auth/register
    │   ├── model/
    │   │   ├── Book.java
    │   │   ├── CartItem.java
    │   │   ├── Order.java
    │   │   ├── Review.java
    │   │   └── User.java
    │   ├── repository/
    │   │   ├── BookRepository.java            # JDBC queries for books
    │   │   ├── CartRepository.java            # JDBC queries for cart
    │   │   ├── OrderRepository.java           # JDBC queries for orders
    │   │   ├── ReviewRepository.java          # JDBC queries for reviews
    │   │   └── UserRepository.java            # JDBC queries for users
    │   └── service/
    │       ├── BookService.java
    │       ├── CartService.java
    │       ├── OrderService.java
    │       └── UserService.java
    └── resources/
        ├── application.properties
        ├── schema.sql                         # DDL: CREATE TABLE statements
        ├── data.sql                           # Seed data (10 books + demo user)
        └── static/
            ├── index.html                     # SPA shell
            ├── css/styles.css                 # Full stylesheet
            └── js/app.js                      # Frontend logic
```

---

## 🌐 API Reference

### Books

| Method | Endpoint                  | Description                                 |
|--------|---------------------------|---------------------------------------------|
| GET    | `/books`                  | Fetch all books                             |
| GET    | `/books?search=dune`      | Search books by title/author                |
| GET    | `/books?category=Sci-Fi`  | Filter books by category                    |
| GET    | `/books/categories`       | List all distinct categories                |
| GET    | `/books/{id}`             | Fetch a single book                         |
| GET    | `/books/{id}/reviews`     | Get reviews for a book *(bonus)*            |
| POST   | `/books/{id}/reviews`     | Add a review *(bonus)*                      |

**Review request body:**
```json
{ "reviewer": "Alice", "rating": 5, "comment": "Loved it!" }
```

### Cart

| Method | Endpoint       | Description             |
|--------|----------------|-------------------------|
| GET    | `/cart`        | Get session cart        |
| POST   | `/cart`        | Add book to cart        |
| DELETE | `/cart/{id}`   | Remove item from cart   |

**Add to cart body:**
```json
{ "bookId": 3, "quantity": 1 }
```

### Checkout

| Method | Endpoint     | Description           |
|--------|--------------|-----------------------|
| POST   | `/checkout`  | Place order from cart |

### Auth

| Method | Endpoint          | Description         |
|--------|-------------------|---------------------|
| POST   | `/auth/register`  | Create account      |
| POST   | `/auth/login`     | Login               |
| POST   | `/auth/logout`    | Logout              |
| GET    | `/auth/me`        | Get current user    |

---

## 🗃 Database Schema

```
books        → id, title, author, description, price, category, cover_url, rating, stock
users        → id, username, email, password, created_at
cart_items   → id, session_id, user_id (FK), book_id (FK), quantity
orders       → id, session_id, user_id (FK), total_amount, status, placed_at
order_items  → id, order_id (FK), book_id (FK), quantity, unit_price
reviews      → id, book_id (FK), reviewer, rating, comment, created_at
```

---

## ✅ Features Implemented

### Core
- [x] Homepage with book grid (title, author, price, rating, cover image)
- [x] Search bar (search by title or author)
- [x] Category filter chips
- [x] Book detail page (description, price, rating, stock status)
- [x] Add to Cart button
- [x] Cart page (list items, subtotal, remove button)
- [x] Checkout → order confirmation page

### Auth (Optional)
- [x] User registration & login
- [x] Session-based auth
- [x] Guest cart (session-based, no login required)

### Bonus
- [x] Book reviews with star rating + comment form
- [x] Book cover images
- [x] Automatic rating recalculation on new review
- [x] Low stock warnings
- [x] Toast notifications
- [x] Responsive design

---

## 🔑 Learning Outcomes

| Topic                          | Where to find it                                    |
|--------------------------------|-----------------------------------------------------|
| HTML/CSS/JS UI design          | `src/main/resources/static/`                        |
| Spring Web MVC controllers     | `controller/BookController.java` etc.               |
| REST API design                | All `@RestController` classes                       |
| JDBC database access           | `repository/` classes using `JdbcTemplate`          |
| SQL schema design              | `schema.sql`                                        |
| Service layer / business logic | `service/` classes                                  |
| Session management             | `CartController`, `OrderController`, `UserController`|
| Many-to-many via join tables   | `cart_items`, `order_items`                         |

---

## 🔧 Switching to MySQL

1. Add MySQL dependency in `pom.xml`:
```xml
<dependency>
  <groupId>com.mysql</groupId>
  <artifactId>mysql-connector-j</artifactId>
  <scope>runtime</scope>
</dependency>
```

2. Update `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bookstoredb?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

3. Remove the H2 dependency from `pom.xml`.

---

## 🔒 Production Notes

- **Passwords**: Replace plaintext with `BCryptPasswordEncoder` (Spring Security)
- **Sessions**: Use Spring Session with Redis for distributed sessions
- **HTTPS**: Configure SSL in `application.properties`
- **Input Validation**: Add `@Valid` + Bean Validation annotations on request bodies
