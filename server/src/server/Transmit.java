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
	public Transmit(Socket s, String ip, String port){//构造器
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
		ip = socket.getInetAddress().getHostAddress();//获取IP地址
		port = Integer.toString(socket.getPort());//获取端口号
		System.out.println("客户端地址："+ip);
		System.out.println("客户端端口号为："+port);
		File mediaFile = new File("C:/Users/YYF/Documents/atime.mov");//默认传输的视频
		Format fmt = null;
		try {
			rtpTransmit = new Server(fmt,ip,new MediaLocator(mediaFile.toURL()),port);//新建传输
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		//开始传输
		boolean result = rtpTransmit.start();
		if (result==false) {
			// 传输错误
			System.out.println("传输失败！");
			MainWin.insertDocument("传输失败！"+"\r\n",Color.RED );

		}
		else {
			//开始传输
			System.out.println("开始传输……");
			MainWin.insertDocument("开始传输……"+"\r\n", Color.BLUE);
		}
	}

}



