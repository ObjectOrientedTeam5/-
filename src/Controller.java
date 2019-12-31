import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

import Server.Message;

public class Controller implements ActionListener, Runnable {

	static boolean reservationViewChangeCheck;
	static boolean cancelViewChangeCheck;

	private BufferedReader inMsg = null;
	private PrintWriter outMsg = null;

	Gson gson = new Gson();
	Socket socket;
	Message msg;
	Thread thread;
	String ip = "127.0.0.1";

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object obj = e.getSource();
		// loginView���� �α��� ��ư�� ������ ��
		if (obj == LoginView.loginButton) {
			// �����ͺ��̽����� �й� �̸��� ��ȸ�ؼ� �����Ͱ� �����ϴ��� ó���ؾ���
			String name, id;
			boolean isSuccess = false;
			name = LoginView.nameField.getText();
			id = LoginView.numberField.getText();

			try {

				connectServer(name, id); // �α��� ��ư�� �������� ������ ����.

				// Socket s = new Socket("127.0.0.1", 8888);
				// System.out.println("## Ŭ���̾�Ʈ ����...");
				// outMsg = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));

				// outMsg.println(gson.toJson(new Message(id, name, "", "", "", "", "", "login",
				// "")));

			} catch (Exception ex) {
				ex.printStackTrace();
			}

			MainView main = new MainView();
			LoginView.frame.dispose();
		}
		// MainView���� ��ȸ ��ư�� ������ ��
		else if (obj == MainView.findButton) {
			// �����ͺ��̽����� ���� �о��
			setTable();
		}
		// MainView���� ���� ��ư�� ������ ��
		else if (obj == MainView.reservationButton) {
			try {
				// ���̺� �ִ� ������� ������
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
						ReservationView reservationView = new ReservationView();
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
				CancelView cancelView = new CancelView();
				// MainView�� ��� ������� ��
				MainView.frame.setVisible(false);
				cancelViewChangeCheck = true;
			}
		} else if (obj == ReservationView.findButton) {
			// ��ȸ��� ��ȯ
			ReservationView.frame.setVisible(false);
			MainView.frame.setVisible(true);

		} else if (obj == ReservationView.reservationButton) {
			// �����ͺ��̽��� ���� ����
		} else if (obj == ReservationView.cancelButton) {
			// CancelView�� ��ȯ

			if (cancelViewChangeCheck) {

				CancelView.frame.setVisible(true);
				CancelView.frame.invalidate();
				CancelView.frame.validate();
				CancelView.frame.repaint();
				ReservationView.frame.setVisible(false);

			} else {

				CancelView cancelView = new CancelView();
				// ��ȯ�� ���� ���ٸ� CancelView ��ü�� ���� ����� ȭ����ȯ
				ReservationView.frame.setVisible(false);
				cancelViewChangeCheck = true;
			}
		} else if (obj == CancelView.findButton) {
			// MainView�� ��ȯ
			CancelView.frame.setVisible(false);
			MainView.frame.setVisible(true);

		} else if (obj == CancelView.reservationButton) {
			// ReserVationView�� ��ȯ

			if (reservationViewChangeCheck) {

				// ��ȯ�� ���� ���� �� ��� ��������� ReservationView�� �ҷ���
				ReservationView.frame.setVisible(true);
				ReservationView.textAreaSetting();
				ReservationView.frame.invalidate();
				ReservationView.frame.validate();
				ReservationView.frame.repaint();
				CancelView.frame.setVisible(false);

			} else {

				// ��ȯ�� ���� ������ ReservationView ��ü�� ���� ���� ȭ����ȯ
				ReservationView reservationView = new ReservationView();
				ReservationView.textAreaSetting();
				// CancelView�� ��� ������� ��
				CancelView.frame.setVisible(false);
				reservationViewChangeCheck = true;

			}
		} else if (obj == CancelView.cancelButton) {
			// �����ͺ��̽��� ����� ���� ����
		}
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
			msg = new Message(id, name, "", "", "", "", "", "login", "");
			System.out.println("c:" + msg);
			outMsg.println(gson.toJson(msg));

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
		String msg="";
		boolean status =true;
		while (status) {
			try {
				msg = inMsg.readLine();
				System.out.println("client: "+msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}