#!/bin/bash

echo "🚀 Starting deployment..."

# 1. 이미지 빌드
echo "📦 Building Docker image..."
docker build -t petory-app .

# 2. ECR 로그인
echo "🔑 Logging into ECR..."
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 426775245898.dkr.ecr.ap-northeast-2.amazonaws.com

# 3. 이미지 태그 & 푸시
echo "📤 Pushing to ECR..."
docker tag petory-app:latest 426775245898.dkr.ecr.ap-northeast-2.amazonaws.com/petory-app:latest
docker push 426775245898.dkr.ecr.ap-northeast-2.amazonaws.com/petory-app:latest

# 4. EC2에서 배포
echo "🔄 Deploying to EC2..."
ssh -i "/c/Users/dtj06/OneDrive/바탕 화면/AWS/petory-keypair.pem" ubuntu@43.201.241.224 "
    echo 'AWS CLI ECR 로그인...'
    aws ecr get-login-password --region ap-northeast-2 | sudo docker login --username AWS --password-stdin 426775245898.dkr.ecr.ap-northeast-2.amazonaws.com
    
    echo 'Pulling latest image...'
    sudo docker pull 426775245898.dkr.ecr.ap-northeast-2.amazonaws.com/petory-app:latest
    
    echo 'Stopping current container...'
    sudo docker stop petory-app || true
    sudo docker rm petory-app || true
    
    echo 'Starting new container...'
    sudo docker run -d --name petory-app -p 8080:8080 --env-file .env 426775245898.dkr.ecr.ap-northeast-2.amazonaws.com/petory-app:latest
    
    echo 'Deployment completed!'
"

echo "✅ Deployment finished!"
