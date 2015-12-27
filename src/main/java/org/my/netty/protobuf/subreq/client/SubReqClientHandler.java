package org.my.netty.protobuf.subreq.client;

import java.util.ArrayList;
import java.util.List;

import org.my.protobuf.test.protobuf.SubscribeReqProto.SubscribeReq;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class SubReqClientHandler extends ChannelHandlerAdapter {

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		SubscribeReq[] reqs = createReqs(100);
		for(SubscribeReq req : reqs) {
			ctx.write(req);
		}
		ctx.flush();
	}

	private SubscribeReq[] createReqs(int length) {
		SubscribeReq.Builder builder = SubscribeReq.newBuilder();
		List<String> address = new ArrayList<>();
		address.add("NanJing YuHuaTai");
		address.add("BeiJing LiuLiChang");
		address.add("ShenZhen HongShuLin");
		builder.addAllAddress(address);
		
		SubscribeReq[] reqs = new SubscribeReq[length];
		
		for(int i = 0; i < length; i++) {
			builder.setSubReqID(i);
			builder.setUserName("testName_" + i);
			builder.setProductName("productName_" + i);
			reqs[i] = builder.build();
		}
		return reqs;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("服务器的回复为 ： " + msg.toString());
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

}
