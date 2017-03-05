package net.hydrotekz.MCAC.graphics;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;

import javafx.application.Platform;
import net.hydrotekz.MCAC.utils.Printer;

public class DisplayTrayIcon {

	public static TrayIcon trayIcon;

	public DisplayTrayIcon(){
		ShowTrayIcon();
	}

	public static void ShowTrayIcon(){
		if (SystemTray.isSupported()){
			trayIcon = new TrayIcon(CreateIcon("/locked16.png", "Tray Icon"));
			final SystemTray tray = SystemTray.getSystemTray();
			try {
				// Double click
				ActionListener actionListener = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e ) {
						Platform.runLater(() -> {
							Printer.log("Is showing: " + GUI.window.isShowing() + ", Is iconified: " + GUI.window.isIconified());
							if (!GUI.window.isShowing()){
								GUI.window.show();
								GUI.window.setIconified(false);
							} else {
								if (GUI.window.isIconified())
									GUI.window.setIconified(false);
								else
									GUI.window.setIconified(true);
							}
						});
					}
				};
				trayIcon.addActionListener(actionListener);

				// Exit button
				PopupMenu popup = new PopupMenu();
				MenuItem exit = new MenuItem("Avslutt");
				ActionListener exitListener = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e ) {
						Platform.runLater(() -> {
							try {
								trayIcon.displayMessage("Spillere.no Anti-Cheat", "Programmet ble avsluttet.", MessageType.INFO);
								GUI.window.hide();
								tray.remove(trayIcon);
							} catch (Exception ex){}
							System.exit(0);
						});
					}
				};
				exit.addActionListener(exitListener);
				popup.add(exit);

				// Add to tray
				trayIcon.setPopupMenu(popup);
				trayIcon.setToolTip("Spillere.no Anti-Cheat");
				tray.add(trayIcon);

			} catch (Exception e){
				Printer.log(e);
			}
		}
	}

	protected static Image CreateIcon(String path, String desc){
		URL ImageURL = DisplayTrayIcon.class.getResource(path);
		return (new ImageIcon(ImageURL, desc)).getImage();
	}
}