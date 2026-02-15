# Deployment Guide

## 1) Docker (single stack with MySQL)

### Build and run
```bash
docker compose up --build -d
```

### Check logs
```bash
docker compose logs -f app
```

### Access
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

### Stop
```bash
docker compose down
```

## 2) Kubernetes manifests

### Prerequisites
- A Kubernetes cluster
- `kubectl` configured
- App image pushed (update image in `k8s/app-deployment.yaml` if needed)

### Deploy
```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/mysql-pvc.yaml
kubectl apply -f k8s/mysql-deployment.yaml
kubectl apply -f k8s/mysql-service.yaml
kubectl apply -f k8s/app-deployment.yaml
kubectl apply -f k8s/app-service.yaml
```

### Optional ingress
```bash
kubectl apply -f k8s/ingress.yaml
```

### Quick access without ingress
```bash
kubectl -n gymapp port-forward svc/gymapp 8080:80
```

## 3) Helm chart

Chart path:
- `helm/gymapp`

### Install
```bash
helm upgrade --install gymapp ./helm/gymapp -n gymapp --create-namespace
```

### Override image and secrets (recommended)
```bash
helm upgrade --install gymapp ./helm/gymapp -n gymapp --create-namespace \
  --set image.repository=imanishpr/decentralised-gym \
  --set image.tag=latest \
  --set secrets.datasourcePassword='Sample-pass' \
  --set secrets.jwtSecret='replace-with-strong-secret'
```

### Port-forward
```bash
kubectl -n gymapp port-forward svc/gymapp-gymapp 8080:80
```

### Uninstall
```bash
helm uninstall gymapp -n gymapp
```

## Notes
- Set real OAuth client IDs/secrets in `docker-compose.yml`, `k8s/secret.yaml`, `k8s/configmap.yaml`, or Helm values.
- Replace JWT secret before any non-local environment.
