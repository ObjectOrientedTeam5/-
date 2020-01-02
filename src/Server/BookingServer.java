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

	// ���� ���� �� Ŭ���̾�Ʈ ���� ����
	private ServerSocket ss = null;
	private Socket s = null;
	
	// ����� Ŭ���̾�Ʈ �����带 �����ϴ� ArrayList
	ArrayList<BookingThread> bookingThreads = new ArrayList<BookingThread>();
	ArrayList<String> onReservationList = new ArrayList<String>(); // ���� ���� room�� ������ ��� �ٸ� Ŭ���̾�Ʈ�� �����ϴ� ���� ����
	
	// �ΰ� ��ü
	Logger logger;

	// ���� ���α׷� �κ�
	public void start() {
		logger = Logger.getLogger(this.getClass().getName());

		try {
			// ���� ���� ����
			ss = new ServerSocket(8888);
			logger.info("Server start");

			// ���� ������ ���鼭 Ŭ���̾�Ʈ ������ ��ٸ���.
			while (true) {
				s = ss.accept();
				// ����� Ŭ���̾�Ʈ�� ���� ������ Ŭ���� ����
				BookingThread booking = new BookingThread(s, onReservationList, bookingThreads);
				// Ŭ���̾�Ʈ ����Ʈ �߰�
				bookingThreads.add(booking);
				// ������ ����
				booking.start();
			}
		} catch (Exception e) {
			logger.info("[BookingServer]start(); Exception �߻�");
			e.printStackTrace();
		} finally {// �ڿ� ��ü
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
