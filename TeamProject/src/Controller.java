import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

public class Controller implements ActionListener{
	int reservationViewchangeCheck = 0;
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object obj = e.getSource();
		if(obj == MainView.findButton) {
			setTable();
		}
		else if(obj == MainView.reservationButton) {
			try {
				
				getTableResult();
				
				if(MainView.buildingResult == null) {
					JOptionPane.showMessageDialog(null, "�ùٸ� ���� ������ �ֽʽÿ�!");
				}
				else {
					setUserReservationData();
					if(reservationViewchangeCheck == 0) {	
						ReservationView reservationView = new ReservationView();
						MainView.frame.setVisible(false);
						reservationViewchangeCheck++;
					}
					else {
						ReservationView.frame.setVisible(true);
						ReservationView.textAreaSetting();
						ReservationView.frame.invalidate();
						ReservationView.frame.validate();
						ReservationView.frame.repaint();
						MainView.frame.setVisible(false);
						
					}
					
					
				}
				
			}catch (ArrayIndexOutOfBoundsException exception) {
				// TODO: handle exception
				JOptionPane.showMessageDialog(null, "�����Ͻ� ���͵���� �������ּ���!!");
			}
			
		}
		else if(obj == MainView.cancelButton) {
			
		}
		else if(obj == ReservationView.findButton) {
			
			ReservationView.frame.setVisible(false);
			MainView.frame.setVisible(true);
			
		}
		else if(obj == ReservationView.reservationButton) {
			
		}
		else if(obj == ReservationView.cancelButton) {
			
		}
	}
	// ���������� ����View���� �����ͷ� ����
	public void setUserReservationData() {
			
			ReservationView.reservationDate = (int) MainView.dateComboBox.getSelectedItem();
			ReservationView.reservationHour = (int) MainView.hourComboBox.getSelectedItem();
			ReservationView.reservationRoom = MainView.buildingResult + " " + MainView.roomResult;
			ReservationView.reservationPeople = MainView.peopleResult;
			ReservationView.reservationProjecter = MainView.projectCheckBox.isSelected();
			
	}
	// JTable�� �ԷµǾ��ִ� �������� ������ ����
	public void getTableResult() {
			int getRow = MainView.table.getSelectedRow();
			MainView.buildingResult = (String) MainView.table.getValueAt(getRow, 0);
			MainView.roomResult = (String) MainView.table.getValueAt(getRow, 1);
			MainView.peopleResult = (String) MainView.table.getValueAt(getRow, 2);
		}
	// ���̺� �����͸� ����
	public void setTable() {
		try {
			// ������ ���̽����� ó���ؾ��� �κ�
			for(int i = 0; i < MainView.dataBaseRow; i++) {
				// ���밡�� �ο� ��  ��ȸ (�� ��)
				// peopleResult = �����ͺ��̽����� i��° �� 2��° ��(���밡�� �ο��� �κ�)���� �������� �� 
				
				// TextField�� ���� �����ͺ��̽����� ������ �ο��� ���� peopleResult�� ���ؼ� TextField�� �ִ� ������ ���ų� ū �ο� ���� �ش��ϴ� �������� ������
				if(Integer.parseInt(MainView.peopleField.getText()) <= Integer.parseInt(MainView.peopleResult)) {
					MainView.mod.setValueAt(MainView.peopleResult, i, 2);
					// roomResult = �����ͺ��̽����� i��° �� 1��° ��(���͵�� �κ�)���� �������� ��
					MainView.mod.setValueAt(MainView.roomResult, i, 1); 
					// buildingResult = �����ͺ��̽����� i��° �� 0��° ��(�ǹ��κ�)���� �������� ��
					MainView.mod.setValueAt(MainView.buildingResult, i, 0);
				}
				else {
					clearTable(i);
				}
			}
			
			
		}catch (Exception e) {
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
}
