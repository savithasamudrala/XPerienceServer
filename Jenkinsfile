pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'xperienceserver:latest'
        MAVEN_HOME = '/usr/share/maven'  // Replace this with the correct Maven install path
    }

    stages {
        stage('Checkout') {
            steps {
                // Pull the latest code from GitHub
                git branch: 'main', credentialsId: 'GitHub-Credentials', url: 'git@github.com:savithasamudrala/XPerienceServer.git'
            }
        }

        stage('Run Unit Tests') {
            steps {
                // Run unit tests using Maven
                sh "'${MAVEN_HOME}/bin/mvn' clean test"
            }
        }

        stage('Build JAR') {
            steps {
                // Build the executable JAR file using Maven
                sh "'${MAVEN_HOME}/bin/mvn' clean package"
            }
        }

        stage('Build Docker Image') {
            steps {
                // Build Docker image from the Dockerfile
                sh 'docker build -t ${DOCKER_IMAGE} .'
            }
        }

        stage('Run Docker Image') {
            steps {
                // Run Docker container, replacing any previously deployed container
                sh 'docker rm -f xperienceserver || true'
                sh 'docker run -d --name xperienceserver -p 8080:8080 ${DOCKER_IMAGE}'
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
