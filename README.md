# Gym Visit Tracking App (Phase 1) - Backend

Spring Boot backend for:
- Social login (Google, Facebook, Twitter/X)
- Local signup/login with JWT
- Gym listing
- Booking creation
- Visit code scanning
- Visit tracking + streaks
- Swagger + Actuator

## Tech Stack
- Java 21 runtime (project property is Java 25 target by default)
- Spring Boot 3.x
- Maven
- MySQL
- JWT auth
- Swagger/OpenAPI
- Docker / Kubernetes / Helm

---

## 1) Run Locally (Without Docker)

### One-command dev start
```bash
./scripts/dev-up.sh
```

This starts Docker Compose (app + MySQL).  
If you want Maven local mode instead:
```bash
./scripts/dev-up.sh --local
```

Stop Docker stack:
```bash
./scripts/dev-down.sh
```

### Prerequisites
- Java 21+
- Maven
- MySQL running locally

### MySQL quick setup
Use your local DB (example used in this project):
- Host: `localhost`
- DB: `gym`
- User: `root`
- Password: `Sample-pass`

### Start app
```bash
mvn spring-boot:run -Djava.version=21
```

### Open
- Swagger: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- Health: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- OpenAPI: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 2) Local Run With Docker Compose (Recommended)

This starts both MySQL and the app.

```bash
docker compose up --build -d
```

Check logs:
```bash
docker compose logs -f app
```

Stop:
```bash
docker compose down
```

App URLs are same as local run:
- [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

---

## 3) API Auth Flow (Swagger)

### Get token
Use one of:
- `POST /api/v1/auth/signup`
- `POST /api/v1/auth/login`

Both return:
- `token`
- `tokenType` = `Bearer`

### Authorize in Swagger
1. Open Swagger UI
2. Click `Authorize`
3. Paste token as:
   - `Bearer <your_jwt_token>`
4. Call protected APIs

---

## 4) Quick Test Payloads

### Create booking
`POST /api/v1/bookings/create`
```json
{
  "gymId": 1,
  "bookingDate": "2026-02-15",
  "note": "Evening session"
}
```

### Scan code
`POST /api/v1/visit-codes/scan`
```json
{
  "code": "AUS-DT-1001"
}
```

Seeded sample codes:
- `AUS-DT-1001` to `AUS-DT-1005`
- `AUS-RV-2001` to `AUS-RV-2005`
- `DAL-MT-3001` to `DAL-MT-3005`

---

## 5) Kubernetes (Raw Manifests)

Folder: `k8s/`

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

Optional ingress:
```bash
kubectl apply -f k8s/ingress.yaml
```

Port-forward if no ingress:
```bash
kubectl -n gymapp port-forward svc/gymapp 8080:80
```

Then open: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 6) Helm (Easiest for Staging/Production)

Chart path: `helm/gymapp`

### Staging
```bash
helm upgrade --install gymapp-staging ./helm/gymapp \
  -n gymapp-staging --create-namespace \
  -f ./helm/gymapp/values-staging.yaml
```

### Production
```bash
helm upgrade --install gymapp-prod ./helm/gymapp \
  -n gymapp-prod --create-namespace \
  -f ./helm/gymapp/values-prod.yaml
```

### Important before deploy
Edit these files and replace placeholders:
- `helm/gymapp/values-staging.yaml`
- `helm/gymapp/values-prod.yaml`

Replace:
- DB password
- JWT secret
- OAuth client IDs/secrets
- Domain/TLS host/secret names
- Image tag/repository

---

## 7) Environment Variables Used by App

Main variables:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_JWT_SECRET`
- `APP_JWT_EXPIRATION_SECONDS`
- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`
- `FACEBOOK_CLIENT_ID`
- `FACEBOOK_CLIENT_SECRET`
- `TWITTER_CLIENT_ID`
- `TWITTER_CLIENT_SECRET`

---

## 8) Common Issues

### `release version 25 not supported`
Run with Java 21:
```bash
mvn spring-boot:run -Djava.version=21
```

### DB connection errors
Check DB URL/user/pass and that MySQL is running.

### Unauthorized on protected APIs
Use JWT in Swagger `Authorize` as:
`Bearer <token>`

### OAuth login not working
Set real OAuth provider credentials (Google/Facebook/Twitter).

---

## 9) Helpful Commands

```bash
# compile
mvn -DskipTests -Djava.version=21 compile

# run tests (if you add tests later)
mvn test -Djava.version=21

# helm dry render
helm template gymapp ./helm/gymapp
```
