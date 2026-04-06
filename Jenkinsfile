pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                withCredentials([string(credentialsId: 'jwt-secret', variable: 'JWT_SECRET')]) {
                    sh '''
                        mvn clean verify \
                        -Dspring.datasource.url=jdbc:mysql://mysql:3306/mydatabase \
                        -Dspring.datasource.username=myuser \
                        -Dspring.datasource.password=password
                    '''
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withCredentials([string(credentialsId: 'jwt-secret', variable: 'JWT_SECRET')]) {
                    withSonarQubeEnv('SonarQube') {
                        sh '''
                            mvn sonar:sonar \
                            -Dspring.datasource.url=jdbc:mysql://mysql:3306/mydatabase \
                            -Dspring.datasource.username=myuser \
                            -Dspring.datasource.password=password \
                            -Dsonar.projectKey=backend \
                            -Dsonar.projectName=backend
                        '''
                    }
                }
            }
        }
    }
}