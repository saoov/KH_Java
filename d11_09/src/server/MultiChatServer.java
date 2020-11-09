package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class MultiChatServer {
	private HashMap clients;

	public MultiChatServer() {
		clients = new HashMap();
		Collections.synchronizedMap(clients);
	}

	class ServerReceiver extends Thread {
		private Socket socket;
		private DataInputStream in;
		private DataOutputStream out;

		public ServerReceiver(Socket socket) {
			this.socket = socket;
			try {
				this.in = new DataInputStream(socket.getInputStream());
				this.out = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			String name = null;
			try {
				name = in.readUTF();
				sendToAll("#" + name + "���� �����̽��ϴ�.");
				clients.put(name, out);
				System.out.println("���� ���������� ���� " + clients.size() + "�Դϴ�.");
				while (in != null) {
					sendToAll(in.readUTF());
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				sendToAll("#" + name + "���� �����̽��ϴ�.");
				clients.remove(name);
				System.out.println("���� ������ ���� " + clients.size() + "�Դϴ�.");

			}
		}
	}

	public void start() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(7979);
			System.out.println("������ ���� �Ǿ����ϴ�.");
			while (true) {
				socket = serverSocket.accept();
				System.out.println("���ο� ������ �α��� �Ͽ����ϴ�.");
				ServerReceiver thread = new ServerReceiver(socket);
				thread.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendToAll(String msg) {
		Iterator it = clients.keySet().iterator();
		while (it.hasNext()) {
			try {
				DataOutputStream out = (DataOutputStream) clients.get(it.next());
				out.writeUTF(msg);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}