package org.my.netty.halfpak.timeserver.server;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeServerHandler extends ChannelHandlerAdapter {
	
	private int counter;

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("接收到第" + ++counter + "条命令");
		
		String request = (String) msg;
		String response = ("QUERY TIME ORDER".equalsIgnoreCase(request) ? 
				new Date(System.currentTimeMillis()).toString() : "BAD ORDER")
				+ System.getProperty("line.separator");
		ByteBuf responseBuf = Unpooled.copiedBuffer(response.getBytes());
		ctx.writeAndFlush(responseBuf);
	}


}
