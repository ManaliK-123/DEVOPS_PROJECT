pipeline {
    agent any

    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['staging', 'production'], description: 'Target Deployment Environment')
        string(name: 'APP_VERSION', defaultValue: '1.1.0', description: 'Application Version Tag')
        booleanParam(name: 'RUN_SELENIUM_TESTS', defaultValue: true, description: 'Run Selenium End-to-End Suite')
    }

    environment {
        JAVA_HOME = tool name: 'JDK17', type: 'jdk'
        MAVEN_HOME = tool name: 'Maven3', type: 'maven'
        PATH = "${env.MAVEN_HOME}/bin:${env.JAVA_HOME}/bin:${env.PATH}"
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
                sh 'java -version || true'
                sh 'mvn -version || true'
            }
        }

        stage('Maven Build & Unit Tests') {
            steps {
                echo "Compiling code and executing JUnit unit & integration tests..."
                sh './mvnw clean test'
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
                sh './mvnw test -Dtest=SmartRideSeleniumIT'
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
                sh './mvnw package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.war', fingerprint: true
                }
            }
        }

        stage('Docker Build & Image Tagging') {
            steps {
                echo "Building Docker container image: ${env.IMAGE_NAME}..."
                sh "docker build -t ${env.IMAGE_NAME} -t smartride-app:latest ."
            }
        }

        stage('Continuous Deployment & Health Probe') {
            steps {
                echo "Deploying container to ${params.DEPLOY_ENV}..."
                sh """
                    docker stop smartride-container || true
                    docker rm smartride-container || true
                    docker run -d -p 8080:8080 --name smartride-container ${env.IMAGE_NAME}
                """
                echo "Verifying deployment health at http://localhost:8080/actuator/health..."
                sh """
                    sleep 10
                    curl -f http://localhost:8080/actuator/health || curl -f http://localhost:8080/api/health
                """
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
