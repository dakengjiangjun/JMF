package com.xjtu.hello.main;

import javax.media.ControllerListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;
import javax.swing.*;
import javax.media.*;
import javax.media.bean.playerbean.MediaPlayer;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import com.xjtu.hello.view.MainWindow;

// 视频播放程序
public abstract class Main extends JFrame implements ControllerListener, ActionListener {

	private static final long serialVersionUID = 1L;
	static MainWindow frame;//创建窗体
	public static String filename = "";
	public static String filename2= "";
	public static String ip = null;
	public static int port ;
	public static int port2 ;
	public static Vector vct = new Vector();
	public static void main(String[] args) {
		try {//lookandfeel设置界面风格
	        BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.generalNoTranslucencyShadow;
	        org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
	        } 
		catch (Exception e) {
			e.printStackTrace();
		}
	EventQueue.invokeLater(new Runnable() { //主线程
		public void run() {//运行UI线程
			try{
				frame=new MainWindow();
				frame.setVisible(true);
			}catch (Exception e){
				e.printStackTrace();
			}
		} 
		});
	}
	//开始播放
	public static void play(){
				if (frame.Player == null) {
					frame.Player = new MediaPlayer();
				} else {
					frame.closePreviosPlayer();
				}
				frame.closePreviosPlayer();
				frame.Player.setMediaLocator(new MediaLocator("file:///" + filename));
				frame.Player.addControllerListener(frame);
				frame.Player.realize();
				frame.Player.start();
	}
	//停止播放器
	public static void stop() {
		if (frame.Player != null) {
			frame.Player.stop();
			frame.Player.deallocate();
		}
	}
	//接收RTP数据流的方法
	public static void RTP(){
		frame.closePreviosPlayer();
		String args[];
		args=new String[]{ip+"/"+port,ip+"/"+port2};
		Receiver receiver = new Receiver(args);
		Thread thread = new Thread(receiver);
		thread.start();

		
	}
	//运行socket的方法
	public static void socket(){
		Socket socket = null ;
			try {
				 socket = new Socket("192.168.1.104",60008);
				ip = socket.getInetAddress().getHostAddress();//获取本机的ip
				port = socket.getLocalPort();//获取本机的端口
				port2 = socket.getLocalPort()+2;
				System.out.println("rtp传输的IP地址:"+ip+"/"+port);
				System.out.println("port2:"+port2);
			}catch (IOException e) {
			}finally{
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println(e);
				}
			}
	}
	//打开文件并将文件名放入列表中
	public static void OpenFile() {
		FileDialog fd = new FileDialog(frame, "Choose Video", FileDialog.LOAD);//文件选择窗口
		fd.setVisible(true);
		filename = fd.getDirectory() + fd.getFile();//文件地址+文件名
		filename2= fd.getFile();//文件名
		System.out.println(filename);
		if (filename.equals("")) {
			return;
		} else if (filename.equals("nullnull")) {
			return;
		}
		boolean j = false;
		for (int i = 0; i < vct.size(); i++) {
			if (vct.get(i).toString().equals(filename)) {
				j = true;
				break;
			}
		}
		//将文件名显示在Jlist中
		if (j == false) {
			vct.add(filename2);
			frame.jList.setListData(vct);
		}
	}	
}
	
