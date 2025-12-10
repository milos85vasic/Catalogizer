#!/bin/bash

# Catalogizer Services Management Script
# This script provides start, stop, and status commands for all Catalogizer services

set -e

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
COMPOSE_FILES="$PROJECT_ROOT/docker-compose.yml $PROJECT_ROOT/docker-compose.enhanced.yml $PROJECT_ROOT/docker-compose.dev.yml"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Service categories
CORE_SERVICES="postgres redis api"
WEB_SERVICES="nginx"
MONITORING_SERVICES="elasticsearch logstash kibana prometheus grafana jaeger"
STORAGE_SERVICES="minio mongodb"
MESSAGING_SERVICES="rabbitmq"
TESTING_SERVICES="test-postgres kafka zookeeper influxdb"
DEVELOPMENT_SERVICES="pgadmin redis-commander"

# Function to print colored output
print_status() {
    local status=$1
    local message=$2
    case $status in
        "SUCCESS")
            echo -e "${GREEN}✅ $message${NC}"
            ;;
        "ERROR")
            echo -e "${RED}❌ $message${NC}"
            ;;
        "WARNING")
            echo -e "${YELLOW}⚠️  $message${NC}"
            ;;
        "INFO")
            echo -e "${BLUE}ℹ️  $message${NC}"
            ;;
    esac
}

# Function to check if Docker is available
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_status "ERROR" "Docker is not installed or not in PATH"
        exit 1
    fi
    
    if ! docker info &> /dev/null; then
        print_status "ERROR" "Docker daemon is not running"
        exit 1
    fi
    
    # Check Docker Compose
    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        print_status "ERROR" "Docker Compose is not installed"
        exit 1
    fi
}

# Function to check if network exists
ensure_network() {
    local network_name="catalogizer-network"
    
    if ! docker network ls | grep -q "$network_name"; then
        print_status "INFO" "Creating Docker network: $network_name"
        docker network create "$network_name"
    fi
}

# Function to start services
start_services() {
    local service_category=${1:-"all"}
    local environment=${2:-"production"}
    
    print_status "INFO" "Starting Catalogizer services..."
    print_status "INFO" "Environment: $environment"
    print_status "INFO" "Service category: $service_category"
    
    cd "$PROJECT_ROOT"
    ensure_network
    
    case $environment in
        "development"|"dev")
            print_status "INFO" "Starting in development mode..."
            
            case $service_category in
                "core")
                    print_status "INFO" "Starting core services..."
                    docker-compose -f docker-compose.dev.yml up -d $CORE_SERVICES
                    ;;
                "monitoring")
                    print_status "INFO" "Starting monitoring services..."
                    docker-compose -f docker-compose.enhanced.yml up -d $MONITORING_SERVICES
                    ;;
                "testing")
                    print_status "INFO" "Starting testing services..."
                    docker-compose -f docker-compose.enhanced.yml up -d $TESTING_SERVICES
                    ;;
                "tools")
                    print_status "INFO" "Starting development tools..."
                    docker-compose -f docker-compose.dev.yml --profile tools up -d
                    ;;
                "all"|*)
                    print_status "INFO" "Starting all development services..."
                    docker-compose -f docker-compose.dev.yml up -d
                    docker-compose -f docker-compose.dev.yml --profile tools up -d
                    docker-compose -f docker-compose.enhanced.yml up -d
                    ;;
            esac
            ;;
        "production"|"prod")
            print_status "INFO" "Starting in production mode..."
            
            case $service_category in
                "core")
                    print_status "INFO" "Starting core services..."
                    docker-compose -f docker-compose.yml up -d $CORE_SERVICES
                    ;;
                "web")
                    print_status "INFO" "Starting web services..."
                    docker-compose -f docker-compose.yml --profile production up -d
                    ;;
                "monitoring")
                    print_status "INFO" "Starting monitoring services..."
                    docker-compose -f docker-compose.enhanced.yml up -d $MONITORING_SERVICES
                    ;;
                "all"|*)
                    print_status "INFO" "Starting all production services..."
                    docker-compose -f docker-compose.yml up -d
                    docker-compose -f docker-compose.yml --profile production up -d
                    docker-compose -f docker-compose.enhanced.yml up -d $MONITORING_SERVICES
                    ;;
            esac
            ;;
    esac
    
    print_status "SUCCESS" "Services started successfully"
    print_status "INFO" "Run '$0 status' to check service health"
}

# Function to stop services
stop_services() {
    local service_category=${1:-"all"}
    local environment=${2:-"production"}
    
    print_status "INFO" "Stopping Catalogizer services..."
    
    cd "$PROJECT_ROOT"
    
    case $environment in
        "development"|"dev")
            case $service_category in
                "core")
                    print_status "INFO" "Stopping core services..."
                    docker-compose -f docker-compose.dev.yml down
                    ;;
                "monitoring")
                    print_status "INFO" "Stopping monitoring services..."
                    docker-compose -f docker-compose.enhanced.yml down
                    ;;
                "testing")
                    print_status "INFO" "Stopping testing services..."
                    docker-compose -f docker-compose.enhanced.yml down
                    ;;
                "tools")
                    print_status "INFO" "Stopping development tools..."
                    docker-compose -f docker-compose.dev.yml --profile tools down
                    ;;
                "all"|*)
                    print_status "INFO" "Stopping all development services..."
                    docker-compose -f docker-compose.dev.yml down
                    docker-compose -f docker-compose.dev.yml --profile tools down
                    docker-compose -f docker-compose.enhanced.yml down
                    ;;
            esac
            ;;
        "production"|"prod")
            case $service_category in
                "core")
                    print_status "INFO" "Stopping core services..."
                    docker-compose -f docker-compose.yml down
                    ;;
                "web")
                    print_status "INFO" "Stopping web services..."
                    docker-compose -f docker-compose.yml --profile production down
                    ;;
                "monitoring")
                    print_status "INFO" "Stopping monitoring services..."
                    docker-compose -f docker-compose.enhanced.yml down
                    ;;
                "all"|*)
                    print_status "INFO" "Stopping all production services..."
                    docker-compose -f docker-compose.yml down
                    docker-compose -f docker-compose.yml --profile production down
                    docker-compose -f docker-compose.enhanced.yml down
                    ;;
            esac
            ;;
    esac
    
    print_status "SUCCESS" "Services stopped successfully"
}

# Function to check service status
check_status() {
    local service_category=${1:-"all"}
    
    print_status "INFO" "Checking Catalogizer service status..."
    
    cd "$PROJECT_ROOT"
    
    # Function to check individual service
    check_service_health() {
        local service_name=$1
        local container_name=$2
        local health_url=$3
        local expected_status=$4
        
        if docker ps --filter "name=$container_name" --format "table {{.Names}}\t{{.Status}}" | grep -q "$container_name"; then
            local status=$(docker ps --filter "name=$container_name" --format "{{.Status}}" | head -1)
            
            if [[ $status == *"healthy"* ]]; then
                print_status "SUCCESS" "$service_name: $status"
                return 0
            elif [[ $status == *"Up"* ]]; then
                if [ -n "$health_url" ]; then
                    # Try to check health endpoint
                    if curl -f -s "$health_url" > /dev/null 2>&1; then
                        print_status "SUCCESS" "$service_name: Running and healthy"
                        return 0
                    else
                        print_status "WARNING" "$service_name: Running but health check failed"
                        return 1
                    fi
                else
                    print_status "WARNING" "$service_name: Running (no health check)"
                    return 1
                fi
            else
                print_status "ERROR" "$service_name: $status"
                return 2
            fi
        else
            print_status "ERROR" "$service_name: Not running"
            return 3
        fi
    }
    
    # Check core services
    echo ""
    echo "=== Core Services ==="
    check_service_health "PostgreSQL" "catalogizer-postgres" "http://localhost:5432"
    check_service_health "PostgreSQL (Dev)" "catalogizer-postgres-dev" "http://localhost:5432"
    check_service_health "PostgreSQL (Test)" "catalogizer-test-postgres" "http://localhost:5433"
    check_service_health "Redis" "catalogizer-redis" "http://localhost:6379"
    check_service_health "Redis (Dev)" "catalogizer-redis-dev" "http://localhost:6379"
    check_service_health "Catalog API" "catalogizer-api" "http://localhost:8080/health"
    check_service_health "Catalog API (Dev)" "catalogizer-api-dev" "http://localhost:8080/health"
    
    # Check web services
    echo ""
    echo "=== Web Services ==="
    check_service_health "Nginx" "catalogizer-nginx" "http://localhost:80"
    
    # Check monitoring services
    echo ""
    echo "=== Monitoring Services ==="
    check_service_health "Elasticsearch" "catalogizer-elasticsearch" "http://localhost:9200/_cluster/health"
    check_service_health "Kibana" "catalogizer-kibana" "http://localhost:5601/api/status"
    check_service_health "Logstash" "catalogizer-logstash" "http://localhost:9600"
    check_service_health "Prometheus" "catalogizer-prometheus" "http://localhost:9090/metrics"
    check_service_health "Grafana" "catalogizer-grafana" "http://localhost:3000/api/health"
    check_service_health "Jaeger" "catalogizer-jaeger" "http://localhost:16686"
    
    # Check storage services
    echo ""
    echo "=== Storage Services ==="
    check_service_health "MinIO" "catalogizer-minio" "http://localhost:9000/minio/health/live"
    check_service_health "MongoDB" "catalogizer-mongodb" "http://localhost:27017"
    check_service_health "InfluxDB" "catalogizer-influxdb" "http://localhost:8086/health"
    
    # Check messaging services
    echo ""
    echo "=== Messaging Services ==="
    check_service_health "RabbitMQ" "catalogizer-rabbitmq" "http://localhost:15672"
    
    # Check streaming services
    echo ""
    echo "=== Streaming Services ==="
    check_service_health "Zookeeper" "catalogizer-zookeeper" "http://localhost:2181"
    check_service_health "Kafka" "catalogizer-kafka" "http://localhost:9092"
    
    # Check development tools
    echo ""
    echo "=== Development Tools ==="
    check_service_health "pgAdmin" "catalogizer-pgadmin-dev" "http://localhost:5050"
    check_service_health "Redis Commander" "catalogizer-redis-commander-dev" "http://localhost:8081"
    
    # Display access URLs
    echo ""
    echo "=== Access URLs ==="
    echo "📊 Grafana Dashboard:     http://localhost:3000 (admin/admin)"
    echo "🔍 Prometheus Metrics:    http://localhost:9090"
    echo "📈 Kibana Logs:          http://localhost:5601"
    echo "🔗 Jaeger Tracing:       http://localhost:16686"
    echo "💾 MinIO Console:         http://localhost:9001 (minioadmin/minioadmin123)"
    echo "📬 RabbitMQ Management:   http://localhost:15672 (admin/admin123)"
    echo "🗄️ MongoDB:               mongodb://admin:admin123@localhost:27017"
    echo "⏰ InfluxDB:              http://localhost:8086 (admin/admin123)"
    echo "🐘 PostgreSQL (Dev):      localhost:5432 (catalogizer/dev_password_change_me)"
    echo "🐘 PostgreSQL (Test):     localhost:5433 (test_user/test_password)"
    echo "🔴 Redis (Dev):           localhost:6379"
    echo "🔧 pgAdmin:               http://localhost:5050 (admin@catalogizer.dev/admin)"
    echo "🔧 Redis Commander:       http://localhost:8081"
}

# Function to restart services
restart_services() {
    local service_category=${1:-"all"}
    local environment=${2:-"production"}
    
    print_status "INFO" "Restarting Catalogizer services..."
    
    stop_services "$service_category" "$environment"
    sleep 5
    start_services "$service_category" "$environment"
    
    print_status "SUCCESS" "Services restarted successfully"
}

# Function to show logs
show_logs() {
    local service_name=${1:-""}
    local follow=${2:-"false"}
    local tail_lines=${3:-"100"}
    
    cd "$PROJECT_ROOT"
    
    if [ -z "$service_name" ]; then
        print_status "INFO" "Showing logs for all services..."
        if [ "$follow" = "true" ]; then
            docker-compose -f docker-compose.yml -f docker-compose.dev.yml -f docker-compose.enhanced.yml logs -f --tail=$tail_lines
        else
            docker-compose -f docker-compose.yml -f docker-compose.dev.yml -f docker-compose.enhanced.yml logs --tail=$tail_lines
        fi
    else
        print_status "INFO" "Showing logs for service: $service_name"
        if [ "$follow" = "true" ]; then
            docker-compose -f docker-compose.yml -f docker-compose.dev.yml -f docker-compose.enhanced.yml logs -f --tail=$tail_lines "$service_name"
        else
            docker-compose -f docker-compose.yml -f docker-compose.dev.yml -f docker-compose.enhanced.yml logs --tail=$tail_lines "$service_name"
        fi
    fi
}

# Function to clean up
cleanup() {
    local force=${1:-"false"}
    
    print_status "INFO" "Cleaning up Catalogizer services..."
    
    cd "$PROJECT_ROOT"
    
    # Stop all services
    docker-compose -f docker-compose.yml down
    docker-compose -f docker-compose.dev.yml down
    docker-compose -f docker-compose.enhanced.yml down
    
    # Remove volumes if force is true
    if [ "$force" = "true" ]; then
        print_status "WARNING" "Removing all volumes (this will delete all data)..."
        docker-compose -f docker-compose.yml -f docker-compose.dev.yml -f docker-compose.enhanced.yml down -v
        docker system prune -f
    fi
    
    # Remove network
    if docker network ls | grep -q "catalogizer-network"; then
        docker network rm catalogizer-network
    fi
    
    print_status "SUCCESS" "Cleanup completed"
}

# Function to show usage
show_usage() {
    echo "Catalogizer Services Management Script"
    echo ""
    echo "Usage: $0 <command> [options]"
    echo ""
    echo "Commands:"
    echo "  start [category] [env]    Start services"
    echo "  stop [category] [env]     Stop services"
    echo "  restart [category] [env]  Restart services"
    echo "  status [category]         Check service status"
    echo "  logs [service] [follow]  Show logs"
    echo "  cleanup [force]           Clean up services and volumes"
    echo "  help                      Show this help message"
    echo ""
    echo "Categories:"
    echo "  core                      Core services (postgres, redis, api)"
    echo "  monitoring                Monitoring services (prometheus, grafana, etc.)"
    echo "  testing                   Testing services (test db, kafka, etc.)"
    echo "  tools                     Development tools (pgadmin, redis-commander)"
    echo "  web                       Web services (nginx)"
    echo "  all (default)             All services"
    echo ""
    echo "Environments:"
    echo "  development, dev          Development environment"
    echo "  production, prod          Production environment (default)"
    echo ""
    echo "Examples:"
    echo "  $0 start all dev          Start all services in development mode"
    echo "  $0 start core             Start core services in production mode"
    echo "  $0 start monitoring dev   Start monitoring services in development"
    echo "  $0 status                 Check status of all services"
    echo "  $0 logs api true          Follow logs for API service"
    echo "  $0 restart core          Restart core services"
    echo "  $0 cleanup true           Remove all services and volumes"
}

# Main execution
main() {
    check_docker
    
    case ${1:-"help"} in
        "start")
            start_services "${2:-all}" "${3:-production}"
            ;;
        "stop")
            stop_services "${2:-all}" "${3:-production}"
            ;;
        "restart")
            restart_services "${2:-all}" "${3:-production}"
            ;;
        "status")
            check_status "${2:-all}"
            ;;
        "logs")
            show_logs "${2:-}" "${3:-false}" "${4:-100}"
            ;;
        "cleanup")
            cleanup "${2:-false}"
            ;;
        "help"|"--help"|"-h")
            show_usage
            ;;
        *)
            print_status "ERROR" "Unknown command: $1"
            show_usage
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@"