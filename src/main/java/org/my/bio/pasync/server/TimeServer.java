package org.my.bio.pasync.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.my.bio.server.TimeServerHandler;

public class TimeServer {

	public static void main(String... args) {
		int port = 8080;
		if(args != null && args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch(NumberFormatException e) {
				//使用默认值
			}
		}
		ServerSocket server = null;
		TimeServerHandlerExecutorPool executor = new TimeServerHandlerExecutorPool(50, 10000);
		try {
			server = new ServerSocket(port);
			System.out.println("时间服务器启动，监听端口：" + port);
			while(true) {
				Socket socket = server.accept();
				executor.execute(new TimeServerHandler(socket));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();
		}
	}

}
