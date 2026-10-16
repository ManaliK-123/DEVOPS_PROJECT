pipeline {
    agent any

    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['staging', 'production'], description: 'Target Deployment Environment')
        string(name: 'APP_VERSION', defaultValue: '1.1.0', description: 'Application Version Tag')
        booleanParam(name: 'RUN_SELENIUM_TESTS', defaultValue: true, description: 'Run Selenium End-to-End Suite')
    }

    environment {
        JAVA_HOME = tool name: 'JDK21', type: 'jdk'
        MAVEN_HOME = tool name: 'Maven3', type: 'maven'
        PATH = "${env.MAVEN_HOME}\\bin;${env.JAVA_HOME}\\bin;${env.PATH}"
        DOCKER_REGISTRY = 'docker.io'
        IMAGE_NAME = "smartride-app:${params.APP_VERSION}"
        SERVER_PORT = '8080'
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
    }

    stages {
        stage('SCM & Environment Inspection') {
            steps {
                echo "=========================================="
                echo "Starting SmartRide CI/CD Pipeline Build"
                echo "Commit: ${env.GIT_COMMIT}"
                echo "Branch: ${env.GIT_BRANCH}"
                echo "Target Environment: ${params.DEPLOY_ENV}"
                echo "=========================================="
                bat 'java -version'
                bat 'mvn -version'
            }
        }

        stage('Maven Build & Unit Tests') {
            steps {
                echo "Compiling code and executing JUnit unit & integration tests..."
                bat 'mvnw.cmd clean test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Selenium UI Continuous Testing') {
            when {
                expression { return params.RUN_SELENIUM_TESTS }
            }
            steps {
                echo "Executing automated headless Selenium browser tests..."
                bat 'mvnw.cmd test -Dtest=SmartRideSeleniumIT'
            }
            post {
                always {
                    publishHTML(target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/selenium-screenshots',
                        reportFiles: '*.png',
                        reportName: 'Selenium Test Screenshots'
                    ])
                }
                failure {
                    echo "Selenium test stage failed! Deployment blocked."
                }
            }
        }

        stage('Package Application Artifact') {
            steps {
                echo "Packaging executable WAR artifact..."
                bat 'mvnw.cmd package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.war', fingerprint: true
                }
            }
        }

        stage('Docker Build & Image Tagging') {
            steps {
                echo "Checking Docker availability and building image: ${env.IMAGE_NAME}..."
                powershell '''
                    $dockerCheck = Get-Command docker -ErrorAction SilentlyContinue
                    if (-not $dockerCheck) {
                        Write-Error "Docker CLI is not found on PATH. Docker build cannot proceed."
                        exit 1
                    }
                    docker info 2>&1 | Out-Null
                    if ($LASTEXITCODE -ne 0) {
                        Write-Error "Docker daemon is not running or accessible. Start Docker Desktop/Daemon to proceed with Docker build."
                        exit 1
                    }
                    docker build -t ${env.IMAGE_NAME} -t smartride-app:latest .
                    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
                '''
            }
        }

        stage('Continuous Deployment & Health Probe') {
            steps {
                echo "Deploying container to ${params.DEPLOY_ENV}..."
                powershell '''
                    docker stop smartride-container 2>$null
                    docker rm smartride-container 2>$null
                    docker run -d -p 8080:8080 --name smartride-container ${env.IMAGE_NAME}
                    if ($LASTEXITCODE -ne 0) {
                        Write-Error "Failed to start container smartride-container"
                        exit $LASTEXITCODE
                    }
                '''
                echo "Verifying deployment health at http://localhost:8080/actuator/health..."
                powershell '''
                    Start-Sleep -Seconds 10
                    $endpoints = @("http://localhost:8080/actuator/health", "http://localhost:8080/api/health")
                    $healthy = $false
                    foreach ($ep in $endpoints) {
                        try {
                            $res = Invoke-WebRequest -Uri $ep -UseBasicParsing -TimeoutSec 5
                            if ($res.StatusCode -eq 200) {
                                Write-Host "Health check succeeded on $ep"
                                $healthy = $true
                                break
                            }
                        } catch {
                            Write-Host "Endpoint $ep not ready yet..."
                        }
                    }
                    if (-not $healthy) {
                        Write-Error "Health check failed on all endpoints."
                        exit 1
                    }
                '''
            }
        }
    }

    post {
        success {
            echo "Pipeline completed successfully! Application live at http://localhost:8080"
        }
        failure {
            echo "Pipeline failed. Review stage logs and test failures above."
        }
    }
}
