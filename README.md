# Advanced Task Management

## About the Project

Advanced Task Management is an application that helps users manage tasks and projects. This project is developed using Java, Spring Boot, and Maven.

## Requirements

- Java 17 or higher
- Maven 3.6 or higher
- An SQL database (e.g., MySQL, PostgreSQL)

## Setup

### 1. Clone the Repository

```bash
git clone https://github.com/mgumussoy/advanced-task-management.git
cd advanced-task-management
```

### 2. Install Dependencies

```bash
mvn clean install
```

### 3. Configure the Database

Open the `application.properties` file located in `src/main/resources` and configure your database:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/yourdatabase
spring.datasource.username=yourusername
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

### 4. Start the Application

```bash
mvn spring-boot:run
```

Once the application starts successfully, you can access it at `http://localhost:8080`.

## API Usage

### User Operations

- **Create User**

  ```http
  POST /users
  Content-Type: application/json

  {
    "username": "newuser",
    "password": "password"
  }
  ```

- **Update User**

  ```http
  PUT /users/{userId}
  Content-Type: application/json

  {
    "username": "updateduser",
    "password": "newpassword"
  }
  ```

- **Delete User**

  ```http
  DELETE /users/{userId}
  ```

- **Get User**

  ```http
  GET /users/{userId}
  ```

- **Get All Users**

  ```http
  GET /users
  ```

### Project Operations

- **Create Project**

  ```http
  POST /projects
  Content-Type: application/json

  {
    "name": "New Project",
    "description": "Project Description"
  }
  ```

- **Update Project**

  ```http
  PUT /projects/{projectId}
  Content-Type: application/json

  {
    "name": "Updated Project",
    "description": "Updated Description"
  }
  ```

- **Delete Project**

  ```http
  DELETE /projects/{projectId}
  ```

- **Get Project**

  ```http
  GET /projects/{projectId}
  ```

- **Get Tasks of Project**

  ```http
  GET /projects/{projectId}/tasks
  ```

### Task Operations

- **Create Task**

  ```http
  POST /tasks/create
  Content-Type: application/json

  {
    "title": "New Task",
    "description": "Task Description",
    "projectId": 1
  }
  ```

- **Update Task**

  ```http
  PUT /tasks/{taskId}
  Content-Type: application/json

  {
    "title": "Updated Task",
    "description": "Updated Description"
  }
  ```

- **Delete Task**

  ```http
  DELETE /tasks/{taskId}
  ```

- **Get Task**

  ```http
  GET /tasks/{taskId}
  ```

- **Assign Task**

  ```http
  POST /tasks/{taskId}/assign/{userId}
  ```

## Contributing

If you would like to contribute, please fork the repository and submit a pull request. Any contributions and feedback are welcome.
