#!/bin/bash

set -e

# 后端打包
echo "正在打包后端..."
cd cloud
export MAVEN_OPTS="--add-opens=java.base/java.lang=ALL-UNNAMED"
mvn clean package -DskipTests
cd ..

# 复制后端构建结果
echo "准备复制后端构建结果..."
SERVER_OUTPUT_DIR=docker/output/server
rm -rf $SERVER_OUTPUT_DIR

mkdir -p $SERVER_OUTPUT_DIR/gateway-9527
cp cloud/gateway-9527/target/*.jar $SERVER_OUTPUT_DIR/gateway-9527/app.jar

mkdir -p $SERVER_OUTPUT_DIR/web-system-8081/classes
rsync -av --exclude='com/bgasol/**' cloud/web/web-system-8081/target/classes/ $SERVER_OUTPUT_DIR/web-system-8081/classes/
cp cloud/web/web-system-8081/target/*.jar $SERVER_OUTPUT_DIR/web-system-8081/app.jar

mkdir -p $SERVER_OUTPUT_DIR/web-file-8082/classes
rsync -av --exclude='com/bgasol/**' cloud/web/web-file-8082/target/classes/ $SERVER_OUTPUT_DIR/web-file-8082/classes/
cp cloud/web/web-file-8082/target/*.jar $SERVER_OUTPUT_DIR/web-file-8082/app.jar

# 前端打包
echo "正在打包前端..."
cd client
npm ci
npm run build
cd ..

# 复制前端构建结果
echo "准备复制前端构建结果..."
FRONTEND_OUTPUT_DIR=docker/output/client
rm -rf $FRONTEND_OUTPUT_DIR
mkdir -p $FRONTEND_OUTPUT_DIR

cp -r client/dist/* $FRONTEND_OUTPUT_DIR/

echo "构建完成，输出目录："
echo "后端：$SERVER_OUTPUT_DIR"
echo "前端：$FRONTEND_OUTPUT_DIR"
