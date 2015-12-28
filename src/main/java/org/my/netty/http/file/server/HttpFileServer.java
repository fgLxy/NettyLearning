package org.my.netty.http.file.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpFileServer {
	
	private static final String DEFAULT_URL = "/";
	
	public static void main(String... args) {
		int port = 8080;
		String url = DEFAULT_URL;
		if(args != null && args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
				url = args.length > 1 ? args[1] : url;
			} catch(NumberFormatException e) {
				//使用默认端口值
			}
		}
		new HttpFileServer().run(url, port);
	}

	private void run(final String url, final int port) {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup)
				.option(ChannelOption.SO_BACKLOG, 100)
				.channel(NioServerSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel channel) throws Exception {
						channel.pipeline().addLast(new HttpRequestDecoder())
										.addLast(new HttpObjectAggregator(65536))
										.addLast(new HttpResponseEncoder())
										.addLast(new ChunkedWriteHandler())
										.addLast(new HttpFileServerHandler(url));
					}
					
				});
		try {
			ChannelFuture future = bootstrap.bind(port).sync();
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
}
