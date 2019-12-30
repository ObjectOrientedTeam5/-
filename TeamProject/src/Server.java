
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.google.gson.Gson;

public class Server {

	// 서버 소켓 및 클라이언트 연결 소켓
	private ServerSocket ss = null;
	private Socket s = null;

	// 연결된 클라이언트 스레드를 관리하는 ArrayList
	ArrayList<ChatThread> chatThreads = new ArrayList<ChatThread>();

	// 로거 객체
	Logger logger;

	// 멀티 채팅 메인 프로그램 부분
	public void start() {
		logger = Logger.getLogger(this.getClass().getName());

		try {
			// 서버 소켓 생성
			ss = new ServerSocket(8888);
			logger.info("MultichatServer start");

			// 무한 루프를 돌면서 클라이언트 연결을 기다린다.
			while (true) {
				s = ss.accept();
				// 연결된 클라이언트에 대해 스레드 클래스 생성
				ChatThread chat = new ChatThread();
				// 클라이언트 리스트 추가
				chatThreads.add(chat);
				// 스레드 시작
				chat.start();
			}
		} catch (Exception e) {
			logger.info("[MultiChatServer]start() Exception발생!!");
			e.printStackTrace();
		}

	}

	// 연결된 모든 클라이언트에 메시지 중계
	public void msgSendAll(String msg) {
		for (ChatThread ct : chatThreads) {
			ct.outMsg.println(msg);
		}
	}
	public static void main(String[] args) {
		Server chatserver = new Server();
		chatserver.start();
	}

	class ChatThread extends Thread {

		// 수신 메시지 및 파싱 메시지 처리를 위한 변수 선언
		String msg;

		// 메시지 객체 생성
		Message m = new Message();

		// JSON 파서 초기화
		Gson gson = new Gson();
		
		String id;

		// 입출력 스트림
		private BufferedReader inMsg = null;
		private PrintWriter outMsg = null;

		public void run() {
			boolean status = true;

			try {
				inMsg = new BufferedReader(new InputStreamReader(s.getInputStream()));
				outMsg = new PrintWriter(s.getOutputStream(), true);

				while (status) {
					// 수신된 메시지를 msg 변수에 저장
					try {
						msg = inMsg.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}

					// JSON메시지를 Message 객체로 매핑
					m = gson.fromJson(msg, Message.class);

					// 파싱된 문자열 배열의 두 번째 요소값에 따라 처리
					// 로그아웃 메시지 일 떄
					if (m.getType().equals("logout")) {
						chatThreads.remove(this);
						msgSendAll(gson.toJson(new Message(m.getId(), "", "님이 종료 했습니다","", "server")));

						// 해당ㅇ 클라이언트 스레드 종료로 status를 false 로 설정
						status = false;
					}
					// 로그인 메시지일 때
					else if (m.getType().equals("login")) {
						id = m.getId();
						msgSendAll(gson.toJson(new Message(m.getId(), "", "님이 로그인했습니다","", "server")));
					}
					else if(m.getType().equals("find")) 	//조회 
					{
						
					}
					else if(m.getType().equals("reservation")) //예약
					{
						
					}
					else if(m.getType().equals("cancle")) //예약취소
					{
						
					}
					
					else if(m.getType().equals("secret"))
					{
						System.out.println("귓말 ON");
						//msgSendSecret(msg, m.getTo());
						//msgSendSecret(msg, m.getId());
					}
					else
					{
						msgSendAll(msg);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 루프를 벗어나면 클라이언트 연결이 종료되므로 스레드 인터럽트
			this.interrupt();
			logger.info(this.getName() + " 종료됨!!");
		}

	}

}
