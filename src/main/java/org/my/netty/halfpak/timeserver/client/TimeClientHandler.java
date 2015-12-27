package org.my.netty.halfpak.timeserver.client;


import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeClientHandler extends ChannelHandlerAdapter {
	
	private Logger logger = Logger.getLogger(TimeClientHandler.class.getName());
	
	private int counter;
	
	private byte[] req;
	
	public TimeClientHandler() {
		req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
	}
	
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ByteBuf buf = null;
		for(int i = 0; i < 100; i++) {
			buf = Unpooled.buffer(req.length);
			buf.writeBytes(req);
			ctx.writeAndFlush(buf);
		}
	}
	
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String request = (String) msg;
		System.out.println("这是第" + ++counter + "条回复");
		System.out.println("当前时间为：" + request);
    }
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.warning("Unexpected exception from downstream :" + cause.getMessage());
		ctx.close();
    }
}
