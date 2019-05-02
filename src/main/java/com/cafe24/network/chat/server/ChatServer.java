package com.cafe24.network.chat.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 서버에서 accept 메소드를 통해 Socket을 받아오는 클래스
 * @author 김석현
 *
 */
public class ChatServer {
	private static final int PORT = 7009;
	
	public static List<PrintWriter> prList = new ArrayList<PrintWriter>();
	public static Map<PrintWriter, String> userMap = new HashMap<PrintWriter, String>();
	
	
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
				
				Thread thread = new ChatReceiveThread(socket);
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
	
	/**
	 * 서버입장에서 log 메세지를 기록하는 메소드
	 * @param message log 메세지
	 */
	public static void log(String message) {
		System.out.println("[server#"+Thread.currentThread().getId()+"] " + message);
	}
}
