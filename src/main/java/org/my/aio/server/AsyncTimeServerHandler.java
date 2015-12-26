package org.my.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeServerHandler implements Runnable {

	int port;
	CountDownLatch latch;
	AsynchronousServerSocketChannel serverChannel;
	
	public AsyncTimeServerHandler(int port) {
		this.port = port;
		try {
			serverChannel = AsynchronousServerSocketChannel.open();
			serverChannel.bind(new InetSocketAddress(port));
			System.out.println("时间服务器启动，监听端口：" + port);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	@Override
	public void run() {
		latch = new CountDownLatch(1);
		serverChannel.accept(this, new AcceptCompletionHandler());
		try {
			latch.await();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

}
