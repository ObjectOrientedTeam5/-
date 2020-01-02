package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//import Server.BookingServer.BookingThread;
import database.BookAvailableDTO;
import database.BookedDTO;
import database.DB_DAO;
import database.UserDAO;

//�� Ŭ���̾�Ʈ�� ������ ���, �������� ��Ʈ��ũ ����� �߻�
class BookingThread extends Thread {
	// ���� �޽��� �� �Ľ� �޽��� ó���� ���� ���� ����
	String id;// �ʿ��Ѱ�?
	String msg;
	// �޽��� ��ü
	Message m;
	// JSON �ļ�
	Gson gson; // Gson - java ��ü�� JSON ǥ�������� ��ȯ�ϴ� API
	Gson gsonBuilder;

	// Ŭ���̾�Ʈ�� ����� ó���� ����� ��Ʈ�� ����
	private BufferedReader inMsg = null;
	private PrintWriter outMsg = null;

	// DAO
	private DB_DAO DB_dao;
	private UserDAO user_dao;

	Socket s;
	ArrayList<String> onReservationList;
	ArrayList<BookingThread> bookingThreads;

	// �ΰ� ��ü
	Logger logger;

	public BookingThread(Socket s, ArrayList<String> onReservationList, ArrayList<BookingThread> bookingThreads) {
		this.s = s;
		this.onReservationList = onReservationList;
		this.bookingThreads = bookingThreads;
		// �޽��� ��ü ����
		m = new Message();
		// JSON �ļ� �ʱ�ȭ
		gson = new Gson(); // Gson - java ��ü�� JSON ǥ�������� ��ȯ�ϴ� API
		gsonBuilder = new GsonBuilder().create();
		DB_dao = new DB_DAO();
		user_dao = new UserDAO();

		logger = Logger.getLogger(this.getClass().getName());
	}

	// Ŭ���̾�Ʈ���� �����ϴ� JSON �޽����� �о�� Message ��ü�� ������ �� Message ��ü�� �����Ͽ� �޽��� ������ ���� ó���ϴ�
	// ����
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

				System.out.println(msg);

				// JSON �޽����� Message ��ü�� ���� , m�� ��ü, msg�� JSON
				m = gson.fromJson(msg, Message.class);// json�� ��ü�� ��ȯ

				/* �Ľ̵� ���ڿ� �迭�� type ��� ���� ���� ó�� */
				// �α��� �޽����� ��
				if (m.getType().equals("login")) {
					if (login(m))
						id = m.getStudentId(); // �ش� �������� id ����
					if(m.getStudentId().equals("00000000") && m.getMsg().equals("true")) {
						m.setType("managerlogin");
					}
					MsgSendClient(gson.toJson(m)); // Ŭ���̾�Ʈ�� ��Ʈ�ѷ��� ���� ����
					System.out.println("�α���" + m);
				}

				// ���� �õ�
				else if (m.getType().equals("try")) {
					System.out.println("���� �õ�");
					tryReservation(m); // '���� ��' ����Ʈ�� �ش� ���͵�뿡 ���� ������ �Է��Ͽ� �ٸ� ����ڰ� ����õ����� ���ϰ� ��.
					MsgSendClient(gson.toJson(m));
					System.out.println("------���� �� ����Ʈ�� ����ִ� ���------");
					for (String room : onReservationList)
						System.out.println(room);
					System.out.println("-------------------------------");
				}
				// ���� �޽����� ��
				else if (m.getType().equals("reservation")) {
					System.out.println("���� Ȯ��");
					System.out.println("------���� �� ����Ʈ�� ����ִ� ���------");
					for (String room : onReservationList)
						System.out.println(room);
					System.out.println("-------------------------------");
					System.out.println("���� �� �� ����");
					System.out.println(m);

					makeReservation(m);// ���������� DB�� �����ϰ�

					System.out.println("������ ����Ʈ���� ������ ������");
					System.out.println(m.getRoomNum() + " " + m.getDate());

					onReservationList.remove(m.getRoomNum() + " " + m.getDate());// '���� ��' ����Ʈ�� �ش� ���͵�뿡 ���� ������ ����.

					System.out.println("���� �� ����Ʈ�� ����ִ� ���");
					for (String room : onReservationList)
						System.out.println(room);

					MsgSendClient(gson.toJson(m));

				}
				// ���� Ȯ���� ��- list�� ��ȯ
				else if (m.getType().equals("check")) {
					System.out.println("����Ȯ��: " + m + m.getStudentId());
					String msgList;
					msgList = gsonBuilder.toJson(checkReservation(m));// ��ȯ�� ArrayList<Message>
					MsgSendClient(msgList);
					System.out.println("�������� �����ϴ�:" + msgList);
				}
				// ���� ����� ��
				else if (m.getType().equals("cancel")) {
					System.out.println("�������: " + m + m.getStudentId());
					cancelReservation(m);
					MsgSendClient(gson.toJson(m));
				}
				// ��ȸ�� ��- list�� ��ȯ
				else if (m.getType().equals("lookUp")) {
					String msgList;
					msgList = gsonBuilder.toJson(lookUpEmptyRoom(m));// ��ȯ�� ArrayList<Message>
					MsgSendClient(msgList);// jsonȭ �� ���� ����
				}
				// ������ ���- ��ȸ
				else if (m.getType().equals("managerLookUp")) {
					String msgList;
					msgList = gsonBuilder.toJson(ManagerLookUp(m));// ��ȯ�� ArrayList<Message>
					MsgSendClient(msgList);// jsonȭ �� ���� ����
					System.out.println("�����ڸ��: " + msgList);
				}
				// �������� ���
				else if (m.getType().equals("exit")) {
					bookingThreads.remove(this);
					// �ش� Ŭ���̾�Ʈ ������ ����� status�� false�� ����
					status = false;
				}
				// �������������� �������� �ʰ� ���� ���
				else if (m.getType().equals("bookingCancel")) {
					System.out.println("�������������� �������� �ʰ� ���� ���");
					System.out.println(m);
					onReservationList.remove(m.getRoomNum() + " " + m.getDate());// '���� ��' ����Ʈ�� �ش� ���͵�뿡 ���� ������ ����.
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
		} catch (Exception e) {
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

	// ���� ���� ������ üũ�Ѵ�.
	boolean isOnReservation(Message m) {
		for (String room : onReservationList) {
			if (room.equals(m.getRoomNum() + " " + m.getDate()))// ���� ���� ����Ʈ���
				return true;
		}
		return false;
	}

	// ������ �õ��Ѵ�. ���� ���� ���� �޼ҵ��� ������ �� ����.
	void tryReservation(Message m) {// ó�� ���� ��ư Ŭ�� ��
		if (isOnReservation(m)) {// �̹� ���� ���̸�
			m.setMsg("already on a reservation");// Ŭ���̾�Ʈ �ܿ��� �������̶�� �޽��� ��������
		} else {// ���� ���� �ƴϸ�
			onReservationList.add(m.getRoomNum() + " " + m.getDate());// ���� �� ��Ͽ� �߰�
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
					Integer.toString(dataList.get(i).getMaxPeople()), Integer.toString(dataList.get(i).getIsProject()),
					"", "lookUp", ""));
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
	void cancelReservation(Message m) {
		boolean iscanceled;
		iscanceled = DB_dao.bookingCancel(m.getBuilding(), m.getRoomNum(), m.getStudentId(), m.getName(), m.getDate());
		if (iscanceled) // ��� ����
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
					Integer.toString(dataList.get(i).getMaxPeople()), Integer.toString(dataList.get(i).getIsProject()),
					dataList.get(i).getDate(), "managerLookUp", ""));
		}
		return msgList;
	}
}
