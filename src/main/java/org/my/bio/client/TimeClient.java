package org.my.bio.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
		Socket socket = null;
		
		try {
			socket = new Socket("127.0.0.1", port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try(
				InputStreamReader inR = new InputStreamReader(socket.getInputStream());
				BufferedReader in = new BufferedReader(inR);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				) {
			String order = "QUERY TIME ORDER";
			out.println(order);
			String body = in.readLine();
			System.out.println("当前时间为:" + body);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
