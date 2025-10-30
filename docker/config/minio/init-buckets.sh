#!/usr/bin/env bash
set -e

# === 从环境变量读取配置 ===
MINIO_ENDPOINT="http://${MINIO_HOST}:${MINIO_PORT}"
MINIO_ACCESS_KEY=${MINIO_ROOT_USER}
MINIO_SECRET_KEY=${MINIO_ROOT_PASSWORD}
MINIO_ALIAS=${MINIO_ALIAS:-local}

# === 定义需要创建的桶列表 ===
BUCKETS=(
  "tempo"
)

echo "🔗 Connecting to MinIO at: $MINIO_ENDPOINT"
echo "👤 Using access key: $MINIO_ACCESS_KEY"
echo "📦 Buckets to ensure: ${BUCKETS[*]}"
echo

# === 配置 mc 客户端别名 ===
mc alias set "$MINIO_ALIAS" "$MINIO_ENDPOINT" "$MINIO_ACCESS_KEY" "$MINIO_SECRET_KEY" >/dev/null

# === 检查并创建桶 ===
for bucket in "${BUCKETS[@]}"; do
  if mc ls "$MINIO_ALIAS/$bucket" >/dev/null 2>&1; then
    echo "✅ Bucket '$bucket' already exists."
  else
    echo "🪣 Creating bucket '$bucket'..."
    mc mb "$MINIO_ALIAS/$bucket"
  fi
done

echo
echo "🎉 All buckets are ready."
