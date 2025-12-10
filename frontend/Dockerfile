# Stage 1: Build
FROM node:20-alpine AS build
WORKDIR /app

# Copy package files first (for layer caching)
COPY package*.json ./
RUN npm install

# Copy source code
COPY . .

# Build arguments for environment configuration
ARG API_URL=http://backend:8080
ENV API_URL=$API_URL

# Build Angular application
RUN npm run build -- --configuration production

# Debug: List build output
RUN echo "=== Build output structure ===" && \
    ls -la /app/dist/curseando-frontend/ 2>/dev/null || echo "curseando-frontend not found" && \
    ls -la /app/dist/curseando-frontend/browser/ 2>/dev/null || echo "browser subdirectory not found" && \
    find /app/dist -type f -name "index.html" 2>/dev/null || echo "index.html not found"

# Stage 2: Runtime
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# Remove default nginx static assets
RUN rm -rf ./* .[^.]*

# Copy all dist files to temp location first
COPY --from=build /app/dist/curseando-frontend /tmp/dist

# Move files from browser subdirectory if it exists, otherwise use root
RUN if [ -d /tmp/dist/browser ]; then \
        cp -r /tmp/dist/browser/. /usr/share/nginx/html/; \
    else \
        cp -r /tmp/dist/. /usr/share/nginx/html/; \
    fi && \
    rm -rf /tmp/dist

# Fix permissions - nginx runs as user nginx (UID 101)
RUN chown -R 101:101 /usr/share/nginx/html && \
    chmod -R 755 /usr/share/nginx/html && \
    ls -la /usr/share/nginx/html && \
    test -f /usr/share/nginx/html/index.html || (echo "ERROR: index.html not found!" && ls -la /usr/share/nginx/html)

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost/ || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]

