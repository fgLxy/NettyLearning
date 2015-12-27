package org.my.netty.protobuf.subreq.server;

import org.my.protobuf.test.protobuf.SubscribeReqProto.SubscribeReq;
import org.my.protobuf.test.protobuf.SubscribeRespProto.SubscribeResp;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class SubReqServerHandler extends ChannelHandlerAdapter {

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		SubscribeReq req = (SubscribeReq) msg;
		System.out.println("服务器接收到的客户端请求为 ：" + req.toString());
		ctx.writeAndFlush(resp(req));
	}

	private SubscribeResp resp(SubscribeReq req) {
		SubscribeResp.Builder builder = SubscribeResp.newBuilder();
		builder.setSubReqID(req.getSubReqID());
		builder.setRespCode(0);
		builder.setDesc(req.getProductName() + " success");
		return builder.build();
	}

}
