package database;

import com.google.gson.Gson;

public class MyMain {
    private static Gson gson;

    public static void main(String[] args) {
        DB_DAO db = new DB_DAO();
        BookedDTO tmp;
        UserDAO user = new UserDAO();




        db.getAll();

        db.getBookedByClient("12345", "Kim");

        db.bookingCancel("대양AI", "703", "11111111", "홍길동", "2019-12-31 20:00:00");
        db.getBookAvailableList("2019-12-27 16:00:00",1, 1);
        //(String building, String roomNumber, String date, int isProject, int maxPeople, String studentID, String studentName){
        //tmp = new BookedDTO("대양AI", "713", "2019-12-31 20:00:00", 1, 16, "11111111", "홍길동");

        //db.book(tmp);


    }
}
