pipeline {
    agent any

    environment {
        // Set any environment variables here, if needed
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Run Unit Tests') {
            steps {
                script {
                    // Run unit tests using Maven
                    sh 'mvn clean test'
                }
            }
        }

        stage('Build JAR') {
            steps {
                script {
                    // Build the JAR file
                    sh 'mvn clean package'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Build Docker image
                    sh 'docker build -t xperience-server .'
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                script {
                    // Stop any running containers
                    sh 'docker ps -q --filter "name=xperience-server" | xargs --no-run-if-empty docker stop'

                    // Run the Docker container
                    sh 'docker run -d --name xperience-server -p 8080:8080 xperience-server'
                }
            }
        }
    }

    post {
        always {
            echo 'Cleaning up...'
            // Clean up Docker containers after the build
            sh 'docker rm -f xperience-server || true'
        }

        success {
            echo 'Pipeline succeeded!'
        }

        failure {
            echo 'Pipeline failed. Check logs.'
        }
    }
}
