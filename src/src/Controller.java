import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import Server.Message;

public class Controller implements Runnable {

	static boolean reservationViewChangeCheck;
	static boolean cancelViewChangeCheck;

	private LoginView loginv;
	private MainView mainv;
	private ReservationView reservationv;
	private CancelView cancelv;

	private BufferedReader inMsg = null;
	private PrintWriter outMsg = null;

	Gson gson = new Gson();
	Socket socket;
	Message m;
	Thread thread;
	String ip = "127.0.0.1";
	boolean status;

	Controller(LoginView loginView, MainView mainView, ReservationView reservationv, CancelView cancelView) {
		this.loginv = loginView;
		this.mainv = mainView;
		this.reservationv = reservationv;
		this.cancelv = cancelView;

	}

	public void appMainLogin() {
		loginv.addButtonActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object obj = e.getSource();
				// loginView���� �α��� ��ư�� ������ ��
				if (obj == LoginView.loginButton) {
					// �����ͺ��̽����� �й� �̸��� ��ȸ�ؼ� �����Ͱ� �����ϴ��� ó���ؾ���
					connectServer(LoginView.nameField.getText(), LoginView.numberField.getText()); // �α��� ��ư�� �������� ������
																									// ����.

					if (LoginView.nameField.getText().equals("11")) {
						ManagerView manager = new ManagerView();
						LoginView.frame.dispose();
					}
				}
			}
		});
	}

	public void appMainMain() {
		mainv.addButtonActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object obj = e.getSource();

				if (obj == MainView.findButton) {
					// �����ͺ��̽����� ���� �о��
					System.out.println("��ȸ");
//					
//					// test
//					Message m2 = new Message("11111111", "ȫ�浿", "�м�������", "s1", "5", "1", "2019-12-12 15:22:39",
//							"lookUp", "");
//					outMsg.println(gson.toJson(m2));
//					System.out.println("�޽��� ����");
					setTable();
				}
				// MainView���� ���� ��ư�� ������ ��
				else if (obj == MainView.reservationButton) {
					try {
						// ���� �õ� ���� // ���̺� �ִ� ������� ������

						tryResevation();

						// ���� �õ� ��

						getTableResult();

						// ���̺��� ��ĭ�� ������ ��
						if (MainView.building == null) {
							JOptionPane.showMessageDialog(null, "�ùٸ� ���� ������ �ֽʽÿ�!");
						} else {
							// ������ �����Ϸ��� ������ ����
							setUserReservationData();
							// ������ ��ȯ�� ���� �ִ��� üũ
							if (reservationViewChangeCheck) {

								// ��ȯ�� ���� ���� �� ��� ��������� ReservationView�� �ҷ���
								ReservationView.frame.setVisible(true);
								ReservationView.textAreaSetting();
								ReservationView.frame.invalidate();
								ReservationView.frame.validate();
								ReservationView.frame.repaint();
								MainView.frame.setVisible(false);

							} else {
								// ��ȯ�� ���� ������ ReservationView ��ü�� ���� ���� ȭ����ȯ
								reservationv = new ReservationView();
								appMainReservation();

								// MainView�� ��� ������� ��
								MainView.frame.setVisible(false);
								reservationViewChangeCheck = true;
							}
						}
						// ���͵���� ���� ���ϰ� �������� ��� ����ó��
					} catch (ArrayIndexOutOfBoundsException exception) {
						// TODO: handle exception
						JOptionPane.showMessageDialog(null, "�����Ͻ� ���͵���� �������ּ���!!");
					}

				}
				// MainView���� ��ҹ�ư�� ������ ��
				else if (obj == MainView.cancelButton) {
					// ��Һ�� ��ȯ���� �ִ��� üũ
					if (cancelViewChangeCheck) {
						// ��ȯ�� ���� ���� �� ��� ��������� CancelView�� �ҷ���
						CancelView.frame.setVisible(true);
						CancelView.frame.invalidate();
						CancelView.frame.validate();
						CancelView.frame.repaint();
						MainView.frame.setVisible(false);

					} else {

						// ��ȯ�� ���� ���ٸ� CancelView ��ü�� ���� ����� ȭ����ȯ
						cancelv = new CancelView();
						appMainCancel();
						// MainView�� ��� ������� ��
						MainView.frame.setVisible(false);
						cancelViewChangeCheck = true;
					}
				}
			}
		});
	}

	public void appMainReservation() {
		reservationv.addButtonActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object obj = e.getSource();
				if (obj == ReservationView.findButton) {
					// ��ȸ��� ��ȯ
					ReservationView.frame.setVisible(false);
					MainView.frame.setVisible(true);

				} else if (obj == ReservationView.reservationButton) {
					// �����ͺ��̽��� ���� ����
					Resevation();

				} else if (obj == ReservationView.cancelButton) {
					// CancelView�� ��ȯ

					if (cancelViewChangeCheck) {

						CancelView.frame.setVisible(true);
						CancelView.frame.invalidate();
						CancelView.frame.validate();
						CancelView.frame.repaint();
						ReservationView.frame.setVisible(false);

					} else {

						cancelv = new CancelView();
						appMainCancel();
						loadReservating();// ������� ��ư �������� ����� ���� �������� �޽��� ����
						// ��ȯ�� ���� ���ٸ� CancelView ��ü�� ���� ����� ȭ����ȯ
						ReservationView.frame.setVisible(false);
						cancelViewChangeCheck = true;
					}
				}
			}
		});
	}

	public void appMainCancel() {
		cancelv.addButtonActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object obj = e.getSource();

				if (obj == CancelView.cancelButton) {
					// �����ͺ��̽��� ����� ���� ����
					CancelReservation();// ���� ���

				}
			}

		});
	}

	public void appMain() {
	}

	public void loadReservating() { // ������� ��ư �������� ����� ���� �������� �޽��� ����

	}

	public void CancelReservation() { // ���� ���
		m = new Message();
		// m.setBuilding();
	}

	public void Resevation() {
		m = new Message();
		m.setName(LoginView.nameField.getText());
		m.setStudentId(LoginView.numberField.getText());
		m.setRoomNum(ReservationView.reservationRoom);
		m.setCapacity(ReservationView.reservationPeople);
		m.setEquipment(ReservationView.reservationProjecter);
		m.setType("reservation");
		outMsg.println(gson.toJson(m));
	}

	public void tryResevation() {
		m = new Message();
		System.out.println("����õ�");
		String DATE = "2020-01-" + MainView.dateComboBox.getSelectedItem() + " "
				+ MainView.hourComboBox.getSelectedItem() + ":00:00";
		// DATE : 2020-01-�� ��:00:00
		m.setDate(DATE);
		m.setBuilding(MainView.building);
		m.setRoomNum(MainView.roomNumber);
		// m.setCapacity(ReservationView.reservationPeople);
		// m.setEquipment(ReservationView.reservationProjecter);
		m.setType("try");
		outMsg.println(gson.toJson(m));
	}

	// ���������� ����View���� �����ͷ� ����
	public void setUserReservationData() {

		ReservationView.reservationDate = MainView.dateComboBox.getSelectedItem() + "�� "
				+ MainView.hourComboBox.getSelectedItem() + "��";
		ReservationView.reservationRoom = MainView.building + " " + MainView.roomNumber;
		ReservationView.reservationPeople = MainView.maxPeople;
		if (MainView.projectCheckBox.isSelected()) {
			ReservationView.reservationProjecter = "O";
		} else {
			ReservationView.reservationProjecter = "X";
		}

	}

	// JTable�� �ԷµǾ��ִ� �������� ������ ����
	public void getTableResult() {
		int getRow = MainView.table.getSelectedRow();
		MainView.building = (String) MainView.table.getValueAt(getRow, 0);
		MainView.roomNumber = (String) MainView.table.getValueAt(getRow, 1);
		MainView.maxPeople = (String) MainView.table.getValueAt(getRow, 2);
	}

	// ���̺� �����͸� ����
	public void setTable() {
		try {
			// ������ ���̽����� ó���ؾ��� �κ�
			for (int i = 0; i < MainView.dataBaseRow; i++) {
				// ���밡�� �ο� �� ��ȸ (�� ��)
				// maxPeople = �����ͺ��̽����� i��° �� 2��° ��(���밡�� �ο��� �κ�)���� �������� ��

				// TextField�� ���� �����ͺ��̽����� ������ �ο��� ���� maxPeople�� ���ؼ� TextField�� �ִ� ������ ���ų� ū �ο�
				// ���� �ش��ϴ� �������� ������
				if (Integer.parseInt(MainView.peopleField.getText()) <= Integer.parseInt(MainView.maxPeople)) {
					MainView.mod.setValueAt(MainView.maxPeople, i, 2);
					// roomNumber = �����ͺ��̽����� i��° �� 1��° ��(���͵�� �κ�)���� �������� ��
					MainView.mod.setValueAt(MainView.roomNumber, i, 1);
					// building = �����ͺ��̽����� i��° �� 0��° ��(�ǹ��κ�)���� �������� ��
					MainView.mod.setValueAt(MainView.building, i, 0);
				} else {
					clearTable(i);
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			JOptionPane.showMessageDialog(null, "�ο� ���� ��Ȯ�� �Է����ּ���!!");
		}

	}

	public void userSetTable() {
		try {
			// ������ ���̽����� ó���ؾ��� �κ�
			for (int i = 0; i < CancelView.dataBaseRow; i++) {

				// date = �����ͺ��̽����� i��° �� 3��° ��(����ð� �κ�)���� �������� ��
				CancelView.mod.setValueAt(CancelView.date, i, 3);
				// maxPeople = �����ͺ��̽����� i��° �� 2��° ��(���밡�� �ο��� �κ�)���� �������� ��
				CancelView.mod.setValueAt(CancelView.maxPeople, i, 2);
				// roomNumber = �����ͺ��̽����� i��° �� 1��° ��(���͵�� �κ�)���� �������� ��
				CancelView.mod.setValueAt(CancelView.roomNumber, i, 1);
				// building = �����ͺ��̽����� i��° �� 0��° ��(�ǹ��κ�)���� �������� ��
				CancelView.mod.setValueAt(CancelView.building, i, 0);

			}

		} catch (Exception e) {
			// TODO: handle exception
			JOptionPane.showMessageDialog(null, "�ο� ���� ��Ȯ�� �Է����ּ���!!");
		}

	}

	// ���̺� �ִ� �����͸� �������� �ʱ�ȭ
	public void clearTable(int i) {
		MainView.mod.setValueAt(" ", i, 0);
		MainView.mod.setValueAt(" ", i, 1);
		MainView.mod.setValueAt(" ", i, 2);
	}

	public void connectServer(String name, String id) {
		try {
			// ���� ����
			socket = new Socket(ip, 8888);
			// logger.log(Level.INFO,"[Client]Server ���� ����!");

			// ����� ��Ʈ�� ����
			inMsg = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outMsg = new PrintWriter(socket.getOutputStream(), true);

			// ������ �α��� �޽��� ����
			m = new Message();
			m.setStudentId(id);
			m.setName(name);
			m.setType("login");
			outMsg.println(gson.toJson(m));

			// �޽��� ������ ���� ������ ����
			thread = new Thread(this);
			thread.start();
		} catch (Exception e) {
			// logger.log(Level.WARNING ,"[MultichatUI]connectServer() Exception �߻�!!");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		String msg;
		status = true;
		ArrayList<Message> msgArray=null;
		
		while (status) {
			try {
				msg = inMsg.readLine();// json, string
				
				//�������� Message�� ������ ���� ArrayList<Message>�� ������ ��� ����
				if(msg.charAt(0)=='[') {// ArrayList<Message>
					Type msgListType = new TypeToken<ArrayList<Message>>(){}.getType();
					msgArray = gson.fromJson(msg, msgListType);
					System.out.println("arraylist");
					System.out.println(msg);
				}else {//Message
					m = gson.fromJson(msg, Message.class);// ��ü
					System.out.println(m);
				}
				if (m.getType().equals("login")) {
					if (m.getMsg().equals("true")) {
						System.out.println("���ῡ �����Ͽ����ϴ�. ��� : " + m.getMsg());
						// MainView main = new MainView();

						this.mainv = new MainView();
						appMainMain();

						LoginView.frame.dispose();
						
					} else if (m.getMsg().equals("false")) {
						JOptionPane.showMessageDialog(null, "��ȸ�� ������ �����ϴ�! �ٽ� ������ �Է����ּ���!");
						LoginView.nameField.setText("");
						LoginView.numberField.setText("");
					}
				} else if (m.getType().equals("try")) // ���� �õ�
				{
					if (m.getMsg().equals("already on a reservation")) // �̹� �������̶��
					{
						// ������ ������ �Դϴٶ�� �˾�â ����
					} else if (m.getMsg().equals("available")) // ���� ������ �� �̶��
					{
						// ����� ����
					}
				} else if (m.getType().equals("reservation")) // �����ϱ�
				{
					if (m.getMsg().equals("success")) // �̹� �������̶��
					{
						// ���� ����
					} else if (m.getMsg().equals("failed")) // ���� ������ �� �̶��
					{
						// ���� ����
					}
				} else if (m.getType().equals("reservation")) // �����ϱ�
				{
					if (m.getMsg().equals("already on a reservation")) // �̹� �������̶��
					{
						// ������ ������ �Դϴٶ�� �˾�â ����
					} else if (m.getMsg().equals("available")) // ���� ������ �� �̶��
					{
						// ����� ����
					}
				}
				else {// ArrayList<Message>
					System.out.println(msg);
					System.out.println("��ȸ�޽��� ���Ź���");
					for(Message message: msgArray)
						System.out.println(msg);
				}

				

				// chatData.refreshData(m.getId()+">"+m.getMsg()+"\n");
				// v.msgOut.setCaretPosition(v.msgOut.getDocument().getLength());
			} catch (Exception e) {
				// logger.log(Level.WARNING,"[MultiChatUI] �޽��� ��Ʈ�� ����!!");
				e.printStackTrace();
			}
		}
		
		// logger.info("[MultiChatUI]"+thread.getName()+" �޽��� ���� ������ �����!!");
	}

	public static void main(String[] args) {
		Controller app = new Controller(new LoginView(), null, null, null);
		app.appMainLogin();

	}

}