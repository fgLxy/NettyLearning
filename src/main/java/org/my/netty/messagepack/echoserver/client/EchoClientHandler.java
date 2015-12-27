package org.my.netty.messagepack.echoserver.client;

import org.my.netty.messagepack.echoserver.entity.UserInfo;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class EchoClientHandler extends ChannelHandlerAdapter {
private int counter;
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		UserInfo[] userInfos = createUserInfos(500);
		for(UserInfo info : userInfos) {
			ctx.write(info);
		}
		ctx.flush();
	}

	private UserInfo[] createUserInfos(int length) {
		UserInfo[] userInfos = new UserInfo[length];
		for(int i = 0; i < length; i++) {
			userInfos[i] = new UserInfo();
			userInfos[i].setAge(i);
			userInfos[i].setName("HelloWorld --->" + i);
		}
		return userInfos;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("第" + ++counter + "条消息 :" + msg);
	}

}
