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

import database.DB_DAO;
import database.UserDAO;

public class BookingServer {

	// ���� ���� �� Ŭ���̾�Ʈ ���� ����
	private ServerSocket ss = null;
	private Socket s = null;
	private DB_DAO DB_dao = new DB_DAO();
	private UserDAO user_dao = new UserDAO();
	// ����� Ŭ���̾�Ʈ �����带 �����ϴ� ArrayList
	ArrayList<BookingThread> bookingThreads = new ArrayList<BookingThread>();
	ArrayList<String> onReservationList = new ArrayList<String>();

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
				BookingThread booking = new BookingThread();
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

	// �� Ŭ���̾�Ʈ�� ������ ���, �������� ��Ʈ��ũ ����� �߻�
	class BookingThread extends Thread {
		// ���� �޽��� �� �Ľ� �޽��� ó���� ���� ���� ����
		String id;// �ʿ��Ѱ�?
		String msg;
		// �޽��� ��ü ����
		Message m = new Message();
		// JSON �ļ� �ʱ�ȭ
		Gson gson = new Gson(); // Gson - java ��ü�� JSON ǥ�������� ��ȯ�ϴ� API

		// Ŭ���̾�Ʈ�� ����� ó���� ����� ��Ʈ�� ����
		private BufferedReader inMsg = null;
		private PrintWriter outMsg = null;

		/*
		 * Ŭ���̾�Ʈ���� �����ϴ� JSON �޽����� �о�� Message ��ü�� ������ �� Message ��ü�� �����Ͽ� �޽��� ������ ���� ó���ϴ�
		 * ����
		 */
		public void run() {
			boolean status = true;
			logger.info("BookingThread start...");

			try {
				inMsg = new BufferedReader(new InputStreamReader(s.getInputStream()));
				outMsg = new PrintWriter(s.getOutputStream(), true);

				// ���� ������ true�̸� ������ ���鼭 ����ڿ��Լ� ���ŵ� �޽��� ó��
				while (status) {
					// ���ŵ� �޽����� msg ������ ����
					msg = inMsg.readLine();// ���ڿ��̱� �ѵ� json ����

					// JSON �޽����� Message ��ü�� ���� , m�� ��ü, msg�� JSON
					m = gson.fromJson(msg, Message.class);// json�� ��ü�� ��ȯ

					/* �Ľ̵� ���ڿ� �迭�� type ��� ���� ���� ó�� */
					// �α��� �޽����� ��
					if (m.getType().equals("login")) {
						if(login(m))
							id = m.getStudentId();
						MsgSendClient(gson.toJson(m));
						
					}
					// ���� �õ�
					else if (m.getType().equals("try")) {
						tryReservation(m);
						MsgSendClient(gson.toJson(m));
					}
					// ���� �޽����� ��
					else if (m.getType().equals("reservation")) {
						makeReservation(m);
						onReservationList.remove(m.getRoomNum());//���� �� ��Ͽ��� ����
						MsgSendClient(gson.toJson(m));
					}
					// ���� Ȯ���� ��- list�� ��ȯ
					else if (m.getType().equals("check")) {
						checkReservation(m);
						// m ����
					}
					// ���� ����� ��
					else if (m.getType().equals("cancle")) {
						cancleReservation(m);
						MsgSendClient(gson.toJson(m));
					}
					// ��ȸ�� ��- list�� ��ȯ
					else {
						lookUpEmptyRoom(m);// Message[] msgArr �� ��Ƽ� json array ������?
						// m ����
						// json array ����
					}
				}
				// ������ ����� Ŭ���̾�Ʈ ������ ����ǹǷ� ������ ���ͷ�Ʈ
				this.interrupt();// �Ͻ����� ����� �޼ҵ尡 �ְų�, Thread.interrupted(), obj.isInterrupted() interrupt()����
									// block ���°� ��
				logger.info(this.getName() + " �����");
			} catch (IOException e) {
				bookingThreads.remove(this);
				logger.info("[ChatThread]run() IOException �߻�");
				e.printStackTrace();
			}

			// �ڿ� ��ü
			try {
				if (inMsg != null)
					inMsg.close();

				if (outMsg != null)
					outMsg.close();

				if (s != null)
					s.close();
				System.out.println("�ڿ� ��ü");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// ---------Ŭ������ �и��ؾ� �ҵ�-------------
		// Ŭ���̾�Ʈ�� �޽��� ����
		void MsgSendClient(String msg) {
			this.outMsg.println(msg);
		}
		
		// �α���
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
		
		// ���� ������ üũ
		boolean isOnReservation(Message m) {
			for (String room : onReservationList)
				if (room.equals(m.getRoomNum()))
					return true;
			return false;
		}
		
		// ���� ��
		void tryReservation(Message m) {// ó�� ���� ��ư Ŭ�� ��
			if (isOnReservation(m)) {// �̹� ���� ���̸�
				m.setMsg("already on a reservation");// Ŭ���̾�Ʈ �ܿ��� �������̶�� �޽��� ��������
			} else {
				onReservationList.add(m.getRoomNum());//���� �� ��Ͽ� �߰�
				m.setMsg("available");
			}
		}

		// �������� db�� ����
		void makeReservation(Message m) {
			if (DB_dao.book(m))
				m.setMsg("success");
			else
				m.setMsg("failed");
		}

		// db���� �̿밡���� ���͵��� ��ȸ - list�� ��ȯ
		void lookUpEmptyRoom(Message m) {
			DB_dao.getBookAvailableList(m.getDate(), Integer.parseInt(m.getCapacity()),
					Integer.parseInt(m.getEquipment()));
			// ���⼭ ���ϰ� �޾ƿ�
			// return arr
		}

		// �л� id �޾Ƽ� ���� ��ȸ - list�� ��ȯ
		void checkReservation(Message m) {
			DB_dao.getBookedByClient(m.getStudentId(), m.getName());
			// return ���� arr
		}

		// db�� ���� ��Ͽ��� ����
		void cancleReservation(Message m) {
			boolean isCancled;
			isCancled = DB_dao.bookingCancel(m.getBuilding(), m.getRoomNum(), m.getStudentId(), m.getName(), m.getDate());
			if(isCancled) //��� ����
				m.setMsg("success");
			else 
				m.setMsg("failed");
		}
	}
}
