#!/bin/bash
set -e

# 加锁，防止并发执行
LOCK_FILE="/tmp/sol-cloud-deploy.lock"
exec 200>"$LOCK_FILE"
flock -n 200 || { echo "$(date) 🔒 已有实例在运行，退出"; exit 1; }

# 获取当前 commit hash
OLD_HASH=$(git rev-parse HEAD)

# 拉取最新代码
echo "$(date) 🔄 开始拉取最新代码..."
git pull origin main

# 获取拉取后的 commit hash
NEW_HASH=$(git rev-parse HEAD)

# 判断是否有变更
if [ "$OLD_HASH" = "$NEW_HASH" ]; then
  echo "$(date) ✅ 没有新提交，跳过 build 和 run"
  exit 0
fi

echo "$(date) 🚀 检测到新提交，准备执行 build 和 run"

# 执行构建脚本
if [ -f "./build.sh" ]; then
  echo "$(date) 🔧 执行 build.sh"
  bash ./build.sh
else
  echo "$(date) ⚠️ 未找到 build.sh"
  exit 1
fi

# 执行部署脚本
if [ -f "./run.sh" ]; then
  echo "$(date) 🚀 执行 run.sh"
  bash ./run.sh
else
  echo "$(date) ⚠️ 找不到可执行的 run.sh"
  exit 1
fi

echo "$(date) ✅ 部署完成"
