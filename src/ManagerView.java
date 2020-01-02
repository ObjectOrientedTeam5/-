import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ManagerView extends JFrame {

	static String building = "������ ���̽� �ǹ� ��� ��";
	static String roomNumber = "������ ���̽� ���͵�� ��� ��";
	static String studentName = "���̽�";
	static String studentNumber = "16011094";

	// Controller buttonListener = new Controller();

	JLabel titleLabel = new JLabel("�����ڿ� ���͵�� ��ȸ");
	JPanel titlePanel = new JPanel();

	static String[] header = { "�ǹ���", "���͵� �� ��", "����� �̸�", "����� �й�", "���ð�" };
	static String[][] data = new String[50][4];
	static DefaultTableModel mod = new DefaultTableModel(data, header) {
		public boolean isCellEditable(int rowIndex, int mColIndex) {
			return false;
		}
	};

	JTable table = new JTable(mod);
	JScrollPane tablePanel = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	static JButton findButton = new JButton("��ȸ");
	JPanel buttonPanel = new JPanel();

	public ManagerView() {

		setFrame();
		startUI();

	}

	public void setFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("���� ���͵�� ���� �ý���");
		setSize(1000, 700);
		setVisible(true);
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

		buttonPanel.add(findButton);
		// findButton.addActionListener(buttonListener);

		add(titlePanel, BorderLayout.NORTH);
		add(tablePanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		invalidate();
		validate();
		repaint();

	}

	public void addButtonActionListener(ActionListener listener) {
		findButton.addActionListener(listener);
	}

}
