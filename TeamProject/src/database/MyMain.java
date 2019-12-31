package database;

public class MyMain {
    public static void main(String[] args) {
        DB_DAO db = new DB_DAO();
        DB_DTO tmp = new DB_DTO();




        db.getBookAvailableList("2019-12-27 16:00:00",1, 1);


        db.getAll();

        db.getBookedByClient("12345", "Kim");

        db.bookingCancel("A", "701", "12345", "Kim", "2019-12-31 19:22:33");

    }
}
