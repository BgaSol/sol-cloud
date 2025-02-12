# 第一阶段：构建
FROM node:20-alpine AS builder
RUN npm config set registry https://registry.npmmirror.com

WORKDIR /app
COPY . .
RUN npm install
RUN npm run build

FROM nginx:alpine

COPY --from=builder /app/dist /usr/share/nginx/html

COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 3000

CMD ["nginx", "-g", "daemon off;"]

