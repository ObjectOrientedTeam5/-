
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
            // loginView���� �α��� ��ư�� ������ ��
            if (obj == LoginView.loginButton) {
               // �����ͺ��̽����� �й� �̸��� ��ȸ�ؼ� �����Ͱ� �����ϴ��� ó���ؾ���
               
               // ����.
               connectServer(LoginView.nameField.getText(), LoginView.numberField.getText()); // �α��� ��ư�� �������� ������
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
               // �����ͺ��̽����� ���� �о��
               System.out.println("��ȸ");

               if (MainView.peopleField.getText().equals("")) {
                  JOptionPane.showMessageDialog(null, "�ùٸ� ���� �Է����ּ���!");
               } else {
                  lookup(); // ��ȸ �޽��� ����
               }
            }
            // MainView���� ���� ��ư�� ������ ��
            else if (obj == MainView.reservationButton) {
               // ���� �õ� ���� // ���̺� �ִ� ������� ������
               tryResevation();
               // ���� �õ� ��

               // ���͵���� ���� ���ϰ� �������� ��� ����ó��
            }

            // MainView���� ��ҹ�ư�� ������ ��
            else if (obj == MainView.cancelButton) {
               // ������� ��ư
               LookupReservation();

            } else if (obj == MainView.exitButton) {
               exitwindow();
            }
         }
      });
   }

   public void appMainManager() // �ǹ�,���, �̸�,�й�
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
      System.out.println("bookingCancel ȣ���");
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
               // ��ȸ��� ��ȯ
               bookingCancel();
               ReservationView.frame.setVisible(false);
               MainView.frame.setVisible(true);

            } else if (obj == ReservationView.reservationButton) {
               // �����ͺ��̽��� ���� ����
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
               // �����ͺ��̽��� ����� ���� ����
               CancelReservation();// ���� ���

            }
            if (obj == CancelView.backButton) {
               bookingCancel();
               CancelView.frame.setVisible(false);
               MainView.frame.setVisible(true);
            }
         }
      });
   }

   public void loginManager() // �����ڸ��� �α��� �Ѵٴ� �޽��� ����
   {
      m = new Message();
      m.setType("managerlogin");
      outMsg.println(gson.toJson(m));
   }
  
   public void managerLookUp()   //������ ��ȸ �޽��� ����
   {
      m = new Message();
      m.setType("managerLookUp");
      outMsg.println(gson.toJson(m));
      System.out.println(gson.toJson(m));
   }

   public void lookup() // ��ȸ �޽��� ����
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

   public void tryResevation() // ���� �õ� �޽��� ����
   {
      m = new Message();
      System.out.println("����õ�");

      DATE = MainView.currentYear + "-" + MainView.currentMonth + "-" + MainView.dateComboBox.getSelectedItem() + " "
            + MainView.hourComboBox.getSelectedItem() + ":00:00";
      // DATE : 2020-01-�� ��:00:00

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

   public void Resevation() { // ���� ���� Ȯ�� �޽��� ����
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

   public void LookupReservation() { // ������� ��ư �������� ����� ���� �������� �޽��� ����
      m = new Message();
      m.setStudentId(LoginView.numberField.getText());
      m.setName(LoginView.nameField.getText());
      m.setType("check");
      outMsg.println(gson.toJson(m));
      System.out.println("����Ȯ��:" + gson.toJson(m));
   }

   public void CancelReservation() { // ���� ���� ��� �޽��� ����///����,��ѹ�,�й�,�̸�,��¥
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

   // ���������� ����View���� �����ͷ� ����
   public void setUserReservationData() {

      ReservationView.reservationDate = MainView.dateComboBox.getSelectedItem() + "�� "
            + MainView.hourComboBox.getSelectedItem() + "��";
      ReservationView.reservationRoom = MainView.building + " " + MainView.roomNumber;
      ReservationView.reservationPeople = MainView.maxPeople;
      if (MainView.projectCheckBox.isSelected()) {
         ReservationView.reservationProjecter = "O";
      } else {
         ReservationView.reservationProjecter = "X";
      }
   }

   // JTable�� �ԷµǾ��ִ� �������� ������ ����
   public void getTableResult() {
      int getRow = MainView.table.getSelectedRow();
      MainView.building = (String) MainView.table.getValueAt(getRow, 0);
      MainView.roomNumber = (String) MainView.table.getValueAt(getRow, 1);
      MainView.maxPeople = (String) MainView.table.getValueAt(getRow, 2);
   }

   // ���̺� �����͸� ����
   public void setTable() {
      try {
         for (int i = 0; i < 50; i++) {
            clearTable(i);
            System.out.println("building : " + MainView.table.getValueAt(i, 0));
            System.out.println("room : " + MainView.table.getValueAt(i, 0));
            System.out.println("capacity : " + MainView.table.getValueAt(i, 0));
         }

         // ������ ���̽����� ó���ؾ��� �κ�
         for (int i = 0; i < buildings.size(); i++) {
            // ���밡�� �ο� �� ��ȸ (�� ��)
            // maxPeople = �����ͺ��̽����� i��° �� 2��° ��(���밡�� �ο��� �κ�)���� �������� ��

            // TextField�� ���� �����ͺ��̽����� ������ �ο��� ���� maxPeople�� ���ؼ� TextField�� �ִ� ������ ���ų� ū �ο�
            // ���� �ش��ϴ� �������� ������
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
         // ������ ���̽����� ó���ؾ��� �κ�
         for (int i = 0; i < CancelView.dataBaseRow; i++) {

            // date = �����ͺ��̽����� i��° �� 3��° ��(����ð� �κ�)���� �������� ��
            CancelView.mod.setValueAt(CancelView.date, i, 3);
            // maxPeople = �����ͺ��̽����� i��° �� 2��° ��(���밡�� �ο��� �κ�)���� �������� ��
            CancelView.mod.setValueAt(CancelView.maxPeople, i, 2);
            // roomNumber = �����ͺ��̽����� i��° �� 1��° ��(���͵�� �κ�)���� �������� ��
            CancelView.mod.setValueAt(CancelView.roomNumber, i, 1);
            // building = �����ͺ��̽����� i��° �� 0��° ��(�ǹ��κ�)���� �������� ��
            CancelView.mod.setValueAt(CancelView.building, i, 0);

         }

      } catch (Exception e) {
         // TODO: handle exception
         e.printStackTrace();
         JOptionPane.showMessageDialog(null, "�ο� ���� ��Ȯ�� �Է����ּ���!!");
      }

   }

   // ���̺� �ִ� �����͸� �������� �ʱ�ȭ
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

   public void printResevationTable() // �ǹ���, ���͵���, �ο���, ����ð� ���
   {
      try {
         for (int i = 0; i < 50; i++) {
            clearcancelTable(i);
         }

         // ������ ���̽����� ó���ؾ��� �κ�
         for (int i = 0; i < buildings.size(); i++) {
            // ���밡�� �ο� �� ��ȸ (�� ��)
            // maxPeople = �����ͺ��̽����� i��° �� 2��° ��(���밡�� �ο��� �κ�)���� �������� ��

            // TextField�� ���� �����ͺ��̽����� ������ �ο��� ���� maxPeople�� ���ؼ� TextField�� �ִ� ������ ���ų� ū �ο�
            // ���� �ش��ϴ� �������� ������

            CancelView.mod.setValueAt(buildings.get(i), i, 0);
            CancelView.mod.setValueAt(Roomnums.get(i), i, 1);
            CancelView.mod.setValueAt(caps.get(i), i, 2);
            CancelView.mod.setValueAt(dates.get(i), i, 3);
         }

      } catch (Exception e) {
         // TODO: handle exception
         e.printStackTrace();
         JOptionPane.showMessageDialog(null, "�ο� ���� ��Ȯ�� �Է����ּ���!!");
      }
   }

   public void connectServer(String name, String id) {
      try {
         // ���� ����
         socket = new Socket(ip, 8888);
         // logger.log(Level.INFO,"[Client]Server ���� ����!");

         // ����� ��Ʈ�� ����
         inMsg = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         outMsg = new PrintWriter(socket.getOutputStream(), true);

         // ������ �α��� �޽��� ����
         m = new Message();
         m.setStudentId(id);
         m.setName(name);
         m.setType("login");
         outMsg.println(gson.toJson(m));

         // �޽��� ������ ���� ������ ����
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
            // �������� Message�� ������ ���� ArrayList<Message>�� ������ ��� ����
            if (msg.charAt(0) == '[') {// ArrayList<Message>

               Type msgListType = new TypeToken<ArrayList<Message>>() {
               }.getType();
               msgArray = gson.fromJson(msg, msgListType);

               if (msgArray.get(0).getType().equals("lookUp")) // ��ȸ
               {
                  setJSONData();
                  setTable();
                  arrayClear();
               }

               else if (msgArray.get(0).getType().equals("check")) // ���� ��ҹ�ư ��������, ������Һ� ���, ������ �� ���� ����Ʈ�� ���
               {
                  System.out.println("��Ʈ�ѷ����� �޽��ϴ�:" + msg);
                  setJSONcancelData();
                  printResevationTable();
                  arrayClear();
                  // ��Һ�� ��ȯ���� �ִ��� üũ
                  if (cancelViewChangeCheck) {
                     changeCancelView();
                  } else {
                     changeNewCancelView();
                  }
                  // JTable ���
               } else if (msgArray.get(0).getType().equals("managerLookUp")) {
                  setJSONManagerData();
                  printManagerTable();
                  arrayClear();
               }
            } else {// Message
               m = gson.fromJson(msg, Message.class);// ��ü
               System.out.println(m);

               if (m.getType().equals("login")) {
                  if (m.getMsg().equals("true")) {
                     System.out.println("���ῡ �����Ͽ����ϴ�. ��� : " + m.getMsg());
                     changeMainView();
                  } else if (m.getMsg().equals("false")) {
                     JOptionPane.showMessageDialog(null, "��ȸ�� ������ �����ϴ�! �ٽ� ������ �Է����ּ���!");
                     LoginView.nameField.setText("");
                     LoginView.numberField.setText("");
                  }
               }

               else if (m.getType().equals("managerlogin")) // ������ ��� �α���
               {
            	   if(m.getMsg().equals("true"))
            	   {
            		   managerv = new ManagerView();
            		   appMainManager();
            		   LoginView.frame.dispose();
            	   }
            	   else
            	   {
            		   System.out.println("�α��� ����");
            	   }
               }

               else if (m.getType().equals("try")) // ���� �õ�
               {
                  if (m.getMsg().equals("already on a reservation")) // �̹� �������̶��
                  {
                     // ������ ������ �Դϴٶ�� �˾�â ����
                     JOptionPane.showMessageDialog(null, "������ �̹� �������Դϴ�. ����� �ٽ� �õ��� �ּ���");
                  } else if (m.getMsg().equals("available")) // ���� ������ �� �̶��
                  {
                     try {
                        // ����� ����
                        getTableResult();

                        // ���̺��� ��ĭ�� ������ ��
                        if (MainView.building == null) {
                           JOptionPane.showMessageDialog(null, "�ùٸ� ���� ������ �ֽʽÿ�!");
                        } else {
                           // ������ �����Ϸ��� ������ ����
                           setUserReservationData();
                           // ������ ��ȯ�� ���� �ִ��� üũ
                           if (reservationViewChangeCheck) {
                              changeReservationView();
                           } else {
                              changeNewReservationView();
                           }
                        }
                     }

                     catch (ArrayIndexOutOfBoundsException exception) {
                        // TODO: handle exception
                        JOptionPane.showMessageDialog(null, "�����Ͻ� ���͵���� �������ּ���!!");
                     }
                  }
               } else if (m.getType().equals("reservation")) // �����ϱ�
               {
                  if (m.getMsg().equals("success")) // ���࿡ ����
                  {
                     // ���� ���� �˾�â ����
                     JOptionPane.showMessageDialog(null, "���࿡ �����Ͽ����ϴ�.");
                  } else if (m.getMsg().equals("failed")) // ���� ������ �� �̶��
                  {
                     // ���� ����
                     JOptionPane.showMessageDialog(null, "���࿡ ���� �Ͽ����ϴ�. ����� �ٽ� �õ��� �ּ���");
                  }
               } else if (m.getType().equals("cancel")) // ���� ���
               {
                  if (m.getMsg().equals("success")) {
                     JOptionPane.showMessageDialog(null, "���� ��ҿ� �����Ͽ����ϴ�.");
                     LookupReservation();
                  } else if (m.getMsg().equals("failed")) {
                     JOptionPane.showMessageDialog(null, "���� ��ҿ� ���� �Ͽ����ϴ�.");
                  }
               }
            }
         } catch (Exception e) {
            clearTable(0);
         }
      }
   }

   public void setJSONData() {
      // ���� �޽��� JSONarr�� ����Ʈ�� ���
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

   public void setJSONManagerData() {// �ǹ�,���, �̸�,�й�
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
         for (int i = 0; i < buildings.size(); i++) // �ǹ�,���, �̸�,�й�
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
      // ��ȯ�� ���� ���� �� ��� ��������� CancelView�� �ҷ���
      CancelView.frame.setVisible(true);
      CancelView.frame.invalidate();
      CancelView.frame.validate();
      CancelView.frame.repaint();
      MainView.frame.setVisible(false);
   }

   public void changeNewCancelView() {
      // ��ȯ�� ���� ���ٸ� CancelView ��ü�� ���� ����� ȭ����ȯ
      cancelv = new CancelView();
      appMainCancel();
      // MainView�� ��� ������� ��
      MainView.frame.setVisible(false);
      cancelViewChangeCheck = true;
   }

   public void changeMainView() {

      this.mainv = new MainView();
      appMainMain();

      LoginView.frame.dispose();
   }

   public void changeReservationView() {
      // ��ȯ�� ���� ���� �� ��� ��������� ReservationView�� �ҷ���
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
      // ��ȯ�� ���� ������ ReservationView ��ü�� ���� ���� ȭ����ȯ
      reservationv = new ReservationView();
      appMainReservation();

      // MainView�� ��� ������� ��
      MainView.frame.setVisible(false);
      reservationViewChangeCheck = true;
   }

   public void changeNewcancelToResevationView() {
      reservationv = new ReservationView();
      appMainReservation();

      // MainView�� ��� ������� ��
      CancelView.frame.setVisible(false);
      reservationViewChangeCheck = true;
   }

   public static void main(String[] args) {
      Controller app = new Controller(new LoginView(), null, null, null, null);
      app.appMainLogin();
   }
}