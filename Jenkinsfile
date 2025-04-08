pipeline {
    agent any

    environment {
        // Define environment variables here
        MAVEN_HOME = '/usr/local/maven'          // Path to Maven
        DOCKER_IMAGE = 'xperience_server_image'  // Docker image name
        DOCKER_TAG = 'latest'                   // Docker image tag
        GIT_REPO = 'https://github.com/savithasamudrala/XPerienceServer.git'  // GitHub repository URL
        GIT_BRANCH = 'main'                     // Branch to checkout
    }

    tools {
        // Define Maven version installed on Jenkins
        maven 'Maven 3.6.3'
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout the code from GitHub
                git branch: "${env.GIT_BRANCH}", url: "${env.GIT_REPO}"
            }
        }

        stage('Build') {
            steps {
                // Run unit tests and build the executable JAR
                script {
                    echo "Running Maven build..."
                    sh "${MAVEN_HOME}/bin/mvn clean test package"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                // Build the Docker image
                script {
                    echo "Building Docker image..."
                    sh """
                    docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                    """
                }
            }
        }

        stage('Deploy Docker Image') {
            steps {
                // Run the Docker image, replacing any previously deployed container
                script {
                    echo "Deploying Docker image..."
                    sh """
                    docker rm -f ${DOCKER_IMAGE} || true
                    docker run -d --name ${DOCKER_IMAGE} ${DOCKER_IMAGE}:${DOCKER_TAG}
                    """
                }
            }
        }
    }

    post {
        // If the build fails, send an error message
        failure {
            echo "Build failed. Please check the logs."
        }
    }
}
