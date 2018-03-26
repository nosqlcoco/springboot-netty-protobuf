package com.netty.client;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.netty.client.handler.IdleClientHandler;
import com.netty.client.handler.LogicClientHandler;
import com.netty.common.protobuf.Message.MessageBase;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
/**
 * netty客户端
 * @author lenovo
 *
 */
public class NettyClient {
	public Logger log = Logger.getLogger(this.getClass());	

	private final static String HOST = "127.0.0.1";
	private final static int PORT = 8090;
	private final static int READER_IDLE_TIME_SECONDS = 20;//读操作空闲20秒
	private final static int WRITER_IDLE_TIME_SECONDS = 20;//写操作空闲20秒
	private final static int ALL_IDLE_TIME_SECONDS = 40;//读写全部空闲40秒

	private EventLoopGroup loop = new NioEventLoopGroup();

	public static void main(String[] args) throws Exception {
		NettyClient client = new NettyClient();  
		client.run();  
	}

	public void run() throws Exception {
		try {  
			doConnect(new Bootstrap(), loop);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * netty client 连接，连接失败10秒后重试连接
	 */
	public Bootstrap doConnect(Bootstrap bootstrap, EventLoopGroup eventLoopGroup) {
		try {
			if (bootstrap != null) {
				bootstrap.group(eventLoopGroup);
				bootstrap.channel(NioSocketChannel.class);
				bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
				bootstrap.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline p = ch.pipeline();

						p.addLast("idleStateHandler", new IdleStateHandler(READER_IDLE_TIME_SECONDS
								, WRITER_IDLE_TIME_SECONDS, ALL_IDLE_TIME_SECONDS, TimeUnit.SECONDS));
						p.addLast("idleTimeoutHandler", new IdleClientHandler(NettyClient.this));

						p.addLast(new ProtobufVarint32FrameDecoder());
						p.addLast(new ProtobufDecoder(MessageBase.getDefaultInstance()));

						p.addLast(new ProtobufVarint32LengthFieldPrepender());
						p.addLast(new ProtobufEncoder());

						p.addLast("clientHandler", new LogicClientHandler());
					}
				});
				bootstrap.remoteAddress(HOST, PORT);
				ChannelFuture f = bootstrap.connect().addListener((ChannelFuture futureListener)->{
					final EventLoop eventLoop = futureListener.channel().eventLoop();
					if (!futureListener.isSuccess()) {
						log.warn("Failed to connect to server, try connect after 10s");
						futureListener.channel().eventLoop().schedule(() -> doConnect(new Bootstrap(), eventLoop), 10, TimeUnit.SECONDS);
					}
				});
				f.channel().closeFuture().sync();
				eventLoopGroup.shutdownGracefully();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return bootstrap;
	}
}
