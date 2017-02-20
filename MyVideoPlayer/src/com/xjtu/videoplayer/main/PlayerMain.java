package com.xjtu.videoplayer.main;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.media.*;
import com.xjtu.videoplayer.view.MainWindow;


public class PlayerMain {
	static MainWindow frame;//´´½¨´°Ìå

	public static void main(String[] args) {
		
		
		
	EventQueue.invokeLater(new Runnable() { 
		public void run() { 
			try{
				frame=new MainWindow();
				frame.setVisible(true);
				frame.getMediaPlayer().playMedia();
			}catch (Exception e){
				e.printStackTrace();
			}
		} 
		}); 	

	}
	
	public Object getMediaPlayer(){
		return getMediaPlayer();
	}
	
	
	
	
	
	
	
	
	
	public static void openVideo(){
		JFileChooser chooser= new JFileChooser();
		int v = chooser.showOpenDialog(null);
		if (v == JFileChooser.APPROVE_OPTION){
			File file =chooser.getSelectedFile();
			frame.getMediaPlayer().playMedia(file.getAbsolutePath());
		}
		
	}
	
	public static void exit(){
		frame.getMediaPlayer().release();
		
	}

}
