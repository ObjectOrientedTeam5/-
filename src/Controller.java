
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JOptionPane;
import com.google.gson.Gson;
import Server.Message;

public class Controller implements Runnable {

   ArrayList<Message> msgArray = null;

   static boolean reservationViewChangeCheck;
   static boolean cancelViewChangeCheck;

   private LoginView loginv;
   private MainView mainv;
   private ReservationView reservationv;
   private CancelView cancelv;

   private BufferedReader inMsg = null;
   private PrintWriter outMsg = null;
   private ManagerView managerv;

   

   Gson gson = new Gson();
   Socket socket;
   Message m;
   Thread thread;
   String ip = "127.0.0.1";
   String DATE;
   boolean status;

   ArrayList<String> buildings = new ArrayList<String>();
   ArrayList<String> Roomnums = new ArrayList<String>();
   ArrayList<String> caps = new ArrayList<String>();
   ArrayList<String> projects = new ArrayList<String>();
   ArrayList<String> dates = new ArrayList<String>();
   ArrayList<String> names = new ArrayList<String>();
   ArrayList<String> numbers = new ArrayList<String>();

   Controller(LoginView loginView, MainView mainView, ReservationView reservationv, CancelView cancelView,
         ManagerView managerView) {
      this.loginv = loginView;
      this.mainv = mainView;
      this.reservationv = reservationv;
      this.cancelv = cancelView;
      this.managerv = managerView;
   }

   public void appMainLogin() {

      loginv.addButtonActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Object obj = e.getSource();
            // loginView에서 로그인 버튼을 눌렀을 때
            if (obj == LoginView.loginButton) {
               // 데이터베이스에서 학번 이름을 조회해서 데이터가 존재하는지 처리해야함
               
               // 연결.
               connectServer(LoginView.nameField.getText(), LoginView.numberField.getText()); // 로그인 버튼을 눌렀을때 서버와
               if (LoginView.nameField.getText().equals("admin")) 
               {
                  //loginManager();
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

               if (MainView.peopleField.getText().equals("")) {
                  JOptionPane.showMessageDialog(null, "올바른 값을 입력해주세요!");
               } else {
                  lookup(); // 조회 메시지 전송
               }
            }
            // MainView에서 예약 버튼을 눌렀을 때
            else if (obj == MainView.reservationButton) {
               // 예약 시도 시작 // 테이블에 있는 결과값을 가져옴
               tryResevation();
               // 예약 시도 끝

               // 스터디룸을 선택 안하고 예약했을 경우 예외처리
            }

            // MainView에서 취소버튼을 눌렀을 때
            else if (obj == MainView.cancelButton) {
               // 예약취소 버튼
               LookupReservation();

            } else if (obj == MainView.exitButton) {
               exitwindow();
            }
         }
      });
   }

   public void appMainManager() // 건물,룸명, 이름,학번
   {
      managerv.addButtonActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Object obj = e.getSource();
            if (obj == ManagerView.findButton) {
               managerLookUp();
            }
         }
      });
   }

   public void bookingCancel() {
      System.out.println("bookingCancel 호출됨");
      DATE = MainView.currentYear + "-" + MainView.currentMonth + "-" + MainView.dateComboBox.getSelectedItem() + " "
            + MainView.hourComboBox.getSelectedItem() + ":00:00";
      int getRow = MainView.table.getSelectedRow();
      m = new Message();
      m.setType("bookingCancel");
      m.setDate(DATE);
      m.setRoomNum((String) MainView.table.getValueAt(getRow, 1));
      outMsg.println(gson.toJson(m));
   }

   public void appMainReservation() {
      reservationv.addButtonActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Object obj = e.getSource();
            if (obj == ReservationView.backButton) {
               // 조회뷰로 전환
               bookingCancel();
               ReservationView.frame.setVisible(false);
               MainView.frame.setVisible(true);

            } else if (obj == ReservationView.reservationButton) {
               // 데이터베이스로 값들 전송
               Resevation();

            } else if (obj == MainView.exitButton) {
               exitwindow();
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
            if (obj == CancelView.backButton) {
               bookingCancel();
               CancelView.frame.setVisible(false);
               MainView.frame.setVisible(true);
            }
         }
      });
   }

   public void loginManager() // 관리자모드로 로그인 한다는 메시지 전송
   {
      m = new Message();
      m.setType("managerlogin");
      outMsg.println(gson.toJson(m));
   }
  
   public void managerLookUp()   //관리자 조회 메시지 전송
   {
      m = new Message();
      m.setType("managerLookUp");
      outMsg.println(gson.toJson(m));
      System.out.println(gson.toJson(m));
   }

   public void lookup() // 조회 메시지 전송
   {
      boolean projectcheck = MainView.projectCheckBox.isSelected();
      String isproject;
      if (projectcheck)
         isproject = "1";
      else
         isproject = "0";

      DATE = "2020-01-" + MainView.dateComboBox.getSelectedItem() + " " + MainView.hourComboBox.getSelectedItem()
            + ":00:00";

      m = new Message();
      m.setDate(DATE);
      m.setEquipment(isproject);
      m.setCapacity(MainView.peopleField.getText());
      m.setType("lookUp");
      outMsg.println(gson.toJson(m));
   }

   public void clearManagerTable(int i) {
      ManagerView.mod.setValueAt("", i, 0);
      ManagerView.mod.setValueAt("", i, 1);
      ManagerView.mod.setValueAt("", i, 2);
      ManagerView.mod.setValueAt("", i, 3);
   }

   public void tryResevation() // 예약 시도 메시지 전송
   {
      m = new Message();
      System.out.println("예약시도");

      DATE = MainView.currentYear + "-" + MainView.currentMonth + "-" + MainView.dateComboBox.getSelectedItem() + " "
            + MainView.hourComboBox.getSelectedItem() + ":00:00";
      // DATE : 2020-01-일 시:00:00

      m = new Message();

      int getRow = MainView.table.getSelectedRow();
      m.setDate(DATE);
      m.setBuilding((String) MainView.table.getValueAt(getRow, 0));
      m.setRoomNum((String) MainView.table.getValueAt(getRow, 1));
      // m.setCapacity(ReservationView.reservationPeople);
      // m.setEquipment(ReservationView.reservationProjecter);
      m.setType("try");
      outMsg.println(gson.toJson(m));
   }

   public void Resevation() { // 최종 예약 확인 메시지 전송
      String project;

      if (ReservationView.reservationProjecter.equals("O"))
         project = "1";
      else
         project = "0";

      String DATE = MainView.currentYear + "-" + MainView.currentMonth + "-" + MainView.dateComboBox.getSelectedItem()
            + " " + MainView.hourComboBox.getSelectedItem() + ":00:00";
      System.out.println();
      m = new Message();
      m.setName(LoginView.nameField.getText());
      m.setStudentId(LoginView.numberField.getText());

      m.setRoomNum(MainView.roomNumber);
      m.setBuilding(MainView.building);
      m.setCapacity(ReservationView.reservationPeople);
      m.setEquipment(project);
      m.setType("reservation");
      m.setDate(DATE);
      outMsg.println(gson.toJson(m));
   }

   public void LookupReservation() { // 예약취소 버튼 눌렀을때 예약된 룸을 보기위해 메시지 전송
      m = new Message();
      m.setStudentId(LoginView.numberField.getText());
      m.setName(LoginView.nameField.getText());
      m.setType("check");
      outMsg.println(gson.toJson(m));
      System.out.println("예약확인:" + gson.toJson(m));
   }

   public void CancelReservation() { // 최종 예약 취소 메시지 전송///빌딩,룸넘버,학번,이름,날짜
      int getRow = CancelView.table.getSelectedRow();

      m = new Message();
      m.setBuilding((String) CancelView.table.getValueAt(getRow, 0));
      m.setRoomNum((String) CancelView.table.getValueAt(getRow, 1));
      m.setStudentId(LoginView.numberField.getText());
      m.setName(LoginView.nameField.getText());
      m.setDate((String) CancelView.table.getValueAt(getRow, 3));
      m.setType("cancel");
      outMsg.println(gson.toJson(m));
   }

   public void exitwindow() {
      m = new Message();
      m.setType("exit");
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
         for (int i = 0; i < 50; i++) {
            clearTable(i);
            System.out.println("building : " + MainView.table.getValueAt(i, 0));
            System.out.println("room : " + MainView.table.getValueAt(i, 0));
            System.out.println("capacity : " + MainView.table.getValueAt(i, 0));
         }

         // 데이터 베이스에서 처리해야할 부분
         for (int i = 0; i < buildings.size(); i++) {
            // 수용가능 인원 수 조회 (값 비교)
            // maxPeople = 데이터베이스에서 i번째 열 2번째 행(수용가능 인원수 부분)에서 가져오는 값

            // TextField에 값과 데이터베이스에서 가져온 인원수 값인 maxPeople를 비교해서 TextField에 있는 값보다 같거나 큰 인원
            // 수에 해당하는 정보들을 가져옴
            if (Integer.parseInt(MainView.peopleField.getText()) <= Integer.parseInt(caps.get(i))) {
               MainView.mod.setValueAt(buildings.get(i), i, 0);
               MainView.mod.setValueAt(Roomnums.get(i), i, 1);
               MainView.mod.setValueAt(caps.get(i), i, 2);
            }
         }

      } catch (Exception e) {
         // TODO: handle exception

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
         e.printStackTrace();
         JOptionPane.showMessageDialog(null, "인원 수를 정확히 입력해주세요!!");
      }

   }

   // 테이블에 있는 데이터를 공백으로 초기화
   public void clearTable(int i) {
      MainView.mod.setValueAt("", i, 0);
      MainView.mod.setValueAt("", i, 1);
      MainView.mod.setValueAt("", i, 2);
   }

   public void clearcancelTable(int i) {
      CancelView.mod.setValueAt("", i, 0);
      CancelView.mod.setValueAt("", i, 1);
      CancelView.mod.setValueAt("", i, 2);
      CancelView.mod.setValueAt("", i, 3);
   }

   public void printResevationTable() // 건물명, 스터디룸명, 인원수, 예약시간 출력
   {
      try {
         for (int i = 0; i < 50; i++) {
            clearcancelTable(i);
         }

         // 데이터 베이스에서 처리해야할 부분
         for (int i = 0; i < buildings.size(); i++) {
            // 수용가능 인원 수 조회 (값 비교)
            // maxPeople = 데이터베이스에서 i번째 열 2번째 행(수용가능 인원수 부분)에서 가져오는 값

            // TextField에 값과 데이터베이스에서 가져온 인원수 값인 maxPeople를 비교해서 TextField에 있는 값보다 같거나 큰 인원
            // 수에 해당하는 정보들을 가져옴

            CancelView.mod.setValueAt(buildings.get(i), i, 0);
            CancelView.mod.setValueAt(Roomnums.get(i), i, 1);
            CancelView.mod.setValueAt(caps.get(i), i, 2);
            CancelView.mod.setValueAt(dates.get(i), i, 3);
         }

      } catch (Exception e) {
         // TODO: handle exception
         e.printStackTrace();
         JOptionPane.showMessageDialog(null, "인원 수를 정확히 입력해주세요!!");
      }
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
         e.printStackTrace();
      }
   }

   public void run() {
      String msg;
      status = true;
      while (status) {
         try {
            msg = inMsg.readLine();// json, string
            System.out.println(msg);
            // 서버에서 Message를 보내는 경우와 ArrayList<Message>를 보내는 경우 구분
            if (msg.charAt(0) == '[') {// ArrayList<Message>

               Type msgListType = new TypeToken<ArrayList<Message>>() {
               }.getType();
               msgArray = gson.fromJson(msg, msgListType);

               if (msgArray.get(0).getType().equals("lookUp")) // 조회
               {
                  setJSONData();
                  setTable();
                  arrayClear();
               }

               else if (msgArray.get(0).getType().equals("check")) // 예약 취소버튼 눌렀을때, 예약취소뷰 출력, 예약한 룸 정보 리스트에 출력
               {
                  System.out.println("컨트롤러에서 받습니다:" + msg);
                  setJSONcancelData();
                  printResevationTable();
                  arrayClear();
                  // 취소뷰로 전환된적 있는지 체크
                  if (cancelViewChangeCheck) {
                     changeCancelView();
                  } else {
                     changeNewCancelView();
                  }
                  // JTable 출력
               } else if (msgArray.get(0).getType().equals("managerLookUp")) {
                  setJSONManagerData();
                  printManagerTable();
                  arrayClear();
               }
            } else {// Message
               m = gson.fromJson(msg, Message.class);// 객체
               System.out.println(m);

               if (m.getType().equals("login")) {
                  if (m.getMsg().equals("true")) {
                     System.out.println("연결에 성공하였습니다. 비고 : " + m.getMsg());
                     changeMainView();
                  } else if (m.getMsg().equals("false")) {
                     JOptionPane.showMessageDialog(null, "조회된 정보가 없습니다! 다시 정보를 입력해주세요!");
                     LoginView.nameField.setText("");
                     LoginView.numberField.setText("");
                  }
               }

               else if (m.getType().equals("managerlogin")) // 관리자 모드 로그인
               {
            	   if(m.getMsg().equals("true"))
            	   {
            		   managerv = new ManagerView();
            		   appMainManager();
            		   LoginView.frame.dispose();
            	   }
            	   else
            	   {
            		   System.out.println("로그인 실패");
            	   }
               }

               else if (m.getType().equals("try")) // 예약 시도
               {
                  if (m.getMsg().equals("already on a reservation")) // 이미 예약중이라면
                  {
                     // 누군가 예약중 입니다라는 팝업창 띄우기
                     JOptionPane.showMessageDialog(null, "누군가 이미 예약중입니다. 잠시후 다시 시도해 주세요");
                  } else if (m.getMsg().equals("available")) // 예약 가능한 방 이라면
                  {
                     try {
                        // 예약뷰 띄우기
                        getTableResult();

                        // 테이블에서 빈칸을 선택할 시
                        if (MainView.building == null) {
                           JOptionPane.showMessageDialog(null, "올바른 값을 선택해 주십시오!");
                        } else {
                           // 유저가 예약하려는 정보를 저장
                           setUserReservationData();
                           // 예약뷰로 전환된 적이 있는지 체크
                           if (reservationViewChangeCheck) {
                              changeReservationView();
                           } else {
                              changeNewReservationView();
                           }
                        }
                     }

                     catch (ArrayIndexOutOfBoundsException exception) {
                        // TODO: handle exception
                        JOptionPane.showMessageDialog(null, "예약하실 스터디룸을 선택해주세요!!");
                     }
                  }
               } else if (m.getType().equals("reservation")) // 예약하기
               {
                  if (m.getMsg().equals("success")) // 예약에 성공
                  {
                     // 예약 성공 팝업창 띄우기
                     JOptionPane.showMessageDialog(null, "예약에 성공하였습니다.");
                  } else if (m.getMsg().equals("failed")) // 예약 가능한 방 이라면
                  {
                     // 예약 실패
                     JOptionPane.showMessageDialog(null, "예약에 실패 하였습니다. 잠시후 다시 시도해 주세요");
                  }
               } else if (m.getType().equals("cancel")) // 예약 취소
               {
                  if (m.getMsg().equals("success")) {
                     JOptionPane.showMessageDialog(null, "예약 취소에 성공하였습니다.");
                     LookupReservation();
                  } else if (m.getMsg().equals("failed")) {
                     JOptionPane.showMessageDialog(null, "예약 취소에 실패 하였습니다.");
                  }
               }
            }
         } catch (Exception e) {
            clearTable(0);
         }
      }
   }

   public void setJSONData() {
      // 들어온 메시지 JSONarr를 리스트에 출력
      for (int i = 0; i < msgArray.size(); i++) {
         buildings.add(msgArray.get(i).getBuilding());
         Roomnums.add(msgArray.get(i).getRoomNum());
         caps.add(msgArray.get(i).getCapacity());
         projects.add(msgArray.get(i).getEquipment());
      }
   }

   public void setJSONcancelData() {
      for (int i = 0; i < msgArray.size(); i++) {
         buildings.add(msgArray.get(i).getBuilding());
         Roomnums.add(msgArray.get(i).getRoomNum());
         caps.add(msgArray.get(i).getCapacity());
         projects.add(msgArray.get(i).getEquipment());
         dates.add(msgArray.get(i).getDate());
      }
   }

   public void setJSONManagerData() {// 건물,룸명, 이름,학번
      for (int i = 0; i < msgArray.size(); i++) {
         buildings.add(msgArray.get(i).getBuilding());
         Roomnums.add(msgArray.get(i).getRoomNum());
         names.add(msgArray.get(i).getName());
         numbers.add(msgArray.get(i).getStudentId());
      }
   }

   public void arrayClear() {
      buildings.clear();
      Roomnums.clear();
      caps.clear();
      projects.clear();
      dates.clear();
      names.clear();
      numbers.clear();
   }

   public void printManagerTable() {
      try {
         for (int i = 0; i < managerv.table.getRowCount(); i++) {
            clearManagerTable(i);
         }
         for (int i = 0; i < buildings.size(); i++) // 건물,룸명, 이름,학번
         {
            ManagerView.mod.setValueAt(buildings.get(i), i, 0);
            ManagerView.mod.setValueAt(Roomnums.get(i), i, 1);
            ManagerView.mod.setValueAt(names.get(i), i, 2);
            ManagerView.mod.setValueAt(numbers.get(i), i, 3);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void changeCancelView() {
      // 전환된 적이 있을 시 잠시 사라지게한 CancelView를 불러옴
      CancelView.frame.setVisible(true);
      CancelView.frame.invalidate();
      CancelView.frame.validate();
      CancelView.frame.repaint();
      MainView.frame.setVisible(false);
   }

   public void changeNewCancelView() {
      // 전환된 적이 없다면 CancelView 객체를 새로 만들어 화면전환
      cancelv = new CancelView();
      appMainCancel();
      // MainView는 잠시 사라지게 함
      MainView.frame.setVisible(false);
      cancelViewChangeCheck = true;
   }

   public void changeMainView() {

      this.mainv = new MainView();
      appMainMain();

      LoginView.frame.dispose();
   }

   public void changeReservationView() {
      // 전환된 적이 있을 시 잠시 사라지게한 ReservationView를 불러옴
      ReservationView.frame.setVisible(true);
      ReservationView.textAreaSetting();
      ReservationView.frame.invalidate();
      ReservationView.frame.validate();
      ReservationView.frame.repaint();
      MainView.frame.setVisible(false);

   }

   public void changecancelToReservationView() {
      ReservationView.frame.setVisible(true);
      ReservationView.textAreaSetting();
      ReservationView.frame.invalidate();
      ReservationView.frame.validate();
      ReservationView.frame.repaint();
      CancelView.frame.setVisible(false);
   }

   public void changeNewReservationView() {
      // 전환된 적이 없으면 ReservationView 객체를 새로 만들어서 화면전환
      reservationv = new ReservationView();
      appMainReservation();

      // MainView는 잠시 사라지게 함
      MainView.frame.setVisible(false);
      reservationViewChangeCheck = true;
   }

   public void changeNewcancelToResevationView() {
      reservationv = new ReservationView();
      appMainReservation();

      // MainView는 잠시 사라지게 함
      CancelView.frame.setVisible(false);
      reservationViewChangeCheck = true;
   }

   public static void main(String[] args) {
      Controller app = new Controller(new LoginView(), null, null, null, null);
      app.appMainLogin();
   }
}