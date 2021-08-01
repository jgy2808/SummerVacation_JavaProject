package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ConsoleChatServer extends Thread{
	
	private Socket sock;
	private static ArrayList<Socket> clients = new ArrayList<Socket>(5);

	
	public ConsoleChatServer(Socket sock) {
		this.sock=sock;
	}
	
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
	public void run() {
		//Thread�� ������ ����Ѵ�.
		InputStream fromClient = null;
		OutputStream toClient = null;
		
		try {
			System.out.println(sock+ " : ����� -> " + sock.getPort());
			fromClient = sock.getInputStream();
			
			byte[] buf = new byte[1024];
			int count; //�� ����Ʈ�� ���� �޾Ҵ��� ī��Ʈ�ϴ� ����
			while((count = fromClient.read(buf))!= -1) { //�ߴ� ctrl c������ ������ -1�� �����⋚����
				for(Socket s : ConsoleChatServer.clients) {
					if(sock!=s) { //�ڱ��ڽ� sock���� ���� ���� �ʿ䰡 �����ϱ� a�� �ƴ� b,c����ڿ��� �����¿뵵
						toClient = s.getOutputStream();
						toClient.write(buf,0,count); //write( byte[] b, int off, int count ) : b[off] ���� count ���� ����Ʈ�� ��� ��Ʈ������ �����ϴ�.
						toClient.flush();
					}
				}
				System.out.write(buf,0,count);
				System.out.println();
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
				
			}
		}
	}
	public static void main(String[] args) throws IOException{
		ServerSocket serverSock=new ServerSocket(9999);
		System.out.println(serverSock+": �������ϻ���");
		
		while(true) {
			Socket client = serverSock.accept();
			clients.add(client);//arraylist�� ������ ������ �־���
			
			ConsoleChatServer myServer=new ConsoleChatServer(client);
			myServer.start();
			
		}
	}
}