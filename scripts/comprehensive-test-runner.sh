#!/bin/bash

# Comprehensive Test Runner
# Orchestrates all test suites for complete system validation

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
REPORTS_DIR="$PROJECT_ROOT/reports"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

mkdir -p "$REPORTS_DIR"
LOG_FILE="$REPORTS_DIR/comprehensive-test-runner-$TIMESTAMP.log"

# Test suite results
declare -A SUITE_RESULTS

echo "Comprehensive Test Runner - $(date)" > "$LOG_FILE"
echo "========================================" >> "$LOG_FILE"

log() {
    echo "$1" | tee -a "$LOG_FILE"
}

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

# Function to run test suite
run_test_suite() {
    local suite_name=$1
    local test_script=$2
    local description=$3
    
    log "🚀 Running test suite: $suite_name"
    log "Description: $description"
    
    print_status "INFO" "Starting: $suite_name"
    
    # Check if test script exists and is executable
    if [ ! -f "$test_script" ]; then
        SUITE_RESULTS["$suite_name"]="SKIP - Script not found"
        print_status "WARNING" "$suite_name: Script not found - $test_script"
        return 0
    fi
    
    if [ ! -x "$test_script" ]; then
        SUITE_RESULTS["$suite_name"]="SKIP - Script not executable"
        print_status "WARNING" "$suite_name: Script not executable - $test_script"
        return 0
    fi
    
    # Run the test suite
    local start_time=$(date +%s)
    local suite_log="$REPORTS_DIR/${suite_name}-suite-$TIMESTAMP.log"
    
    if "$test_script" > "$suite_log" 2>&1; then
        local end_time=$(date +%s)
        local duration=$((end_time - start_time))
        SUITE_RESULTS["$suite_name"]="PASS (${duration}s)"
        print_status "SUCCESS" "$suite_name: Completed in ${duration}s"
    else
        local end_time=$(date +%s)
        local duration=$((end_time - start_time))
        SUITE_RESULTS["$suite_name"]="FAIL (${duration}s)"
        print_status "ERROR" "$suite_name: Failed after ${duration}s"
        
        # Show last few lines of error log
        log "Last 10 lines of $suite_name log:"
        tail -10 "$suite_log" | tee -a "$LOG_FILE"
    fi
    
    log "Suite log saved to: $suite_log"
}

# Function to start required services
start_services() {
    local environment=$1
    
    print_status "INFO" "Starting required services for testing..."
    
    if [ "$environment" = "development" ]; then
        "$SCRIPT_DIR/services.sh" start all dev
    else
        "$SCRIPT_DIR/services.sh" start core prod
        "$SCRIPT_DIR/services.sh" start monitoring prod
    fi
    
    # Wait for services to be ready
    print_status "INFO" "Waiting for services to be ready..."
    sleep 30
    
    # Additional wait for slower services
    print_status "INFO" "Waiting additional time for service initialization..."
    sleep 30
}

# Function to stop services
stop_services() {
    local environment=$1
    
    print_status "INFO" "Stopping services..."
    
    if [ "$environment" = "development" ]; then
        "$SCRIPT_DIR/services.sh" stop all dev
    else
        "$SCRIPT_DIR/services.sh" stop core prod
        "$SCRIPT_DIR/services.sh" stop monitoring prod
    fi
}

# Function to generate comprehensive report
generate_comprehensive_report() {
    local total_suites=${#SUITE_RESULTS[@]}
    local passed_suites=0
    local failed_suites=0
    local skipped_suites=0
    
    # Count results
    for result in "${SUITE_RESULTS[@]}"; do
        if [[ $result == PASS* ]]; then
            ((passed_suites++))
        elif [[ $result == FAIL* ]]; then
            ((failed_suites++))
        elif [[ $result == SKIP* ]]; then
            ((skipped_suites++))
        fi
    done
    
    local success_rate=$((passed_suites * 100 / total_suites))
    
    cat > "$REPORTS_DIR/comprehensive-test-report-$TIMESTAMP.html" << EOF
<!DOCTYPE html>
<html>
<head>
    <title>Catalogizer - Comprehensive Test Report</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 20px; background: #f5f7fa; }
        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 15px; text-align: center; margin-bottom: 30px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }
        .summary { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-bottom: 30px; }
        .metric { background: white; padding: 25px; border-radius: 10px; text-align: center; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .metric h3 { margin: 0; color: #495057; font-size: 14px; text-transform: uppercase; letter-spacing: 1px; }
        .metric p { margin: 10px 0 0 0; font-size: 32px; font-weight: bold; }
        .success { color: #28a745; }
        .warning { color: #ffc107; }
        .error { color: #dc3545; }
        .info { color: #17a2b8; }
        .section { background: white; margin: 20px 0; padding: 25px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .section h2 { color: #495057; border-bottom: 2px solid #e9ecef; padding-bottom: 10px; }
        .test-suite { display: flex; justify-content: space-between; align-items: center; padding: 15px; margin: 10px 0; border-radius: 8px; border-left: 4px solid #007bff; background: #f8f9fa; transition: transform 0.2s ease; }
        .test-suite:hover { transform: translateX(5px); }
        .suite-status { padding: 8px 16px; border-radius: 20px; font-weight: bold; font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; }
        .status-pass { background: #d4edda; color: #155724; }
        .status-fail { background: #f8d7da; color: #721c24; }
        .status-skip { background: #fff3cd; color: #856404; }
        .test-details { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-top: 15px; }
        .detail-box { background: #e9ecef; padding: 15px; border-radius: 8px; }
        .detail-box h4 { margin-top: 0; color: #495057; }
        .progress-bar { width: 100%; height: 30px; background: #e9ecef; border-radius: 15px; overflow: hidden; margin: 20px 0; }
        .progress-fill { height: 100%; background: linear-gradient(90deg, #28a745, #20c997); transition: width 0.3s ease; display: flex; align-items: center; justify-content: center; color: white; font-weight: bold; }
        .test-flow { text-align: center; margin: 30px 0; }
        .flow-step { display: inline-block; background: #007bff; color: white; padding: 12px 20px; margin: 5px; border-radius: 25px; font-weight: bold; }
        .footer { text-align: center; margin-top: 40px; padding: 20px; color: #6c757d; font-size: 14px; }
        .recommendations { background: #e7f3ff; border-left: 4px solid #007bff; padding: 15px; margin: 15px 0; border-radius: 0 8px 8px 0; }
        .recommendations h3 { margin-top: 0; color: #007bff; }
    </style>
</head>
<body>
    <div class="header">
        <h1>🧪 Catalogizer Comprehensive Test Report</h1>
        <p>Complete System Validation and Quality Assurance</p>
        <p>Generated on $(date)</p>
    </div>
    
    <div class="summary">
        <div class="metric">
            <h3>Total Test Suites</h3>
            <p>$total_suites</p>
        </div>
        <div class="metric">
            <h3>Passed</h3>
            <p class="success">$passed_suites</p>
        </div>
        <div class="metric">
            <h3>Failed</h3>
            <p class="error">$failed_suites</p>
        </div>
        <div class="metric">
            <h3>Skipped</h3>
            <p class="warning">$skipped_suites</p>
        </div>
        <div class="metric">
            <h3>Success Rate</h3>
            <p class="success">$success_rate%</p>
        </div>
    </div>
    
    <div class="progress-bar">
        <div class="progress-fill" style="width: $success_rate%;">
            $success_rate% Complete
        </div>
    </div>
    
    <div class="section">
        <h2>🧪 Test Suite Results</h2>
        
EOF

    # Add each test suite result
    for suite_name in $(printf '%s\n' "${!SUITE_RESULTS[@]}" | sort); do
        local result=${SUITE_RESULTS[$suite_name]}
        local status_class="status-skip"
        
        if [[ $result == PASS* ]]; then
            status_class="status-pass"
        elif [[ $result == FAIL* ]]; then
            status_class="status-fail"
        fi
        
        local duration=$(echo "$result" | sed 's/.*(\([^)]*\)s)/\1/' | grep -E '^[0-9]+$' || echo "N/A")
        
        cat >> "$REPORTS_DIR/comprehensive-test-report-$TIMESTAMP.html" << EOF
        <div class="test-suite">
            <div>
                <h3>$suite_name</h3>
                <p>Duration: ${duration}s</p>
            </div>
            <div class="suite-status $status_class">
                $result
            </div>
        </div>
EOF
    done
    
    cat >> "$REPORTS_DIR/comprehensive-test-report-$TIMESTAMP.html" << EOF
    </div>
    
    <div class="section">
        <h2>🔄 Test Execution Flow</h2>
        <div class="test-flow">
            <div class="flow-step">1. Environment Setup</div>
            <div class="flow-step">2. Service Startup</div>
            <div class="flow-step">3. Database Tests</div>
            <div class="flow-step">4. API Integration</div>
            <div class="flow-step">5. System Integration</div>
            <div class="flow-step">6. Security Scanning</div>
            <div class="flow-step">7. Report Generation</div>
        </div>
    </div>
    
    <div class="section">
        <h2>📊 Test Categories</h2>
        <div class="test-details">
            <div class="detail-box">
                <h4>🗄️ Database Tests</h4>
                <ul>
                    <li>PostgreSQL connectivity and operations</li>
                    <li>MongoDB document operations</li>
                    <li>Redis caching functionality</li>
                    <li>InfluxDB time series operations</li>
                </ul>
            </div>
            <div class="detail-box">
                <h4>🔧 API Tests</h4>
                <ul>
                    <li>REST endpoint functionality</li>
                    <li>Authentication & authorization</li>
                    <li>Error handling and validation</li>
                    <li>Rate limiting and CORS</li>
                </ul>
            </div>
            <div class="detail-box">
                <h4>🌐 Integration Tests</h4>
                <ul>
                    <li>Service-to-service communication</li>
                    <li>Message queue operations</li>
                    <li>Object storage functionality</li>
                    <li>Search and indexing</li>
                </ul>
            </div>
            <div class="detail-box">
                <h4>🔒 Security Tests</h4>
                <ul>
                    <li>Vulnerability scanning</li>
                    <li>Dependency security</li>
                    <li>Container security</li>
                    <li>Code quality analysis</li>
                </ul>
            </div>
        </div>
    </div>
    
    <div class="section">
        <h2>🎯 Recommendations</h2>
        <div class="recommendations">
            <h3>✅ System Health</h3>
            <p>The comprehensive test suite validates all critical system components:</p>
            <ul>
                <li>Data persistence and integrity across all databases</li>
                <li>API reliability and security compliance</li>
                <li>Service integration and communication</li>
                <li>Security posture and vulnerability status</li>
                <li>Performance and scalability indicators</li>
            </ul>
        </div>
        
        <div class="recommendations">
            <h3>🚀 Continuous Improvement</h3>
            <ul>
                <li>Integrate automated testing into CI/CD pipeline</li>
                <li>Monitor test execution trends and patterns</li>
                <li>Implement performance regression testing</li>
                <li>Expand test coverage for edge cases</li>
                <li>Regular security assessments and updates</li>
            </ul>
        </div>
    </div>
    
    <div class="section">
        <h2>📁 Available Reports</h2>
        <ul>
            <li><strong>Comprehensive Test Report:</strong> <code>comprehensive-test-report-$TIMESTAMP.html</code></li>
            <li><strong>System Integration Report:</strong> <code>system-integration-test-report-*.html</code></li>
            <li><strong>Database Connectivity Report:</strong> <code>database-connectivity-test-report-*.html</code></li>
            <li><strong>API Integration Report:</strong> <code>api-integration-test-report-*.html</code></li>
            <li><strong>Security Reports:</strong> <code>sonarqube-report.json</code>, <code>snyk-*.json</code></li>
        </ul>
    </div>
    
    <div class="footer">
        <p>This comprehensive test report was generated automatically by Catalogizer Test Runner.</p>
        <p>For detailed logs, check the individual test suite execution logs.</p>
        <p>Report generated on $(date) with timestamp $TIMESTAMP</p>
    </div>
</body>
</html>
EOF
    
    # Create symlink to latest report
    ln -sf "comprehensive-test-report-$TIMESTAMP.html" "$REPORTS_DIR/latest-comprehensive-test-report.html"
    
    log "📊 Comprehensive test report generated: $REPORTS_DIR/comprehensive-test-report-$TIMESTAMP.html"
}

# Main execution
main() {
    local environment=${1:-"development"}
    
    log "🚀 Starting Comprehensive Test Runner..."
    log "Environment: $environment"
    
    # Start required services
    start_services "$environment"
    
    # Run all test suites
    run_test_suite "Database Connectivity" "$SCRIPT_DIR/database-connectivity-test.sh" "Tests all database connectivity and operations"
    run_test_suite "API Integration" "$SCRIPT_DIR/api-integration-test.sh" "Tests all API endpoints and functionality"
    run_test_suite "System Integration" "$SCRIPT_DIR/system-integration-test.sh" "Tests complete system integration"
    run_test_suite "Security Scanning" "$SCRIPT_DIR/security-test.sh" "Runs security vulnerability scans"
    
    # Stop services (optional - keep running for debugging)
    if [ "$2" = "--cleanup" ]; then
        stop_services "$environment"
    fi
    
    # Generate comprehensive report
    generate_comprehensive_report
    
    # Print summary
    local total_suites=${#SUITE_RESULTS[@]}
    local passed_suites=0
    local failed_suites=0
    
    for result in "${SUITE_RESULTS[@]}"; do
        if [[ $result == PASS* ]]; then
            ((passed_suites++))
        elif [[ $result == FAIL* ]]; then
            ((failed_suites++))
        fi
    done
    
    echo ""
    print_status "INFO" "Comprehensive Test Runner Summary"
    print_status "INFO" "================================="
    echo "Total Test Suites: $total_suites"
    echo -e "${GREEN}Passed: $passed_suites${NC}"
    echo -e "${RED}Failed: $failed_suites${NC}"
    echo "Success Rate: $((passed_suites * 100 / total_suites))%"
    echo "Report: $REPORTS_DIR/latest-comprehensive-test-report.html"
    echo "Log: $LOG_FILE"
    
    # Show detailed results
    echo ""
    echo "Detailed Results:"
    for suite_name in $(printf '%s\n' "${!SUITE_RESULTS[@]}" | sort); do
        local result=${SUITE_RESULTS[$suite_name]}
        if [[ $result == PASS* ]]; then
            echo -e "${GREEN}✅ $suite_name: $result${NC}"
        elif [[ $result == FAIL* ]]; then
            echo -e "${RED}❌ $suite_name: $result${NC}"
        else
            echo -e "${YELLOW}⏭️  $suite_name: $result${NC}"
        fi
    done
    
    if [ "$failed_suites" -gt 0 ]; then
        echo ""
        print_status "ERROR" "Some test suites failed!"
        echo "Check individual test logs for details."
        exit 1
    else
        echo ""
        print_status "SUCCESS" "All test suites passed!"
        echo "System is ready for deployment."
        exit 0
    fi
}

# Show usage if no arguments
if [ $# -eq 0 ]; then
    echo "Usage: $0 <environment> [--cleanup]"
    echo "  environment: development (default) or production"
    echo "  --cleanup:   Stop services after testing"
    echo ""
    echo "Examples:"
    echo "  $0 development        # Run all tests in development mode"
    echo "  $0 production         # Run all tests in production mode"
    echo "  $0 development --cleanup  # Run tests and cleanup afterwards"
    exit 0
fi

# Run main function with all arguments
main "$@"