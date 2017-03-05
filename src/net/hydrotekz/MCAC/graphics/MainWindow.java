package net.hydrotekz.MCAC.graphics;

import java.awt.TrayIcon.MessageType;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.hydrotekz.MCAC.Client;
import net.hydrotekz.MCAC.handlers.JsonHandler;

public class MainWindow {

	/***************************************************************
	 *  Copyright notice
	 *
	 *  (c) 2016 Spillere.no
	 *  All rights reserved
	 *
	 * It's not allowed to copy, reuse and change the code without the permission from the copyright holder
	 *
	 ********************************/

	public static void startMainWindow(Stage primaryWindow){
		GUI.window = primaryWindow;
		Platform.setImplicitExit(false);

		// Reopen at last location
		if ((boolean) JsonHandler.get("reopen_at_last_loc")){
			JsonHandler.put("reopen_at_last_loc", false);
			JsonHandler.saveConfig();
			if (!(boolean) JsonHandler.get("window_iconified")){
				GUI.window.setX((double) JsonHandler.get("window_location_x"));
				GUI.window.setY((double) JsonHandler.get("window_location_y"));
			}
			GUI.window.setIconified((boolean) JsonHandler.get("window_iconified"));
			if ((boolean) JsonHandler.get("window_showing") && !(boolean) JsonHandler.get("window_iconified")){
				GUI.auto = false;
				
			} else {
				GUI.auto = true;
			}
		}

		// Icon
		GUI.window.getIcons().add(new Image("locked.png"));

		// Dashboard
		GUI.dashboard = new Scene(Dashboard.setupDashboard(), 450, 250);

		// Add CSS and finish
		GUI.dashboard.getStylesheets().add("Style.css");
		GUI.window.setScene(GUI.dashboard);
		GUI.window.setTitle("Spillere.no Anti-Cheat v" + Client.version);
		GUI.window.setOnCloseRequest(e -> {
			e.consume();
			GUI.window.hide();
			DisplayTrayIcon.trayIcon.displayMessage("Spillere.no Anti-Cheat", "Programmet kjører fortsatt.", MessageType.INFO);
		});
		GUI.window.setResizable(false);
		if (!GUI.auto) GUI.window.show();
	}
}