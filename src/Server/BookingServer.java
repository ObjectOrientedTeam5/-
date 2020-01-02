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
	
	// 연결된 클라이언트 스레드를 관리하는 ArrayList
	ArrayList<BookingThread> bookingThreads = new ArrayList<BookingThread>();
	ArrayList<String> onReservationList = new ArrayList<String>(); // 예약 중인 room의 정보를 담아 다른 클라이언트가 예약하는 것을 막음
	
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
				BookingThread booking = new BookingThread(s, onReservationList, bookingThreads);
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
}
