package com.ubanillx.smartclass.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket心跳处理器
 */
@Slf4j
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.debug("连接空闲超时，关闭通道: {}", ctx.channel().id());
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
} 