# Catalogizer Enhanced System Documentation

## Overview

The Catalogizer system has been enhanced with comprehensive Docker-based infrastructure including monitoring, logging, testing databases, and additional services to create a complete enterprise-grade media management solution.

## Architecture

### Core Components

1. **Application Layer**
   - Catalog API (Go)
   - Catalog Web (React)
   - Desktop Applications (Tauri)
   - Mobile Applications (Android)

2. **Data Layer**
   - PostgreSQL (Primary Database)
   - MongoDB (Document Storage)
   - Redis (Cache & Session Store)
   - InfluxDB (Time Series Data)

3. **Processing Layer**
   - Kafka (Event Streaming)
   - RabbitMQ (Message Queue)
   - Logstash (Log Processing)
   - Elasticsearch (Search & Analytics)

4. **Storage Layer**
   - MinIO (Object Storage)
   - Local File System
   - Network File Systems (SMB/FTP/NFS/WebDAV)

5. **Observability Layer**
   - Prometheus (Metrics Collection)
   - Grafana (Metrics Visualization)
   - Kibana (Log Visualization)
   - Jaeger (Distributed Tracing)

## Docker Services

### Core Services

#### PostgreSQL
- **Container**: `catalogizer-postgres`
- **Port**: 5432
- **Environment**:
  - `POSTGRES_USER`: catalogizer
  - `POSTGRES_PASSWORD`: [from .env]
  - `POSTGRES_DB`: catalogizer
- **Health Check**: `pg_isready -U catalogizer`
- **Data Volume**: `postgres_data`

#### Redis
- **Container**: `catalogizer-redis`
- **Port**: 6379
- **Config**: `redis.conf`
- **Health Check**: `redis-cli ping`
- **Data Volume**: `redis_data`

#### Catalog API
- **Container**: `catalogizer-api`
- **Port**: 8080
- **Build Context**: `./catalog-api`
- **Health Check**: `curl -f http://localhost:8080/health`
- **Depends On**: postgres, redis

### Enhanced Services

#### Elasticsearch
- **Container**: `catalogizer-elasticsearch`
- **Port**: 9200
- **Purpose**: Centralized logging and search
- **Health Check**: `/cluster/health`
- **Data Volume**: `elasticsearch_data`

#### Logstash
- **Container**: `catalogizer-logstash`
- **Port**: 5044 (Beats), 8080 (HTTP), 9600 (API)
- **Purpose**: Log processing and transformation
- **Config**: `./monitoring/logstash/`

#### Kibana
- **Container**: `catalogizer-kibana`
- **Port**: 5601
- **Purpose**: Log visualization
- **Health Check**: `/api/status`
- **Depends On**: elasticsearch

#### Prometheus
- **Container**: `catalogizer-prometheus`
- **Port**: 9090
- **Purpose**: Metrics collection
- **Config**: `./monitoring/prometheus/prometheus.yml`
- **Health Check**: `/metrics`
- **Data Volume**: `prometheus_data`

#### Grafana
- **Container**: `catalogizer-grafana`
- **Port**: 3000
- **Purpose**: Metrics visualization
- **Credentials**: admin/admin
- **Health Check**: `/api/health`
- **Data Volume**: `grafana_data`

#### Jaeger
- **Container**: `catalogizer-jaeger`
- **Port**: 16686 (UI), 14268 (Collector)
- **Purpose**: Distributed tracing
- **Health Check**: HTTP 16686

#### MinIO
- **Container**: `catalogizer-minio`
- **Port**: 9000 (API), 9001 (Console)
- **Purpose**: Object storage
- **Credentials**: minioadmin/minioadmin123
- **Health Check**: `/minio/health/live`
- **Data Volume**: `minio_data`

#### RabbitMQ
- **Container**: `catalogizer-rabbitmq`
- **Port**: 5672 (AMQP), 15672 (Management)
- **Purpose**: Message queue
- **Credentials**: admin/admin123
- **Health Check**: `rabbitmq-diagnostics ping`
- **Data Volume**: `rabbitmq_data`

#### MongoDB
- **Container**: `catalogizer-mongodb`
- **Port**: 27017
- **Purpose**: Document storage
- **Credentials**: admin/admin123
- **Health Check**: `mongosh --eval "ping"`
- **Data Volume**: `mongodb_data`

#### Test PostgreSQL
- **Container**: `catalogizer-test-postgres`
- **Port**: 5433
- **Purpose**: Testing database
- **Credentials**: test_user/test_password
- **Health Check**: `pg_isready -U test_user`
- **Data Volume**: `test_postgres_data`

#### Kafka & Zookeeper
- **Containers**: `catalogizer-kafka`, `catalogizer-zookeeper`
- **Ports**: 2181 (Zookeeper), 9092 (Kafka)
- **Purpose**: Event streaming
- **Health Check**: Kafka broker API
- **Data Volume**: `kafka_data`

#### InfluxDB
- **Container**: `catalogizer-influxdb`
- **Port**: 8086
- **Purpose**: Time series data
- **Credentials**: admin/admin123
- **Health Check**: `/health`
- **Data Volume**: `influxdb_data`

## Service Management

### Scripts

#### `scripts/services.sh`
Comprehensive service management script with start, stop, restart, status, and cleanup commands.

**Usage:**
```bash
./scripts/services.sh <command> [category] [environment]

Commands:
  start [category] [env]    Start services
  stop [category] [env]     Stop services
  restart [category] [env]  Restart services
  status [category]         Check service status
  logs [service] [follow]  Show logs
  cleanup [force]           Clean up services and volumes
  help                      Show help

Categories:
  core                      Core services (postgres, redis, api)
  monitoring                Monitoring services (prometheus, grafana, etc.)
  testing                   Testing services (test db, kafka, etc.)
  tools                     Development tools (pgadmin, redis-commander)
  web                       Web services (nginx)
  all (default)             All services

Environments:
  development, dev          Development environment
  production, prod          Production environment (default)
```

**Examples:**
```bash
# Start all services in development mode
./scripts/services.sh start all dev

# Start core services in production mode
./scripts/services.sh start core prod

# Check status of all services
./scripts/services.sh status

# Follow logs for API service
./scripts/services.sh logs api true

# Stop all services and remove volumes
./scripts/services.sh cleanup true
```

## Testing

### System Integration Testing

#### `scripts/system-integration-test.sh`
Comprehensive system integration testing that validates all components working together.

**Features:**
- Tests all database connections
- Validates HTTP endpoints
- Checks message queue functionality
- Verifies object storage
- Tests streaming capabilities
- Validates search functionality
- Checks distributed tracing
- Tests metrics collection
- Validates log aggregation

**Usage:**
```bash
./scripts/system-integration-test.sh
```

**Output:**
- HTML report with detailed results
- Success/failure statistics
- System architecture overview
- Recommendations for next steps

## Development Workflow

### 1. Development Environment Setup
```bash
# Start all development services
./scripts/services.sh start all dev

# This starts:
# - Core services (postgres, redis, api)
# - Development tools (pgadmin, redis-commander)
# - All enhanced services for testing
```

### 2. Code Development
- Make changes to application code
- Run unit tests locally
- Test API endpoints

### 3. Integration Testing
```bash
# Run system integration tests
./scripts/system-integration-test.sh

# This validates:
# - All services are running
# - Service connectivity
# - Endpoints are responding
# - Data flows work correctly
```

### 4. Production Deployment
```bash
# Start production services
./scripts/services.sh start all prod

# This starts:
# - Core services with production config
# - Monitoring services
# - Web services with nginx
```

## Configuration

### Environment Variables

Create `.env` file in project root:
```env
# Database Configuration
POSTGRES_USER=catalogizer
POSTGRES_PASSWORD=your_secure_password
POSTGRES_DB=catalogizer

# API Configuration
API_PORT=8080
JWT_SECRET=your_jwt_secret_here

# Monitoring
GRAFANA_ADMIN_PASSWORD=your_grafana_password
PROMETHEUS_RETENTION=200h

# Storage
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=your_minio_password

# Messaging
RABBITMQ_DEFAULT_USER=admin
RABBITMQ_DEFAULT_PASS=your_rabbitmq_password
```

### Service URLs

#### Development Environment
- **API**: http://localhost:8080
- **Web**: http://localhost:5173
- **pgAdmin**: http://localhost:5050
- **Redis Commander**: http://localhost:8081

#### Monitoring Services
- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus**: http://localhost:9090
- **Kibana**: http://localhost:5601
- **Jaeger**: http://localhost:16686

#### Storage & Messaging
- **MinIO Console**: http://localhost:9001
- **RabbitMQ Management**: http://localhost:15672
- **MongoDB**: mongodb://admin:password@localhost:27017
- **InfluxDB**: http://localhost:8086

#### Databases
- **PostgreSQL**: localhost:5432
- **PostgreSQL (Test)**: localhost:5433
- **Redis**: localhost:6379

## Monitoring & Observability

### Metrics Collection
- **Prometheus** collects metrics from all services
- **Grafana** provides visualization and alerting
- Pre-configured dashboards for system health

### Log Aggregation
- **Elasticsearch** stores logs centrally
- **Logstash** processes and transforms logs
- **Kibana** provides log visualization and search

### Distributed Tracing
- **Jaeger** tracks requests across services
- OpenTelemetry integration for instrumentation
- Performance bottleneck identification

### Health Monitoring
- All services have health checks
- Automated service status verification
- Alerting capabilities for failures

## Security Considerations

### Network Security
- Docker network isolation
- Service-to-service communication within network
- External access limited to required ports

### Authentication
- Default passwords should be changed in production
- Environment variable based credential management
- Support for external secret management

### Data Protection
- Volume encryption at rest
- SSL/TLS termination at nginx
- Network traffic encryption

## Backup & Recovery

### Database Backups
```bash
# PostgreSQL backup
docker exec catalogizer-postgres pg_dump -U catalogizer catalogizer > backup.sql

# MongoDB backup
docker exec catalogizer-mongodb mongodump --host localhost --port 27017 --username admin --password password123

# Redis backup
docker exec catalogizer-redis redis-cli BGSAVE
```

### Volume Backups
```bash
# Backup all volumes
docker run --rm -v catalogizer_postgres_data:/data -v $(pwd):/backup alpine tar czf /backup/postgres_backup.tar.gz -C /data .
docker run --rm -v catalogizer_mongodb_data:/data -v $(pwd):/backup alpine tar czf /backup/mongodb_backup.tar.gz -C /data .
```

## Performance Optimization

### Resource Allocation
- CPU and memory limits configured for each service
- Resource reservations for critical services
- Horizontal scaling capabilities

### Caching Strategy
- Redis for application caching
- Browser caching via nginx
- Database query optimization

### Load Balancing
- Nginx as reverse proxy
- Service discovery capabilities
- Health check based routing

## Troubleshooting

### Common Issues

#### Services Not Starting
```bash
# Check Docker status
docker ps

# Check service logs
./scripts/services.sh logs [service_name]

# Check network
docker network ls
docker network inspect catalogizer-network
```

#### Port Conflicts
```bash
# Check port usage
netstat -tulpn | grep [port]

# Kill conflicting processes
sudo kill -9 [pid]

# Or change port in docker-compose.yml
```

#### Health Check Failures
```bash
# Manual health check
curl -f http://localhost:[port]/[health_endpoint]

# Check service logs for errors
./scripts/services.sh logs [service_name] true
```

### Performance Issues
```bash
# Check resource usage
docker stats

# Check service-specific metrics
curl http://localhost:9090/metrics

# Analyze logs for errors
./scripts/services.sh logs [service_name] | grep ERROR
```

## Contributing

### Development Standards
- Follow existing code patterns
- Write comprehensive tests
- Update documentation
- Use consistent naming conventions

### Testing Requirements
- Unit tests for new features
- Integration tests for service interactions
- System integration tests for end-to-end validation
- Performance tests for critical paths

### Deployment Process
1. Run all tests locally
2. Validate system integration
3. Deploy to staging environment
4. Run integration tests in staging
5. Deploy to production with monitoring
6. Validate production deployment

## Support

### Documentation
- Service-specific documentation in respective directories
- API documentation in `docs/api/`
- Architecture documentation in `ARCHITECTURE.md`
- Troubleshooting guide in `TROUBLESHOOTING.md`

### Community
- GitHub Issues for bug reports
- Wiki for detailed guides
- Discussion forums for questions
- Discord/Slack for real-time support

---

**Last Updated**: December 2024
**Version**: 1.0.0
**Maintainer**: Catalogizer Development Team