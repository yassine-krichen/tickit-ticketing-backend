#!/bin/bash

# Docker management script for TickIT Backend

case "$1" in
    "build")
        echo "Building Docker images..."
        # export COMPOSE_BAKE=true  # Disabled due to Windows path issues
        docker-compose build --no-cache
        ;;
    "up")
        echo "Starting services..."
        docker-compose up -d
        ;;
    "down")
        echo "Stopping services..."
        docker-compose down
        ;;
    "logs")
        echo "Showing logs..."
        docker-compose logs -f
        ;;
    "restart")
        echo "Restarting services..."
        docker-compose down
        docker-compose up -d
        ;;
    "clean")
        echo "Cleaning up Docker resources..."
        docker-compose down
        docker system prune -f
        docker volume prune -f
        ;;
    "status")
        echo "Service status:"
        docker-compose ps
        ;;
    *)
        echo "Usage: $0 {build|up|down|logs|restart|clean|status}"
        echo ""
        echo "Commands:"
        echo "  build    - Build Docker images"
        echo "  up       - Start services in background"
        echo "  down     - Stop services"
        echo "  logs     - Show service logs"
        echo "  restart  - Restart all services"
        echo "  clean    - Clean up Docker resources"
        echo "  status   - Show service status"
        ;;
esac
