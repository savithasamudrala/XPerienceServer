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
                    echo 'Checking out source code...'
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: '*/main']],
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

        stage('Build JAR (Skip Tests)') {
            steps {
                script {
                    echo 'Building JAR with tests skipped...'
                    sh "'${MAVEN_HOME}/bin/mvn' clean package -DskipTests"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    echo 'Building Docker image...'
                    sh "docker build -t ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ."
                }
            }
        }

        stage('Run Docker Image') {
            steps {
                script {
                    echo 'Running Docker container...'
                    sh """
                        docker stop '${DOCKER_IMAGE_NAME}' || true
                        docker rm '${DOCKER_IMAGE_NAME}' || true

                        docker run -d \\
                          --name '${DOCKER_IMAGE_NAME}' \\
                          -p 5000:5000 \\
                          -v \$(pwd)/passwords.txt:/app/passwords.txt \\
                          '${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}' \\
                          5000 /app/passwords.txt
                    """
                }
            }
        }
    }
}

