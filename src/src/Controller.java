import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import Server.Message;

public class Controller implements Runnable {

	static boolean reservationViewChangeCheck;
	static boolean cancelViewChangeCheck;

	private LoginView loginv;
	private MainView mainv;
	private ReservationView reservationv;
	private CancelView cancelv;

	private BufferedReader inMsg = null;
	private PrintWriter outMsg = null;

	Gson gson = new Gson();
	Socket socket;
	Message m;
	Thread thread;
	String ip = "127.0.0.1";
	boolean status;

	Controller(LoginView loginView, MainView mainView, ReservationView reservationv, CancelView cancelView) {
		this.loginv = loginView;
		this.mainv = mainView;
		this.reservationv = reservationv;
		this.cancelv = cancelView;

	}

	public void appMainLogin() {
		loginv.addButtonActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object obj = e.getSource();
				// loginView에서 로그인 버튼을 눌렀을 때
				if (obj == LoginView.loginButton) {
					// 데이터베이스에서 학번 이름을 조회해서 데이터가 존재하는지 처리해야함
					connectServer(LoginView.nameField.getText(), LoginView.numberField.getText()); // 로그인 버튼을 눌렀을때 서버와
																									// 연결.

					if (LoginView.nameField.getText().equals("11")) {
						ManagerView manager = new ManagerView();
						LoginView.frame.dispose();
					}
				}
			}
		});
	}

	public void appMainMain() {
		mainv.addButtonActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object obj = e.getSource();

				if (obj == MainView.findButton) {
					// 데이터베이스에서 값을 읽어옴
					System.out.println("조회");
//					
//					// test
//					Message m2 = new Message("11111111", "홍길동", "학술정보원", "s1", "5", "1", "2019-12-12 15:22:39",
//							"lookUp", "");
//					outMsg.println(gson.toJson(m2));
//					System.out.println("메시지 보냄");
					setTable();
				}
				// MainView에서 예약 버튼을 눌렀을 때
				else if (obj == MainView.reservationButton) {
					try {
						// 예약 시도 시작 // 테이블에 있는 결과값을 가져옴

						tryResevation();

						// 예약 시도 끝

						getTableResult();

						// 테이블에서 빈칸을 선택할 시
						if (MainView.building == null) {
							JOptionPane.showMessageDialog(null, "올바른 값을 선택해 주십시오!");
						} else {
							// 유저가 예약하려는 정보를 저장
							setUserReservationData();
							// 예약뷰로 전환된 적이 있는지 체크
							if (reservationViewChangeCheck) {

								// 전환된 적이 있을 시 잠시 사라지게한 ReservationView를 불러옴
								ReservationView.frame.setVisible(true);
								ReservationView.textAreaSetting();
								ReservationView.frame.invalidate();
								ReservationView.frame.validate();
								ReservationView.frame.repaint();
								MainView.frame.setVisible(false);

							} else {
								// 전환된 적이 없으면 ReservationView 객체를 새로 만들어서 화면전환
								reservationv = new ReservationView();
								appMainReservation();

								// MainView는 잠시 사라지게 함
								MainView.frame.setVisible(false);
								reservationViewChangeCheck = true;
							}
						}
						// 스터디룸을 선택 안하고 예약했을 경우 예외처리
					} catch (ArrayIndexOutOfBoundsException exception) {
						// TODO: handle exception
						JOptionPane.showMessageDialog(null, "예약하실 스터디룸을 선택해주세요!!");
					}

				}
				// MainView에서 취소버튼을 눌렀을 때
				else if (obj == MainView.cancelButton) {
					// 취소뷰로 전환된적 있는지 체크
					if (cancelViewChangeCheck) {
						// 전환된 적이 있을 시 잠시 사라지게한 CancelView를 불러옴
						CancelView.frame.setVisible(true);
						CancelView.frame.invalidate();
						CancelView.frame.validate();
						CancelView.frame.repaint();
						MainView.frame.setVisible(false);

					} else {

						// 전환된 적이 없다면 CancelView 객체를 새로 만들어 화면전환
						cancelv = new CancelView();
						appMainCancel();
						// MainView는 잠시 사라지게 함
						MainView.frame.setVisible(false);
						cancelViewChangeCheck = true;
					}
				}
			}
		});
	}

	public void appMainReservation() {
		reservationv.addButtonActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object obj = e.getSource();
				if (obj == ReservationView.findButton) {
					// 조회뷰로 전환
					ReservationView.frame.setVisible(false);
					MainView.frame.setVisible(true);

				} else if (obj == ReservationView.reservationButton) {
					// 데이터베이스로 값들 전송
					Resevation();

				} else if (obj == ReservationView.cancelButton) {
					// CancelView로 전환

					if (cancelViewChangeCheck) {

						CancelView.frame.setVisible(true);
						CancelView.frame.invalidate();
						CancelView.frame.validate();
						CancelView.frame.repaint();
						ReservationView.frame.setVisible(false);

					} else {

						cancelv = new CancelView();
						appMainCancel();
						loadReservating();// 예약취소 버튼 눌렀을때 예약된 룸을 보기위해 메시지 전송
						// 전환된 적이 없다면 CancelView 객체를 새로 만들어 화면전환
						ReservationView.frame.setVisible(false);
						cancelViewChangeCheck = true;
					}
				}
			}
		});
	}

	public void appMainCancel() {
		cancelv.addButtonActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object obj = e.getSource();

				if (obj == CancelView.cancelButton) {
					// 데이터베이스로 취소한 값을 전송
					CancelReservation();// 예약 취소

				}
			}

		});
	}

	public void appMain() {
	}

	public void loadReservating() { // 예약취소 버튼 눌렀을때 예약된 룸을 보기위해 메시지 전송

	}

	public void CancelReservation() { // 예약 취소
		m = new Message();
		// m.setBuilding();
	}

	public void Resevation() {
		m = new Message();
		m.setName(LoginView.nameField.getText());
		m.setStudentId(LoginView.numberField.getText());
		m.setRoomNum(ReservationView.reservationRoom);
		m.setCapacity(ReservationView.reservationPeople);
		m.setEquipment(ReservationView.reservationProjecter);
		m.setType("reservation");
		outMsg.println(gson.toJson(m));
	}

	public void tryResevation() {
		m = new Message();
		System.out.println("예약시도");
		String DATE = "2020-01-" + MainView.dateComboBox.getSelectedItem() + " "
				+ MainView.hourComboBox.getSelectedItem() + ":00:00";
		// DATE : 2020-01-일 시:00:00
		m.setDate(DATE);
		m.setBuilding(MainView.building);
		m.setRoomNum(MainView.roomNumber);
		// m.setCapacity(ReservationView.reservationPeople);
		// m.setEquipment(ReservationView.reservationProjecter);
		m.setType("try");
		outMsg.println(gson.toJson(m));
	}

	// 예약정보를 예약View쪽의 데이터로 전송
	public void setUserReservationData() {

		ReservationView.reservationDate = MainView.dateComboBox.getSelectedItem() + "일 "
				+ MainView.hourComboBox.getSelectedItem() + "시";
		ReservationView.reservationRoom = MainView.building + " " + MainView.roomNumber;
		ReservationView.reservationPeople = MainView.maxPeople;
		if (MainView.projectCheckBox.isSelected()) {
			ReservationView.reservationProjecter = "O";
		} else {
			ReservationView.reservationProjecter = "X";
		}

	}

	// JTable에 입력되어있는 정보들을 변수에 저장
	public void getTableResult() {
		int getRow = MainView.table.getSelectedRow();
		MainView.building = (String) MainView.table.getValueAt(getRow, 0);
		MainView.roomNumber = (String) MainView.table.getValueAt(getRow, 1);
		MainView.maxPeople = (String) MainView.table.getValueAt(getRow, 2);
	}

	// 테이블에 데이터를 셋팅
	public void setTable() {
		try {
			// 데이터 베이스에서 처리해야할 부분
			for (int i = 0; i < MainView.dataBaseRow; i++) {
				// 수용가능 인원 수 조회 (값 비교)
				// maxPeople = 데이터베이스에서 i번째 열 2번째 행(수용가능 인원수 부분)에서 가져오는 값

				// TextField에 값과 데이터베이스에서 가져온 인원수 값인 maxPeople를 비교해서 TextField에 있는 값보다 같거나 큰 인원
				// 수에 해당하는 정보들을 가져옴
				if (Integer.parseInt(MainView.peopleField.getText()) <= Integer.parseInt(MainView.maxPeople)) {
					MainView.mod.setValueAt(MainView.maxPeople, i, 2);
					// roomNumber = 데이터베이스에서 i번째 열 1번째 행(스터디룸 부분)에서 가져오는 값
					MainView.mod.setValueAt(MainView.roomNumber, i, 1);
					// building = 데이터베이스에서 i번째 열 0번째 행(건물부분)에서 가져오는 값
					MainView.mod.setValueAt(MainView.building, i, 0);
				} else {
					clearTable(i);
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			JOptionPane.showMessageDialog(null, "인원 수를 정확히 입력해주세요!!");
		}

	}

	public void userSetTable() {
		try {
			// 데이터 베이스에서 처리해야할 부분
			for (int i = 0; i < CancelView.dataBaseRow; i++) {

				// date = 데이터베이스에서 i번째 열 3번째 행(예약시간 부분)에서 가져오는 값
				CancelView.mod.setValueAt(CancelView.date, i, 3);
				// maxPeople = 데이터베이스에서 i번째 열 2번째 행(수용가능 인원수 부분)에서 가져오는 값
				CancelView.mod.setValueAt(CancelView.maxPeople, i, 2);
				// roomNumber = 데이터베이스에서 i번째 열 1번째 행(스터디룸 부분)에서 가져오는 값
				CancelView.mod.setValueAt(CancelView.roomNumber, i, 1);
				// building = 데이터베이스에서 i번째 열 0번째 행(건물부분)에서 가져오는 값
				CancelView.mod.setValueAt(CancelView.building, i, 0);

			}

		} catch (Exception e) {
			// TODO: handle exception
			JOptionPane.showMessageDialog(null, "인원 수를 정확히 입력해주세요!!");
		}

	}

	// 테이블에 있는 데이터를 공백으로 초기화
	public void clearTable(int i) {
		MainView.mod.setValueAt(" ", i, 0);
		MainView.mod.setValueAt(" ", i, 1);
		MainView.mod.setValueAt(" ", i, 2);
	}

	public void connectServer(String name, String id) {
		try {
			// 소켓 생성
			socket = new Socket(ip, 8888);
			// logger.log(Level.INFO,"[Client]Server 연결 성공!");

			// 입출력 스트림 생성
			inMsg = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outMsg = new PrintWriter(socket.getOutputStream(), true);

			// 서버에 로그인 메시지 전달
			m = new Message();
			m.setStudentId(id);
			m.setName(name);
			m.setType("login");
			outMsg.println(gson.toJson(m));

			// 메시지 수신을 위한 스레드 생성
			thread = new Thread(this);
			thread.start();
		} catch (Exception e) {
			// logger.log(Level.WARNING ,"[MultichatUI]connectServer() Exception 발생!!");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		String msg;
		status = true;
		ArrayList<Message> msgArray=null;
		
		while (status) {
			try {
				msg = inMsg.readLine();// json, string
				
				//서버에서 Message를 보내는 경우와 ArrayList<Message>를 보내는 경우 구분
				if(msg.charAt(0)=='[') {// ArrayList<Message>
					Type msgListType = new TypeToken<ArrayList<Message>>(){}.getType();
					msgArray = gson.fromJson(msg, msgListType);
					System.out.println("arraylist");
					System.out.println(msg);
				}else {//Message
					m = gson.fromJson(msg, Message.class);// 객체
					System.out.println(m);
				}
				if (m.getType().equals("login")) {
					if (m.getMsg().equals("true")) {
						System.out.println("연결에 성공하였습니다. 비고 : " + m.getMsg());
						// MainView main = new MainView();

						this.mainv = new MainView();
						appMainMain();

						LoginView.frame.dispose();
						
					} else if (m.getMsg().equals("false")) {
						JOptionPane.showMessageDialog(null, "조회된 정보가 없습니다! 다시 정보를 입력해주세요!");
						LoginView.nameField.setText("");
						LoginView.numberField.setText("");
					}
				} else if (m.getType().equals("try")) // 예약 시도
				{
					if (m.getMsg().equals("already on a reservation")) // 이미 예약중이라면
					{
						// 누군가 예약중 입니다라는 팝업창 띄우기
					} else if (m.getMsg().equals("available")) // 예약 가능한 방 이라면
					{
						// 예약뷰 띄우기
					}
				} else if (m.getType().equals("reservation")) // 예약하기
				{
					if (m.getMsg().equals("success")) // 이미 예약중이라면
					{
						// 예약 성공
					} else if (m.getMsg().equals("failed")) // 예약 가능한 방 이라면
					{
						// 예약 실패
					}
				} else if (m.getType().equals("reservation")) // 예약하기
				{
					if (m.getMsg().equals("already on a reservation")) // 이미 예약중이라면
					{
						// 누군가 예약중 입니다라는 팝업창 띄우기
					} else if (m.getMsg().equals("available")) // 예약 가능한 방 이라면
					{
						// 예약뷰 띄우기
					}
				}
				else {// ArrayList<Message>
					System.out.println(msg);
					System.out.println("조회메시지 수신받음");
					for(Message message: msgArray)
						System.out.println(msg);
				}

				

				// chatData.refreshData(m.getId()+">"+m.getMsg()+"\n");
				// v.msgOut.setCaretPosition(v.msgOut.getDocument().getLength());
			} catch (Exception e) {
				// logger.log(Level.WARNING,"[MultiChatUI] 메시지 스트림 종료!!");
				e.printStackTrace();
			}
		}
		
		// logger.info("[MultiChatUI]"+thread.getName()+" 메시지 수신 스레드 종료됨!!");
	}

	public static void main(String[] args) {
		Controller app = new Controller(new LoginView(), null, null, null);
		app.appMainLogin();

	}

}