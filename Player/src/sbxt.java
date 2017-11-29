import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


public class sbxt extends JFrame{
	private JButton button;
	public sbxt(){
		button=new JButton();
		button.setToolTipText("这是什么按钮？");
		this.add(button);
		this.setSize(200,300);
		this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public static void main(String[] args){
		new sbxt();
	}
}




