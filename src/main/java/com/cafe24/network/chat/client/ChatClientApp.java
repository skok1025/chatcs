package com.cafe24.network.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;



public class ChatClientApp {

	private static final String SERVER_IP = "192.168.1.34";
	private static final int SERVER_PORT = 7006;

	public static void main(String[] args) {
		String name = null;
		Scanner scanner = new Scanner(System.in);

		while (true) {

			System.out.println("대화명을 입력하세요.");
			System.out.print(">>> ");
			name = scanner.nextLine();

			if (name.isEmpty() == false) {
				break;
			}

			System.out.println("대화명은 한글자 이상 입력해야 합니다.\n");
		}

		// 1. 소켓 만들고
		// 2. iostream
		// 3. join 프로토콜 join::name 성공
		//

		Socket socket = null;

		try {
			socket = new Socket();
			// 3. 서버연결
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
			log("connected");

			// 4. IOStream 생성(받아오기)
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			PrintWriter pr = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true); // autoFlush
																												// true
			System.out.println(pr);
			
				
			pr.println("JOIN::"+name);
			
			scanner.close();

			new ChatWindow(name,socket, br, pr).show();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
	public static void log(String log) {
		System.out.println("[client] " + log);
	}

}
