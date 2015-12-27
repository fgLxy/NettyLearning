package org.my.netty.halfpak.timeserver.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class TimeClient {
	
	public static void main(String... args) {
		int port = 8080;
		if(args != null && args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch(NumberFormatException e) {
				//使用默认端口值
			}
		}
		new TimeClient().connect(port, "127.0.0.1");
	}

	private void connect(int port, String host) {
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group)
				.option(ChannelOption.TCP_NODELAY, true)
				.channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel channel) throws Exception {
						channel.pipeline().addLast(new LineBasedFrameDecoder(1024));
						channel.pipeline().addLast(new StringDecoder());
						channel.pipeline().addLast(new TimeClientHandler());
					}
					
				});
		ChannelFuture future;
		try {
			future = bootstrap.connect(host, port).sync();
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}
	
}
