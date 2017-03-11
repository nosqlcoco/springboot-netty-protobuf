package com.netty.server.handler;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
@Component
public class IdleServerHandler extends ChannelInboundHandlerAdapter {
	public Logger log = Logger.getLogger(this.getClass());
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			String type = "";
			if (event.state() == IdleState.READER_IDLE) {
				type = "read idle";
			} else if (event.state() == IdleState.WRITER_IDLE) {
				type = "write idle";
			} else if (event.state() == IdleState.ALL_IDLE) {
				type = "all idle";
			}
			log.debug(ctx.channel().remoteAddress()+"超时类型：" + type);
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}
}
