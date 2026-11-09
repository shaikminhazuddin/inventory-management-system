# Contributing to Inventory Management System

Thank you for your interest in contributing to the **Inventory Management System**! This is an open-source college B.Tech project designed to demonstrate clean Core Java, Servlets, JDBC, and JSP architecture.

---

## Code of Conduct

Please be respectful and constructive in all discussions, pull requests, and issue reports.

---

## How Can You Contribute?

1. **Reporting Bugs**:
   - Check existing issues to see if the bug has already been reported.
   - If not, open a new issue with a clear title, description, and steps to reproduce.
   - Include relevant Tomcat server logs or stack traces.

2. **Suggesting Enhancements**:
   - Open an issue describing the proposed feature or improvement.
   - Explain the use case and why it would benefit warehouse management.

3. **Submitting Pull Requests**:
   - Fork the repository and create a new feature branch from `main`.
   - Keep changes modular, clean, and well-commented.
   - Avoid adding unnecessary heavyweight frameworks (this project intentionally focuses on native Core Java, Servlets, JDBC, and JSP).
   - Ensure the project builds successfully using `mvn clean package`.
   - Submit a pull request with a clear description of your changes.

---

## Coding Standards

- Follow standard Java naming conventions (CamelCase for classes, lowerCamelCase for variables and methods).
- Use `PreparedStatement` for all database interactions to prevent SQL injection.
- Manage database connections responsibly and ensure all resources (`Connection`, `Statement`, `ResultSet`) are closed properly.
- Keep JSP views clean by delegating business and database logic to Servlets and DAOs (MVC separation).
