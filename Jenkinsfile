pipeline {
    agent any

    environment {
        MAVEN_HOME = tool 'Maven 3.8.7'
        DOCKER_IMAGE_NAME = 'xperienceserver'
        DOCKER_IMAGE_TAG = 'latest'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    // Checkout the code using SSH key for Git authentication
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: '*/main']],  // or specify the branch you want
                        userRemoteConfigs: [
                            [
                                url: 'git@github.com:savithasamudrala/XPerienceServer.git',
                                credentialsId: '2f12f77f-8857-451c-bef5-80c7504f294a'
                            ]
                        ]
                    ])
                }
            }
        }

        stage('Run Unit Tests') {
            steps {
                script {
                    // Run Maven to execute unit tests
                    sh "'${MAVEN_HOME}/bin/mvn' clean test"
                }
            }
        }

        stage('Build JAR') {
            steps {
                script {
                    // Build the JAR file using Maven
                    sh "'${MAVEN_HOME}/bin/mvn' clean package"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Build Docker image using the Dockerfile
                    sh "docker build -t ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ."
                }
            }
        }

        stage('Run Docker Image') {
            steps {
                script {
                    // Stop any running container and run a new container from the built image
                    sh "docker stop ${DOCKER_IMAGE_NAME} || true"
                    sh "docker rm ${DOCKER_IMAGE_NAME} || true"
                    sh "docker run -d --name ${DOCKER_IMAGE_NAME} -p 5000:5000 ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}"
                }
            }
        }
    }

    post {
        always {
            echo 'Cleaning up...'
            // Clean up resources if needed
        }
        success {
            echo 'Pipeline executed successfully.'
        }
        failure {
            echo 'Pipeline execution failed.'
        }
    }
}
