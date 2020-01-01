package database;

public class BookAvailableDTO extends  DB_DTO {
	//�� Ŭ������ ���డ���� �����͸� �����ö� ����ϴ� Ŭ����
    private String building;
    private String roomNumber;
    private int isProject;//true =1, false = 0
    private int maxPeople;

    @Override
    public String getBuilding() {
        return building;
    }

    @Override
    public void setBuilding(String building) {
        this.building = building;
    }

    @Override
    public String getRoomNumber() {
        return roomNumber;
    }

    @Override
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    @Override
    public int getIsProject() {
        return isProject;
    }

    @Override
    public void setIsProject(int isProejct) {
        this.isProject = isProejct;
    }

    @Override
    public int getMaxPeople() {
        return maxPeople;
    }

    @Override
    public void setMaxPeople(int maxPeople) {
        this.maxPeople = maxPeople;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.building + " ");
        sb.append(this.roomNumber + " ");
        sb.append(this.isProject+" ");
        sb.append(this.maxPeople+" ");
        return sb.toString();
    }
}
