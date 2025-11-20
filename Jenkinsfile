pipeline {
    agent any

    tools {
        maven 'maven'
        jdk 'jdk21'
    }

    environment {
        DOCKER_IMAGE = 'vladimirchavkin/spring-todo'  // Замените на ваш Docker Hub repo
        DOCKER_TAG = "${env.BUILD_NUMBER}"         // Тег — номер build'а
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm  // Получаем код из Git
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests'  // Проверка сборки без тестов
                echo 'Сборка прошла успешно!'
            }
        }

        stage('Test') {  // Опционально: прогон тестов
            when {
                expression { return true }  // Включите для всех, или only for PRs: branch pattern 'PR-*'
            }
            steps {
                sh 'mvn test'  // Если тесты сломаются, pipeline fail
                echo 'Тесты прошли успешно!'
            }
        }

        stage('Build and Push Docker') {
            when {
                branch 'master'  // Только для master
            }
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}", ".")  // Собираем image
                    withDockerRegistry([credentialsId: 'docker-hub-credentials', url: 'https://index.docker.io/v1/']) {
                        dockerImage.push()  // Пушим в Docker Hub
                        dockerImage.push('latest')  // Опционально: тег latest
                    }
                }
                echo 'Docker image собран и загружен в Docker Hub!'
            }
        }
    }

    post {
        always {
            echo 'Pipeline завершен.'
        }
        failure {
            echo 'Pipeline провалился! Проверьте логи.'
        }
    }
}