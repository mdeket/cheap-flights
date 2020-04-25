pipeline {
    agent any

    stages {
        stage("Build Docker Image - flight-advisor") {
            steps {
                echo "Building Image - flight-advisor"
                sh "docker image build -f Dockerfile -t flight-advisor ."
                echo "Tagging Image - flight-advisor"
                sh "docker tag flight-advisor:latest 864313221502.dkr.ecr.eu-central-1.amazonaws.com/flight-advisor:latest"
            }
        }

        stage("Login To AWS ECR - flight-advisor") {
            steps {
                script {
                    sh "aws ecr get-login-password --region eu-central-1 | docker login --username AWS --password-stdin 864313221502.dkr.ecr.eu-central-1.amazonaws.com/flight-advisor"
                }
            }
        }

        stage("Push Docker Image To AWS ECR - flight-advisor") {
            steps {
                script {
                    sh "docker push 864313221502.dkr.ecr.eu-central-1.amazonaws.com/flight-advisor:latest"
                }
            }
        }
    }
}