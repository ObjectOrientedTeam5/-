
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.google.gson.Gson;

public class Server {

	// ���� ���� �� Ŭ���̾�Ʈ ���� ����
	private ServerSocket ss = null;
	private Socket s = null;

	// ����� Ŭ���̾�Ʈ �����带 �����ϴ� ArrayList
	ArrayList<ChatThread> chatThreads = new ArrayList<ChatThread>();

	// �ΰ� ��ü
	Logger logger;

	// ��Ƽ ä�� ���� ���α׷� �κ�
	public void start() {
		logger = Logger.getLogger(this.getClass().getName());

		try {
			// ���� ���� ����
			ss = new ServerSocket(8888);
			logger.info("MultichatServer start");

			// ���� ������ ���鼭 Ŭ���̾�Ʈ ������ ��ٸ���.
			while (true) {
				s = ss.accept();
				// ����� Ŭ���̾�Ʈ�� ���� ������ Ŭ���� ����
				ChatThread chat = new ChatThread();
				// Ŭ���̾�Ʈ ����Ʈ �߰�
				chatThreads.add(chat);
				// ������ ����
				chat.start();
			}
		} catch (Exception e) {
			logger.info("[MultiChatServer]start() Exception�߻�!!");
			e.printStackTrace();
		}

	}

	// ����� ��� Ŭ���̾�Ʈ�� �޽��� �߰�
	public void msgSendAll(String msg) {
		for (ChatThread ct : chatThreads) {
			ct.outMsg.println(msg);
		}
	}
	public static void main(String[] args) {
		Server chatserver = new Server();
		chatserver.start();
	}

	class ChatThread extends Thread {

		// ���� �޽��� �� �Ľ� �޽��� ó���� ���� ���� ����
		String msg;

		// �޽��� ��ü ����
		Message m = new Message();

		// JSON �ļ� �ʱ�ȭ
		Gson gson = new Gson();
		
		String id;

		// ����� ��Ʈ��
		private BufferedReader inMsg = null;
		private PrintWriter outMsg = null;

		public void run() {
			boolean status = true;

			try {
				inMsg = new BufferedReader(new InputStreamReader(s.getInputStream()));
				outMsg = new PrintWriter(s.getOutputStream(), true);

				while (status) {
					// ���ŵ� �޽����� msg ������ ����
					try {
						msg = inMsg.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}

					// JSON�޽����� Message ��ü�� ����
					m = gson.fromJson(msg, Message.class);

					// �Ľ̵� ���ڿ� �迭�� �� ��° ��Ұ��� ���� ó��
					// �α׾ƿ� �޽��� �� ��
					if (m.getType().equals("logout")) {
						chatThreads.remove(this);
						msgSendAll(gson.toJson(new Message(m.getId(), "", "���� ���� �߽��ϴ�","", "server")));

						// �ش礷 Ŭ���̾�Ʈ ������ ����� status�� false �� ����
						status = false;
					}
					// �α��� �޽����� ��
					else if (m.getType().equals("login")) {
						id = m.getId();
						msgSendAll(gson.toJson(new Message(m.getId(), "", "���� �α����߽��ϴ�","", "server")));
					}
					else if(m.getType().equals("find")) 	//��ȸ 
					{
						
					}
					else if(m.getType().equals("reservation")) //����
					{
						
					}
					else if(m.getType().equals("cancle")) //�������
					{
						
					}
					
					else if(m.getType().equals("secret"))
					{
						System.out.println("�Ӹ� ON");
						//msgSendSecret(msg, m.getTo());
						//msgSendSecret(msg, m.getId());
					}
					else
					{
						msgSendAll(msg);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// ������ ����� Ŭ���̾�Ʈ ������ ����ǹǷ� ������ ���ͷ�Ʈ
			this.interrupt();
			logger.info(this.getName() + " �����!!");
		}

	}

}
