package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ConsoleChatServer extends Thread{
	// room >> 0 : String : room code / 1 : HashMap : clients
	// clients >> 0 : Socket : chat socket / 1 : String : nickname
	private static HashMap<String, HashMap<Socket, String>> room = new HashMap<String, HashMap<Socket, String>>();
	private static HashMap<Socket, String> clients;
	
	private static ArrayList<Socket> rclients = new ArrayList<>();
	
	ServerSocket serverSock;
	
	public void openChatServer(Socket sock, String rNum) {
		InputStream fromClient = null;
		OutputStream toClient = null;
		byte[] buf = new byte[1024];
		int count; //몇 바이트의 글을 받았는지 카운트하는 변수
		String currentNum = Integer.toString(room.get(rNum).size());
		String myNick = room.get(rNum).get(sock);
		String enterInfo = "in#" + currentNum + "#" + myNick + "#" + "님이 입장하셨습니다." + "#";

		try {
			// 입장 할 때
			// 입장하는 나 자신에게는 나 포함 방의 모든 nick을 모두 보내기 -> member_list를 위해서
			// 입장해있는 다른 멤버들에겐 입장하는 나의 nick만 보내기
			// inout#currentNum#nick
			
			byte[] buffer;
			for (Socket s : room.get(rNum).keySet()) {
				// 누군가 입장했을 때 그 입장한 nick을 member list에 추가하기 위함
				if (s != sock) {
					buffer = (enterInfo + myNick).getBytes("UTF-8");
					toClient = s.getOutputStream();
					toClient.write(buffer);
					toClient.flush();
				} else {
					// 방만들기 또는 입장했들 때 내 화면에 member list를 보여주기 위함
					buffer = (enterInfo + room.get(rNum).values()).getBytes("UTF-8");
					toClient = sock.getOutputStream();
					toClient.write(buffer);
					toClient.flush();
				}
			}
			System.out.println(sock+ " : chat socket 연결됨 -> " + sock.getPort());
			fromClient = sock.getInputStream();
			while((count = fromClient.read(buf))!= -1) { //중단 ctrl c같은거 누르면 -1을 보내기떄문에
				String message = new String(buf, 0, count, "UTF-8");
				System.out.println("server message : " + sock.getPort() + " : " + message);
				if (message.subSequence(0, 19).equals("closeChattingSocket")) {
					System.out.println("closeChattingSocket(0~18) : " + message.subSequence(0, 19));
					for(Socket s : room.get(rNum).keySet()) {
						if(sock!=s) {
							toClient = s.getOutputStream();
							buf = ("out#" + Integer.toString(room.get(rNum).size() - 1) + "#" + message.split("#")[1] + "님이 퇴장하셨습니다.").getBytes("UTF-8");
							count = buf.length;
							toClient.write(buf, 0, count);
							toClient.flush();
						} else {
							toClient = sock.getOutputStream();
							buf = ("closeChattingSocket").getBytes("UTF-8");
							count = buf.length;
							toClient.write(buf, 0, count);
							toClient.flush();
						}
					}
					Remove_client(sock, rNum);
					sock.close();
					// 접속 후 나가버린 클라이언트인 경우 ArrayList에서 제거

					fromClient.close();
					toClient.close();
					return ;
				} else {
					for (Socket s : room.get(rNum).keySet()) {
						if (sock != s) { // 자기자신 sock에는 글을 보낼 필요가 없으니까 a가 아닌 b,c사용자에게 보내는용도
							toClient = s.getOutputStream();
							toClient.write(buf, 0, count); // write( byte[] b, int off, int count ) : b[off] 부터 count 개의
															// 바이트를 출력 스트림으로 보냅니다.
							toClient.flush();
						}
					}
				}
			}
		}
		catch(IOException ex) {
			System.out.println(sock+" : 에러(" + ex + ")");	
		}
	}
	
	public void openWaitServer(Socket sock) {
		try {
			System.out.println(sock+ " : room socket 연결됨 -> " + sock.getPort());
			InputStream is = sock.getInputStream();
			byte[] buffer = new byte[512];
			int length;
			while ((length = is.read(buffer)) != -1) {
				String client_info = new String(buffer, 0, length, "UTF-8"); // roomcode
				String[] ciArray = client_info.split("#"); 
				// [0] : 방 종류(entry: 입장, int: 방 크기(new))
				// [1] : 방 코드(hashmap에 들어갈것)
				// [2] : 입장하는 사람의 닉네임
				
				// ---------------입장 버튼-----------------
				if (ciArray[0].equals("entry")) { 
					System.out.println(client_info + ": 입장");
					Thread thread = new Thread() {
						public void run() {
							try {
								System.out.println("entry 방 입장 thread in");
								
								Socket socket = serverSock.accept();
								clients = room.remove(ciArray[1]);
								clients.put(socket, ciArray[2]);
								room.put(ciArray[1], clients);
								openChatServer(socket, ciArray[1]);
								System.out.println("entry openChatServer called");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					};
					thread.start();
				} else { // 방만들기 일때
					System.out.println(client_info + ": new 방");
					Thread thread = new Thread() {
						public void run() {
							try {
								System.out.println("new 방 생성 thread in");
								
								Socket socket = serverSock.accept();
								clients = new HashMap<>(Integer.parseInt(ciArray[0]));
								clients.put(socket, ciArray[2]);
								room.put(ciArray[1], clients);
								openChatServer(socket, Integer.toString(room.size() - 1));
								System.out.println("openChatServer() 실행");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					};
					thread.start();
				}
				buffer = new byte[512];
				for (String key : room.keySet()) {
					for (Socket s : room.get(key).keySet()) {
						System.out.print(s.getPort() + ", ");
					}
					System.out.println();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				System.out.println("openWaitServer() exception " + sock.getPort() + " : 연결 종료(방소켓)");
				sock.close();
				Remove_rClient(sock);
			} catch (IOException e2) { 
				e2.printStackTrace(); 
			}
		} finally {
			System.out.println("openWaitServer finally");
		}
	}
	
	public void run() {
		//Thread가 할일을 기술한다.
		Thread chatThread = new Thread() {
			public void run() {
				try {
					serverSock = new ServerSocket(9999);
					System.out.println(serverSock + " : serversocket 생성 완료");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		Thread roomThread = new Thread() {
			public void run() {
				ServerSocket roomserverSock = null;
				try {
					roomserverSock = new ServerSocket(8888);
					System.out.println(roomserverSock + " : 방 코드 전용 소켓 생성 완료");
					while (true) {
						Socket rclient = roomserverSock.accept();
						rclients.add(rclient);
						Thread thread = new Thread() {
							public void run() {
								openWaitServer(rclient);
							}
						};
						thread.start();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						roomserverSock.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
		};
		chatThread.start();
		roomThread.start();
		
	}
	public static void main(String[] args) throws IOException{
		ConsoleChatServer myServer=new ConsoleChatServer();
		myServer.start();
	}

	// ArrayList에서 클라이언트 소켓 제거
	// 접속 후 나가버리는 경우 쓸때 오류가 발생
	public void Remove_client(Socket socket, String roomCode) {
		for (Socket s : room.get(roomCode).keySet()) {
			if (socket == s) {
				ConsoleChatServer.clients.remove(socket);// 배열의 remove메서드임 이건
				break;
			}
		}
	}
	
	public void Remove_rClient(Socket socket) {
		for (Socket s : rclients) {
			if (socket == s) {
				rclients.remove(socket);
				break;
			}
		}
	}
}