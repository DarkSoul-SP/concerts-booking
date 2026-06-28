# Concerts Booking

Separate Spring Boot and Vue applications for booking concert tickets.

## Backend

```powershell
cd backend
mvn spring-boot:run
```

The API runs at `http://localhost:8080`.

Seeded login:

- username: `demo`
- password: `password`

The backend expects PostgreSQL at `localhost:5432` by default:

- database: `concerts`
- username: `concerts`
- password: `concerts`

## Frontend

```powershell
cd frontend
npm install
npm run dev
```

The Vue app runs at `http://127.0.0.1:5173`.

## Docker Compose

```powershell
docker compose up --build
```

The frontend is available at `http://localhost:5173` and proxies API requests to the backend service. The backend is also exposed directly at `http://localhost:8080`. PostgreSQL runs from the `postgres:16.14-alpine3.23` image and is exposed at `localhost:5432`.
