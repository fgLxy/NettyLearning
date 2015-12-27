package org.my.netty.delimiter.echoserver.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class EchoClientHandler extends ChannelHandlerAdapter {

	private int counter;
	
	private static final byte[] ECHO_REQ = "Hello world!$_".getBytes();
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		for(int i = 0; i < 100; i++) {
			ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ));
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String response = (String) msg;
		System.out.println("第" + ++counter + "条消息");
		System.out.println("接收到的消息为：" + response);
	}

}
