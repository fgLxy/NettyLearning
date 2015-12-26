package org.my.nio.client;

public class TimeClient {

	public static void main(String... args) {
		int port = 8080;
		if(args != null && args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch(NumberFormatException e) {
				//使用默认端口值
			}
		}
		new Thread(new TimeClientHandle("127.0.0.1", port), "NIOTimeClient").start();
	}

}
