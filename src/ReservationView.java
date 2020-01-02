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
	static String reservationDate = "정보없음";
	static String reservationRoom = "정보없음";
	static String reservationPeople = "정보없음";
	static String reservationProjecter = "정보없음";
	static String studentName;
	static String studentNumber;

	static JFrame frame = new JFrame();

	JLabel titleLabel = new JLabel("스터디룸 예약");
	JPanel titlePanel = new JPanel();

	static JTextArea textArea = new JTextArea();
	static JPanel textAreaPanel = new JPanel();

	static JButton backButton = new JButton("뒤로가기");
	static JButton reservationButton = new JButton("예약");
	JPanel buttonPanel = new JPanel();

	public ReservationView() {
		setFrame();
		startUI();
	}

	public void setFrame() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("통합 스터디룸 예약 시스템");
		frame.setSize(1000, 700);
		frame.setVisible(true);

	}

	public void startUI() {

		titleLabel.setFont(new Font("돋움", Font.PLAIN, 30));
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
		textArea.setFont(new Font("돋움", Font.PLAIN, 20));

		textArea.setText("<예약정보>" + "\n" + "예약한 날짜 : " + reservationDate + "\n" + "예약한 스터디룸 : " + reservationRoom + "\n"
				+ "사용가능 인원 수 : " + reservationPeople + "\n" + "빔 프로젝터 유무 : " + reservationProjecter);
		textAreaPanel.add(textArea);

	}

	public void addButtonActionListener(ActionListener listener) {
		backButton.addActionListener(listener);
		reservationButton.addActionListener(listener);
	}

}
