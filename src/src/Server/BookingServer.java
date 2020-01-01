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
		Gson gsonBuilder = new GsonBuilder().create();

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

			// Message m = new Message("11111111", "ȫ�浿", "�м�������", "s1", "5","1","2019-12-12
			// 15:22:39", "lookUp", "");
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
						if (login(m))
							id = m.getStudentId(); // �ش� �������� id ����
						MsgSendClient(gson.toJson(m)); // Ŭ���̾�Ʈ�� ��Ʈ�ѷ��� ���� ����

					}
					// ���� �õ�
					else if (m.getType().equals("try")) {
						tryReservation(m); // '���� ��' ����Ʈ�� �ش� ���͵�뿡 ���� ������ �Է��Ͽ� �ٸ� ����ڰ� ����õ����� ���ϰ� ��.
						MsgSendClient(gson.toJson(m));
					}
					// ���� �޽����� ��
					else if (m.getType().equals("reservation")) {
						makeReservation(m);// ���������� DB�� �����ϰ�
						onReservationList.remove(m.getRoomNum());// '���� ��' ����Ʈ�� �ش� ���͵�뿡 ���� ������ ����.
						MsgSendClient(gson.toJson(m));
					}
					// ���� Ȯ���� ��- list�� ��ȯ
					else if (m.getType().equals("check")) {
						String msgList;
						msgList = gsonBuilder.toJson(checkReservation(m));// ��ȯ�� ArrayList<Message>
						MsgSendClient(msgList);
						System.out.println("[����]�ش� ����ڰ� ������ ��� ");
						System.out.println(msgList);
					}
					// ���� ����� ��
					else if (m.getType().equals("cancle")) {
						cancleReservation(m);
						MsgSendClient(gson.toJson(m));
					}
					// ��ȸ�� ��- list�� ��ȯ
					else if (m.getType().equals("lookUp")) {
						String msgList;
						msgList = gsonBuilder.toJson(lookUpEmptyRoom(m));// ��ȯ�� ArrayList<Message>
						MsgSendClient(msgList);// jsonȭ �� ���� ����
						System.out.println("[����]��ȸ�� ��� ");
						System.out.println(msgList);
					}
					// ������ ���- ��ȸ
					else if (m.getType().equals("managerLookUp")) {
						String msgList;
						msgList = gsonBuilder.toJson(ManagerLookUp(m));// ��ȯ�� ArrayList<Message>
						MsgSendClient(msgList);// jsonȭ �� ���� ����
						System.out.println("[����]��ȸ�� ��� ");
						System.out.println(msgList);
					}
					// �������� ���
					else if (m.getType().equals("exit")) {
						bookingThreads.remove(this);
						// �ش� Ŭ���̾�Ʈ ������ ����� status�� false�� ����
						status = false;
					} else {
						System.out.println("�޽��� ���ܻ��� �߻�");
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
				onReservationList.add(m.getRoomNum());// ���� �� ��Ͽ� �߰�
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
		ArrayList<Message> lookUpEmptyRoom(Message m) {
			ArrayList<BookAvailableDTO> dataList = new ArrayList<BookAvailableDTO>();// db���� ��ȯ���� ������
			ArrayList<Message> msgList = new ArrayList<Message>();// MessageŸ������ ��ȯ�ϱ� ����

			// ��ȸ �� ��ȯ�� ����
			dataList = DB_dao.getBookAvailableList(m.getDate(), Integer.parseInt(m.getCapacity()),
					Integer.parseInt(m.getEquipment())); // ArrayList<BookAvailableDTO>

			// ��ȯ ���� �޽����� ���
			for (int i = 0; i < dataList.size(); i++) {
				msgList.add(new Message("", "", dataList.get(i).getBuilding(), dataList.get(i).getRoomNumber(),
						Integer.toString(dataList.get(i).getMaxPeople()),
						Integer.toString(dataList.get(i).getIsProject()), "", "lookUp", ""));
			}
			return msgList;
		}

		// �л� id �޾Ƽ� ���� ��ȸ - list�� ��ȯ
		ArrayList<Message> checkReservation(Message m) {
			ArrayList<BookedDTO> dataList = new ArrayList<BookedDTO>();// db���� ��ȯ�޴� ������
			ArrayList<Message> msgList = new ArrayList<Message>();// MessageŸ������ ��ȯ�ϱ� ����

			// ��ȸ �� ��ȯ�� ����
			dataList = DB_dao.getBookedByClient(m.getStudentId(), m.getName());// ArrayList<BookedDTO>

			// ��ȯ ���� �޽����� ���
			for (int i = 0; i < dataList.size(); i++) {
				msgList.add(new Message("", "", dataList.get(i).getBuilding(), dataList.get(i).getRoomNumber(),
						Integer.toString(dataList.get(i).getMaxPeople()), "", dataList.get(i).getDate(), "check", ""));
			}
			return msgList;
		}

		// db�� ���� ��Ͽ��� ����
		void cancleReservation(Message m) {
			boolean isCancled;
			isCancled = DB_dao.bookingCancel(m.getBuilding(), m.getRoomNum(), m.getStudentId(), m.getName(),
					m.getDate());
			if (isCancled) // ��� ����
				m.setMsg("success");
			else
				m.setMsg("failed");
		}

		// �����ڰ� ������ ��ȸ
		ArrayList<Message> ManagerLookUp(Message m) {
			ArrayList<BookedDTO> dataList = new ArrayList<BookedDTO>();// db���� ��ȯ�޴� ������
			ArrayList<Message> msgList = new ArrayList<Message>();// MessageŸ������ ��ȯ�ϱ� ����

			// ��ȸ �� ��ȯ�� ����
			dataList = DB_dao.getAll();// ArrayList<BookedDTO>

			// ��ȯ ���� �޽����� ���
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
