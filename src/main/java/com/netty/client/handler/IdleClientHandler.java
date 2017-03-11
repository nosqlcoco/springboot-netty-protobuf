package com.netty.client.handler;

import org.apache.log4j.Logger;

import com.netty.common.protobuf.Message;
import com.netty.client.NettyClient;
import com.netty.common.protobuf.Command.CommandType;
import com.netty.common.protobuf.Message.MessageBase;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class IdleClientHandler extends SimpleChannelInboundHandler<Message> {
	public Logger log = Logger.getLogger(this.getClass());	
	
	private NettyClient nettyClient;
	private int heartbeatCount = 0;
	private final static String CLIENTID = "123456789";


	/**
	 * @param nettyClient
	 */
	public IdleClientHandler(NettyClient nettyClient) {
		this.nettyClient = nettyClient;
	}

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
			System.out.println( ctx.channel().remoteAddress()+"超时类型：" + type);
			sendPingMsg(ctx);
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

	/**
	 * 发送ping消息
	 * @param context
	 */
	protected void sendPingMsg(ChannelHandlerContext context) {
		context.writeAndFlush(
				MessageBase.newBuilder()
				.setClientId(CLIENTID)
				.setCmd(CommandType.PING)
				.setData("This is a ping msg")
				.build()
				);
		heartbeatCount++;
		log.info("Client sent ping msg to " + context.channel().remoteAddress() + ", count: " + heartbeatCount);
	}

	/**
	 * 处理断开重连
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		nettyClient.doConnect();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

	}
}
