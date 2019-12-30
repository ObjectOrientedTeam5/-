
public class Message {

	private String id;
	private String passwd;
	private String msg;
	private String type;
	private String To;
	public Message() {
		// TODO Auto-generated constructor stub
	}
	
	public Message(String id, String pwd, String msg,String To, String type) {
		this.id = id;
		this.passwd = pwd;
		this.msg = msg;
		this.To = To;
		this.type = type;
	}
	public String getTo() {
		return To;
	}

	public void setTo(String to) {
		To = to;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
