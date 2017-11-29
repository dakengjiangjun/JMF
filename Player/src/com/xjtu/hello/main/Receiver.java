package com.xjtu.hello.main;
import java.net.*;
import java.awt.*;

import javax.media.rtp.*;
import javax.media.rtp.event.*;

import java.awt.event.*;
import java.util.Vector;

import javax.media.*;
import javax.media.protocol.DataSource;
import javax.media.bean.playerbean.MediaPlayer;
import javax.media.control.BufferControl;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.xjtu.hello.view.MainWindow;

// Receiver�����ڽ���RTP����
public class Receiver implements ReceiveStreamListener, SessionListener,ControllerListener,Runnable{
	//�洢rtp�Ự��Ϣ����
	String rtpSessions[] = null; 
	//RTP����������
	RTPManager rtpManagers[] = null;     
	//��Ź����������ڵ�����
	Vector playerFrames = null;         
	//�Ƿ���յ����ݵı�־
	boolean isDataArrived = false;        
	//����ͬ��������ͬ������
	Object synObj = new Object();

	//����������socket�����RTPSession���ݽ���
	public Receiver(String[] args) {
		this.rtpSessions = args;
	}

	// �رղ������ͻỰ������
	public void closeAll() {
		// �رղ��Ŵ���
		for (int i = 0; i < playerFrames.size(); i++) {
			try {
				((PlayerFrame)playerFrames.elementAt(i)).player.close();
			}
			catch (Exception e) {}
		}
		// ɾ������player
		playerFrames.removeAllElements();
		// �ر�RTP�Ự�������Լ�RTP�Ự
		for (int i = 0; i < rtpManagers. length; i++) {
			if (rtpManagers[i] != null) {
				rtpManagers[i].removeTargets( "�ر�RTP�Ự");
				rtpManagers[i].dispose();
				rtpManagers[i] = null;
			}
		}
	}

	// ׼���������ݣ���ʼ��RTP�Ự
	public boolean iniReceiver() {
		try {
			// ���в��Ŵ��ڷ�������ͳһ����
			playerFrames = new Vector();
			// Ϊÿһ��RTP�Ự����һ��������
			rtpManagers = new RTPManager[rtpSessions. length];
			SessionARP seLabel;
			for (int i = 0; i < rtpSessions. length; i++) {
				// ����ÿһ��RTP�Ự
				try {
					// ����RTP�Ự��ַ
					seLabel = new SessionARP(rtpSessions[i]);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					System.out.println("���ܽ���: " + rtpSessions[i]);
					return false;
				}
				System.out.println("  - RTP �Ự����: ��ַ: " + seLabel.addr
						+ " �˿ں�: " + seLabel.port);
				// Ϊÿһ��RTP�Ự����һ��RTP������
				rtpManagers[i] = (RTPManager) RTPManager.newInstance();
				// ע��������������
				rtpManagers[i].addReceiveStreamListener(this);
				// ע��Ự������
				rtpManagers[i].addSessionListener(this);
				// ��÷��ͷ�IP��ַ
				InetAddress ipAddr = InetAddress.getByName(seLabel.addr);
				// ���ص�ַ��Ϣ
				SessionAddress localAddr = null;
				// Ŀ�ĵ�ַ��Ϣ
				SessionAddress destAddr = null;
				if (ipAddr.isMulticastAddress()) {
					// ������鲥�����غ�Ŀ�ĵص�IP��ַ��ͬ
					localAddr = new SessionAddress(ipAddr, seLabel.port);
					System.out.println("is MulticastAddress, localAddr = "
							+ localAddr.toString() + ", port=" + seLabel.port);
					destAddr = new SessionAddress(ipAddr, seLabel.port);
					System.out.println("is MulticastAddress, destAddr = "
							+ destAddr.toString() + ", port =" + seLabel.port);
				} else {
					// �ñ���IP��ַ�Ͷ˿ںŹ���Դ�Ự��ַ
					localAddr = new SessionAddress(InetAddress.getLocalHost(),
							seLabel.port);
					System.out.println(" localAddr = " + localAddr.toString()
							+ ", port=" + seLabel.port);
					// ��Ŀ�Ļ������Ͷˣ���IP��ַ�Ͷ˿ںŹ���Ŀ�ĻỰ��ַ
					destAddr = new SessionAddress(ipAddr, seLabel.port);
					System.out.println(" destAddr = " + destAddr.toString()
							+ ", port =" + seLabel.port);
				}
				// ʹ�ñ����Ự��ַ��ʼ��RTP������
				rtpManagers[i].initialize(localAddr);
				BufferControl bc = (BufferControl) rtpManagers[i]
						.getControl("javax.media.control.BufferControl");
				if (bc != null)
					// �������û�������С
					bc.setBufferLength(500);
				// ����Ŀ�ĻỰ��ַ
				rtpManagers[i].addTarget(destAddr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("����RTP�Ựʧ��: " + e.getMessage());
			return false;
		}
		// ֱ�����ݽ���
		long startTime = System.currentTimeMillis();
		// ������ȴ�ʱ��30��
		long maxTime = 30000;
		try {
			//���³���ѭ��ʱ�����������ᱻ�����������潫��ʧȥ��Ӧֱ��RTP����ʧ�ܻ�ɹ������Խ�Receiver����Ϊ�߳�
			synchronized (synObj) {
				while (!isDataArrived
						&& System.currentTimeMillis() - startTime < maxTime) {

					// �ȴ��趨��ʱ��
					if (!isDataArrived)
						System.out.println("  - �ȴ�RTP������...");
					synObj.wait(1000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (!isDataArrived) {
			// �������û�е�
			System.err.println("û�н��յ�RTP���ݣ�");
			JOptionPane.showMessageDialog(null, "������Ƶʧ��");

			closeAll();
			return false;
		}
		return true;
	}
	// �ж����ݴ����Ƿ����
	public boolean isFinished() {
		if(playerFrames.size()==0){
			return true;
		}else{
			return false;
		}
	}
	// ͨ�����������Ҳ��Ŵ���
	PlayerFrame find(Player p) {
		for (int i = 0; i < playerFrames.size(); i++) {
			PlayerFrame pw = (PlayerFrame)playerFrames.elementAt(i);
			if (pw.player == p)
			{
				return pw;
			}
		}
		return null;
	}
	// ͨ���������������Ҳ��Ŵ���
	PlayerFrame find(ReceiveStream strm) {
		for (int i = 0; i < playerFrames.size(); i++) {
			PlayerFrame pw = (PlayerFrame)playerFrames.elementAt(i);
			if (pw.stream == strm){
				return pw;
			}
		}
		return null;
	}
	// ʵ��ReceiveStreamListener�ӿ� v��update����
	@SuppressWarnings("unchecked")
	public synchronized void update(ReceiveStreamEvent event) {
		@SuppressWarnings("unused")
		RTPManager rtpManager = (RTPManager) event.getSource();
		// ��÷�������Ϣ
		Participant sender = event.getParticipant();
		// ��ý��յ���Ϣ��
		ReceiveStream stream = event.getReceiveStream();
		if (event instanceof NewReceiveStreamEvent) {
			// ������½��յ�������
			try {
				stream = ((NewReceiveStreamEvent) event).getReceiveStream(); // �õ���������
				DataSource ds = stream.getDataSource(); // �õ�����Դ
				RTPControl rtpCtrl = (RTPControl) ds.getControl("javax.media.rtp.RTPControl"); // �õ�RTP������
				//��ʾ��������Ϣ
				if (rtpCtrl != null) {
					System.out.println("-���յ��µ�RTP��: " + rtpCtrl.getFormat()); // �õ��������ݵĸ�ʽ
				} else {
					System.err.println("-���µ�RTP��");
				}
				if (sender == null) {
					System.out.println("������������Ҫ��һ������.");
				} else {
					System.out.println("�µ�����������: " + sender.getCNAME());
				}
				// ͨ������Դ����һ��ý�岥����
				Player p  = (Player) Manager.createPlayer(ds);
				if (p == null) {
					// ����ʧ���򷵻�
					System.err.println("player����ʧ��");
					return;
				}
				p.addControllerListener(this);//���½���player��Ӽ���
				p.realize();//����player���ֵĿ�����
				PlayerFrame pw = new PlayerFrame(p, stream);//����һ����������������PlayerFrame�����һ����Ӧ����Ԫ��
				playerFrames.addElement(pw);
				// ֪ͨiniReceiver()����(��ʼ��RTPManager)�еĵȴ����̣��Ѿ����յ���һ����������
				synchronized (synObj) {
					isDataArrived = true;
					synObj.notifyAll();
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("������������" + e.getMessage());
				return;
			}
		}
		else if (event instanceof StreamMappedEvent) {
			//ԭ�ȹ�������������������һ��������
			if (stream != null && stream.getDataSource() != null) {
				DataSource ds = stream.getDataSource();
				RTPControl rtpCtrl = (RTPControl) ds.getControl("javax.media.rtp.RTPControl");
				if (rtpCtrl != null) {
					System.out.println(rtpCtrl.getFormat());
					System.out.println("��������ʶ�𣬷��ͷ���: " + sender.getCNAME());
				}
			}
		}
		else if (event instanceof ByeEvent) { 
			// ���ݽ������
			System.err.println("������ϣ�" + sender.getCNAME());
			PlayerFrame playerWin = find(stream);
			if (!(playerWin == null)) {
				// �رղ��Ŵ���
				playerWin.player.close();
				playerFrames.removeElement(playerWin);
			}
		}
	}
	// ʵ��SessionListener�ӿ��е�update����
	public synchronized void update(SessionEvent evnet) {
		if (evnet instanceof NewParticipantEvent) {
			Participant pt = ((NewParticipantEvent)evnet).getParticipant();
			System.err.println("��Session������: " + pt.getCNAME()+pt.getSourceDescription());
		}
	}
	// player����˼�����ʵ��ControllerListener�ӿڵ�controllerUpdate����
	public synchronized void controllerUpdate(ControllerEvent event) {
		// �õ��¼�Դ
		Player	player = (Player) event.getSourceController();
		if (event instanceof RealizeCompleteEvent) {
			// �������������Realize״̬
			Component comp;
			if ((comp = player.getControlPanelComponent()) != null) {//��ȡ�������Ŀ��ƽ�������·�
				MainWindow.playPanel.add("South",comp);
			}
			if ((comp = player.getVisualComponent()) != null) {//��ȡ�������ݷ����м�
				MainWindow.playPanel.add("Center", comp);
			}
//FIXME
			MainWindow.playPanel.validate();
			player.start();
		}
		if (event instanceof ControllerErrorEvent) {
			// �������������
			player.removeControllerListener(this);
			PlayerFrame pw = find(player);
			if (pw != null) {
				player.close();
				playerFrames.removeElement(pw);
			}
			System.err.println("player�ڲ�����: " + event);
		}
	}



	public void run(){
		if (rtpSessions. length == 0) {
			System.err.println("��RTP��ַ");
			System.exit(0);
		}
		Receiver myReceiver = new Receiver(rtpSessions);
		if (!myReceiver.iniReceiver()) {
			System.out.println("��ʼ��ʧ�ܣ�");
		}

		try {
			while (myReceiver.isFinished()==false) {
				Thread.sleep(1000);
			}
			closeAll();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("�������");
		}
		System.err.println("ʧ�ܣ�");
	}

}
//Session��ַ����
class SessionARP {
	public String addr = null;
	public int port;
	SessionARP(String rtpSession) throws IllegalArgumentException {
		int offset;
		String portStr = null;
		if (rtpSession != null && rtpSession.length() > 0) {
			while (rtpSession.length() > 1 && rtpSession.charAt(0) == '/')
				rtpSession = rtpSession.substring(1);
			offset = rtpSession.indexOf('/');
			if (offset == -1) {
				if (!rtpSession.equals(""))
					addr = rtpSession;
			} else {
				addr = rtpSession.substring(0, offset);
				rtpSession = rtpSession.substring(offset + 1);
				offset = rtpSession.indexOf('/');
				if (offset == -1) {
					if (!rtpSession.equals("")){
						portStr = rtpSession;
					}
				}
			}
		}
		if (addr == null) {
			throw new IllegalArgumentException();
		}
		if (portStr != null) {
			try {
				Integer integer = Integer.valueOf(portStr);
				if (integer != null)
					port = integer.intValue();
			} catch (Throwable t) {
				throw new IllegalArgumentException();
			}
		} else {
			throw new IllegalArgumentException();
		}
	}
}