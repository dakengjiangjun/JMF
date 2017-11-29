package com.xjtu.hello.view;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.RealizeCompleteEvent;
import javax.media.bean.playerbean.MediaPlayer;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.xjtu.hello.main.Main;

public class MainWindow extends JFrame implements ControllerListener, ActionListener {
	public static javax.media.Player player2;
	public MediaPlayer Player;
	private static final long serialVersionUID = 1L;
	private JSplitPane jSplitPane = null;
	public static JPanel playPanel ;
	static JPanel video = null;
	static JPanel audio = null;
	public JList jList = null;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem mntmOpen;
	private JMenuItem mntmExit;
	private JMenuItem mntmLoad;
	private JMenuItem mntmRtp;
	private JMenu mnNewMenu;
	private JMenu menu_1;
	private JMenuItem menuItem;
	private JMenuItem menuItem_1;
	private JMenu menu_2;
	private JMenuItem menuItem_2;
	private JMenuItem mntmNewMenuItem;
	private JMenu mnNewMenu_1;
	private JMenuItem menuItem_3;
	private JMenuItem menuItem_4;
	/**
	 * Launch the application.
	  
	 * public static void main(String[] args) { EventQueue.invokeLater(new
	 * Runnable() { public void run() { try { MainWindow frame = new MainWindow();
	 * frame.setVisible(true); } catch (Exception e) { e.printStackTrace(); } }
	 * }); }
	 * 
	 * /** Create the frame.
	 */
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {              //主线程
		EventQueue.invokeLater(new Runnable() {           //UI线程
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 默认关闭方法
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

	// 主面板（分割面板）设置

	public MainWindow() {   
		super("MainWindow");
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);	
		menu = new JMenu("\u6587\u4EF6");
		menu.setToolTipText("主菜单");
		menuBar.add(menu);
		mntmOpen = new JMenuItem("\u6253\u5F00\u6587\u4EF6");
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.OpenFile();
				Main.play();
			}
		});
		menu.add(mntmOpen);
		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}
		});
		menu.add(mntmExit);
		
		menu_1 = new JMenu("\u64AD\u653E");
		menuBar.add(menu_1);
		
		menuItem = new JMenuItem("\u64AD\u653E/\u6682\u505C");
		menu_1.add(menuItem);
		
		menuItem_1 = new JMenuItem("\u505C\u6B62");
		menu_1.add(menuItem_1);
		
		menu_2 = new JMenu("\u64AD\u653E\u63A7\u5236");
		menu_1.add(menu_2);
		
		menuItem_2 = new JMenuItem("\u5FEB\u8FDB");
		menu_2.add(menuItem_2);
		
		mntmNewMenuItem = new JMenuItem("\u5FEB\u9000");
		menu_2.add(mntmNewMenuItem);
		
		mnNewMenu = new JMenu("\u76F4\u64AD");
		menuBar.add(mnNewMenu);
		mntmLoad = new JMenuItem("\u8FDE\u63A5\u670D\u52A1\u5668");
		mnNewMenu.add(mntmLoad);
		mntmLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.socket();
			}
		});
		
		mntmRtp = new JMenuItem("RTP");
		mnNewMenu.add(mntmRtp);
		
		mnNewMenu_1 = new JMenu("\u5E2E\u52A9");
		menuBar.add(mnNewMenu_1);
		
		menuItem_3 = new JMenuItem("\u5173\u4E8E");
		mnNewMenu_1.add(menuItem_3);
		
		menuItem_4 = new JMenuItem("\u5E2E\u52A9");
		mnNewMenu_1.add(menuItem_4);
		mntmRtp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.RTP();
			}
		});
//		initialize();
//	}
//
//	// 初始化
//	private void initialize() {
		
		
		
//		getContentPane().setLayout(null);
		this.setBackground(Color.DARK_GRAY);
		getContentPane().setLayout(new BorderLayout(5,5));
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		jSplitPane = new JSplitPane();
		jSplitPane.setDividerSize(5);
		jSplitPane.setResizeWeight(0.8);
		getContentPane().add(jSplitPane,BorderLayout.CENTER);
		
		jList = new JList();
		jList.setToolTipText("");
		jList.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 1) {
					String str = (String) jList.getSelectedValue();
					if (str == null) {
						return;
					}
					Main.filename = str;
					System.out.println(str);
				}
				if (e.getClickCount() == 2) {
					String str = (String) jList.getSelectedValue();
					if (str == null) {
						return;
					}
					Main.filename = str;
					Main.play();
				}
			}
		});
		jSplitPane.setRightComponent(jList);
		
		playPanel = new JPanel();
		playPanel.setLayout(new BorderLayout(0, 0));
		jSplitPane.setLeftComponent(playPanel);
		

		
		
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
		
		
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// 把窗口放在屏幕中间
		this.setPreferredSize(new Dimension(600, 450));
		this.setBounds(screenSize.width / 2 - 250, screenSize.height / 2 - 300,
				437, 450);
		this.addNotify();
		pack();
		
		this.setVisible(true);
		setResizable(false);
	}


	

	
	








	public void closePreviosPlayer() {
		if (Player == null)
			return;
		Player.stop();
		Player.deallocate(); 
		Component visual = Player.getVisualComponent();
		Component control = Player.getControlPanelComponent();
		if (visual != null) {
			playPanel.remove(visual);
		}
		if (control != null) {
			playPanel.remove(control);
		}
	}

	//设置player的显示
	public synchronized void controllerUpdate(ControllerEvent event) {//controllerUpdate方法对播放器的事件作出反应。一个播放器的用户接口容器只有在该播放器的状态为Realized时才能显示。
		if (event instanceof RealizeCompleteEvent) {
			Component comp;
//			FIXME
			getContentPane().setPreferredSize(Player.getPreferredSize());
			if ((comp = Player.getControlPanelComponent()) != null) {//获取播放器的控制界面放在下方
				playPanel.add("South", comp);
			} else {
				closePreviosPlayer();
			}
			if ((comp = Player.getVisualComponent()) != null) {//获取播放内容放在中间
				playPanel.add("Center", comp);
			}
			validate();
		}
		;
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {	
	}
}

