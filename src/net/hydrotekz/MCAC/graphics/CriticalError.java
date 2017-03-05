package net.hydrotekz.MCAC.graphics;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.hydrotekz.MCAC.Client;
import net.hydrotekz.MCAC.Intel;
import net.hydrotekz.MCAC.Updater;

public class CriticalError extends JFrame {

	private static final long serialVersionUID = 2452233478210697897L;

	private JButton okay = new JButton("OK");
	public static JLabel title = new JLabel("");
	public static JLabel message = new JLabel("");

	public CriticalError(){
		super("Spillere.no Anti-Cheat v" + Client.version);
		setLayout(new FlowLayout());

		add(title);

		okay.setToolTipText("Avslutt programmet");
		add(okay);

		add(message);

		HandlerClass handler = new HandlerClass();
		okay.addActionListener(handler);
	}

	private class HandlerClass implements ActionListener {
		public void actionPerformed(ActionEvent e){
			Updater.updateCheck();
			Updater.updateIfPossible();
			Intel.updateOnExit = false;
			System.exit(0);
		}
	}

	public static void castError(String title, String msg){
		CriticalError.title = new JLabel(title);
		CriticalError.message = new JLabel(msg);
		CriticalError wid = new CriticalError();
		wid.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension dimemsion = Toolkit.getDefaultToolkit().getScreenSize();
		wid.setLocation(dimemsion.width/2-wid.getSize().width/2, dimemsion.height/2-wid.getSize().height/2);
		wid.setSize(853, 100);
		wid.setVisible(true);
	}
}