package org.my.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable {

	private Selector selector;
	private ServerSocketChannel servChannel;
	private volatile boolean stop;
	
	public MultiplexerTimeServer(int port) {
		try {
			selector = Selector.open();
			servChannel = ServerSocketChannel.open();
			servChannel.configureBlocking(false);
			servChannel.socket().bind(new InetSocketAddress(port), 1024);
			servChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("时间服务器启动，监听端口：" + port);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void stop() {
		this.stop = true;
	}

	@Override
	public void run() {
		while(!stop) {
			try {
				selector.select(1000);
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> keysIter = selectedKeys.iterator();
				while(keysIter.hasNext()) {
					SelectionKey key = keysIter.next();
					keysIter.remove();
					try {
						hanldeInput(key);
					} catch(Exception e) {
						if(key != null) {
							key.cancel();
							if(key.channel() != null) {
								key.channel().close();
							}
						}
					}
				}
			} catch(Throwable t) {
				t.printStackTrace();
			}
		}
		if(selector != null) {
			try {
				selector.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void hanldeInput(SelectionKey key) throws IOException {
		if(!key.isValid()) return;
		if(key.isAcceptable()) {
			ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
			SocketChannel socketChannel = serverChannel.accept();
			socketChannel.configureBlocking(false);
			socketChannel.register(selector, SelectionKey.OP_READ);
		}
		if(key.isReadable()) {
			SocketChannel socketChannel = (SocketChannel) key.channel();
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			int length = socketChannel.read(buffer);
			if(length > 0) {
				buffer.flip();
				byte[] bytes = new byte[buffer.remaining()];
				buffer.get(bytes);
				String body = new String(bytes, "UTF-8");
				System.out.println("接收到的命令为：" + body);
				String response = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
				buffer.clear();
				buffer.put(response.getBytes());
				buffer.flip();
				socketChannel.write(buffer);
			} else if(length == 0) {
				;
			} else {
				key.cancel();
				socketChannel.close();
			}
		}
	}

}
