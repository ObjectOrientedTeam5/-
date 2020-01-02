import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ReservationView extends JFrame {
	static String reservationDate = "��������";
	static String reservationRoom = "��������";
	static String reservationPeople = "��������";
	static String reservationProjecter = "��������";
	static String studentName;
	static String studentNumber;

	static JFrame frame = new JFrame();

	JLabel titleLabel = new JLabel("���͵�� ����");
	JPanel titlePanel = new JPanel();

	static JTextArea textArea = new JTextArea();
	static JPanel textAreaPanel = new JPanel();

	static JButton backButton = new JButton("�ڷΰ���");
	static JButton reservationButton = new JButton("����");
	JPanel buttonPanel = new JPanel();

	public ReservationView() {
		setFrame();
		startUI();
	}

	public void setFrame() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("���� ���͵�� ���� �ý���");
		frame.setSize(1000, 700);
		frame.setVisible(true);

	}

	public void startUI() {

		titleLabel.setFont(new Font("����", Font.PLAIN, 30));
		titlePanel.add(titleLabel);

		textAreaSetting();

		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPanel.add(backButton);
		buttonPanel.add(reservationButton);

		frame.add(titlePanel, BorderLayout.NORTH);
		frame.add(textAreaPanel, BorderLayout.CENTER);
		frame.add(buttonPanel, BorderLayout.SOUTH);
		frame.invalidate();
		frame.validate();
		frame.repaint();
	}

	public static void textAreaSetting() {

		textArea.setPreferredSize(new Dimension(800, 500));
		textArea.setFont(new Font("����", Font.PLAIN, 20));

		textArea.setText("<��������>" + "\n" + "������ ��¥ : " + reservationDate + "\n" + "������ ���͵�� : " + reservationRoom + "\n"
				+ "��밡�� �ο� �� : " + reservationPeople + "\n" + "�� �������� ���� : " + reservationProjecter);
		textAreaPanel.add(textArea);

	}

	public void addButtonActionListener(ActionListener listener) {
		backButton.addActionListener(listener);
		reservationButton.addActionListener(listener);
	}

}
