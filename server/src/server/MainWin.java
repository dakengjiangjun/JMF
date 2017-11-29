package server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JPanel;

public class MainWin extends JFrame implements ControllerListener, ActionListener{
	static String ip = null;
	static String port = null;
	static String locator = null;
	Server rtpTransmit = null;
	ButtonGroup bGroup = null;
	JButton browsBut = null;
	JTextField fileNameText =null;
	JLabel desMchLabel=null;
	JLabel ipLabel =null;
	JTextField ipText =null;
	JLabel portLabel =null;
	JTextField portText =null;
	JButton tranBut =null;
	JButton stopBut =null;
	String str = null;
	String filename="C:/Users/YYF/Documents/atime.mov";
	String filename2= "";
	Vector vct = new Vector();
	JTextField textField_2;
	static JTextArea textArea;
	static JTextPane textPane;
	public MainWin(){
		
		getContentPane().addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				
				textField_2.setText(filename);
				//FIXME
			}
		});
		jbInit();            // 显示出界面	    
	}
	
	// 初始化界面
	private void jbInit() {
		getContentPane().setLayout(null);
		this.setBackground(Color.DARK_GRAY);
		this.getContentPane().setLayout(null);
		desMchLabel =new JLabel("\u5BA2\u6237\u7AEF\u4FE1\u606F");
		desMchLabel.setFont(new Font("", Font.BOLD, 20));
		desMchLabel.setPreferredSize(new Dimension(200,30));
		desMchLabel.setBounds(10, 81, 200, 30);
		getContentPane().add(desMchLabel);
		stopBut=new JButton("停止");
		stopBut.setPreferredSize(new Dimension(80,30));
		stopBut.setBounds(167,363,80,30);
		getContentPane().add(stopBut);

		JButton button = new JButton("\u89C6\u9891\u4F4D\u7F6E");
		button.addActionListener(this);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
			}
		});
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		button.setBounds(10, 48, 93, 23);
		getContentPane().add(button);

		textField_2 = new JTextField();


		textField_2.setBounds(105, 48, 325, 25);
		getContentPane().add(textField_2);
		textField_2.setColumns(10);
		textPane = new JTextPane();
		textPane.setFont(new Font("Monospaced", Font.ITALIC, 16));
		textPane.setText("等待客户端连接……"+"\r\n");


		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 120, 430, 230);
		getContentPane().add(scrollPane);
		
		scrollPane.setViewportView(textPane);
		
		JLabel label = new JLabel("\u670D\u52A1\u5668\u4FE1\u606F");
		label.setPreferredSize(new Dimension(200, 30));
		label.setFont(new Font("Dialog", Font.BOLD, 20));
		label.setBounds(10, 8, 200, 30);
		getContentPane().add(label);
		
		JPanel panel = new JPanel();
		panel.setBounds(81, 392, 10, 10);
		getContentPane().add(panel);
		
		stopBut.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopButPress(e);
			}
		});
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				this_windowClosing(e);
			}
		});
		// 设置标题
		this.setTitle("RTP\u4F20\u8F93\u670D\u52A1\u5668");                  
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// 把窗口放在屏幕中间
		this.setPreferredSize(new Dimension(450, 450));
		this.setBounds(screenSize.width / 2 - 250, screenSize.height / 2 - 300,
				437, 450);
		this.setVisible(true);
		setResizable(false);
		pack();
	}
	public static void insertDocument(String text , Color textColor)//根据传入的颜色及文字，将文字插入文本域
	{
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setForeground(set, textColor);//设置文字颜色
		StyleConstants.setFontSize(set, 15);//设置字体大小
		Document doc = textPane.getStyledDocument();
		try
		{
			doc.insertString(doc.getLength(), text+"\r\n", set);//插入文字
		}
		catch (BadLocationException e)
		{
		}
	}
	//		选择文件
	public  void OpenFile() {
		FileDialog fd = new FileDialog(this, "Choose Video", FileDialog.LOAD);
		fd.setVisible(true);
		filename = fd.getDirectory() + fd.getFile();
		filename2= fd.getFile();
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
		if (j == false) {
			vct.add(filename2);
		}
	}
	// 处理停止按钮事件
	void stopButPress(ActionEvent e) {
		System.out.println("...传输结束.");
		System.exit(0);
	}
	// 相应窗口事件
	void this_windowClosing(WindowEvent e) {
		System.exit(0);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
	}
	@Override
	public void controllerUpdate(ControllerEvent arg0) {
	}

	public static void main(String args[]){	
		//界面风格
		try {
			BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.generalNoTranslucencyShadow;
			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
		} catch (Exception e){

			e.printStackTrace();
		}
		try {
			int i = 1;
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(60008);//服务器端口号
			MainWin window = new MainWin();
			System.out.println("客户端地址:"+ip);
			System.out.println("客户端端口号为:"+port);
			while(true){
				//阻塞方法监听
				Socket socket = serverSocket.accept();
				ip = socket.getInetAddress().getHostAddress();
				port = Integer.toString(socket.getPort());
				//建立连接
				System.out.println("第"+i+"台服务器连入");
				insertDocument("新客户端连入……" , Color.BLUE);
				insertDocument("IP地址和端口号："+ip+"/"+port, Color.BLACK);
				//将socket传递给新的线程
				Transmit  ts = new Transmit(socket,ip,port);
				Thread thread = new Thread(ts);
				thread.start();
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("这是主线程"); //循环外，不会运行这行代码
	}
}


