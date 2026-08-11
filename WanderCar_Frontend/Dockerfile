# syntax=docker/dockerfile:1

# =====================================================================
#  WanderCar — React frontend (Vite 8 + React 19), served by nginx
#  Build context: ./WanderCar_Frontend
# =====================================================================

# ---------------------------------------------------------------------
# Stage 1 — build the static bundle
# ---------------------------------------------------------------------
FROM node:22-bookworm-slim AS build

WORKDIR /build

# Lockfile first so the npm layer is reused whenever only src changes.
COPY package.json package-lock.json ./

RUN --mount=type=cache,target=/root/.npm \
    npm ci

COPY . .

# Baked into the bundle at build time — Vite inlines import.meta.env at
# compile time, so this cannot be changed later without rebuilding. Keeping
# it relative ('/api') is what lets the nginx proxy below decide which
# backend the app talks to at *run* time instead.
ARG VITE_API_BASE_URL=/api
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

RUN npm run build

# ---------------------------------------------------------------------
# Stage 2 — runtime
# nginx-unprivileged rather than stock nginx: it runs as a non-root user
# and listens on 8080 out of the box, matching the Java and .NET images.
# ---------------------------------------------------------------------
FROM nginxinc/nginx-unprivileged:1.27-bookworm AS runtime

LABEL org.opencontainers.image.title="wandercar-frontend" \
      org.opencontainers.image.description="WanderCar fleet management UI (React + nginx)" \
      org.opencontainers.image.version="1.0.0"

# curl backs the healthcheck, same as the two backend images. Installed as
# root, then dropped back to the image's own non-root nginx user.
USER root
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

# Templates in this directory are envsubst'd into /etc/nginx/conf.d/ by the
# image entrypoint, which is how API_UPSTREAM reaches the config.
COPY nginx/default.conf.template /etc/nginx/templates/default.conf.template

COPY --from=build /build/dist /usr/share/nginx/html

RUN chown -R nginx:nginx /usr/share/nginx/html

USER nginx

EXPOSE 8080

# Which backend /api/* is forwarded to. Override in compose to point the UI
# at dotnet-backend:8080 instead — no rebuild required.
ENV API_UPSTREAM=java-backend:8080 \
    TZ=Asia/Kolkata

# Confirms nginx is serving the real built bundle, not just accepting
# connections — an empty or half-copied html root still returns 200.
HEALTHCHECK --interval=5m --timeout=5s --start-period=30s --start-interval=5s --retries=3 \
  CMD curl -fsS http://localhost:8080/ | grep -q '<div id="root">' || exit 1
