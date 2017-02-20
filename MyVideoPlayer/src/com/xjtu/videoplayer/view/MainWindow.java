package com.xjtu.videoplayer.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JProgressBar;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.xjtu.videoplayer.main.PlayerMain;
import javax.swing.JSplitPane;

public class MainWindow extends JFrame {
	private JPanel contentPane = null;
	private JPanel playPane = null;
	private JPanel getplayPane() { 
		if (playPane == null) { 
		playPane = new JPanel(); 
		playPane.setLayout(new BorderLayout()); 
		playPane.add(getplayPane(), BorderLayout.CENTER); 
		} 
		return playPane; 
		} 
	
	
	
	
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {           //UIÏß³Ì
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	
	
	
	
	
	
	
	public MainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 498, 411);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmOpenVideo = new JMenuItem("Open Video");
		mntmOpenVideo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlayerMain.openVideo();
			}
		});
		mnFile.add(mntmOpenVideo);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlayerMain.exit();
			}
		});
		mnFile.add(mntmExit);
		
		
		
		
		
		
		
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel videopane = new JPanel();
		videopane.setToolTipText("JMF Plyaer");
		contentPane.add(videopane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel controlPanel = new JPanel();
		panel.add(controlPanel, BorderLayout.CENTER);
		
		JButton btnStop = new JButton("Stop");
		controlPanel.add(btnStop);
		
		JButton btnPlay = new JButton("Play");
		controlPanel.add(btnPlay);
		
		JButton btnPasue = new JButton("Pasue");
		controlPanel.add(btnPasue);
		
		JProgressBar progress = new JProgressBar();
		progress.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		panel.add(progress, BorderLayout.NORTH);
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		//playerComponent = new 
		//contentPane.add(playerComponent, BorderLayout.CENTER);
	}

}
