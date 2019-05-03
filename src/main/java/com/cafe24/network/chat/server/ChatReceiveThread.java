package com.cafe24.network.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

/**
 * 프로토콜에 따른 데이터 처리와 브로드캐스트 역할을 하는 클래스
 * 
 * @author 김석현
 *
 */
public class ChatReceiveThread extends Thread {

	private Socket socket;

	public ChatReceiveThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		InetSocketAddress inetRemoteSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
		String remoteHostAddress = inetRemoteSocketAddress.getAddress().getHostAddress();
		int remotePort = inetRemoteSocketAddress.getPort();
		ChatServer.log("connected by client[" + remoteHostAddress + ":" + remotePort + "]");

		try {
			// 4. IOStream 생성(받아오기)
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			PrintWriter pr = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true); // autoFlush
																												// true

			// userMap 에 있는 userName 다 보내기
			StringBuilder userNames = new StringBuilder();

			for (PrintWriter key : ChatServer.userMap.keySet()) {
				userNames.append(ChatServer.userMap.get(key) + ",");
			}

			pr.println(userNames); // 사용자 대화명 전송 (현재 대화방 참여자 리스트 출력을 위해)

			String firstData = br.readLine();
			String[] firstDatas = firstData.split("::");
			String firstProtocol = firstDatas[0];
			String name = firstDatas[1];

			if ("JOIN".equals(firstProtocol)) {
				ChatServer.userMap.put(pr, name);
				ChatServer.prList.add(pr);
				ChatServer.log(firstData);
				Broadcast(ChatServer.userMap.get(pr) + " 님이 입장 하였습니다.");

				ChatServer.log("received:" + ChatServer.userMap.get(pr) + "님이 입장 하였습니다.");

			}

			// 입장처리완료

			while (true) {
				// 5. 데이터 읽기

				String data = br.readLine();

				String[] datas = data.split("::");
				if (datas.length >= 2) {
					String protocol = datas[0];
					String message = datas[1];

					if (data == null) {
						ChatServer.log("closed by client");
						break;
					}

					if (data.startsWith("MSG::/q")) { // 귓속말모드 (/q 아이디 메세지)
						String[] qMessageSender = data.split(":sender:");

						String[] qDatas = qMessageSender[0].split(" ");
						if (qDatas.length >= 3) { // MSG::/q 받는사람 메세지 보내는사람
							String qReceive = qDatas[1];
							String qSend = qMessageSender[1];

							StringBuilder qMessage = new StringBuilder();
							for (int i = 2; i < qDatas.length; i++) {
								qMessage.append(qDatas[i] + " ");
							}

							Multicast(qSend, qReceive, qMessage.toString());
						}

					} else { // 일반모드
						for (PrintWriter printWriter : ChatServer.prList) {
							if (printWriter == pr) { // 해당 보내는 클라이언트인지 확인
								ChatServer.log(data);
								if ("MSG".equals(protocol)) {
									data = ChatServer.userMap.get(pr) + ":" + message;

								} else if ("EXIT".equals(protocol)) {
									data = ChatServer.userMap.get(pr) + " 님이 퇴장 하였습니다";
									ChatServer.userMap.remove(pr);

									pr.close();
								}
								break;
							}
						}
						// 6. 데이터 쓰기
						Broadcast(data);
					}

					ChatServer.log("received:" + data);
				}
			}
		} catch (SocketException e) {
			System.out.println("[server] sudden closed by client");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (socket != null && !socket.isClosed()) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 모든 사용자에게 메세지를 발송해주는 메소드
	 * 
	 * @param message 메세지 내용
	 */
	private void Broadcast(String message) {
		for (PrintWriter printwriter : ChatServer.prList) {
			printwriter.println(message);
		}

	}

	/**
	 * 귓속말을 위한 Multicast 메소드
	 * 
	 * @param qSend    귓속말을 보내는 사람의 대화명
	 * @param qReceive 귓속말을 받는 사람의 대화명
	 * @param message  메세지 내용
	 */
	private void Multicast(String qSend, String qReceive, String message) {
		boolean isReceiverExist = false;
		for (PrintWriter printWriter : ChatServer.prList) {
			if (qReceive.equals(ChatServer.userMap.get(printWriter))) { // 귓속말할 대상을 찾음
				printWriter.println(qReceive + "<<" + qSend + "(귓속말):" + message);
				isReceiverExist = true;
			}
			if (qSend.equals(ChatServer.userMap.get(printWriter))) {
				printWriter.println(qSend + ">>" + qReceive + "(귓속말):" + message);
			}

		}

		if (!isReceiverExist) { // 받는 사람이 없는 경우에
			for (PrintWriter printWriter : ChatServer.prList) {
				if (qSend.equals(ChatServer.userMap.get(printWriter))) {
					printWriter.println(qReceive + "는 존재하지 않는 대화명입니다....(귓속말 실패)");
				}
			}

		}
	}

}
