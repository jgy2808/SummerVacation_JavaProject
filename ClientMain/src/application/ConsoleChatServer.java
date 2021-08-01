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
	public void run() {
		//Thread가 할일을 기술한다.
		InputStream fromClient = null;
		OutputStream toClient = null;
		
		try {
			System.out.println(sock+ " : 연결됨 -> " + sock.getPort());
			fromClient = sock.getInputStream();
			
			byte[] buf = new byte[1024];
			int count; //몇 바이트의 글을 받았는지 카운트하는 변수
			while((count = fromClient.read(buf))!= -1) { //중단 ctrl c같은거 누르면 -1을 보내기떄문에
				for(Socket s : ConsoleChatServer.clients) {
					if(sock!=s) { //자기자신 sock에는 글을 보낼 필요가 없으니까 a가 아닌 b,c사용자에게 보내는용도
						toClient = s.getOutputStream();
						toClient.write(buf,0,count); //write( byte[] b, int off, int count ) : b[off] 부터 count 개의 바이트를 출력 스트림으로 보냅니다.
						toClient.flush();
					}
				}
				System.out.write(buf,0,count);
				System.out.println();
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
				
			}
		}
	}
	public static void main(String[] args) throws IOException{
		ServerSocket serverSock=new ServerSocket(9999);
		System.out.println(serverSock+": 서버소켓생성");
		
		while(true) {
			Socket client = serverSock.accept();
			clients.add(client);//arraylist에 접속한 소켓을 넣어줌
			
			ConsoleChatServer myServer=new ConsoleChatServer(client);
			myServer.start();
			
		}
	}
}