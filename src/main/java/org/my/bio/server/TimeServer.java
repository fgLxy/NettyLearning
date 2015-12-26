package org.my.bio.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;;

public class TimeServer {

	public static void main(String... args) {
		int port = 8080;
		if(args != null && args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch(NumberFormatException e) {
				//使用默认端口值
			}
		}
		ServerSocket server = null;
		
		try {
			server = new ServerSocket(port);
			System.out.println("时间服务器启动，监听端口：" + port);
			while(true) {
				Socket socket = server.accept();//获得客户端得socket
				new Thread(new TimeServerHandler(socket), "BIOTimeServer").start();
			}
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
