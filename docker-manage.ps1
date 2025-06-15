# Docker management script for TickIT Backend (PowerShell)

param(
    [Parameter(Mandatory=$true)]
    [ValidateSet("build", "up", "down", "logs", "restart", "clean", "status", "check-docker")]
    [string]$Action
)

# Function to check if Docker is running
function Test-DockerRunning {
    try {
        docker version > $null 2>&1
        return $LASTEXITCODE -eq 0
    }
    catch {
        return $false
    }
}

switch ($Action) {
    "check-docker" {
        Write-Host "Checking Docker status..." -ForegroundColor Cyan
        if (Test-DockerRunning) {
            Write-Host "✓ Docker is running" -ForegroundColor Green
            docker --version
            docker-compose --version
        } else {
            Write-Host "✗ Docker is not running or not installed" -ForegroundColor Red
            Write-Host "Please start Docker Desktop and try again" -ForegroundColor Yellow
        }
    }    "build" {
        if (-not (Test-DockerRunning)) {
            Write-Host "✗ Docker is not running! Please start Docker Desktop first." -ForegroundColor Red
            exit 1
        }
        
        # Check if required files exist
        if (-not (Test-Path ".\Dockerfile")) {
            Write-Host "✗ Dockerfile not found!" -ForegroundColor Red
            exit 1
        }
        if (-not (Test-Path ".\docker-compose.yml")) {
            Write-Host "✗ docker-compose.yml not found!" -ForegroundColor Red
            exit 1
        }
        if (-not (Test-Path ".\src\main\resources\application-secrets.properties")) {
            Write-Host "✗ application-secrets.properties not found!" -ForegroundColor Red
            exit 1
        }
        
        # Ensure uploads directory exists
        if (-not (Test-Path ".\uploads")) {
            New-Item -ItemType Directory -Path ".\uploads" -Force | Out-Null
            Write-Host "Created uploads directory" -ForegroundColor Yellow
        }
        
        Write-Host "Building Docker images..." -ForegroundColor Green
        # Disable bake for now due to Windows path issues
        # $env:COMPOSE_BAKE = "true"
        docker-compose build --no-cache
        if ($LASTEXITCODE -ne 0) {
            Write-Host "Build failed! Check the error messages above." -ForegroundColor Red
            exit 1
        }
        Write-Host "✓ Build completed successfully!" -ForegroundColor Green
    }
    "up" {
        if (-not (Test-DockerRunning)) {
            Write-Host "✗ Docker is not running! Please start Docker Desktop first." -ForegroundColor Red
            exit 1
        }
        Write-Host "Starting services..." -ForegroundColor Green
        docker-compose up -d
    }
    "down" {
        Write-Host "Stopping services..." -ForegroundColor Yellow
        docker-compose down
    }
    "logs" {
        Write-Host "Showing logs..." -ForegroundColor Cyan
        docker-compose logs -f
    }
    "restart" {
        if (-not (Test-DockerRunning)) {
            Write-Host "✗ Docker is not running! Please start Docker Desktop first." -ForegroundColor Red
            exit 1
        }
        Write-Host "Restarting services..." -ForegroundColor Yellow
        docker-compose down
        docker-compose up -d
    }
    "clean" {
        Write-Host "Cleaning up Docker resources..." -ForegroundColor Red
        docker-compose down
        docker system prune -f
        docker volume prune -f
    }
    "status" {
        Write-Host "Service status:" -ForegroundColor Cyan
        docker-compose ps
    }
}

if ($null -eq $Action) {
    Write-Host "Usage: .\docker-manage.ps1 -Action <command>" -ForegroundColor White
    Write-Host ""
    Write-Host "Commands:" -ForegroundColor White
    Write-Host "  check-docker - Check if Docker is running" -ForegroundColor Gray
    Write-Host "  build        - Build Docker images" -ForegroundColor Gray
    Write-Host "  up           - Start services in background" -ForegroundColor Gray
    Write-Host "  down         - Stop services" -ForegroundColor Gray
    Write-Host "  logs         - Show service logs" -ForegroundColor Gray
    Write-Host "  restart      - Restart all services" -ForegroundColor Gray
    Write-Host "  clean        - Clean up Docker resources" -ForegroundColor Gray
    Write-Host "  status       - Show service status" -ForegroundColor Gray
}
