import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;

public class LoginView extends JFrame{

	Controller buttonListener = new Controller();
	
	static JFrame frame = new JFrame();
	
	JLabel titleLabel = new JLabel("통합 스터디룸 예약 시스템 로그인");
	JPanel titlePanel = new JPanel();
	
	JLabel nameLabel = new JLabel("이름");
	static JTextField nameField = new JTextField(20);
	JLabel numberLabel = new JLabel("학번");
	static JTextField numberField = new JTextField(20);
	
	JPanel loginPanel = new JPanel();
	
	static JButton loginButton = new JButton("로그인");
	
	
	public LoginView() {
		setFrame();
		startUI();
	}
	
	public void setFrame() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("통합 스터디룸 예약 시스템");
		frame.setSize(1000,700);
		frame.setVisible(true);
	}
	
	public void startUI() {
		
		titleLabel.setFont(new Font("돋움", Font.PLAIN, 30));
		titlePanel.add(titleLabel);
		
		
		nameField.setPreferredSize(new Dimension(150, 30));
		numberField.setPreferredSize(new Dimension(150, 30));
		loginPanel.add(nameLabel);
		loginPanel.add(nameField);
		loginPanel.add(numberLabel);
		loginPanel.add(numberField);
		
		loginPanel.add(loginButton);
		loginButton.addActionListener(buttonListener);
		
		frame.add(titlePanel, BorderLayout.NORTH);
		frame.add(loginPanel, BorderLayout.CENTER);
		
		frame.invalidate();
		frame.validate();
		frame.repaint();
	}
	public static void main(String[] args) {
		 
		LoginView login = new LoginView();
		
	}
}
