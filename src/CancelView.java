import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CancelView extends JFrame {

	static String building = "������ ���̽� �ǹ� ��� ��";
	static String roomNumber = "������ ���̽� ���͵�� ��� ��";
	static String maxPeople = "20";
	static String date = "���� 1��";
	static String studentName;
	static String studentNumber;

	static int dataBaseRow = 3;
	static int dataBaseCol = 3;

	static JFrame frame = new JFrame();

	JLabel titleLabel = new JLabel("���͵�� ���� ���");
	JPanel titlePanel = new JPanel();

	static String[] header = { "�ǹ���", "���͵� �� ��", "��밡�� �ο� ��", "����ð�" };
	static String[][] data = new String[50][3];
	static DefaultTableModel mod = new DefaultTableModel(data, header) {
		public boolean isCellEditable(int rowIndex, int mColIndex) {
			return false;
		}
	};

	static JTable table = new JTable(mod);
	JScrollPane tablePanel = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	static JButton backButton = new JButton("�ڷΰ���");
	static JButton cancelButton = new JButton("���� ���");
	JPanel buttonPanel = new JPanel();

	public CancelView() {
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

		table.setRowHeight(25);
		tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));
		tablePanel.setPreferredSize(new Dimension(1000, 400));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // ���̺� �� �ุ ���õǰ� ����
		table.getTableHeader().setReorderingAllowed(false); // ���̺� ��� �̵��Ұ�
		table.getTableHeader().setResizingAllowed(false); // ���̺� ��� ũ�� ���� �Ұ�

		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPanel.add(backButton);
		buttonPanel.add(cancelButton);

		frame.add(titlePanel, BorderLayout.NORTH);
		frame.add(tablePanel, BorderLayout.CENTER);
		frame.add(buttonPanel, BorderLayout.SOUTH);

		frame.invalidate();
		frame.validate();
		frame.repaint();
	}

	public void addButtonActionListener(ActionListener listener) {
		backButton.addActionListener(listener);
		cancelButton.addActionListener(listener);

	}
}
