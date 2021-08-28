package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;


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
		int count; //�� ����Ʈ�� ���� �޾Ҵ��� ī��Ʈ�ϴ� ����

		try {
			// ���� �� ��
			// �����ϴ� �� �ڽſ��Դ� �� ���� ���� ��� nick�� ��� ������ -> member_list�� ���ؼ�
			// �������ִ� �ٸ� ����鿡�� �����ϴ� ���� nick�� ������
			// inout#currentNum#nick
			String currentNum = Integer.toString(room.get(rNum).size());
			String myNick = room.get(rNum).get(sock);
			String enterInfo = "in#" + currentNum + "#" + myNick + "#" + "���� �����ϼ̽��ϴ�." + "#";
			
			byte[] buffer = (enterInfo + room.get(rNum).values()).getBytes("UTF-8");
			toClient = sock.getOutputStream();
			toClient.write(buffer);
			toClient.flush();
			
			for (Socket s : room.get(rNum).keySet()) {
				if (s != sock) {
					buffer = (enterInfo + myNick).getBytes("UTF-8");
					toClient = s.getOutputStream();
					toClient.write(buffer);
					toClient.flush();
				}
			}
			System.out.println(sock+ " : chat socket ����� -> " + sock.getPort());
			fromClient = sock.getInputStream();
			while((count = fromClient.read(buf))!= -1) { //�ߴ� ctrl c������ ������ -1�� �����⋚����
				for(Socket s : room.get(rNum).keySet()) {
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
					for(Socket s : room.get(rNum).keySet()) {
						if(sock != s) {
							toClient = s.getOutputStream();
							buf = ("out#" + Integer.toString(room.get(rNum).size() - 1) + "#" + room.get(rNum).get(sock)).getBytes("UTF-8");
							toClient.write(buf);
							toClient.flush();
						}
					}
					Remove_client(sock, rNum);
					sock.close();
					//���� �� �������� Ŭ���̾�Ʈ�� ��� ArrayList���� ����
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
				String client_info = new String(buffer, 0, length, "UTF-8"); // roomcode
				String[] ciArray = client_info.split("#"); 
				// [0] : �� ����(entry: ����, int: �� ũ��(new))
				// [1] : �� �ڵ�(hashmap�� ����)
				// [2] : �����ϴ� ����� �г���
				
				// ---------------���� ��ư-----------------
				if (ciArray[0].equals("entry")) { 
					System.out.println(client_info + ": ����");
					Thread thread = new Thread() {
						public void run() {
							try {
								Socket socket = serverSock.accept();
								clients = room.remove(ciArray[1]);
								clients.put(socket, ciArray[2]);
								room.put(ciArray[1], clients);
								openChatServer(socket, ciArray[1]);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					};
					thread.start();
				} else { // �游��� �϶�
					System.out.println(client_info + ": new ��");
					Thread thread = new Thread() {
						public void run() {
							try {
								Socket socket = serverSock.accept();
								clients = new HashMap<>(Integer.parseInt(ciArray[0]));
								clients.put(socket, ciArray[2]);
								room.put(ciArray[1], clients);
								openChatServer(socket, Integer.toString(room.size() - 1));
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
		} finally {
			try {
				System.out.println(sock.getPort() + " : ���� ����(�����)");
				sock.close();
				Remove_rClient(sock);
			} catch (IOException e) { 
				e.printStackTrace(); 
			}
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

	// ArrayList���� Ŭ���̾�Ʈ ���� ����
	// ���� �� ���������� ��� ���� ������ �߻�
	public void Remove_client(Socket socket, String roomCode) {
		for (Socket s : room.get(roomCode).keySet()) {
			if (socket == s) {
				ConsoleChatServer.clients.remove(socket);// �迭�� remove�޼����� �̰�
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