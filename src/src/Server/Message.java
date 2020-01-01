package Server;

public class Message {

	private String studentId;
	private String name;
	private String building;
	private String roomNum;
	private String capacity;
	private String equipment;
	private String date;// 2019-12-12 15:22:39
	private String type;
	private String msg;
	
	//constructor
	public Message() {
		this.studentId = "";
		this.name = "";
		this.building = "";
		this.roomNum = "";		
		this.capacity = "";
		this.equipment = "";
		this.date = "";
		this.type = "";
		this.msg="";
	}

	public Message(String studentId, String name, String building, String roomNum, String capacity, String equipment,String date, String type, String msg) {
		this.studentId = studentId;
		this.name = name;
		this.building = building;
		this.roomNum = roomNum;
		this.capacity = capacity;
		this.equipment = equipment;
		this.date = date;
		this.type = type;
		this.msg = msg;
	}

	//getter & setter
	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getRoomNum() {
		return roomNum;
	}

	public void setRoomNum(String roomNum) {
		this.roomNum = roomNum;
	}

	public String getCapacity() {
		return capacity;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}

	public String getEquipment() {
		return equipment;
	}

	public void setEquipment(String equipment) {
		this.equipment = equipment;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
