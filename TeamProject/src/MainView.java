import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.Calendar;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class MainView extends JFrame{
	
	static JFrame frame = new JFrame();
	
	DecimalFormat df = new DecimalFormat("00");
    Calendar currentCalendar = Calendar.getInstance();

    int lastDay = Integer.parseInt(df.format(currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH ))); // �̹����� ������ ���� ������
    int currentDate = currentCalendar.get(currentCalendar.DATE); // ���� ��¥�� ������
   
    
	Controller buttonListener = new Controller();
	
	// ������ ���̽� ���� ������
	static String buildingResult = "������ ���̽� �ǹ� ��� ��";
	static String roomResult = "������ ���̽� ���͵�� ��� ��";
	static String peopleResult = "20";
	
	static int dataBaseRow = 3;
	static int dataBaseCol = 3;
	
	// ù��° �г�
	JLabel titleLabel = new JLabel("���� ���͵�� ���� �ý���");
	JPanel titlePanel = new JPanel();
	
	// ù��° �г� ���� ù��° �г�
	JPanel subPanel = new JPanel();
	
	// ù��° �г� ���� �ι�° �г�
	static JComboBox dateComboBox = new JComboBox();
	JLabel dateLabel = new JLabel("��");
	
	static JComboBox hourComboBox = new JComboBox();
	JLabel hourLabel = new JLabel("��");
	
	static JTextField peopleField = new JTextField(3);
	JLabel peopleLabel = new JLabel("��");
	
	static JCheckBox projectCheckBox = new JCheckBox();
	JLabel projectLabel = new JLabel("�� �������� ����");
	
	JPanel selectionPanel = new JPanel();
	
	// ����° �г�
	static String[] header = {"�ǹ���","���͵� �� ��", "��밡�� �ο� ��"};
	static String[][] data = new String[20][2];
	static DefaultTableModel mod = new DefaultTableModel(data, header) {
		public boolean isCellEditable(int rowIndex, int mColIndex) {
            return false;
        }
    };
	static JTable table = new JTable(mod);
	JScrollPane tablePanel = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	// �׹�° �г�
	static JButton findButton = new JButton("��ȸ");
	static JButton reservationButton = new JButton("����");
	static JButton cancelButton = new JButton("���");
	JPanel buttonPanel = new JPanel();
	
	public MainView() {
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
		
		titlePanel.setLayout(new BoxLayout(titlePanel ,BoxLayout.Y_AXIS));
		titleLabel.setFont(new Font("����", Font.PLAIN, 30));
		
		subPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));
		subPanel.add(titleLabel);
		
		titlePanel.add(subPanel);
		
		dateComboBox.setPreferredSize(new Dimension(100,30));
		hourComboBox.setPreferredSize(new Dimension(100,30));
		comboBoxSetting();
		
		selectionPanelSetting();
		titlePanel.add(selectionPanel);
		frame.repaint();
		
		tableSetting();
		
		buttonPanelSetting();
		
		
		findButton.addActionListener(buttonListener);
		reservationButton.addActionListener(buttonListener);
		cancelButton.addActionListener(buttonListener);
		
		addAllPanel();
		frame.invalidate();
		frame.validate();
		frame.repaint();
	}
	public void comboBoxSetting() {
		for(int i = currentDate; i <= lastDay; i++) {
			dateComboBox.addItem(i); // ���糯¥���� ������ ��¥���� dateComboBox�� Item�߰�
		}
		for(int i = 0; i <= 24; i++) {
			hourComboBox.addItem(i);
		}
	}
	
	public void selectionPanelSetting() {
		selectionPanel.add(dateComboBox);
		selectionPanel.add(dateLabel);
		selectionPanel.add(hourComboBox);
		selectionPanel.add(hourLabel);
		selectionPanel.add(peopleField);
		selectionPanel.add(peopleLabel);
		selectionPanel.add(projectCheckBox);
		selectionPanel.add(projectLabel);
		selectionPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
	}
	public void tableSetting() {
		table.setRowHeight(25);

		tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));
		tablePanel.setPreferredSize(new Dimension(1000,400));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // ���̺� �� �ุ ���õǰ� ����
		table.getTableHeader().setReorderingAllowed(false); // ���̺� ��� �̵��Ұ�
		table.getTableHeader().setResizingAllowed(false); // ���̺� ��� ũ�� ���� �Ұ�
		
		
	}
	
	public void buttonPanelSetting() {
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		buttonPanel.add(findButton);		
		buttonPanel.add(reservationButton);		
		buttonPanel.add(cancelButton);
		
	}
	
	public void addAllPanel() {
		frame.add(titlePanel, BorderLayout.NORTH);
		frame.add(tablePanel,BorderLayout.CENTER);
		frame.add(buttonPanel, BorderLayout.SOUTH);
		
	}
	public static void main(String[] args) {
		 
		MainView main = new MainView();
		   
	}

	

}
