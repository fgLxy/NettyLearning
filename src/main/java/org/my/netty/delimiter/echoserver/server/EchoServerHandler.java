package org.my.netty.delimiter.echoserver.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class EchoServerHandler extends ChannelHandlerAdapter {

	private int counter;
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("接收到第" + ++counter + "条消息");
		String request = (String) msg;
		System.out.println("接收到的消息为:" + request);
		ByteBuf responseBuf = Unpooled.copiedBuffer((request + "$_").getBytes());
		ctx.writeAndFlush(responseBuf);
	}

}
