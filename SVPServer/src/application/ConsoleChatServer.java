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
	
	//ArrayList���� Ŭ���̾�Ʈ ���� ����
	//���� �� ���������� ��� ���� ������ �߻�
	public void remove(Socket socket) {
		for( Socket s : ConsoleChatServer.clients) {
			if(socket == s) {
				ConsoleChatServer.clients.remove(socket);//�迭�� remove�޼����� �̰�
				break;
			}
		}
	}
	
	public void openChatServer(Socket sock, int rNum) {
		InputStream fromClient = null;
		OutputStream toClient = null;
		byte[] buf = new byte[1024];
		int count; //�� ����Ʈ�� ���� �޾Ҵ��� ī��Ʈ�ϴ� ����
		
		try {
			System.out.println(sock+ " : chat socket ����� -> " + sock.getPort());
			fromClient = sock.getInputStream();
			while((count = fromClient.read(buf))!= -1) { //�ߴ� ctrl c������ ������ -1�� �����⋚����
				for(Socket s : room.get(rNum)) {
					if(sock!=s) { //�ڱ��ڽ� sock���� ���� ���� �ʿ䰡 �����ϱ� a�� �ƴ� b,c����ڿ��� �����¿뵵
						toClient = s.getOutputStream();
						toClient.write(buf,0,count); //write( byte[] b, int off, int count ) : b[off] ���� count ���� ����Ʈ�� ��� ��Ʈ������ �����ϴ�.
						toClient.flush();
					}
				}
				System.out.println(sock.getPort() + " : " + new String(buf, 0, count, "UTF-8"));
			}
		}
		catch(IOException ex) {
			System.out.println(sock+" : ����(" + ex + ")");
		}finally {
			try {
				if(sock !=null) {
					sock.close();
					//���� �� �������� Ŭ���̾�Ʈ�� ��� ArrayList���� ����
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
			System.out.println(sock+ " : room socket ����� -> " + sock.getPort());
			InputStream is = sock.getInputStream();
			byte[] buffer = new byte[10];
			int length;
			while ((length = is.read(buffer)) != -1) {
				String rc = new String(buffer, 0, length, "UTF-8"); // roomcode
				if (!rc.equals("new")) { // �游��Ⱑ �ƴϰ� �����϶�
					System.out.println(rc + ": ����");
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
				} else { // �游��� �϶�
					System.out.println(rc + ": new ��");
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
		//Thread�� ������ ����Ѵ�.
		Thread chatThread = new Thread() {
			public void run() {
				try {
					serverSock=new ServerSocket(9999);
					System.out.println(serverSock+": �������ϻ���");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		Thread roomThread = new Thread() {
			public void run() {
				try {
					ServerSocket roomserverSock = new ServerSocket(8888);
					System.out.println(roomserverSock + " : �� �ڵ� ���� ���� ���� �Ϸ�");
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