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

// Receiver类用于接收RTP数据
public class Receiver implements ReceiveStreamListener, SessionListener,ControllerListener,Runnable{
	//存储rtp会话信息参数
	String rtpSessions[] = null; 
	//RTP管理器数组
	RTPManager rtpManagers[] = null;     
	//存放管理播放器窗口的容器
	Vector playerFrames = null;         
	//是否接收到数据的标志
	boolean isDataArrived = false;        
	//所有同步操作的同步对象
	Object synObj = new Object();

	//构造器，将socket捕获的RTPSession传递进来
	public Receiver(String[] args) {
		this.rtpSessions = args;
	}

	// 关闭播放器和会话管理器
	public void closeAll() {
		// 关闭播放窗口
		for (int i = 0; i < playerFrames.size(); i++) {
			try {
				((PlayerFrame)playerFrames.elementAt(i)).player.close();
			}
			catch (Exception e) {}
		}
		// 删除所有player
		playerFrames.removeAllElements();
		// 关闭RTP会话管理器以及RTP会话
		for (int i = 0; i < rtpManagers. length; i++) {
			if (rtpManagers[i] != null) {
				rtpManagers[i].removeTargets( "关闭RTP会话");
				rtpManagers[i].dispose();
				rtpManagers[i] = null;
			}
		}
	}

	// 准备接收数据，初始化RTP会话
	public boolean iniReceiver() {
		try {
			// 所有播放窗口放入容器统一管理
			playerFrames = new Vector();
			// 为每一个RTP会话建立一个管理器
			rtpManagers = new RTPManager[rtpSessions. length];
			SessionARP seLabel;
			for (int i = 0; i < rtpSessions. length; i++) {
				// 处理每一个RTP会话
				try {
					// 解析RTP会话地址
					seLabel = new SessionARP(rtpSessions[i]);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					System.out.println("不能解析: " + rtpSessions[i]);
					return false;
				}
				System.out.println("  - RTP 会话开启: 地址: " + seLabel.addr
						+ " 端口号: " + seLabel.port);
				// 为每一个RTP会话产生一个RTP管理器
				rtpManagers[i] = (RTPManager) RTPManager.newInstance();
				// 注册数据流监听器
				rtpManagers[i].addReceiveStreamListener(this);
				// 注册会话监听器
				rtpManagers[i].addSessionListener(this);
				// 获得发送方IP地址
				InetAddress ipAddr = InetAddress.getByName(seLabel.addr);
				// 本地地址信息
				SessionAddress localAddr = null;
				// 目的地址信息
				SessionAddress destAddr = null;
				if (ipAddr.isMulticastAddress()) {
					// 如果是组播，本地和目的地的IP地址相同
					localAddr = new SessionAddress(ipAddr, seLabel.port);
					System.out.println("is MulticastAddress, localAddr = "
							+ localAddr.toString() + ", port=" + seLabel.port);
					destAddr = new SessionAddress(ipAddr, seLabel.port);
					System.out.println("is MulticastAddress, destAddr = "
							+ destAddr.toString() + ", port =" + seLabel.port);
				} else {
					// 用本机IP地址和端口号构造源会话地址
					localAddr = new SessionAddress(InetAddress.getLocalHost(),
							seLabel.port);
					System.out.println(" localAddr = " + localAddr.toString()
							+ ", port=" + seLabel.port);
					// 用目的机（发送端）的IP地址和端口号构造目的会话地址
					destAddr = new SessionAddress(ipAddr, seLabel.port);
					System.out.println(" destAddr = " + destAddr.toString()
							+ ", port =" + seLabel.port);
				}
				// 使用本机会话地址初始化RTP管理器
				rtpManagers[i].initialize(localAddr);
				BufferControl bc = (BufferControl) rtpManagers[i]
						.getControl("javax.media.control.BufferControl");
				if (bc != null)
					// 设置设置缓冲区大小
					bc.setBufferLength(500);
				// 加入目的会话地址
				rtpManagers[i].addTarget(destAddr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("创建RTP会话失败: " + e.getMessage());
			return false;
		}
		// 直到数据结束
		long startTime = System.currentTimeMillis();
		// 设置最长等待时间30秒
		long maxTime = 30000;
		try {
			//以下程序循环时，其他操作会被阻塞，主界面将会失去响应直到RTP传输失败或成功，所以将Receiver设置为线程
			synchronized (synObj) {
				while (!isDataArrived
						&& System.currentTimeMillis() - startTime < maxTime) {

					// 等待设定的时间
					if (!isDataArrived)
						System.out.println("  - 等待RTP数据中...");
					synObj.wait(1000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (!isDataArrived) {
			// 如果数据没有到
			System.err.println("没有接收到RTP数据！");
			JOptionPane.showMessageDialog(null, "传输视频失败");

			closeAll();
			return false;
		}
		return true;
	}
	// 判断数据传输是否完成
	public boolean isFinished() {
		if(playerFrames.size()==0){
			return true;
		}else{
			return false;
		}
	}
	// 通过播放器查找播放窗口
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
	// 通过接收数据流查找播放窗口
	PlayerFrame find(ReceiveStream strm) {
		for (int i = 0; i < playerFrames.size(); i++) {
			PlayerFrame pw = (PlayerFrame)playerFrames.elementAt(i);
			if (pw.stream == strm){
				return pw;
			}
		}
		return null;
	}
	// 实现ReceiveStreamListener接口 v的update方法
	@SuppressWarnings("unchecked")
	public synchronized void update(ReceiveStreamEvent event) {
		@SuppressWarnings("unused")
		RTPManager rtpManager = (RTPManager) event.getSource();
		// 获得发送者信息
		Participant sender = event.getParticipant();
		// 获得接收的信息流
		ReceiveStream stream = event.getReceiveStream();
		if (event instanceof NewReceiveStreamEvent) {
			// 如果是新接收的数据流
			try {
				stream = ((NewReceiveStreamEvent) event).getReceiveStream(); // 得到新数据流
				DataSource ds = stream.getDataSource(); // 得到数据源
				RTPControl rtpCtrl = (RTPControl) ds.getControl("javax.media.rtp.RTPControl"); // 得到RTP控制器
				//显示数据流信息
				if (rtpCtrl != null) {
					System.out.println("-接收到新的RTP流: " + rtpCtrl.getFormat()); // 得到接收数据的格式
				} else {
					System.err.println("-无新的RTP流");
				}
				if (sender == null) {
					System.out.println("发送数据流需要进一步解析.");
				} else {
					System.out.println("新的数据流来自: " + sender.getCNAME());
				}
				// 通过数据源构造一个媒体播放器
				Player p  = (Player) Manager.createPlayer(ds);
				if (p == null) {
					// 构造失败则返回
					System.err.println("player构造失败");
					return;
				}
				p.addControllerListener(this);//对新建的player添加监听
				p.realize();//构建player部分的控制器
				PlayerFrame pw = new PlayerFrame(p, stream);//产生一个新数据流就在类PlayerFrame中添加一个对应的新元素
				playerFrames.addElement(pw);
				// 通知iniReceiver()函数(初始化RTPManager)中的等待过程：已经接收到了一个新数据流
				synchronized (synObj) {
					isDataArrived = true;
					synObj.notifyAll();
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("新数据流错误：" + e.getMessage());
				return;
			}
		}
		else if (event instanceof StreamMappedEvent) {
			//原先孤立的数据流被关联到一个参与者
			if (stream != null && stream.getDataSource() != null) {
				DataSource ds = stream.getDataSource();
				RTPControl rtpCtrl = (RTPControl) ds.getControl("javax.media.rtp.RTPControl");
				if (rtpCtrl != null) {
					System.out.println(rtpCtrl.getFormat());
					System.out.println("数据流被识别，发送方是: " + sender.getCNAME());
				}
			}
		}
		else if (event instanceof ByeEvent) { 
			// 数据接收完毕
			System.err.println("接收完毕：" + sender.getCNAME());
			PlayerFrame playerWin = find(stream);
			if (!(playerWin == null)) {
				// 关闭播放窗口
				playerWin.player.close();
				playerFrames.removeElement(playerWin);
			}
		}
	}
	// 实现SessionListener接口中的update方法
	public synchronized void update(SessionEvent evnet) {
		if (evnet instanceof NewParticipantEvent) {
			Participant pt = ((NewParticipantEvent)evnet).getParticipant();
			System.err.println("新Session方加入: " + pt.getCNAME()+pt.getSourceDescription());
		}
	}
	// player添加了监听，实现ControllerListener接口的controllerUpdate方法
	public synchronized void controllerUpdate(ControllerEvent event) {
		// 得到事件源
		Player	player = (Player) event.getSourceController();
		if (event instanceof RealizeCompleteEvent) {
			// 如果播放器处于Realize状态
			Component comp;
			if ((comp = player.getControlPanelComponent()) != null) {//获取播放器的控制界面放在下方
				MainWindow.playPanel.add("South",comp);
			}
			if ((comp = player.getVisualComponent()) != null) {//获取播放内容放在中间
				MainWindow.playPanel.add("Center", comp);
			}
//FIXME
			MainWindow.playPanel.validate();
			player.start();
		}
		if (event instanceof ControllerErrorEvent) {
			// 处理控制器错误
			player.removeControllerListener(this);
			PlayerFrame pw = find(player);
			if (pw != null) {
				player.close();
				playerFrames.removeElement(pw);
			}
			System.err.println("player内部错误: " + event);
		}
	}



	public void run(){
		if (rtpSessions. length == 0) {
			System.err.println("无RTP地址");
			System.exit(0);
		}
		Receiver myReceiver = new Receiver(rtpSessions);
		if (!myReceiver.iniReceiver()) {
			System.out.println("初始化失败！");
		}

		try {
			while (myReceiver.isFinished()==false) {
				Thread.sleep(1000);
			}
			closeAll();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("程序出错！");
		}
		System.err.println("失败！");
	}

}
//Session地址解析
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