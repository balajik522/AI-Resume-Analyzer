Run project with Docker Compose

From the repository root:

```bash
# Build images and start services
docker compose up --build -d

# Check logs
docker compose logs -f backend

# Stop services
docker compose down
```

- Backend will be exposed at http://localhost:8081
- Frontend will be exposed at http://localhost:3000 (serving static site via nginx)
- MySQL runs inside the `db` service; credentials are root / 1223

Notes:
- The backend Dockerfile builds the Maven project; it requires internet access to download dependencies.
- If you already have MySQL on host using 3306, adjust ports or stop the local MySQL service.
