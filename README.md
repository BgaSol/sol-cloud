# Sol-Cloud

## Introduction

Sol-Cloud is a cloud service platform based on microservices architecture, adopting a front-end and back-end separation design pattern, providing core functions such as system management and file management. The project uses Docker containerization technology for deployment, featuring high availability, scalability, and security.

## System Architecture

### Overall Architecture

The project is divided into two main parts: frontend (client) and backend (cloud):

- **Frontend**: Single-page application based on Vue 3
- **Backend**: Microservices cluster based on Spring Cloud
- **Deployment**: Containerized deployment based on Docker

### Microservices Architecture

- **API Gateway**: Unified entry point, request routing and filtering
- **System Service**: System management functions including users, roles, and permissions
- **File Service**: File upload, download, and management functions
- **Common Module**: Shared components including utilities, constants, and base models

## Technology Stack

### Frontend Technology Stack

- **Core Framework**: Vue 3.5.12
- **UI Component Library**: Element Plus 2.8.8
- **Build Tool**: Vite 5.4.10
- **Programming Language**: TypeScript 5.6.3
- **HTTP Client**: Axios 1.7.7
- **State Management**: Pinia 2.2.6
- **Router Management**: Vue Router 4.4.5

### Backend Technology Stack

- **Core Framework**: Spring Boot 3.3.4
- **Microservices Framework**: Spring Cloud 2023.0.3
- **Microservices Components**: Spring Cloud Alibaba 2023.0.1.0
- **ORM Framework**: MyBatis Plus 3.5.10.1
- **Authentication Framework**: Sa-Token 1.39.0
- **Service Registry & Config Center**: Nacos 2.5.0
- **Database**: PostgreSQL 17.2
- **Cache**: Redis 7.4.2
- **Object Storage**: MinIO RELEASE.2024-11-07T00-52-20Z
- **Distributed Transaction**: Seata 1.8.0.2

## Project Structure

### Frontend Structure

```
client/
├── src/
│   ├── api/          # API interface definitions
│   ├── assets/       # Static resources
│   ├── components/   # Reusable components
│   ├── composables/  # Composition API
│   ├── generated/    # Auto-generated code
│   ├── pinia/        # State management
│   ├── router/       # Route configuration
│   ├── styles/       # Style files
│   ├── util/         # Utility functions
│   └── views/        # Page view components
├── public/           # Public static resources
├── index.html        # Entry HTML
└── vite.config.ts    # Vite configuration
```

### Backend Structure

```
cloud/
├── common/                 # Common modules
│   ├── common-auth/        # Authentication and authorization
│   ├── common-base-model/  # Base data models
│   ├── common-base-web/    # Web base components
│   ├── common-constant/    # Constant definitions
│   └── common-util/        # Common utilities
├── model/                  # Data model definitions
├── plugin/                 # Plugin modules
├── web/                    # Business service modules
│   ├── web-system-8081/    # System management service
│   └── web-file-8082/      # File management service
└── gateway-9527/           # API Gateway
```

## Deployment Guide

### Environment Requirements

- Docker 20.10+
- Docker Compose 2.0+
- JDK 17+
- Node.js 20+

### Development Environment Deployment

1. Clone the repository
   ```bash
   git clone https://github.com/yourusername/sol-cloud.git
   cd sol-cloud
   ```

2. Start backend services
   > **Warning**: First modify the SEATA_IP in dev.docker-compose.yml to your local IP
   ```bash
   cd docker
   docker-compose -f dev.docker-compose.yml up -d
   ```
   > web-system and web-file need to be started manually
3. Start frontend development server
   ```bash
   cd client
   npm install
   npm run dev
   ```

### Production Environment Deployment

1. Start all services
   ```bash
   docker-compose up -d
   ```

2. Access the system
   ```
   http://localhost:8080
   ```

3. Security Considerations
   > **Warning**: When deploying to production, please note the following security considerations:
   > - Do not expose internal service components (such as Nacos, Redis, PostgreSQL, MinIO, etc.) directly to the public network
   > - Ensure only the API gateway and frontend application are accessible from outside
   > - Use firewalls to restrict access to internal services
   > - Enable HTTPS encryption for all external services
   > - Regularly update passwords and keys
   > - Consider using a reverse proxy (such as Nginx) as the only entry point for external access

## Features

- [x] User Authentication and Authorization Management
- [x] Role and Permission Management
- [x] File Upload and Management
- [x] System Configuration Management
- [ ] Log Audit

## Development Guide

### Adding New Microservices

1. Create a new service module in the cloud directory
2. Add new service configuration in docker-compose.yml
3. Add new service routes in gateway configuration

### Frontend Development

1. Add new API interfaces in src/api
2. Create new page components in src/views
3. Configure new routes in src/router

## Contributing

1. Fork this repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the [LICENSE](LICENSE) License. 