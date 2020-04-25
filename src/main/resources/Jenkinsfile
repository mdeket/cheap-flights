pipeline {
    agent any

    stages {
        stage("Build Docker Image - flight-advisor") {
            steps {
                echo "Building Image - flight-advisor"
                sh "docker image build -f flight-advisor/Dockerfile -t flight-advisor ."
                echo "Tagging Image - flight-advisor"
                sh "docker tag flight-advisor:latest 864313221502.dkr.ecr.eu-central-1.amazonaws.com/flight-advisor:latest"
            }
        }
        stage("Push Docker Image To AWS ECR - flight-advisor") {
            steps {
                script {
                    docker.withRegistry("https://${AWS_ECR_REPO_URL}", "ecr:eu-central-1:${env.profile}-aws-ecr") {
                        sh "docker push 864313221502.dkr.ecr.eu-central-1.amazonaws.com/flight-advisor:latest"
                    }
                }
            }
        }
    }
}