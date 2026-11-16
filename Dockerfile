
# ==============================================================================
# STAGE 1: Build application WAR
# ==============================================================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests -B

# ==============================================================================
# STAGE 2: Apache Tomcat 10.1 Runtime
# ==============================================================================
FROM tomcat:10.1-jdk17-temurin

WORKDIR /usr/local/tomcat

# Remove default applications
RUN rm -rf webapps/ROOT webapps/docs webapps/examples \
    webapps/manager webapps/host-manager

# Deploy application
COPY --from=builder /app/target/InventoryManagementSystem.war \
    webapps/InventoryManagementSystem.war

# Default root redirect
RUN mkdir -p webapps/ROOT && \
    printf '%s\n' \
    '<% response.sendRedirect("/InventoryManagementSystem/"); %>' \
    > webapps/ROOT/index.jsp

# Default port
ENV PORT=8080

EXPOSE 8080

# Configure dynamic Render port
RUN printf '%s\n' \
    '#!/bin/sh' \
    'set -eu' \
    'TARGET_PORT="${PORT:-8080}"' \
    'echo "Configuring Tomcat port: ${TARGET_PORT}"' \
    'sed -i "0,/port=\"8080\"/s//port=\"${TARGET_PORT}\"/" /usr/local/tomcat/conf/server.xml' \
    'echo "Starting Tomcat..."' \
    'exec catalina.sh run' \
    > /usr/local/tomcat/bin/render-entrypoint.sh && \
    chmod +x /usr/local/tomcat/bin/render-entrypoint.sh

ENTRYPOINT ["/usr/local/tomcat/bin/render-entrypoint.sh"]
