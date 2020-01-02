import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CancelView extends JFrame {

	static String building = "데이터 베이스 건물 결과 값";
	static String roomNumber = "데이터 베이스 스터디룸 결과 값";
	static String maxPeople = "20";
	static String date = "ㅁ일 1시";
	static String studentName;
	static String studentNumber;

	static int dataBaseRow = 3;
	static int dataBaseCol = 3;

	static JFrame frame = new JFrame();

	JLabel titleLabel = new JLabel("스터디룸 예약 취소");
	JPanel titlePanel = new JPanel();

	static String[] header = { "건물명", "스터디 룸 명", "사용가능 인원 수", "예약시간" };
	static String[][] data = new String[50][3];
	static DefaultTableModel mod = new DefaultTableModel(data, header) {
		public boolean isCellEditable(int rowIndex, int mColIndex) {
			return false;
		}
	};

	static JTable table = new JTable(mod);
	JScrollPane tablePanel = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	static JButton backButton = new JButton("뒤로가기");
	static JButton cancelButton = new JButton("예약 취소");
	JPanel buttonPanel = new JPanel();

	public CancelView() {
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

		table.setRowHeight(25);
		tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));
		tablePanel.setPreferredSize(new Dimension(1000, 400));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 테이블에 한 행만 선택되게 설정
		table.getTableHeader().setReorderingAllowed(false); // 테이블 헤더 이동불가
		table.getTableHeader().setResizingAllowed(false); // 테이블 헤더 크기 조정 불가

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
