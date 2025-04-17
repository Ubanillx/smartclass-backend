# 第一阶段：构建阶段
FROM maven:3.8.1-jdk-8-slim AS builder

# 工作目录
WORKDIR /app

# 设置Maven镜像源为阿里云，加速依赖下载（可选）
COPY settings.xml /usr/share/maven/conf/

# 首先复制 pom.xml 并下载依赖，利用 Docker 缓存机制
COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn ./.mvn
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用
RUN mvn package -DskipTests -B

# 第二阶段：运行阶段
FROM openjdk:8-jre-slim

# 添加标签信息
LABEL maintainer="SmartClass Team"
LABEL description="SmartClass Backend Application"
LABEL version="1.0.0"

# 创建非 root 用户运行应用
RUN addgroup --system smartclass && adduser --system --group smartclass

# 工作目录
WORKDIR /app

# 从构建阶段复制构建结果
COPY --from=builder /app/target/smartclass-ubanillx-0.0.1-SNAPSHOT.jar /app/app.jar

# 复制可能的配置文件（如果需要特定配置）
COPY src/main/resources/application-prod.yml /app/config/

# 暴露应用端口
EXPOSE 8101

# 定义数据卷，用于存储可能的上传文件等
VOLUME ["/app/data", "/app/logs"]

# 将应用所有权授予非 root 用户
RUN chown -R smartclass:smartclass /app
USER smartclass

# 设置应用启动时的默认环境变量
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms512m -Xmx1024m"
ENV SERVER_PORT=8101
ENV TZ=Asia/Shanghai

# MySQL配置默认值（会被docker-compose中的环境变量覆盖）
ENV SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/smart_class
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=root123456

# Redis配置默认值（会被docker-compose中的环境变量覆盖）
ENV SPRING_REDIS_HOST=localhost
ENV SPRING_REDIS_PORT=6379
ENV SPRING_REDIS_PASSWORD=

# 健康检查 - 使用Java检查应用是否运行
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD java -version || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -Dserver.port=$SERVER_PORT -Dspring.datasource.url=$SPRING_DATASOURCE_URL -Dspring.datasource.username=$SPRING_DATASOURCE_USERNAME -Dspring.datasource.password=$SPRING_DATASOURCE_PASSWORD -Dspring.redis.host=$SPRING_REDIS_HOST -Dspring.redis.port=$SPRING_REDIS_PORT -Dspring.redis.password=$SPRING_REDIS_PASSWORD -jar /app/app.jar"]