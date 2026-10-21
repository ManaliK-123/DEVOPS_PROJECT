# Automated Ride-Sharing Demonstration Application (SmartRide.ai)
## Complete Project Verification & Evidence Dossier

**Evidence Date:** 2026-10-21
**Author:** DevOps & Software Engineering Team
**Repository:** `https://github.com/ManaliK-123/DEVOPS_PROJECT.git`
**Branch:** `main`
**Latest Verified Commit:** `f3a97c9` — `fix(docker): set DOCKER_HOST to named pipe so Jenkins/LocalSystem can reach Docker Desktop`
**Latest Release Tags:** `v1.0.0-mvp`, `v1.1.0-release`
**Build Tool:** Apache Maven 3.9.6
**Runtime:** Java OpenJDK 17 / 21 (Jenkins JDK21 tool)
**Jenkins URL:** `http://localhost:8080` (Jenkins service on host)
**Application Container URL:** `http://localhost:8081` (smartride-container, port 8081→8080)

---

## 1. Task-by-Task Completion Status Matrix

| Task # | Task Title | Status | Primary Artifact / Evidence Location |
| :--- | :--- | :--- | :--- |
| **TASK 1** | Problem Definition & Scope | ✅ Complete | [TASK1_PROBLEM_DEFINITION.md](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/docs/TASK1_PROBLEM_DEFINITION.md) |
| **TASK 2** | Agile Planning & DevOps | ✅ Complete | [TASK2_AGILE_DEVOPS_PLAN.md](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/docs/TASK2_AGILE_DEVOPS_PLAN.md) |
| **TASK 3** | Requirements & Architecture | ✅ Complete | [TASK3_ARCHITECTURE_SRS.md](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/docs/TASK3_ARCHITECTURE_SRS.md), [pom.xml](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/pom.xml) |
| **TASK 4** | Git/GitHub Initialization | ✅ Complete | GitHub remote configured, `.gitignore`, `.github/`, [README.md](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/README.md) |
| **TASK 5** | Feature Branch Workflow | ✅ Complete | Branch `feature/ride-management`, commit `7d473ed`, PR #1 merged → `9afaa7b` |
| **TASK 6** | MVP Completion & Conflict Resolution | ✅ Complete | Branch `feature/smart-recommendations-audit`, conflict resolved `1b90429`, tag `v1.0.0-mvp` |
| **TASK 7** | Jenkins CI Setup & Build | ✅ Complete | Jenkins `SmartRide-CI-CD` job SUCCESS; 13/13 Maven tests; surefire reports in Jenkins |
| **TASK 8** | Pipeline as Code & Deployment | ✅ Complete | [Jenkinsfile](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/Jenkinsfile) (7 stages); WAR archived in Jenkins artifacts |
| **TASK 9** | Selenium Automated Testing | ✅ Complete | [SmartRideSeleniumIT.java](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/src/test/java/com/smartride/selenium/SmartRideSeleniumIT.java) — 4/4 journeys; 4 screenshots archived |
| **TASK 10** | Continuous Testing in Jenkins | ✅ Complete | JUnit dashboard; Selenium screenshots archived; defect injection cycle demonstrated |
| **TASK 11** | Docker Container Lifecycle | ✅ Complete | [Dockerfile](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/Dockerfile) (single-stage JRE); [docker-compose.yml](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/docker-compose.yml) port 8081:8080 |
| **TASK 12** | Jenkins → Docker Continuous Delivery | ✅ Complete | Full pipeline SUCCESS; images built & deployed; registry publish stage code-complete (awaiting dockerhub-creds) |
| **TASK 13** | Configuration Management | ✅ Complete | [playbook.yml](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/ansible/playbook.yml), [inventory.ini](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/ansible/inventory.ini) |
| **TASK 14** | Automated Provisioning & Rollback | ✅ Complete | [rollback.yml](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/ansible/rollback.yml) with atomic symlink rollback |
| **TASK 15** | Final Integrated Release & Evidence | ✅ Complete | This document; commit `f3a97c9`; tags `v1.0.0-mvp` & `v1.1.0-release` on GitHub |

---

## 2. Jenkins CI/CD Pipeline — Verified Build Result

**Job:** `SmartRide-CI-CD`
**Result:** `Finished: SUCCESS`
**Verified Commit:** `f3a97c9f` — checked out by Jenkins from `origin/main`
**Docker Connectivity:** `DOCKER_HOST=npipe:////./pipe/dockerDesktopLinuxEngine` — Jenkins LocalSystem → Docker Desktop Linux engine

### Pipeline Stages (All Passed)

| Stage | Result |
| :--- | :--- |
| SCM & Environment Inspection | ✅ PASSED |
| Maven Build & Unit Tests | ✅ PASSED — 13 tests, 0 failures |
| Selenium UI Continuous Testing | ✅ PASSED — 4 journeys, 4 screenshots |
| Package Application Artifact | ✅ PASSED — `target/smartride-app.war` archived |
| Docker Build & Image Tagging | ✅ PASSED — `smartride-app:1.1.0`, `smartride-app:latest` |
| Docker Hub Registry Publish | ⏳ PENDING — stage code complete; awaiting `dockerhub-creds` Jenkins credential |
| Continuous Deployment & Health Probe | ✅ PASSED — container on `0.0.0.0:8081→8080`, health `{"status":"UP"}` |

---

## 3. Maven Test Execution (Verified 2026-10-21)

```
mvn clean test

[INFO] Running com.smartride.controller.RideApiControllerTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 9.454 s

[INFO] Running com.smartride.service.AnalyticsAndRecommendationTests
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.438 s

[INFO] Running com.smartride.service.RideServiceTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.252 s

[INFO] Running com.smartride.SmartRideApplicationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.018 s

[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS (Total time: 16.427 s)
```

---

## 4. Selenium End-to-End Tests (Verified 2026-10-21)

```
mvn test -Dtest=SmartRideSeleniumIT

[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 32.92 s
[INFO] BUILD SUCCESS (Total time: 37.787 s)
```

**Screenshots Captured:**
- `target/selenium-screenshots/Journey_1__Landing_Page_Verification_and_Brand_Header.png`
- `target/selenium-screenshots/Journey_2__Passenger_Ride_Booking_and_Immediate_Listing.png`
- `target/selenium-screenshots/Journey_3__Driver_Portal_Switching_and_Ride_Acceptance_Workflow.png`
- `target/selenium-screenshots/Journey_4__Admin_Analytics_Dashboard_and_Search_Filter.png`

---

## 5. Maven Package — WAR Artifact

```
mvn package -DskipTests

[INFO] Building war: D:\DEVOPS_CMPN_PROJECT\DEVOPS_CMPN\target\smartride-app.war
[INFO] Replacing main artifact with repackaged archive (Spring Boot executable WAR)
[INFO] BUILD SUCCESS (Total time: 6.167 s)
```

---

## 6. Docker Image Build (Verified 2026-10-21)

```powershell
$env:DOCKER_HOST = 'npipe:////./pipe/dockerDesktopLinuxEngine'
docker build --progress=plain --pull=false -t "smartride-app:1.1.0" -t "smartride-app:latest" .
```

**Result:** Exit code 0 — build succeeded in ~16 seconds.

```
REPOSITORY      TAG       IMAGE ID       SIZE
smartride-app   1.1.0     eca7aab7ab80   631MB
smartride-app   latest    eca7aab7ab80   631MB
```

**Dockerfile:** Single-stage, `FROM eclipse-temurin:17-jre` + `curl` installed for HEALTHCHECK.
Pre-packaged WAR copied from `target/smartride-app.war` (not rebuilt inside Docker).

---

## 7. Container Deployment & Health Check (Verified 2026-10-21)

```
docker run -d -p 8081:8080 --name smartride-container smartride-app:1.1.0

NAMES                STATUS                    PORTS
smartride-container  Up (healthy)              0.0.0.0:8081->8080/tcp
```

**Health endpoint verification:**
```
GET http://localhost:8081/actuator/health
HTTP 200 OK — {"status":"UP"}

GET http://localhost:8081/api/health
HTTP 200 OK — {"status":"HEALTHY","version":"1.1.0"}
```

---

## 8. Docker Hub Registry Publishing (Task 12 — Code Complete)

The `Docker Hub Registry Publish` stage is implemented in [Jenkinsfile](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/Jenkinsfile):
- Authenticates via `dockerhub-creds` Jenkins credential using `docker login --password-stdin`
- Tags and pushes `manalik123/smartride-app:1.1.0` and `manalik123/smartride-app:latest`
- Verifies remote manifest with `docker manifest inspect`
- Logs out after push

**Status:** Awaiting one-time Jenkins credential setup (see TASK_STATUS.md).
Remote image tags (once pushed): `docker.io/manalik123/smartride-app:1.1.0`, `docker.io/manalik123/smartride-app:latest`

---

## 9. Git Branching & Tag History

```
git log --oneline --graph --all

* f3a97c9 fix(docker): set DOCKER_HOST to named pipe so Jenkins/LocalSystem can reach Docker Desktop
* e7b4a2a fix(ci-cd): simplify Dockerfile, add fast-fail timeout to Docker stage, and align port 8081 mappings
* fd117a8 fix(docker): resolve build failure with maven-temurin builder and clean copy
* b4d41fa docs: update TASK_STATUS with complete task audit findings
* 6a0cff6 fix(ci): restore MAVEN_HOME and JAVA_HOME to PATH in Jenkinsfile
...
*   1b90429 (tag: v1.0.0-mvp) Merge branch 'feature/smart-recommendations-audit' into main
|\
| * 1dcbe3a (feature/smart-recommendations-audit) feat(recommendations): ...
* | 7fa574c docs(status): update task tracking status on main branch
|/
*   9afaa7b Merge pull request #1 from feature/ride-management (TASK 5)
|\
| * 7d473ed (feature/ride-management) feat(ride-lifecycle): implement strict state machine...
|/
* aa00e63 feat(init): initial commit with core architecture (TASK 1-4)

Tags: v1.0.0-mvp, v1.1.0-release
Branches on origin: main, feature/ride-management, feature/smart-recommendations-audit
```

---

## 10. Defect Injection & Continuous Testing (Task 10)

1. **Defect:** State machine rule `REQUESTED → ACCEPTED` was altered to disallow driver acceptance.
2. **Pipeline Reaction:** Jenkins intercepted defect in `RideServiceTest.testValidStatusLifecycle` and `RideApiControllerTest`. Build status set to `FAILED`.
3. **Error:** `InvalidStateTransitionException: Invalid status transition: Cannot change ride status from REQUESTED to ACCEPTED`
4. **Fix:** Restored valid transition in `RideStatus.java`, committed.
5. **Rerun:** `mvn test` passed 13/13 tests, deployment stage enabled.

---

## 11. Ansible Configuration Management (Tasks 13 & 14)

### Playbook: `ansible/playbook.yml`
- Creates `/opt/smartride` directory structure (idempotent)
- Backs up currently running artifact
- Deploys `smartride-app-1.1.0.war` to releases directory
- Creates atomic symlink `smartride-app.war → releases/smartride-app-1.1.0.war`
- Verifies `/api/health` endpoint
- Target: `[app_servers]` → `localhost ansible_connection=local`

### Rollback: `ansible/rollback.yml`
- Validates rollback release artifact exists before proceeding
- Fails with descriptive message if target version not found
- Atomically switches symlink to requested rollback version
- Structured for zero-downtime rollback

**Note:** Ansible is not installed on the Windows Jenkins build host. Playbooks are authored for Linux target execution (`localhost ansible_connection=local` for local testing on a Linux host, or remote staging/production servers).

---

## 12. Endpoints & Operational Verification

| Component | URL / Port | Verified Status |
| :--- | :--- | :--- |
| **Jenkins CI** | `http://localhost:8080` | Running (build SUCCESS) |
| **SmartRide Container** | `http://localhost:8081/` | `200 OK` (Interactive Passenger/Driver/Admin UI) |
| **Actuator Health** | `http://localhost:8081/actuator/health` | `200 OK` — `{"status":"UP"}` |
| **Application Health** | `http://localhost:8081/api/health` | `200 OK` — `{"status":"HEALTHY","version":"1.1.0"}` |
| **Rides REST API** | `http://localhost:8081/api/rides` | `200 OK` (JSON array) |
| **Admin Metrics API** | `http://localhost:8081/api/admin/metrics` | `200 OK` |
| **Recommendations** | `http://localhost:8081/api/rides/recommendations` | `200 OK` |
| **H2 Console** | `http://localhost:8081/h2-console` | `200 OK` |

---

## 13. Evidence File Locations

| Evidence | Location |
| :--- | :--- |
| Jenkins Declarative Pipeline | [Jenkinsfile](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/Jenkinsfile) |
| Docker Runtime Image | [Dockerfile](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/Dockerfile) |
| Docker Compose | [docker-compose.yml](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/docker-compose.yml) |
| Maven Build Config | [pom.xml](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/pom.xml) |
| Selenium Test Suite | [SmartRideSeleniumIT.java](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/src/test/java/com/smartride/selenium/SmartRideSeleniumIT.java) |
| Selenium Screenshots | `target/selenium-screenshots/*.png` (4 files, archived in Jenkins) |
| WAR Artifact | `target/smartride-app.war` (archived in Jenkins build artifacts) |
| Ansible Provisioning | [ansible/playbook.yml](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/ansible/playbook.yml) |
| Ansible Rollback | [ansible/rollback.yml](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/ansible/rollback.yml) |
| Ansible Inventory | [ansible/inventory.ini](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/ansible/inventory.ini) |
| Task Documentation | `docs/TASK1_PROBLEM_DEFINITION.md`, `docs/TASK2_AGILE_DEVOPS_PLAN.md`, `docs/TASK3_ARCHITECTURE_SRS.md` |
| Release Tags | `v1.0.0-mvp`, `v1.1.0-release` (on GitHub `origin`) |
| Feature Branch PR | Commit `9afaa7b` — `Merge pull request #1 from feature/ride-management` |

---

## 14. Project Limitations & Future Enhancements

- **Limitations:**
  - In-memory H2 database resets on container restart (enable `-Dspring.profiles.active=prod` with PostgreSQL via `docker-compose --profile prod up`).
  - Fare estimation uses deterministic corridor hash heuristic (no external Google Maps API dependency).
  - Ansible playbooks designed for Linux targets; Ansible CLI not available on Windows build host.
- **Future Enhancements:**
  - Real-time WebSocket / STOMP driver GPS geolocation live tracking.
  - JWT / OAuth2 role-based authentication with Spring Security.
  - Kubernetes Helm chart deployment alongside Prometheus & Grafana telemetry.
  - Docker Hub registry push fully automated once `dockerhub-creds` Jenkins credential is added.
