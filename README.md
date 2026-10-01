# Automated Ride-Sharing Demonstration Application (SmartRide.ai)

An enterprise-grade, lightweight, and modern mobility platform built with **Java 17, Spring Boot 3, Thymeleaf, Vanilla CSS, H2/PostgreSQL, Docker, Jenkins, Ansible, and Selenium WebDriver**.

---

## 🚀 Key Features

- **Passenger Hub:**
  - Create ride requests with dynamic route fare estimation, vehicle preference (Sedan, SUV, Hatchback, Electric), and seat configuration.
  - Smart Ride-Sharing Recommendations with real-time match scoring and potential cost savings calculation.
  - Real-time ride listing and cancellation controls.
- **Driver Operations Console:**
  - Live pickup requests queue with 1-click ride acceptance.
  - State machine progression: `REQUESTED` ➔ `ACCEPTED` ➔ `DRIVER_ASSIGNED` ➔ `ONGOING` ➔ `COMPLETED`.
- **Fleet Admin Analytics:**
  - Dynamic KPI cards: Total Rides, Active Trips, Completed Trips, Cancellations, and Fleet Utilization Rate.
  - Interactive search and filter table.
- **Activity & Audit Timeline:**
  - Chronological audit logging for every lifecycle event and dispatch transition.

---

## 🛠️ Technology Stack

| Layer | Technology |
| :--- | :--- |
| **Backend & APIs** | Java 17, Spring Boot 3.2.3, Spring Data JPA, Spring Validation |
| **Frontend & UI** | Thymeleaf, HTML5, Vanilla CSS (Glassmorphism / Dark Theme), Vanilla JavaScript Fetch API |
| **Persistence** | H2 Database (In-Memory for zero-config demo), PostgreSQL Profile Ready |
| **Packaging** | Executable WAR (Supports standalone execution and Tomcat container deployment) |
| **Automated Testing** | JUnit 5, AssertJ, Mockito, SpringBootTest, Selenium WebDriver |
| **CI / CD Pipeline** | Jenkins declarative `Jenkinsfile` (Checkout ➔ Build ➔ Test ➔ Package ➔ Deploy) |
| **Containerization** | Docker, Multi-Stage Dockerfile, Docker Compose |
| **Config Management** | Ansible Playbooks (`playbook.yml`, `rollback.yml`) |

---

## 💻 Quick Start & Running Locally

### Prerequisites
- JDK 17+ installed
- Maven 3.8+ (or use the included wrapper script)

### 1. Run via Maven Wrapper
```powershell
# Run backend application
.\mvnw.cmd spring-boot:run
```
*(On Linux/macOS: `./mvnw spring-boot:run`)*

### 2. Access the Application
- **Web UI:** [http://localhost:8080](http://localhost:8080)
- **Actuator Health Probe:** [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- **API Health:** [http://localhost:8080/api/health](http://localhost:8080/api/health)
- **H2 Database Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (`JDBC URL: jdbc:h2:mem:smartridedb`, User: `sa`, Password: *empty*)

---

## 🧪 Automated Testing

### Execute Unit & Integration Tests
```powershell
.\mvnw.cmd test
```

### Execute Selenium WebDriver UI Tests
```powershell
.\mvnw.cmd test -Dtest=SmartRideSeleniumIT
```
*Screenshots captured on failure/completion are stored in `target/selenium-screenshots/`.*

---

## 🐳 Docker Deployment

### 1. Build Docker Image
```bash
docker build -t smartride-app:1.1.0 .
```

### 2. Run Container
```bash
docker run -d -p 8080:8080 --name smartride-container smartride-app:1.1.0
```

### 3. Docker Compose (App + PostgreSQL Profile)
```bash
docker-compose up -d
```

---

## ⚙️ Jenkins CI/CD Pipeline as Code

The project includes a production-ready declarative pipeline in `Jenkinsfile`:
- **Stage 1:** Environment & SCM Checkout
- **Stage 2:** Maven Compile & Build
- **Stage 3:** Automated Unit & Integration Testing (`mvn test`)
- **Stage 4:** Continuous Selenium UI Testing
- **Stage 5:** Package Application WAR Artifact
- **Stage 6:** Docker Container Image Build & Version Tagging
- **Stage 7:** Deployment & Automated Health Verification (`/actuator/health`)

---

## 📜 Ansible Configuration & Rollback

### Run Idempotent Provisioning Playbook
```bash
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml
```

### Execute Automated Rollback Playbook
```bash
ansible-playbook -i ansible/inventory.ini ansible/rollback.yml -e "rollback_version=1.0.0"
```

---

## 📂 Project Structure

```
├── .github/                       # GitHub issue templates and PR configuration
├── docs/                          # Task documentation (TASK 1 - 3)
│   ├── TASK1_PROBLEM_DEFINITION.md
│   ├── TASK2_AGILE_DEVOPS_PLAN.md
│   └── TASK3_ARCHITECTURE_SRS.md
├── src/
│   ├── main/
│   │   ├── java/com/smartride/    # Controllers, Services, Entities, DTOs, Repositories
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-prod.properties
│   │       ├── static/            # CSS & JavaScript
│   │       └── templates/         # Thymeleaf views (index.html)
│   └── test/java/com/smartride/   # Unit, Integration & Selenium tests
├── ansible/                       # Ansible automation playbooks
├── Dockerfile                     # Multi-stage optimized Dockerfile
├── docker-compose.yml             # Container orchestration
├── Jenkinsfile                    # CI/CD pipeline as code
├── pom.xml                        # Maven dependency specification
├── TASK_STATUS.md                 # 15-Task execution status tracker
└── PROJECT_EVIDENCE.md            # Comprehensive verification & evidence dossier
```
