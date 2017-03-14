package com.netty.server;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.netty.common.protobuf.Message.MessageBase;
import com.netty.server.handler.IdleServerHandler;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

@Component
@Qualifier("serverChannelInitializer")
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
	private final static int READER_IDLE_TIME_SECONDS = 20;//读操作空闲20秒
	private final static int WRITER_IDLE_TIME_SECONDS = 20;//写操作空闲20秒
	private final static int ALL_IDLE_TIME_SECONDS = 40;//读写全部空闲40秒
	
    @Autowired
    @Qualifier("authServerHandler")
    private ChannelInboundHandlerAdapter authServerHandler;
    
    @Autowired
    @Qualifier("logicServerHandler")
    private ChannelInboundHandlerAdapter logicServerHandler;
    
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
    	ChannelPipeline p = socketChannel.pipeline();
    	
    	p.addLast("idleStateHandler", new IdleStateHandler(READER_IDLE_TIME_SECONDS
    			, WRITER_IDLE_TIME_SECONDS, ALL_IDLE_TIME_SECONDS, TimeUnit.SECONDS));
	    p.addLast("idleTimeoutHandler", new IdleServerHandler());
	    
        p.addLast(new ProtobufVarint32FrameDecoder());
        p.addLast(new ProtobufDecoder(MessageBase.getDefaultInstance()));

        p.addLast(new ProtobufVarint32LengthFieldPrepender());
        p.addLast(new ProtobufEncoder());
	    
        p.addLast("authServerHandler", authServerHandler);
        p.addLast("hearableServerHandler", logicServerHandler);
    }
}
