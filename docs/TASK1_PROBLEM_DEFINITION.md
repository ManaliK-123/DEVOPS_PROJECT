# TASK 1: Problem Definition & Scope

## 1. Executive Summary & Problem Statement
Modern urban transportation systems suffer from inefficient fleet utilization, fragmented communication between passengers and drivers, unpredictable pricing, and lack of real-time visibility into ride status lifecycles. Traditional ride-hailing applications often present complex legacy architectures that are difficult to demonstrate, test, and deploy reliably across diverse environments.

The **Automated Ride-Sharing Demonstration Application (SmartRide)** provides an enterprise-grade, lightweight, and modern mobility platform that demonstrates the complete software engineering and DevOps lifecycle—from agile requirements and robust role-based workflows to automated continuous integration, end-to-end continuous testing, containerization, and configuration management.

---

## 2. Target Users & Stakeholders
| Stakeholder Group | Persona | Primary Goals & Responsibilities |
| :--- | :--- | :--- |
| **Passengers** | Urban Commuters & Corporate Travelers | Request rides with custom preferences (seats, vehicle type, budget), search available rides, track status in real-time, view fare estimates, and cancel if needed. |
| **Drivers** | Fleet Operators & Independent Drivers | Search and view requested rides in their operational zones, accept ride requests, update ride progress (`DRIVER_ASSIGNED` → `ONGOING` → `COMPLETED`), and track trip earnings. |
| **Fleet Administrators** | Operations & Dispatch Managers | Monitor platform metrics via dynamic dashboards, track active vs completed vs cancelled trips, manage vehicle utilization, and inspect ride audit timelines. |
| **DevOps & QA Engineers** | Platform Engineers & Automation Testers | Build, package (WAR/JAR), test automatically with Selenium/JUnit, deploy via Jenkins CI/CD pipelines, containerize with Docker, and configure nodes using Ansible. |

---

## 3. Pain Points Addressed
1. **Opaque Ride Progression:** Status transitions are frequently disconnected; SmartRide enforces a strict state machine (`REQUESTED` → `ACCEPTED` → `DRIVER_ASSIGNED` → `ONGOING` → `COMPLETED` / `CANCELLED`).
2. **Lack of Intelligent Ride Matching:** Users struggle to identify suitable shared rides; SmartRide implements a deterministic rule-based route and time compatibility matching engine.
3. **Audit Trail Deficits:** Operational disputes arise without clear history; SmartRide logs every status transition into an immutable Activity/Audit Timeline.
4. **DevOps & Reliability Gaps:** Applications often fail in staging/production due to configuration drift; SmartRide provides automated Jenkins CI/CD, Docker lifecycle, and idempotent Ansible automation.

---

## 4. Objectives & Measurable Success Criteria
- **Functional Completeness:** 100% working role-based journeys for Passenger, Driver, and Admin with zero placeholder actions.
- **Continuous Integration:** Automated build and unit/integration test suite passing with `mvn test` in under 30 seconds.
- **Continuous Testing:** Selenium WebDriver automated smoke and journey tests covering at least 4 critical paths with automated failure screenshots.
- **Container Portability:** Docker multi-stage build producing an optimized runtime container image exposing port 8080.
- **Idempotent Infrastructure:** Ansible playbooks capable of cleanly provisioning prerequisite packages, services, and running containers with 0 failure on consecutive runs.
- **Resilience & Recovery:** Proven automated rollback mechanism to previous stable builds within 15 seconds.

---

## 5. Frozen 15-Task MVP Scope
The project scope is explicitly partitioned into 15 sequentially verified tasks:
- **Tasks 1–3:** Specifications, Agile Backlog, DevOps Architecture, and Data Model.
- **Tasks 4–6:** Git/GitHub Initialization, Feature Branches, Merge Conflict Resolution, and Release Tagging.
- **Tasks 7–8:** Jenkins CI Maven Pipeline, Artifact Archival, and Parameterized Deployment.
- **Tasks 9–10:** Selenium Automated UI Testing, Jenkins Continuous Testing Gate, Defect Injection & Resolution.
- **Tasks 11–12:** Docker Containerization, Multi-Stage Builds, and Automated Image Deployment.
- **Tasks 13–14:** Ansible Configuration Management, Idempotent Provisioning, Health Check, and Rollback.
- **Task 15:** Integrated End-to-End Validation, Verification Matrix, and Evidence Dossier.
