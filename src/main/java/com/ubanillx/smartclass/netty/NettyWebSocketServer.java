package com.ubanillx.smartclass.netty;

import com.ubanillx.smartclass.config.NettyWebSocketConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * Netty WebSocket服务器
 */
@Slf4j
@Component
public class NettyWebSocketServer {
    
    @Autowired
    private NettyWebSocketConfig config;
    
    @Autowired
    private WebSocketMessageHandler webSocketMessageHandler;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private ChannelManager channelManager;
    
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    
    /**
     * 启动Netty WebSocket服务器
     */
    @PostConstruct
    public void start() {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(
                            // HTTP编解码器
                            new HttpServerCodec(),
                            // 大数据流处理器
                            new ChunkedWriteHandler(),
                            // HTTP消息聚合器
                            new HttpObjectAggregator(65536),
                            // 空闲连接检测
                            new IdleStateHandler(config.getHeartbeatTimeout(), 0, 0, TimeUnit.SECONDS),
                            // 心跳处理器
                            new HeartbeatHandler(),
                            // WebSocket协议处理器
                            new WebSocketServerProtocolHandler("/ws", null, true, 65536),
                            // 认证处理器
                            new WebSocketAuthHandler(config.getAuthTimeout(), redisTemplate, channelManager),
                            // 消息处理器
                            webSocketMessageHandler
                        );
                    }
                });
            
            ChannelFuture future = bootstrap.bind(config.getPort()).sync();
            log.info("Netty WebSocket服务器已启动，端口: {}", config.getPort());
            
            // 异步监听服务器关闭事件
            future.channel().closeFuture().addListener(f -> {
                log.info("Netty WebSocket服务器已关闭");
                shutdown();
            });
        } catch (Exception e) {
            log.error("Netty WebSocket服务器启动失败", e);
            shutdown();
        }
    }
    
    /**
     * 关闭Netty WebSocket服务器
     */
    @PreDestroy
    public void shutdown() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        log.info("Netty WebSocket服务器资源已释放");
    }
} 