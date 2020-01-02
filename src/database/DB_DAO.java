package database;

import java.sql.*;
import java.util.ArrayList;

import Server.Message;


public class DB_DAO{
    //String jdbcDriver = "com.mysql.jdbc.Driver";
    String jdbcUrl = "jdbc:mysql://localhost/javadb?serverTimezone=Asia/Seoul&useSSL=false";//jdbc:mysql://127.0.0.1/javadb?serverTimezone=UTC"
    Connection conn;

    PreparedStatement pstmt;
    ResultSet resultSet;

    //mData�� ���� ����� ��� ����� �����ϴ� ArrayList, getAll�޼ҵ忡 ���� ����
    ArrayList<BookedDTO> mData = new ArrayList<>();
    //bookedListByClient�� �ش� ����ڰ� ������ ����� �����ϴ� ArrayList, getBookedListByClient�޼ҵ忡 ���� ����
    ArrayList<BookedDTO> bookedListByClient = new ArrayList<>();
    //bookAvailableList�� ���� ������ ����� �����ϴ� ArrayList, getBookAvailableList�޼ҵ忡 ���� ����
    ArrayList<BookAvailableDTO> bookAvailableList = new ArrayList<>();


    String sql;

    //���� ����� ������ ���� List�� �ʱ�ȭ��Ű�� �޼ҵ�
    void clearmData(){ mData.clear();}
    void clearBookedListByClient(){bookedListByClient.clear();}
    void clearBookAvailableList(){bookAvailableList.clear();}
    
  //���� �ð��� ����� �ð��� ���Ͽ� 2�ð��� �Ѿ��ٸ� ���� ��Ͽ��� �����Ѵ�.
    //db�����Ҷ����� ����ǹǷ� ���� �������ʿ����.
    public void clearBookedList(){
        String sql = "delete from bookinglist where date_add(date, interval 1 hour) < now()";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
            //System.out.println("clearBookedList() ���� �Ϸ�");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    
    //�Ķ���ͷ� studentID, studentName�� ������ �ش� �л��� ������ ����� ��ȯ�Ѵ�.
    //����� bookedListByClient ����Ʈ�� ����ȴ�.
    public ArrayList<BookedDTO> getBookedByClient(String studentID, String studentName){
        connectDB();
        String sql = "select * from bookinglist where studentName =  ? and studentID = ?";
        try{
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, studentName);
            pstmt.setString(2, studentID);

            resultSet = pstmt.executeQuery();
            clearBookedListByClient();
            BookedDTO tmp;
            while(resultSet.next()){
                tmp=new BookedDTO();
                tmp.setBuilding(resultSet.getString("building"));
                tmp.setStudentName(resultSet.getString("studentName"));
                tmp.setStudentID(resultSet.getString("studentID"));
                tmp.setIsProject(resultSet.getInt("isProject"));
                tmp.setRoomNumber(resultSet.getString("roomNumber"));
                tmp.setMaxPeople(resultSet.getInt("maxPeople"));
                tmp.setDate(resultSet.getString("date"));

                bookedListByClient.add(tmp);
            }
            System.out.println();
            System.out.println("## studentID : "+studentID+" studentName : "+studentName +" ���� ������ ���� ##");
            System.out.println(bookedListByClient);
            System.out.println();
            //System.out.println("bookedListByClient ���� �Ϸ�");
            closeDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
		return bookedListByClient;
    }
    
    //���� ����� ��� ����� �����ش�.
    //mData ����Ʈ�� ����ȴ�.
    //�� �޼ҵ�� �����ڿ�
    public ArrayList<BookedDTO> getAll(){
        connectDB();
        sql = "select * from bookinglist";

        try{
            pstmt = conn.prepareStatement(sql);
            resultSet = pstmt.executeQuery();
            clearmData();
            BookedDTO tmp;
            while(resultSet.next()) {
                tmp = new BookedDTO();
                tmp.setBuilding(resultSet.getString("building"));
                tmp.setRoomNumber(resultSet.getString("roomNumber"));
                tmp.setIsProject(resultSet.getInt("isProject"));
                tmp.setStudentID(resultSet.getString("studentID"));
                tmp.setStudentName(resultSet.getString("studentName"));
                tmp.setMaxPeople(resultSet.getInt("maxPeople"));
                tmp.setDate(resultSet.getString("date"));
                mData.add(tmp);
            }
            System.out.println("## ��� ���� ���� ##");
            System.out.println(mData);
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return mData;
    }

    //�ð��� �ݵ�� ��-��-�� �ð�:��:�� �������� ��Ʈ������ ���� ex) "2019-12-12 15:22:39"
    //������ ��¥�׽ð�, maxPeople, isProject�� �Ķ���ͷ� ����
    //������ ��¥�׽ð�, maxPeople, isProject�� ���డ���� ��� ��ȯ
    //�����ʹ� bookAvailableList ����Ʈ�� ����ȴ�.
    public ArrayList<BookAvailableDTO> getBookAvailableList(String date, int maxPeople, int isProject){
/*
select  B.building ,B.roomNumber, B.isProject, B.maxPeople
from bookinglist A right join roomlist B on A.roomNumber = B.roomNumber and
A.date = date_format('2019-12-27 14:00:00', '%Y-%m-%d %H:%i:%s')
where A.roomNumber is null
and B.isProject = ? and B.maxPeople = ?
 */

        connectDB();
        String sql = "select  B.building ,B.roomNumber, B.isProject, B.maxPeople\n" +
                "from bookinglist A right join roomlist B on A.roomNumber = B.roomNumber and\n" +
                "A.date = date_format(?, '%Y-%m-%d %H:%i:%s')\n" +
                "where A.roomNumber is null\n" +
                "and B.isProject = ? and B.maxPeople >= ?";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, date);
            pstmt.setInt(2, isProject);
            pstmt.setInt(3, maxPeople);

            resultSet = pstmt.executeQuery();
            clearBookAvailableList();
            BookAvailableDTO tmp;
            while(resultSet.next()){
                tmp = new BookAvailableDTO();
                tmp.setBuilding(resultSet.getString("building"));
                tmp.setRoomNumber(resultSet.getString("roomNumber"));
                tmp.setIsProject(resultSet.getInt("isProject"));
                tmp.setMaxPeople(resultSet.getInt("maxPeople"));
                bookAvailableList.add(tmp);
            }
            System.out.println();
            System.out.println("## "+date+"�� ���డ���� ��� isProject = "+isProject+" maxPeople = "+maxPeople+" ##");
            System.out.println(bookAvailableList);
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookAvailableList;
    }
    
    //BookedDTOŬ������ ������ �����ѵ� �Ķ���ͷ� �Ѱ��ָ� �ش� ������ ������
    //������ �Ǿ����� true �ƴϸ� false��ȯ
    public boolean book(Message msg){
        connectDB();
        sql = "select * from bookinglist where building = ? and roomNumber = ? and date = ?";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, msg.getBuilding());
            pstmt.setString(2, msg.getRoomNum());
            pstmt.setString(3, msg.getDate());

            resultSet = pstmt.executeQuery();
            if(resultSet.next()) {
            	System.out.println("�ߺ��� �����Ͱ� �־ ���࿡ �����߽��ϴ�.");
            	return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("���� ����");
        }
        
        sql = "insert into bookinglist(building, roomNumber, studentID, studentName, isProject, maxPeople, date) values(?,?,?,?,?,?,?)";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, msg.getBuilding());
            pstmt.setString(2, msg.getRoomNum());
            pstmt.setString(3, msg.getStudentId());
            pstmt.setString(4, msg.getName());
            pstmt.setInt(5, Integer.parseInt(msg.getEquipment()));
            pstmt.setInt(6, Integer.parseInt(msg.getCapacity()));
            pstmt.setString(7, msg.getDate());

            pstmt.executeUpdate();
            System.out.println(msg+"���࿡ �����߽��ϴ�.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("���� ����");
            return false;
        }
    }
    //������ ����ϴ� �޼���
    //building, roomnumber, studentid, studentname�� ��� ��ġ�ϸ� ������� �� ������ ����
    //�ϳ��� �����Ͱ� ���������� ����
    //�����ϸ� true��ȯ, ���������� ��� ������� ���ϰų� �����߻��� false ��ȯ
    public boolean bookingCancel(String building, String roomNumber, String studentID, String studentName, String date){
        connectDB();
        sql = "delete from bookinglist where building = ? and roomNumber = ? and studentID = ? and studentName = ? and date = ?";

        try {
            pstmt= conn.prepareStatement(sql);

            pstmt.setString(1, building);
            pstmt.setString(2, roomNumber);
            pstmt.setString(3, studentID);
            pstmt.setString(4, studentName);
            pstmt.setString(5, date);
            int x;
            if((x = pstmt.executeUpdate()) == 1){
                System.out.println("���� ��� ����!");
                return true;
            }else{
                System.out.println("�ش��ϴ� ���������� �����ϴ�.");
                return false;
            }


        } catch (Exception e) {
            System.out.println("���� ��Ұ� �����߽��ϴ�.");
            e.printStackTrace();
            return false;
        }
    }
    //DB�� Ŀ��Ʈ
    void connectDB(){
        try {
            //JDBC ����̹� �ε�
            //Class.forName(jdbcDriver);

            //�����ͺ��̽� ����
            conn = DriverManager.getConnection(jdbcUrl, "root", "choi1204");
            //System.out.println("DB�� ����Ǿ����ϴ�!");
            clearBookedList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //DB �ݱ�
    void closeDB(){
        try {
            if(pstmt != null) {
                pstmt.close();
            }
            if(resultSet !=null) {
                resultSet.close();
            }
            if(conn != null) {
                conn.close();
            }
            System.out.println("DB ������ ����Ǿ����ϴ�!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
