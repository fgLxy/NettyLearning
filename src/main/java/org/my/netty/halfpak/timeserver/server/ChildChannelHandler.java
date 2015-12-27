package org.my.netty.halfpak.timeserver.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		channel.pipeline().addLast(new LineBasedFrameDecoder(1024));
		channel.pipeline().addLast(new StringDecoder());
		channel.pipeline().addLast(new TimeServerHandler());
	}

	
	
}
