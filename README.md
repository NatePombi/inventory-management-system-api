# Inventory Management System API

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

* Java 21

* Spring Boot 3

* Spring Security with JWT

* Spring Data JPA

* MySQL

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

