package org.my.netty.delimiter.echoserver.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class EchoClient {
	
	public static void main(String... args) {
		int port = 8080;
		if(args != null && args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch(NumberFormatException e) {
				//使用默认端口值
			}
		}
		new EchoClient().connect("127.0.0.1", port);
	}

	private void connect(String host, int port) {
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group)
				.option(ChannelOption.TCP_NODELAY, true)
				.channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel channel) throws Exception {
						ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
						channel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter))
										.addLast(new StringDecoder())
										.addLast(new EchoClientHandler());
					}
					
				});
		try {
			ChannelFuture future = bootstrap.connect(host, port).sync();
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}
	
}
