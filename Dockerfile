# 第一阶段：构建阶段
FROM maven:3.8.1-jdk-8-slim AS builder

# 工作目录
WORKDIR /app

# 首先复制 pom.xml 并下载依赖，利用 Docker 缓存机制
COPY pom.xml .
RUN mvn dependency:go-offline

# 复制源代码
COPY src ./src

# 构建应用
RUN mvn package -DskipTests

# 第二阶段：运行阶段
FROM openjdk:8-jre-slim

# 添加标签信息
LABEL maintainer="SmartClass Team"
LABEL description="SmartClass Backend Application"

# 创建非 root 用户运行应用
RUN addgroup --system smartclass && adduser --system --group smartclass

# 工作目录
WORKDIR /app

# 从构建阶段复制构建结果
COPY --from=builder /app/target/smartclass-ubanillx-0.0.1-SNAPSHOT.jar /app/app.jar

# 暴露应用端口
EXPOSE 8101

# 将应用所有权授予非 root 用户
RUN chown -R smartclass:smartclass /app
USER smartclass

# 设置应用启动时的默认环境变量
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -q --spider http://localhost:8101/api/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]