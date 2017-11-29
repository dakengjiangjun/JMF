package server;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;

import javax.media.Format;
import javax.media.MediaLocator;

public class Transmit implements Runnable {	
	Socket socket;
	String ip;
	String port;
	Server rtpTransmit = null;
	public Transmit(Socket s, String ip, String port){//������
		this.socket = s;
		this.ip = ip ;
		this.port =port;
	}
	public void out(String out){
		try {
			socket.getOutputStream().write(out.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run(){
		ip = socket.getInetAddress().getHostAddress();//��ȡIP��ַ
		port = Integer.toString(socket.getPort());//��ȡ�˿ں�
		System.out.println("�ͻ��˵�ַ��"+ip);
		System.out.println("�ͻ��˶˿ں�Ϊ��"+port);
		File mediaFile = new File("C:/Users/YYF/Documents/atime.mov");//Ĭ�ϴ������Ƶ
		Format fmt = null;
		try {
			rtpTransmit = new Server(fmt,ip,new MediaLocator(mediaFile.toURL()),port);//�½�����
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		//��ʼ����
		boolean result = rtpTransmit.start();
		if (result==false) {
			// �������
			System.out.println("����ʧ�ܣ�");
			MainWin.insertDocument("����ʧ�ܣ�"+"\r\n",Color.RED );

		}
		else {
			//��ʼ����
			System.out.println("��ʼ���䡭��");
			MainWin.insertDocument("��ʼ���䡭��"+"\r\n", Color.BLUE);
		}
	}

}



