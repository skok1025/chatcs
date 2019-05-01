package com.cafe24.network.chat.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class EchoServer {
	private static final int PORT = 7006;
	
	public static List<PrintWriter> prList = new ArrayList<PrintWriter>();
	
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		
		try {
			//1. 서버소켓 생성
			// 소켓 : TCP/IP 프로토콜의 프로그래머 인터페이스
			serverSocket = new ServerSocket();
			
			//2. 바인딩(binding)
			// 각종 값들이 확정되어 더 이상 변경할 수 없는 구속(bind)상태가 되는 것
			serverSocket.bind(new InetSocketAddress("0.0.0.0", PORT));
			
			log("server starts...[port:"+PORT+"]");
			
			
			while(true) {
			//3. accept
				Socket socket = serverSocket.accept();
				
				Thread thread = new EchoServerReceiveThread(socket);
				thread.start();
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if( serverSocket != null && !serverSocket.isClosed()) {
					serverSocket.close();	
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	
	public static void log(String log) {
		System.out.println("[server#"+Thread.currentThread().getId()+"] " + log);
	}
}
