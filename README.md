# Inventory Management System API

![Java CI](https://github.com/NatePombi/inventory-management-system-api/actions/workflows/test.yml/badge.svg)
[![codecov](https://codecov.io/gh/NatePombi/inventory-management-system-api/branch/master/graph/badge.svg?token=YOUR_TOKEN)](https://codecov.io/gh/NatePombi/inventory-management-system-api)


This is a Spring Boot REST API I’m building to manage products and users in an inventory system.
It supports JWT authentication, role-based access control, and CRUD operations for products.

Right now it’s fully functional, but I’ll keep improving it over time.

---

#### Features (So Far)
*  User Authentication with JWT (Register & Login).

* Role-based access control (User vs Admin).

* Role assignment is determined by email:

* If your email contains “admin”, you’re assigned ADMIN.
Otherwise, you’re assigned USER.

* Product Management (CRUD):

  * Create products

   * Update product details

    * Delete products

    * Get product by ID

* User Management (Admin only):

  * View all users

  * Get a user by username

  * Delete a user

---

### Tech Stack

* Java 17

* Spring Boot 3

* Spring Security with JWT

* Spring Data JPA

* MySQL
* PostgreSQL

* JUnit & Mockito for testing

* Swagger / OpenAPI for API docs

---

### Future Plans

I’m not done yet here’s what I’ll add next:

*  Pagination & sorting for product listings

* Timestamps (createdAt, updatedAt) for products

* Integration tests with MySQL DB

* Docker & Docker Compose (with Postgres)

* Deploy online (Railway/Render/Heroku) for live demo

* Analytics dashboard (product stock levels, sales trends, etc.)

--- 

### Database Configuration

- This project uses PostgreSQl. Configure it in edit Configurations.

      1.Go to run
      2.Go to edit Configurations
      3.in the Application drop down go to InventoryManagementSystemApiApplication
      4.Then go to environment varaibles and configure it to your custom details

- Steps to set up the database:

  - Make sure PostgreSQL is installed and running.

  - Create the database:

    - CREATE DATABASE inventoryAPI;
    - Update the username and password in application.properties to your own.
    - When you run the Spring Boot app, JPA/Hibernate will automatically create/update tables.
    


---

### How to Use (For Now)

1) Clone the repo

- git clone https://github.com/NatePombi/java-projects.git


2) Navigate into the project

- cd inventory-management-system-api


3) Run the project

- ./mvnw spring-boot:run


4) Open Swagger docs in browser:

- http://localhost:8080/swagger-ui/index.html

---

### Status

- This is a work in progress I’m pushing it now so I can keep improving later.

---

##  Postman Collection

You can import the full Postman collection to test the API:

[📥 Download Postman Collection](./docs/postman_collection.json)
