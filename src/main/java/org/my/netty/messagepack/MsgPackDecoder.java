package org.my.netty.messagepack;

import java.util.List;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class MsgPackDecoder extends MessageToMessageDecoder<ByteBuf> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		byte[] bytes = new byte[msg.readableBytes()];
		msg.readBytes(bytes);
		MessagePack msgPack = new MessagePack();
		out.add(msgPack.read(bytes));
	}

}
