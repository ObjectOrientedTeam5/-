import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ReservationView extends JFrame{
	static int reservationDate;
	static int reservationHour;
	static String reservationRoom;
	static String reservationPeople;
	static boolean reservationProjecter;
	
	static JFrame frame = new JFrame();
	
	JLabel titleLabel = new JLabel("���͵�� ����");
	JPanel titlePanel = new JPanel();
	
	JLabel userNameLabel = new JLabel("�̸�");
	JTextField userNameField = new JTextField();
	JLabel userNumberLabel = new JLabel("�й�");
	JTextField userNumberField = new JTextField();
	
	static JButton userCheckButton = new JButton("����� ��ȸ");
	
	JPanel subPanel = new JPanel();
	
	
	
	static JTextArea textArea = new JTextArea();
	static JPanel textAreaPanel = new JPanel();
	
	static JButton findButton = new JButton("��ȸ");
	static JButton reservationButton = new JButton("����");
	static JButton cancelButton = new JButton("���");
	JPanel buttonPanel = new JPanel();
	
	Controller buttonListener = new Controller();
	
	
	public ReservationView() {
		setFrame();
		startUI();
	}
	
	public void setFrame() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("���� ���͵�� ���� �ý���");
		frame.setSize(1000,700);
		frame.setVisible(true);
		
	}
	
	public void startUI() {
		
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
		titleLabel.setFont(new Font("����", Font.PLAIN, 30));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 150, 0, 0));
		titlePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		titlePanel.add(titleLabel);
		
		
		userNameField.setPreferredSize(new Dimension(150,30));
		userNumberField.setPreferredSize(new Dimension(150,30));
		
		subPanel.add(userNameLabel);
		subPanel.add(userNameField);
		subPanel.add(userNumberLabel);
		subPanel.add(userNumberField);
		subPanel.add(userCheckButton);
		
		
		titlePanel.add(subPanel);
		textAreaSetting();
		
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		buttonPanel.add(findButton);
		buttonPanel.add(reservationButton);
		buttonPanel.add(cancelButton);
		
		findButton.addActionListener(buttonListener);
		reservationButton.addActionListener(buttonListener);
		cancelButton.addActionListener(buttonListener);
		
		
		frame.add(titlePanel, BorderLayout.NORTH);
		frame.add(textAreaPanel, BorderLayout.CENTER);
		frame.add(buttonPanel, BorderLayout.SOUTH);
		frame.invalidate();
		frame.validate();
		frame.repaint();
	}
	
	public static void textAreaSetting() {
		textArea.setPreferredSize(new Dimension(800,500));
		textArea.setFont(new Font("����", Font.PLAIN, 20));
		textArea.setText("<��������>" + "\n" + 
						"������ ��¥ : " + reservationDate + "�� " + reservationHour+ "��" + "\n" + 
						"������ ���͵�� : " + reservationRoom + "\n" +
						"��밡�� �ο� �� : " + reservationPeople + "\n" +
						"�� �������� ���� : " + reservationProjecter
						);
		textAreaPanel.add(textArea);
		
	}
	
	
}
