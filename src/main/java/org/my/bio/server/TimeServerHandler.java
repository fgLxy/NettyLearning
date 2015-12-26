package org.my.bio.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class TimeServerHandler implements Runnable {
	
	private Socket socket;
	
	public TimeServerHandler(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try (
				InputStreamReader inR = new InputStreamReader(socket.getInputStream());
				BufferedReader in = new BufferedReader(inR);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				) {
			
			String body = in.readLine();
			if(body == null) return;
			System.out.println("接收到的命令为：" + body);
			String response = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
			out.println(response);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
