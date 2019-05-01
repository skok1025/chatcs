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

public class EchoServerReceiveThread extends Thread {
	
	private Socket socket;
	public static Map<String, String> userMap = new HashMap<String, String>();
	
	public EchoServerReceiveThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		InetSocketAddress inetRemoteSocketAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
		String remoteHostAddress = inetRemoteSocketAddress.getAddress().getHostAddress();
		int remotePort = inetRemoteSocketAddress.getPort();
		EchoServer.log("connected by client[" + remoteHostAddress + ":" + remotePort + "]" );
		
		try {
			//4. IOStream 생성(받아오기)
			BufferedReader br = new BufferedReader( 
					new InputStreamReader( socket.getInputStream(),
										   "utf-8") ); 				
			PrintWriter pr = new PrintWriter( 
					new OutputStreamWriter( socket.getOutputStream(), "utf-8"), true ); // autoFlush true
			
			String[] firstdata = br.readLine().split("::");
			String name = firstdata[1];
			String prName = firstdata[2];
			
			System.out.println("name:"+name);
			System.out.println("prname:"+prName);
			
			userMap.put(prName, name);
			EchoServer.prList.add(pr);
			
			for(PrintWriter printwriter:EchoServer.prList) {
				printwriter.println(name+"님이 입장 하였습니다");
			}
			
			while(true) {
				//5. 데이터 읽기
				String data = br.readLine();
				String[] datas = data.split("::");
				String happen = datas[0];
				String message = datas[1];
				String prkey = datas[2];
				
				if("MSG".equals(happen)) {					
					data = userMap.get(prkey) +":"+ message;
				} else if("EXIT".equals(happen)) {					
					data = userMap.get(prkey) +"님이 퇴장 하였습니다";
				} 
				
				if(data == null) {
					EchoServer.log("closed by client");
					break;
				}
				
				EchoServer.log("received:" + data);
				
				//6. 데이터 쓰기
				
				for(PrintWriter printwriter:EchoServer.prList) {
					printwriter.println(data);
				}
				
			}
		}catch(SocketException e) {
			System.out.println("[server] sudden closed by client");
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(socket != null && socket.isClosed() == false ) {
					socket.close();
				}
			}catch(IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	
}
