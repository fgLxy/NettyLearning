package org.my.protobuf.test;

import java.util.ArrayList;
import java.util.List;

import org.my.protobuf.test.protobuf.SubscribeReqProto;
import org.my.protobuf.test.protobuf.SubscribeReqProto.SubscribeReq;

import com.google.protobuf.InvalidProtocolBufferException;

public class TestSubscribeReqProto {
	
	
	public static void main(String... args) throws InvalidProtocolBufferException {
		
		SubscribeReqProto.SubscribeReq req = createSubscribeReq();
		System.out.println("Before encode : " + req.toString());
		SubscribeReqProto.SubscribeReq req1 = decode(encode(req));
		System.out.println("After decode : " + req.toString());
		System.out.println("Assert equals : --> " + req1.equals(req));
	}

	private static SubscribeReq decode(byte[] bytes) throws InvalidProtocolBufferException {
		return SubscribeReq.parseFrom(bytes);
	}

	private static byte[] encode(SubscribeReq req) {
		return req.toByteArray();
	}

	private static SubscribeReq createSubscribeReq() {
		SubscribeReq.Builder builder = SubscribeReq.newBuilder();
		builder.setSubReqID(1);
		builder.setUserName("fgLxy");
		builder.setProductName("protobuf test");
		List<String> address = new ArrayList<>();
		address.add("NanJing YuHuaTai");
		address.add("BeiJing LiuLiChang");
		address.add("ShenZhen HongShuLin");
		builder.addAllAddress(address);
		return builder.build();
	}
	
	
}
