pipeline {
    agent any

    stages {
        stage("Build Docker Image - flight-advisor") {
            steps {
                echo "Building Image - flight-advisor"
                sh "docker image build -f docker/backend.dockerfile -t flight-advisor ."
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

       stage("Build Docker Image - neo4j") {
           steps {
               echo "Building Image - neo4j"
               sh "docker image build -f docker/neo4j.dockerfile -t neo4j ."
               echo "Tagging Image - neo4j"
               sh "docker tag neo4j:latest 864313221502.dkr.ecr.eu-central-1.amazonaws.com/neo4j:latest"
           }
       }

       stage("Login To AWS ECR - neo4") {
           steps {
               script {
                   sh "aws ecr get-login-password --region eu-central-1 | docker login --username AWS --password-stdin 864313221502.dkr.ecr.eu-central-1.amazonaws.com/neo4j"
               }
           }
       }

       stage("Push Docker Image To AWS ECR - neo4j") {
           steps {
               script {
                   sh "docker push 864313221502.dkr.ecr.eu-central-1.amazonaws.com/neo4j:latest"
               }
           }
       }
    }
}