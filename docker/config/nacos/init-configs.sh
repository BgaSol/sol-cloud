#!/bin/sh
# Nacos配置自动导入脚本（容器内运行）
set -e

echo "========================================="
echo "Nacos配置自动导入"
echo "========================================="
echo "Nacos地址: ${NACOS_ADDR}"
echo "用户名: ${NACOS_USER}"
echo ""

## 等待Nacos完全启动
#echo "等待Nacos完全启动..."
#sleep 15

# 测试Nacos健康状态
MAX_RETRIES=10
RETRY_COUNT=0
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if wget -q -O- "${NACOS_ADDR}/nacos/v2/console/namespace/list" > /dev/null 2>&1; then
        echo "✓ Nacos健康检查通过"
        break
    fi
    RETRY_COUNT=$((RETRY_COUNT + 1))
    echo "等待Nacos启动... ($RETRY_COUNT/$MAX_RETRIES)"
    sleep 5
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    echo "✗ Nacos启动超时，跳过配置导入"
    exit 0
fi

# 登录获取Token（Nacos v2 API）
echo "正在登录Nacos..."
LOGIN_RESPONSE=$(wget -q -O- --post-data="username=${NACOS_USER}&password=${NACOS_PASSWORD}" \
  "${NACOS_ADDR}/nacos/v1/auth/login" 2>/dev/null || echo "")

# v1的登录API返回: {"accessToken":"xxx","tokenTtl":18000}
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4 || echo "")

if [ -z "$TOKEN" ]; then
    echo "⚠️  登录失败，尝试不使用认证继续..."
    echo "响应: $LOGIN_RESPONSE"
    # 不退出，尝试不带token继续
    TOKEN=""
else
    echo "✓ 登录成功，Token: ${TOKEN:0:20}..."
fi
echo ""

# URL编码函数（简化版，处理YAML特殊字符）
urlencode() {
    local string="$1"
    # 使用sed进行URL编码（保留换行符为%0A）
    echo "$string" | sed '
        s/%/%25/g
        s/ /%20/g
        s/!/%21/g
        s/"/%22/g
        s/#/%23/g
        s/\$/%24/g
        s/\&/%26/g
        s/'\''/%27/g
        s/(/%28/g
        s/)/%29/g
        s/\*/%2A/g
        s/+/%2B/g
        s/,/%2C/g
        s/:/%3A/g
        s/;/%3B/g
        s/=/%3D/g
        s/?/%3F/g
        s/@/%40/g
        s/\[/%5B/g
        s/\]/%5D/g
    ' | awk '{printf "%s%%0A", $0}' | sed 's/%0A$//'
}

# 导入配置函数（使用Nacos v2 API）
import_config() {
    local file_path=$1
    local data_id=$2
    local group=$3
    local desc=$4
    
    if [ ! -f "$file_path" ]; then
        echo "⚠ 文件不存在: ${file_path}"
        return
    fi
    
    echo "发布: ${data_id} → ${group}"
    
    # 读取配置内容并URL编码
    local content=$(cat "$file_path")
    local encoded_content=$(urlencode "$content")
    
    # 发布配置 (POST /nacos/v2/cs/config)
    # v2 API返回JSON格式: {"code": 0, "message": "success", "data": true}
    local response=$(wget -q -O- \
      --post-data="dataId=${data_id}&group=${group}&content=${encoded_content}&type=yaml&desc=${desc}&accessToken=${TOKEN}" \
      --header="Content-Type: application/x-www-form-urlencoded" \
      "${NACOS_ADDR}/nacos/v2/cs/config" 2>&1)
    
    # 解析JSON响应（简单的文本解析，不依赖jq）
    if echo "$response" | grep -q '"code":0'; then
        # code为0表示成功
        if echo "$response" | grep -q '"data":true'; then
            echo "  ✅ 发布成功"
        else
            echo "  ℹ️  配置已存在（已更新）"
        fi
    elif echo "$response" | grep -q '"code"'; then
        # 有code但不为0，提取错误信息
        local message=$(echo "$response" | grep -o '"message":"[^"]*"' | cut -d'"' -f4)
        echo "  ⚠️  失败: ${message:-未知错误}"
    else
        # 无法解析响应
        echo "  ⚠️  响应: $response"
    fi
}

# ==================== 导入共享配置 ====================
echo "========================================="
echo "导入共享配置 (SHARED_CONFIG)"
echo "========================================="

# 配置文件目录
YML_DIR="/home/nacos/plugins/yml"

# 检查目录是否存在
if [ ! -d "$YML_DIR" ]; then
    echo "⚠️  配置目录不存在: $YML_DIR"
    echo "请确保配置文件已放置在 docker/config/nacos/plugins/yml/ 目录下"
    exit 1
fi

# 导入所有共享配置（从 yml 目录）
import_config "${YML_DIR}/application-logger.yml" \
  "application-logger.yml" "SHARED_CONFIG" "日志配置"

import_config "${YML_DIR}/application-database.yml" \
  "application-database.yml" "SHARED_CONFIG" "数据库配置"

import_config "${YML_DIR}/application-redis.yml" \
  "application-redis.yml" "SHARED_CONFIG" "Redis配置"

import_config "${YML_DIR}/application-minio.yml" \
  "application-minio.yml" "SHARED_CONFIG" "MinIO配置"

import_config "${YML_DIR}/application-sa-token.yml" \
  "application-sa-token.yml" "SHARED_CONFIG" "认证配置"

import_config "${YML_DIR}/application-web.yml" \
  "application-web.yml" "SHARED_CONFIG" "Web配置"

import_config "${YML_DIR}/application-feign.yml" \
  "application-feign.yml" "SHARED_CONFIG" "Feign配置"

import_config "${YML_DIR}/application-swagger.yml" \
  "application-swagger.yml" "SHARED_CONFIG" "Swagger配置"

import_config "${YML_DIR}/application-loadbalancer.yml" \
  "application-loadbalancer.yml" "SHARED_CONFIG" "负载均衡配置"

import_config "${YML_DIR}/application-micrometer.yml" \
  "application-micrometer.yml" "SHARED_CONFIG" "监控配置"

import_config "${YML_DIR}/application-mail.yml" \
  "application-mail.yml" "SHARED_CONFIG" "邮件配置"

echo ""
echo "========================================="
echo "导入服务配置 (DEFAULT_GROUP)"
echo "========================================="

# 导入服务配置（从 yml 目录）
import_config "${YML_DIR}/gateway.yml" \
  "gateway.yml" "DEFAULT_GROUP" "Gateway网关配置"

import_config "${YML_DIR}/system.yml" \
  "system.yml" "DEFAULT_GROUP" "System系统服务配置"

import_config "${YML_DIR}/file.yml" \
  "file.yml" "DEFAULT_GROUP" "File文件服务配置"

echo ""
echo "========================================="
echo "✓ 配置导入完成"
echo "========================================="
echo "请访问 ${NACOS_ADDR}/nacos 验证配置"
echo ""

