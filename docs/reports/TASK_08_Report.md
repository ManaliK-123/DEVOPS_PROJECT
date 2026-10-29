# Task 08 Report: Jenkins Pipeline and Deployment Parameters

## 1. Task Number and Title
**Task 08 — Jenkins Pipeline and Deployment Parameters**

---

## 2. Objective
Author and version a declarative `Jenkinsfile` (Pipeline as Code) with parameterized execution options (`DEPLOY_ENV`, `RUN_SELENIUM_TESTS`, `PUSH_TO_REGISTRY`), automate packaging into an executable Spring Boot WAR archive, archive build artifacts, and establish post-build test recording.

---

## 3. Tools and Technologies
- **Pipeline as Code:** Declarative Jenkinsfile (Groovy DSL)
- **Archive Packaging:** Maven WAR Plugin, Spring Boot Maven Plugin
- **Artifact Archiving:** Jenkins `archiveArtifacts` and `junit` post-build steps

---

## 4. Implementation and Procedure
1. **Authored Declarative Pipeline as Code:**
   - Authored [Jenkinsfile](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/Jenkinsfile) at repository root.
   - Defined parameters block:
     - `DEPLOY_ENV`: Choice parameter (`staging`, `production`).
     - `RUN_SELENIUM_TESTS`: Boolean flag (default `true`).
     - `PUSH_TO_REGISTRY`: Boolean flag (default `false`).
2. **Integrated Sequential Pipeline Stages:**
   - Stage 1: `SCM & Environment Inspection` (logs git commit, tools, environment variables).
   - Stage 2: `Maven Build & Unit Tests` (compiles and runs unit test suite).
   - Stage 3: `Selenium UI Continuous Testing` (runs headless browser tests).
   - Stage 4: `Package Application Artifact` (`mvn package -DskipTests`).
   - Stage 5: `Docker Build & Image Tagging` (builds `smartride-app:1.1.0`).
   - Stage 6: `Docker Hub Registry Publish` (conditional stage with credentials).
   - Stage 7: `Continuous Deployment & Health Probe` (spins up container and polls `/actuator/health`).
3. **Configured Artifact Archival & Post Actions:**
   - Configured `archiveArtifacts 'target/*.war'` to preserve `smartride-app.war`.
   - Configured `junit '**/target/surefire-reports/*.xml'` to publish test metrics.
   - Configured `archiveArtifacts 'target/selenium-screenshots/*.png'` to save UI test evidence.

---

## 5. Commands and Configuration
- **Jenkinsfile Configuration Excerpt:**
  ```groovy
  parameters {
      choice(name: 'DEPLOY_ENV', choices: ['staging', 'production'], description: 'Deployment environment target')
      booleanParam(name: 'RUN_SELENIUM_TESTS', defaultValue: true, description: 'Execute Selenium WebDriver UI tests')
      booleanParam(name: 'PUSH_TO_REGISTRY', defaultValue: false, description: 'Push built image to Docker Hub')
  }
  stages {
      stage('Package Application Artifact') {
          steps {
              bat 'mvn package -DskipTests'
          }
      }
  }
  post {
      always {
          junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
          archiveArtifacts allowEmptyArchive: true, artifacts: 'target/*.war, target/selenium-screenshots/*.png'
      }
  }
  ```
- **Local WAR Packaging Verification:**
  ```powershell
  mvn package -DskipTests
  Get-Item target\smartride-app.war
  ```

---

## 6. Verification and Test Results
- **WAR Packaging Success:** `smartride-app.war` built successfully (size: 53,682,519 bytes / ~51.2 MB).
- **Repackaging:** Spring Boot Maven plugin replaced main artifact with executable archive containing embedded Tomcat and all runtime dependencies.
- **Jenkins Archival Verified:** Jenkins build #16 archived `target/smartride-app.war` in `C:\ProgramData\Jenkins\.jenkins\jobs\SmartRide-CI-CD\builds\16\archive\target\smartride-app.war`.

---

## 7. Screenshots and Evidence

### Jenkins Build #16 Summary & Archived Artifacts
![Jenkins Build #16 Artifacts](screenshots/task-08/jenkins-build16-artifacts.png)
*Figure 8.1: Jenkins build #16 summary (21 Oct 2026, 15:51:45) showing build metadata, Git revision f3a97c9, zero test failures, and archived artifacts: 4 Selenium journey PNGs and smartride-app.war (51.20 MiB).*

### Pipeline Steps: Checkout & Tool Configuration
![Pipeline Steps: Checkout and Tool Configuration](screenshots/task-08/jenkins-checkout-tool-configuration.png)
*Figure 8.2: Jenkins Pipeline Steps displaying SCM checkout, environment initialization, and configured Maven3 and JDK21 global tools.*

### Pipeline Steps: Environment & Commit Inspection
![Pipeline Steps: Environment Inspection](screenshots/task-08/jenkins-environment-inspection.png)
*Figure 8.3: Jenkins SCM & Environment Inspection stage output showing commit f3a97c916dd31a52c61afeeaebf6ba9e28268f3, branch origin/main, target environment staging, and java -version execution.*

### Stages View: SCM & Environment Inspection
![Stages View: SCM & Environment Inspection](screenshots/task-08/jenkins-scm-environment-stage.png)
*Figure 8.4: Detailed Stages view for SCM & Environment Inspection stage (2s) showing batch commands for java and mvn version verification.*

### Pre-existing Pipeline & Artifact Evidence
![Jenkinsfile Declarative Pipeline Stages and Parameters](screenshots/task-08/task-08-jenkinsfile.png)
*Figure 8.5: Declarative Jenkinsfile pipeline definition showing build parameters, sequential stages, and post actions.*

![Executable WAR Artifact Packaging and Archival](screenshots/task-08/task-08-war-artifact.png)
*Figure 8.6: Executable WAR archive packaging verification and Jenkins build #16 artifact archival confirmation.*

### Supplementary Cross-Stage Evidence
- **Pipeline Execution & Dashboard:** See [jenkins-job-dashboard.png](screenshots/task-07/jenkins-job-dashboard.png) and [jenkins-build16-console-checkout.png](screenshots/task-07/jenkins-build16-console-checkout.png) in Task 07 for SCM checkout and provenance.
- **Pipeline Testing & Packaging Steps:** See [jenkins-automated-tests-and-packaging.png](screenshots/task-09/jenkins-automated-tests-and-packaging.png) in Task 09 for test/package stage execution.
- **Full Pipeline Stages Success:** See [jenkins-build16-all-stages-success.png](screenshots/task-15/jenkins-build16-all-stages-success.png) in Task 15 for Jenkins build #16 completed on 21 October 2026.

---

## 8. Result and Completion Status
- **Status:** **Completed**
- **Summary:** Declarative `Jenkinsfile` authored, parameterized execution implemented, executable WAR packaged, and Jenkins artifact archival verified.

---

## 9. Relevant Source Files and Links
- [Jenkinsfile](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/Jenkinsfile)
- [pom.xml](file:///d:/DEVOPS_CMPN_PROJECT/DEVOPS_CMPN/pom.xml)
- [Jenkins Job SmartRide-CI-CD](http://localhost:8080/job/SmartRide-CI-CD/)
