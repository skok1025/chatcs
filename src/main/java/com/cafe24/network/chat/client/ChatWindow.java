package com.cafe24.network.chat.client;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * 채팅 윈도우 환경을 제공하는 클래스
 * @author 김석현
 *
 */
public class ChatWindow {
	
	private String name;
	private Socket socket;
	private BufferedReader br;
	private PrintWriter pr;
	private List<String> userList;
	
	

	private Frame frame;
	private Panel pannel;
	private Button buttonSend;
	private TextField textField;
	private TextArea textArea;
	private TextArea userListArea;

	public ChatWindow(String name,Socket socket,BufferedReader br,PrintWriter pr,List<String> userList) {
		frame = new Frame(name);
		this.name = name;
		this.userList = userList;
		
		pannel = new Panel();
		buttonSend = new Button("Send");
		textField = new TextField();
		textArea = new TextArea(30, 80);
		userListArea = new TextArea(5,20);
		
		this.socket = socket;
		this.br = br;
		this.pr = pr;
	}
	
	private void finish() {
		pr.println("EXIT::"+name);
		//pr.println(ChatClientApp.getProtocol("EXIT", name, pr));
		
		// socket 정리
		try {
			if(socket!= null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		System.exit(0);
		
	}
	
	
	/**
	 * GUI 환경 제공 메소드
	 */
	public void show() { // thread 작업만
		// Button
		buttonSend.setBackground(Color.GRAY);
		buttonSend.setForeground(Color.WHITE);
		buttonSend.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent actionEvent ) {
				sendMessage();
			}
		});

		// Textfield
		textField.setColumns(80);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				char keyCode = e.getKeyChar();
				if(keyCode == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}
			
		});

		// Pannel
		pannel.setBackground(Color.LIGHT_GRAY);
		pannel.add(textField);
		pannel.add(buttonSend);
		frame.add(BorderLayout.SOUTH, pannel);

		// TextArea
		textArea.setEditable(false);
		userListArea.setEditable(false);
		
		frame.add(BorderLayout.CENTER, textArea);
		
		userListArea.setBackground(Color.LIGHT_GRAY);
		
		
		frame.add(BorderLayout.EAST, userListArea);
		
		
		// Frame
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				finish();
			}
		});
		frame.setVisible(true);
		frame.pack();
		
		
		
		while(true) {
			try {
				String data = br.readLine();
				
				// 대화방 리스트 출력하기 
				if(data.endsWith("님이 입장 하였습니다.")) {
					String addUser = data.split(" ")[0];
					userList.add(addUser);
					updateUserList();
				}
				
				if(data.endsWith("님이 퇴장 하였습니다")) {
					String removeUser = data.split(" ")[0];
					int index = 0;
					for(String userName:userList) {
						if(userName.equals(removeUser)) {
							userList.remove(index);
							break;
						}
						index++;
					}
					updateUserList();
				}
				// 대화방 리스트 출력하기
				
				if(data == null) {
					log("closed by server");
					break;
				}
				
				updateTextArea(data);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	private void updateUserList() {
		userListArea.setText("<<참여자리스트>>\n");
		
		for(String userName:userList) {
			userListArea.append(userName+"\n");
		}
	}
	
	/**
	 * 상단 메세지 상태창에 메세지를 추가하는 메소드
	 * @param message 추가된 메세지
	 */
	private void updateTextArea(String message) {
		
		textArea.append(message);
		textArea.append("\n");
	}
	
	/**
	 * 메세지를 서버로 전송하는 메소드
	 */
	private void sendMessage() {
		
		String message = textField.getText();
		pr.println("MSG::"+message);
		
		textField.setText("");
		textField.requestFocus();
		
		//test
		//updateTextArea(message);
	}
	
	
	/**
	 * 클라이언트 입장에서 log 메세지를 기록하는 메소드
	 * @param message
	 */
	public static void log(String message) {
		System.out.println("[client] " + message);
	}
}
