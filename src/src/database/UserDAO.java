package database;

import java.sql.*;

public class UserDAO {
    //String jdbcDriver = "com.mysql.jdbc.Driver";
    String jdbcUrl = "jdbc:mysql://localhost/javadb?serverTimezone=Asia/Seoul&useSSL=false";//jdbc:mysql://127.0.0.1/javadb?serverTimezone=UTC
    Connection conn;

    PreparedStatement pstmt;
    ResultSet resultSet;

    //�н����� ��ȣȭ
    //select sha2(��, 256)
    //Primary Key is studentID(�й�)
    public void join(String ID, String password, String studentName, String studentID){
        connectDB();

        String sql = "insert into user(ID, password, studentName, studentID) values(?, sha2(?, 256), ?, ?)";
        try {
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, ID);
            pstmt.setString(2, password);
            pstmt.setString(3, studentName);
            pstmt.setString(4, studentID);

            int k = pstmt.executeUpdate();

            if(k != 1) {
                System.out.println("ȸ������ ����..");
                throw new JoinFailException("Join Failed...");
            }
            System.out.println("ȸ������ ����!");
            closeDB();
        } catch (SQLException e) {
            e.printStackTrace();
            closeDB();
        } catch (JoinFailException e) {
            e.printStackTrace();
            closeDB();
        }

    }
    public boolean login(String studentID, String studentName){
        connectDB();
        System.out.println(studentID);
        String sql = "select * from user where studentID = ? and studentName = ?";
        try {
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, studentID);
            pstmt.setString(2, studentName);

            resultSet = pstmt.executeQuery();
            
            if(resultSet.next()){
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    /*//�α����� �����ϸ� true �ƴϸ� false ��ȯ
    public boolean login(String ID, String password){
        connectDB();

        String sql = "select * from user where ID = ? and password = sha2(?, 256)";
            try {
                pstmt = conn.prepareStatement(sql);

                pstmt.setString(1, ID);
                pstmt.setString(2, password);

                resultSet = pstmt.executeQuery();

            if(resultSet.next()){
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }*/
    //DB�� Ŀ��Ʈ
    void connectDB(){
        try {
            //JDBC ����̹� �ε�
            //Class.forName(jdbcDriver);

            //�����ͺ��̽� ����
            conn = DriverManager.getConnection(jdbcUrl, "root", "choi1204");
            //System.out.println("DB�� ����Ǿ����ϴ�!");
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
