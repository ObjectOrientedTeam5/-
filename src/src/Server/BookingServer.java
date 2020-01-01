package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import database.BookAvailableDTO;
import database.BookedDTO;
import database.DB_DAO;
import database.UserDAO;

public class BookingServer {

	// 서버 소켓 및 클라이언트 연결 소켓
	private ServerSocket ss = null;
	private Socket s = null;
	private DB_DAO DB_dao = new DB_DAO();
	private UserDAO user_dao = new UserDAO();
	// 연결된 클라이언트 스레드를 관리하는 ArrayList
	ArrayList<BookingThread> bookingThreads = new ArrayList<BookingThread>();
	ArrayList<String> onReservationList = new ArrayList<String>();

	// 로거 객체
	Logger logger;

	// 메인 프로그램 부분
	public void start() {
		logger = Logger.getLogger(this.getClass().getName());

		try {
			// 서버 소켓 생성
			ss = new ServerSocket(8888);
			logger.info("Server start");

			// 무한 루프를 돌면서 클라이언트 연결을 기다린다.
			while (true) {
				s = ss.accept();
				// 연결된 클라이언트에 대해 스레드 클래스 생성
				BookingThread booking = new BookingThread();
				// 클라이언트 리스트 추가
				bookingThreads.add(booking);
				// 스레드 시작
				booking.start();
			}
		} catch (Exception e) {
			logger.info("[BookingServer]start(); Exception 발생");
			e.printStackTrace();
		} finally {// 자원 해체
			try {
				if (ss != null)
					ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		BookingServer bookingServer = new BookingServer();
		bookingServer.start();
	}

	// 각 클라이언트와 연결을 담당, 실질적인 네트워크 입출력 발생
	class BookingThread extends Thread {
		// 수신 메시지 및 파싱 메시지 처리를 위한 변수 선언
		String id;// 필요한가?
		String msg;
		// 메시지 객체 생성
		Message m = new Message();
		// JSON 파서 초기화
		Gson gson = new Gson(); // Gson - java 객체를 JSON 표현식으로 변환하는 API
		Gson gsonBuilder = new GsonBuilder().create();

		// 클라이언트와 입출력 처리를 담당할 스트림 선언
		private BufferedReader inMsg = null;
		private PrintWriter outMsg = null;

		/*
		 * 클라이언트에서 전달하는 JSON 메시지를 읽어와 Message 객체로 매핑한 후 Message 객체를 참조하여 메시지 유형에 따라 처리하는
		 * 구조
		 */
		public void run() {
			boolean status = true;
			logger.info("BookingThread start...");

			// Message m = new Message("11111111", "홍길동", "학술정보원", "s1", "5","1","2019-12-12
			// 15:22:39", "lookUp", "");
			try {
				inMsg = new BufferedReader(new InputStreamReader(s.getInputStream()));
				outMsg = new PrintWriter(s.getOutputStream(), true);

				// 상태 정보가 true이면 루프를 돌면서 사용자에게서 수신된 메시지 처리
				while (status) {
					// 수신된 메시지를 msg 변수에 저장
					msg = inMsg.readLine();// 문자열이긴 한데 json 형태

					// JSON 메시지를 Message 객체로 매핑 , m은 객체, msg는 JSON
					m = gson.fromJson(msg, Message.class);// json을 객체로 변환

					/* 파싱된 문자열 배열의 type 요소 값에 따라 처리 */
					// 로그인 메시지일 때
					if (m.getType().equals("login")) {
						if (login(m))
							id = m.getStudentId(); // 해당 스레드의 id 설정
						MsgSendClient(gson.toJson(m)); // 클라이언트의 컨트롤러로 정보 전송

					}
					// 예약 시도
					else if (m.getType().equals("try")) {
						tryReservation(m); // '예약 중' 리스트에 해당 스터디룸에 대한 정보를 입력하여 다른 사용자가 예약시도하지 못하게 함.
						MsgSendClient(gson.toJson(m));
					}
					// 예약 메시지일 때
					else if (m.getType().equals("reservation")) {
						makeReservation(m);// 예약정보를 DB에 저장하고
						onReservationList.remove(m.getRoomNum());// '예약 중' 리스트에 해당 스터디룸에 대한 정보를 제거.
						MsgSendClient(gson.toJson(m));
					}
					// 예약 확인일 때- list로 반환
					else if (m.getType().equals("check")) {
						String msgList;
						msgList = gsonBuilder.toJson(checkReservation(m));// 반환값 ArrayList<Message>
						MsgSendClient(msgList);
						System.out.println("[서버]해당 사용자가 예약한 목록 ");
						System.out.println(msgList);
					}
					// 예약 취소일 때
					else if (m.getType().equals("cancle")) {
						cancleReservation(m);
						MsgSendClient(gson.toJson(m));
					}
					// 조회할 때- list로 반환
					else if (m.getType().equals("lookUp")) {
						String msgList;
						msgList = gsonBuilder.toJson(lookUpEmptyRoom(m));// 반환값 ArrayList<Message>
						MsgSendClient(msgList);// json화 한 것을 전송
						System.out.println("[서버]조회한 목록 ");
						System.out.println(msgList);
					}
					// 관리자 모드- 조회
					else if (m.getType().equals("managerLookUp")) {
						String msgList;
						msgList = gsonBuilder.toJson(ManagerLookUp(m));// 반환값 ArrayList<Message>
						MsgSendClient(msgList);// json화 한 것을 전송
						System.out.println("[서버]조회한 목록 ");
						System.out.println(msgList);
					}
					// 종료했을 경우
					else if (m.getType().equals("exit")) {
						bookingThreads.remove(this);
						// 해당 클라이언트 스레드 종료로 status를 false로 설정
						status = false;
					} else {
						System.out.println("메시지 예외사항 발생");
					}
				}
				// 루프를 벗어나면 클라이언트 연결이 종료되므로 스레드 인터럽트
				this.interrupt();// 일시정지 만드는 메소드가 있거나, Thread.interrupted(), obj.isInterrupted() interrupt()쓰면
									// block 상태가 됨
				logger.info(this.getName() + " 종료됨");
			} catch (IOException e) {
				bookingThreads.remove(this);
				logger.info("[ChatThread]run() IOException 발생");
				e.printStackTrace();
			}

			// 자원 해체
			try {
				if (inMsg != null)
					inMsg.close();

				if (outMsg != null)
					outMsg.close();

				if (s != null)
					s.close();
				System.out.println("자원 해체");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// ---------클래스로 분리해야 할듯-------------
		// 클라이언트에 메시지 전송
		void MsgSendClient(String msg) {
			this.outMsg.println(msg);
		}

		// 로그인
		boolean login(Message m) {
			boolean result;
			result = user_dao.login(m.getStudentId(), m.getName());
			if (result) {
				m.setMsg("true");
				return true;
			} else {
				m.setMsg("false");
				return false;
			}
		}

		// 예약 중인지 체크
		boolean isOnReservation(Message m) {
			for (String room : onReservationList)
				if (room.equals(m.getRoomNum()))
					return true;
			return false;
		}

		// 예약 중
		void tryReservation(Message m) {// 처음 예약 버튼 클릭 시
			if (isOnReservation(m)) {// 이미 예약 중이면
				m.setMsg("already on a reservation");// 클라이언트 단에서 예약중이라는 메시지 보내야함
			} else {
				onReservationList.add(m.getRoomNum());// 예약 중 목록에 추가
				m.setMsg("available");
			}
		}

		// 예약정보 db에 저장
		void makeReservation(Message m) {
			if (DB_dao.book(m))
				m.setMsg("success");
			else
				m.setMsg("failed");
		}

		// db에서 이용가능한 스터디룸들 조회 - list로 반환
		ArrayList<Message> lookUpEmptyRoom(Message m) {
			ArrayList<BookAvailableDTO> dataList = new ArrayList<BookAvailableDTO>();// db에서 반환받을 데이터
			ArrayList<Message> msgList = new ArrayList<Message>();// Message타입으로 전환하기 위함

			// 조회 후 반환값 받음
			dataList = DB_dao.getBookAvailableList(m.getDate(), Integer.parseInt(m.getCapacity()),
					Integer.parseInt(m.getEquipment())); // ArrayList<BookAvailableDTO>

			// 반환 값을 메시지에 담기
			for (int i = 0; i < dataList.size(); i++) {
				msgList.add(new Message("", "", dataList.get(i).getBuilding(), dataList.get(i).getRoomNumber(),
						Integer.toString(dataList.get(i).getMaxPeople()),
						Integer.toString(dataList.get(i).getIsProject()), "", "lookUp", ""));
			}
			return msgList;
		}

		// 학생 id 받아서 예약 조회 - list로 반환
		ArrayList<Message> checkReservation(Message m) {
			ArrayList<BookedDTO> dataList = new ArrayList<BookedDTO>();// db에서 반환받는 데이터
			ArrayList<Message> msgList = new ArrayList<Message>();// Message타입으로 전환하기 위함

			// 조회 후 반환값 받음
			dataList = DB_dao.getBookedByClient(m.getStudentId(), m.getName());// ArrayList<BookedDTO>

			// 반환 값을 메시지에 담기
			for (int i = 0; i < dataList.size(); i++) {
				msgList.add(new Message("", "", dataList.get(i).getBuilding(), dataList.get(i).getRoomNumber(),
						Integer.toString(dataList.get(i).getMaxPeople()), "", dataList.get(i).getDate(), "check", ""));
			}
			return msgList;
		}

		// db의 예약 목록에서 제거
		void cancleReservation(Message m) {
			boolean isCancled;
			isCancled = DB_dao.bookingCancel(m.getBuilding(), m.getRoomNum(), m.getStudentId(), m.getName(),
					m.getDate());
			if (isCancled) // 취소 성공
				m.setMsg("success");
			else
				m.setMsg("failed");
		}

		// 관리자가 예약목록 조회
		ArrayList<Message> ManagerLookUp(Message m) {
			ArrayList<BookedDTO> dataList = new ArrayList<BookedDTO>();// db에서 반환받는 데이터
			ArrayList<Message> msgList = new ArrayList<Message>();// Message타입으로 전환하기 위함

			// 조회 후 반환값 받음
			dataList = DB_dao.getAll();// ArrayList<BookedDTO>

			// 반환 값을 메시지에 담기
			for (int i = 0; i < dataList.size(); i++) {
				msgList.add(new Message(dataList.get(i).getStudentID(), dataList.get(i).getStudentName(),
						dataList.get(i).getBuilding(), dataList.get(i).getRoomNumber(),
						Integer.toString(dataList.get(i).getMaxPeople()),
						Integer.toString(dataList.get(i).getIsProject()), dataList.get(i).getDate(), "managerLookUp",
						""));
			}
			return msgList;
		}
	}
}
