package org.my.nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TimeClientHandle implements Runnable {
	
	private Selector selector;
	private SocketChannel socketChannel;
	private String host;
	private int port;
	private volatile boolean stop;
	
	public TimeClientHandle(String host, int port) {
		try {
			this.selector = Selector.open();
			this.socketChannel = SocketChannel.open();
			this.socketChannel.configureBlocking(false);
			this.host = host;
			this.port = port;
			stop = false;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void run() {
		try {
			doConnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(!stop) {
			try {
				selector.select(1000);
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> keysIter = selectionKeys.iterator();
				while(keysIter.hasNext()) {
					SelectionKey key = keysIter.next();
					keysIter.remove();
					try {
						handlerInput(key);
					} catch(Exception e) {
						if(key != null) {
							key.cancel();
							if(key.channel() != null) {
								key.channel().close();
							}
						}
					}
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void handlerInput(SelectionKey key) throws IOException {
		if(!key.isValid()) return;
		
		SocketChannel channel = (SocketChannel) key.channel();
		if(!channel.finishConnect()) {
			System.exit(1);
		}
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		if(key.isConnectable()){
			channel.register(selector, SelectionKey.OP_READ);
			String body = "QUERY TIME ORDER";
			buffer.put(body.getBytes());
			buffer.flip();
			channel.write(buffer);
		}
		if(key.isReadable()) {
			int length = channel.read(buffer);
			buffer.flip();
			if(length > 0) {
				byte[] bytes = new byte[buffer.remaining()];
				buffer.get(bytes);
				String body = new String(bytes, "UTF-8");
				System.out.println("当前时间为:" + body);
				this.stop = true;
			} else if(length == 0) {
				
			} else {
				key.cancel();
				channel.close();
			}
		}
		
	}

	private void doConnect() throws IOException {
		if(socketChannel.connect(new InetSocketAddress(host, port))) {
			socketChannel.register(selector, SelectionKey.OP_READ);
		} else {
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
		}
	}

}
