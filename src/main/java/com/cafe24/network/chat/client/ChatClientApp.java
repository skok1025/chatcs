package com.cafe24.network.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * 대화명을 입력하고  ChatWindow 를 띄우는 역할의 클래스
 * @author 김석현
 *
 */
public class ChatClientApp {

	private static final String SERVER_IP = "192.168.1.34";
	private static final int SERVER_PORT = 7013;

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
				
			
			String[] userNames = br.readLine().split(",");
			List<String> userList = new ArrayList<String>();
			
			
			for(String userName:userNames) {
				userList.add(userName);
				
				if(name.equals(userName)) {
					System.out.println("대화명이 중복됩니다. 클라이언트를 종료합니다.");
					System.exit(0);
					break;
				}
			}
			
			pr.println("JOIN::"+name);
			
			scanner.close();

			new ChatWindow(name,socket, br, pr, userList).show();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
	/**
	 * 클라이언트 입장에서 log 메세지를 남기는 메소드
	 * @param message log 메세지
	 */
	public static void log(String message) {
		System.out.println("[client] " + message);
	}

}
