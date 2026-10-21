pipeline {
    agent any

    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['staging', 'production'], description: 'Target Deployment Environment')
        string(name: 'APP_VERSION', defaultValue: '1.1.0', description: 'Application Version Tag')
        booleanParam(name: 'RUN_SELENIUM_TESTS', defaultValue: true, description: 'Run Selenium End-to-End Suite')
        string(name: 'DOCKERHUB_NAMESPACE', defaultValue: 'manalik123', description: 'Docker Hub username / organisation for registry push')
        booleanParam(name: 'PUSH_TO_REGISTRY', defaultValue: true, description: 'Push built image to Docker Hub registry (requires dockerhub-creds credential in Jenkins)')
    }

    environment {
        JAVA_HOME = tool name: 'JDK21', type: 'jdk'
        MAVEN_HOME = tool name: 'Maven3', type: 'maven'
        PATH = "${env.MAVEN_HOME}\\bin;${env.JAVA_HOME}\\bin;${env.PATH}"
        DOCKER_REGISTRY = 'docker.io'
        IMAGE_NAME = "smartride-app:${params.APP_VERSION}"
        SERVER_PORT = '8080'
        // Connect directly to Docker Desktop's Linux engine named pipe.
        // This allows Jenkins (running as LocalSystem) to reach Docker Desktop
        // without relying on the per-user 'desktop-linux' CLI context.
        DOCKER_HOST = 'npipe:////./pipe/dockerDesktopLinuxEngine'
        // Clear any inherited DOCKER_CONTEXT so it cannot override DOCKER_HOST.
        DOCKER_CONTEXT = ''
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
                bat 'mvn clean test'
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Selenium UI Continuous Testing') {
            when {
                expression { return params.RUN_SELENIUM_TESTS }
            }
            steps {
                echo "Executing automated headless Selenium browser tests..."
                bat 'mvn test -Dtest=SmartRideSeleniumIT'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'target/selenium-screenshots/*.png', allowEmptyArchive: true, fingerprint: true
                }
                failure {
                    echo "Selenium test stage failed! Deployment blocked."
                }
            }
        }

        stage('Package Application Artifact') {
            steps {
                echo "Packaging executable WAR artifact..."
                bat 'mvn package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.war', fingerprint: true
                }
            }
        }

        stage('Docker Build & Image Tagging') {
            options {
                timeout(time: 5, unit: 'MINUTES')
            }
            steps {
                echo "Checking Docker availability and building image: ${env.IMAGE_NAME}..."
                powershell '''
                    $knownPaths = @(
                        "C:\\Users\\hp\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin",
                        "$env:LOCALAPPDATA\\Programs\\DockerDesktop\\resources\\bin",
                        "C:\\Program Files\\Docker\\Docker\\resources\\bin",
                        "C:\\ProgramData\\DockerDesktop\\version-bin"
                    )
                    foreach ($dir in $knownPaths) {
                        if (Test-Path "$dir\\docker.exe") {
                            if ($env:PATH -notlike "*$dir*") {
                                $env:PATH = "$dir;$env:PATH"
                            }
                        }
                    }

                    $dockerCheck = Get-Command docker -ErrorAction SilentlyContinue
                    if (-not $dockerCheck) {
                        Write-Error "Docker CLI is not found on PATH. Docker build cannot proceed."
                        exit 1
                    }

                    Write-Host "=== Docker CLI Version ==="
                    docker --version
                    if ($LASTEXITCODE -ne 0) {
                        Write-Error "Failed to execute docker --version (exit code: $LASTEXITCODE)."
                        exit $LASTEXITCODE
                    }

                    Write-Host "=== Docker System Info ==="
                    docker info
                    if ($LASTEXITCODE -ne 0) {
                        Write-Error "Docker daemon is not running or accessible. Start Docker Desktop/Daemon to proceed with Docker build (exit code: $LASTEXITCODE)."
                        exit $LASTEXITCODE
                    }

                    Write-Host "=== Building Docker Image ==="
                    docker build --progress=plain --pull=false -t "$env:IMAGE_NAME" -t "smartride-app:latest" .
                    if ($LASTEXITCODE -ne 0) {
                        Write-Error "Docker build failed with exit code $LASTEXITCODE."
                        exit $LASTEXITCODE
                    }

                    Write-Host "=== Verifying Docker Image ==="
                    docker image inspect "$env:IMAGE_NAME" | Out-Null
                    if ($LASTEXITCODE -ne 0) {
                        Write-Error "Docker image inspect failed: Image '$env:IMAGE_NAME' not found."
                        exit $LASTEXITCODE
                    }
                    Write-Host "Successfully built and verified image $env:IMAGE_NAME"
                '''
            }
        }

        stage('Docker Hub Registry Publish') {
            when {
                expression { return params.PUSH_TO_REGISTRY }
            }
            options {
                timeout(time: 5, unit: 'MINUTES')
            }
            steps {
                echo "Publishing image to Docker Hub as ${params.DOCKERHUB_NAMESPACE}/smartride-app..."
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_TOKEN'
                )]) {
                    powershell '''
                        $knownPaths = @(
                            "C:\\Users\\hp\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin",
                            "$env:LOCALAPPDATA\\Programs\\DockerDesktop\\resources\\bin",
                            "C:\\Program Files\\Docker\\Docker\\resources\\bin",
                            "C:\\ProgramData\\DockerDesktop\\version-bin"
                        )
                        foreach ($dir in $knownPaths) {
                            if (Test-Path "$dir\\docker.exe") {
                                if ($env:PATH -notlike "*$dir*") { $env:PATH = "$dir;$env:PATH" }
                            }
                        }

                        Write-Host "=== Authenticating with Docker Hub ==="
                        $env:DOCKER_TOKEN | docker login --username "$env:DOCKER_USER" --password-stdin
                        if ($LASTEXITCODE -ne 0) {
                            Write-Error "Docker Hub login FAILED (exit code: $LASTEXITCODE). Check dockerhub-creds credential in Jenkins."
                            exit $LASTEXITCODE
                        }

                        $remoteVersion = "$env:DOCKERHUB_NAMESPACE/smartride-app:$env:APP_VERSION"
                        $remoteLatest  = "$env:DOCKERHUB_NAMESPACE/smartride-app:latest"

                        Write-Host "=== Tagging local image for registry ==="
                        docker tag "$env:IMAGE_NAME" "$remoteVersion"
                        if ($LASTEXITCODE -ne 0) { docker logout; exit $LASTEXITCODE }
                        docker tag "smartride-app:latest" "$remoteLatest"
                        if ($LASTEXITCODE -ne 0) { docker logout; exit $LASTEXITCODE }

                        Write-Host "=== Pushing $remoteVersion ==="
                        docker push "$remoteVersion"
                        if ($LASTEXITCODE -ne 0) {
                            Write-Error "Push of $remoteVersion FAILED (exit code: $LASTEXITCODE)."
                            docker logout
                            exit $LASTEXITCODE
                        }

                        Write-Host "=== Pushing $remoteLatest ==="
                        docker push "$remoteLatest"
                        if ($LASTEXITCODE -ne 0) {
                            Write-Error "Push of $remoteLatest FAILED (exit code: $LASTEXITCODE)."
                            docker logout
                            exit $LASTEXITCODE
                        }

                        Write-Host "=== Verifying remote manifest ==="
                        docker manifest inspect "$remoteVersion" | Out-Null
                        if ($LASTEXITCODE -ne 0) {
                            Write-Error "Remote manifest inspect FAILED for $remoteVersion — push may not have completed."
                            docker logout
                            exit $LASTEXITCODE
                        }

                        Write-Host "Successfully published $remoteVersion and $remoteLatest to Docker Hub."
                        docker logout
                    '''
                }
            }
        }

        stage('Continuous Deployment & Health Probe') {
            steps {
                echo "Deploying container to ${params.DEPLOY_ENV}..."
                powershell '''
                    $knownPaths = @(
                        "C:\\Users\\hp\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin",
                        "$env:LOCALAPPDATA\\Programs\\DockerDesktop\\resources\\bin",
                        "C:\\Program Files\\Docker\\Docker\\resources\\bin",
                        "C:\\ProgramData\\DockerDesktop\\version-bin"
                    )
                    foreach ($dir in $knownPaths) {
                        if (Test-Path "$dir\\docker.exe") {
                            if ($env:PATH -notlike "*$dir*") {
                                $env:PATH = "$dir;$env:PATH"
                            }
                        }
                    }

                    $existing = docker ps -a -q -f "name=^smartride-container$"
                    if ($existing) {
                        Write-Host "Stopping and removing existing container smartride-container..."
                        docker stop smartride-container
                        docker rm smartride-container
                    }

                    Write-Host "Starting container smartride-container on port 8081:8080..."
                    docker run -d -p 8081:8080 --name smartride-container "$env:IMAGE_NAME"
                    if ($LASTEXITCODE -ne 0) {
                        Write-Error "Failed to start container smartride-container (exit code: $LASTEXITCODE)"
                        exit $LASTEXITCODE
                    }
                '''
                echo "Verifying deployment health at http://localhost:8081/actuator/health..."
                powershell '''
                    Start-Sleep -Seconds 10
                    $endpoints = @("http://localhost:8081/actuator/health", "http://localhost:8081/api/health")
                    $healthy = $false
                    for ($i = 0; $i -lt 6; $i++) {
                        foreach ($ep in $endpoints) {
                            try {
                                $res = Invoke-WebRequest -Uri $ep -UseBasicParsing -TimeoutSec 5
                                if ($res.StatusCode -eq 200) {
                                    Write-Host "Health check succeeded on $ep"
                                    $healthy = $true
                                    break
                                }
                            } catch {
                                Write-Host "Endpoint $ep not ready yet (attempt $($i+1))..."
                            }
                        }
                        if ($healthy) { break }
                        Start-Sleep -Seconds 5
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
            echo "Pipeline completed successfully! Application live at http://localhost:8081"
        }
        failure {
            echo "Pipeline failed. Review stage logs and test failures above."
        }
    }
}
