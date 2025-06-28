#!/bin/bash

echo "🚀 Starting deployment..."

# 1. 기존 이미지 정리 (캐시 방지)
echo "🧹 Cleaning up..."
docker system prune -f

# 2. 이미지 빌드 (캐시 없이)
echo "📦 Building Docker image..."
docker build --no-cache -t petory-app:latest .

# 3. ECR 로그인
echo "🔑 Logging into ECR..."
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 426775245898.dkr.ecr.ap-northeast-2.amazonaws.com

# 4. ECR에서 기존 이미지들 정리 (latest 제외한 모든 태그 삭제)
echo "🗑️ Cleaning up old ECR images..."
OLD_IMAGES=$(aws ecr list-images --repository-name petory-app --region ap-northeast-2 --filter tagStatus=TAGGED --query 'imageIds[?imageTag!=`latest`]' --output json)
if [ "$OLD_IMAGES" != "[]" ]; then
    echo "Deleting old tagged images..."
    aws ecr batch-delete-image --repository-name petory-app --region ap-northeast-2 --image-ids "$OLD_IMAGES" || true
fi

# 5. 이미지 푸시 (latest만)
echo "📤 Pushing to ECR..."
docker tag petory-app:latest 426775245898.dkr.ecr.ap-northeast-2.amazonaws.com/petory-app:latest
docker push 426775245898.dkr.ecr.ap-northeast-2.amazonaws.com/petory-app:latest

# 6. EC2 배포
echo "🔄 Deploying to EC2..."
ssh -i "/c/Users/dtj06/OneDrive/바탕 화면/AWS/petory-keypair.pem" ubuntu@43.201.241.224 "
    echo 'AWS CLI ECR 로그인...'
    aws ecr get-login-password --region ap-northeast-2 | sudo docker login --username AWS --password-stdin 426775245898.dkr.ecr.ap-northeast-2.amazonaws.com

    echo 'Stopping current container...'
    sudo docker stop petory-app || true
    sudo docker rm petory-app || true

    echo 'Removing old images...'
    sudo docker rmi 426775245898.dkr.ecr.ap-northeast-2.amazonaws.com/petory-app:latest || true

    echo 'Pulling latest image...'
    sudo docker pull 426775245898.dkr.ecr.ap-northeast-2.amazonaws.com/petory-app:latest

    echo 'Starting new container...'
    sudo docker run -d --name petory-app -p 8080:8080 --env-file .env 426775245898.dkr.ecr.ap-northeast-2.amazonaws.com/petory-app:latest

    echo 'Checking container status...'
    sudo docker ps | grep petory-app

    echo 'Deployment completed!'
"

echo "✅ Deployment finished!"
echo "🔍 Check health: curl http://43.201.241.224:8080/actuator/health"