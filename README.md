# Automated Ride-Sharing Demonstration Application (SmartRide.ai)

An enterprise-grade, lightweight, and modern mobility platform built with **Java 17, Spring Boot 3, Thymeleaf, Vanilla CSS, H2/PostgreSQL, Docker, Jenkins, Ansible, and Selenium WebDriver**.

---

## 📚 Master Reports & Evidence Dossier

- 🏆 **[Master Project Completion Report](docs/reports/FINAL_PROJECT_COMPLETION_REPORT.md)** — Comprehensive master academic report covering all 15 DevOps tasks, architecture, completion matrix, verified test metrics, Jenkins pipeline results, Docker evidence, Ansible provisioning, and release state.
- 📂 **[15 Individual Task Reports & Evidence Index](docs/reports/README.md)** — Detailed task-by-task execution reports (Task 01 through Task 15) with embedded genuine screenshots and command transcripts.
- 📊 **[Project Task Status Tracker](TASK_STATUS.md)** — High-level 15-task status tracking matrix.
- 📑 **[Project Evidence Dossier](PROJECT_EVIDENCE.md)** — Consolidated operational evidence and verification dossier.

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
- **Web UI:** [http://localhost:8081](http://localhost:8081)
- **Actuator Health Probe:** [http://localhost:8081/actuator/health](http://localhost:8081/actuator/health)
- **API Health:** [http://localhost:8081/api/health](http://localhost:8081/api/health)
- **H2 Database Console:** [http://localhost:8081/h2-console](http://localhost:8081/h2-console) (`JDBC URL: jdbc:h2:mem:smartridedb`, User: `sa`, Password: *empty*)
*(Note: Port 8081 is used to prevent port conflict with Jenkins running on 8080)*

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

Jenkins already handles this in the CI/CD pipeline. To build manually:
```bash
docker build -t smartride-app:1.1.0 .
```

### 2. Run Container (Host Port 8081 → Container Port 8080)
```bash
docker run -d -p 8081:8080 --name smartride-container smartride-app:1.1.0
```
**App is accessible at:** `http://localhost:8081`

### 3. Docker Compose (App + Optional PostgreSQL Profile)
```bash
docker-compose up -d
```

---

## ⚙️ Jenkins CI/CD Pipeline as Code

The project includes a production-ready declarative pipeline in `Jenkinsfile`:
- **Stage 1:** Environment & SCM Checkout
- **Stage 2:** Maven Compile & Unit Tests (13 tests)
- **Stage 3:** Selenium End-to-End UI Testing (4 journeys)
- **Stage 4:** Package Application WAR Artifact
- **Stage 5:** Docker Image Build & Version Tagging
- **Stage 6:** Docker Hub Registry Publish (requires `dockerhub-creds` Jenkins credential)
- **Stage 7:** Container Deployment & Automated Health Verification (`/actuator/health` on port 8081)

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
├── docs/                          # Task documentation and reports
│   ├── reports/                   # 15 Individual Task Reports & Evidence
│   │   ├── README.md              # Reports Index
│   │   ├── FINAL_PROJECT_COMPLETION_REPORT.md # Master academic completion report
│   │   ├── TASK_01_Report.md ... TASK_15_Report.md
│   │   └── screenshots/           # Evidence screenshots organized by task (task-01 .. task-15)
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
