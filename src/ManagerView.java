import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ManagerView extends JFrame {

	static String building = "데이터 베이스 건물 결과 값";
	static String roomNumber = "데이터 베이스 스터디룸 결과 값";
	static String studentName = "주이식";
	static String studentNumber = "16011094";

	// Controller buttonListener = new Controller();

	JLabel titleLabel = new JLabel("관리자용 스터디룸 조회");
	JPanel titlePanel = new JPanel();

	static String[] header = { "건물명", "스터디 룸 명", "사용자 이름", "사용자 학번", "사용시간" };
	static String[][] data = new String[50][4];
	static DefaultTableModel mod = new DefaultTableModel(data, header) {
		public boolean isCellEditable(int rowIndex, int mColIndex) {
			return false;
		}
	};

	JTable table = new JTable(mod);
	JScrollPane tablePanel = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	static JButton findButton = new JButton("조회");
	JPanel buttonPanel = new JPanel();

	public ManagerView() {

		setFrame();
		startUI();

	}

	public void setFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("통합 스터디룸 예약 시스템");
		setSize(1000, 700);
		setVisible(true);
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
