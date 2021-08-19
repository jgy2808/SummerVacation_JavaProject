package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


public class ConsoleChatServer extends Thread{
	private static ArrayList<ArrayList<Socket>> room = new ArrayList<ArrayList<Socket>>();
	private static ArrayList<Socket> clients;
	
	private static ArrayList<Socket> rclients = new ArrayList<>();
	
	ServerSocket serverSock;
	
	//ArrayList에서 클라이언트 소켓 제거
	//접속 후 나가버리는 경우 쓸때 오류가 발생
	public void remove(Socket socket) {
		for( Socket s : ConsoleChatServer.clients) {
			if(socket == s) {
				ConsoleChatServer.clients.remove(socket);//배열의 remove메서드임 이건
				break;
			}
		}
	}
	
	public void openChatServer(Socket sock, int rNum) {
		InputStream fromClient = null;
		OutputStream toClient = null;
		byte[] buf = new byte[1024];
		int count; //몇 바이트의 글을 받았는지 카운트하는 변수
		
		try {
			System.out.println(sock+ " : chat socket 연결됨 -> " + sock.getPort());
			fromClient = sock.getInputStream();
			while((count = fromClient.read(buf))!= -1) { //중단 ctrl c같은거 누르면 -1을 보내기떄문에
				for(Socket s : room.get(rNum)) {
					if(sock!=s) { //자기자신 sock에는 글을 보낼 필요가 없으니까 a가 아닌 b,c사용자에게 보내는용도
						toClient = s.getOutputStream();
						toClient.write(buf,0,count); //write( byte[] b, int off, int count ) : b[off] 부터 count 개의 바이트를 출력 스트림으로 보냅니다.
						toClient.flush();
					}
				}
				System.out.println(sock.getPort() + " : " + new String(buf, 0, count, "UTF-8"));
			}
		}
		catch(IOException ex) {
			System.out.println(sock+" : 에러(" + ex + ")");
		}finally {
			try {
				if(sock !=null) {
					sock.close();
					//접속 후 나가버린 클라이언트인 경우 ArrayList에서 제거
					remove(sock);
				}
				fromClient=null;
				toClient=null;
			}catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void openWaitServer(Socket sock) {
		try {
			System.out.println(sock+ " : room socket 연결됨 -> " + sock.getPort());
			InputStream is = sock.getInputStream();
			byte[] buffer = new byte[10];
			int length;
			while ((length = is.read(buffer)) != -1) {
				String rc = new String(buffer, 0, length, "UTF-8"); // roomcode
				if (!rc.equals("new")) { // 방만들기가 아니고 입장일때
					System.out.println(rc + ": 입장");
					int rNum = Integer.parseInt(rc);
					Thread thread = new Thread() {
						public void run() {
							try {
								Socket socket = serverSock.accept();
								clients = room.get(rNum);
								clients.add(socket);
								room.add(rNum, clients);
								openChatServer(socket, rNum);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					};
					thread.start();
				} else { // 방만들기 일때
					System.out.println(rc + ": new 방");
					Thread thread = new Thread() {
						public void run() {
							try {
								Socket socket = serverSock.accept();
								clients = new ArrayList<>();
								clients.add(socket);
								room.add(clients);
								System.out.println(room.size());
								for (ArrayList<Socket> test : room) {
									for (Socket s : test) {
										System.out.print(s.getPort() + ", ");
									}
									System.out.println();
								}
								openChatServer(socket, room.size() - 1);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					};
					thread.start();
				}
				buffer = new byte[512];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		//Thread가 할일을 기술한다.
		Thread chatThread = new Thread() {
			public void run() {
				try {
					serverSock=new ServerSocket(9999);
					System.out.println(serverSock+": 서버소켓생성");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		Thread roomThread = new Thread() {
			public void run() {
				try {
					ServerSocket roomserverSock = new ServerSocket(8888);
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
}