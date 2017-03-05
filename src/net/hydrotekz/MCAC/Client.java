package net.hydrotekz.MCAC;

import java.awt.TrayIcon.MessageType;
import java.io.File;

import org.apache.commons.io.IOUtils;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import net.hydrotekz.MCAC.graphics.CriticalError;
import net.hydrotekz.MCAC.graphics.DisplayTrayIcon;
import net.hydrotekz.MCAC.graphics.GUI;
import net.hydrotekz.MCAC.graphics.MainWindow;
import net.hydrotekz.MCAC.handlers.JsonHandler;
import net.hydrotekz.MCAC.net.SocketClient;
import net.hydrotekz.MCAC.utils.Printer;
import net.hydrotekz.MCAC.utils.RSA;

public class Client extends Application {

	/***************************************************************
	 *  Copyright notice
	 *
	 *  (c) 2016 Spillere.no
	 *  All rights reserved
	 *
	 * It's not allowed to copy, reuse and change the code without the permission from the copyright holder
	 *
	 ********************************/

	public static double version = 5.5;
	// TODO: Test File.separator

	static DisplayTrayIcon DTI = new DisplayTrayIcon();

	@Override
	public void start(Stage primaryWindow) throws Exception {
		// Check if already running
		if (JsonHandler.containsKey("update_restart") && (boolean) JsonHandler.get("update_restart")){
			JsonHandler.put("update_restart", false);
			JsonHandler.saveConfig();
			DisplayTrayIcon.trayIcon.displayMessage("Spillere.no Anti-Cheat", "Programmet ble oppdatert.", MessageType.INFO);
		} else {
			Process process = Runtime.getRuntime().exec("tasklist /v /FI \"IMAGENAME eq java.exe\"");
			String output = IOUtils.toString(process.getInputStream());
			if (output.contains("Spillere.no Anti-Cheat")){
				output = output.replaceFirst("Spillere.no Anti-Cheat", "");
			}
			if (output.contains("TrayMessageWindow")){
				output = output.replaceFirst("TrayMessageWindow", "");
			}
			if (output.contains("Spillere.no Anti-Cheat") || output.contains("TrayMessageWindow")){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Spillere.no Anti-Cheat");
				alert.setHeaderText("Programmet er allerede oppe!");
				alert.setContentText("Finner du ikke programmet? Du kan se etter en grå hengelås, ikonet skal ligge på oppgavelinjen et sted.");
				alert.showAndWait();
				System.exit(0);
			}
		}
		// Continue
		SocketClient.connect();
		MainWindow.startMainWindow(primaryWindow);
	}

	public static void main(String[] args){
		try {
			// Welcome
			Printer.log("");
			Printer.log("###############");
			Printer.log("#  MCAC v" + version + "  #");
			Printer.log("###############");
			Printer.log("");
			// Check for updates
			Updater.startupUpdate();
			// Handle arguments
			for (String arg : args){
				if (arg.equalsIgnoreCase("-auto")){
					GUI.auto = true;
				} else if (arg.equalsIgnoreCase("-manual")){
					GUI.auto = false;
				}
			}
			// Loading intel
			Intel.os = System.getProperty("os.name");
			Intel.appJarLoc = new File(Client.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			Intel.appDirPath = System.getProperty("user.dir");
			Intel.javaHome = System.getProperty("java.home");
			// Print information
			Printer.log("Java home path: " + Intel.javaHome);
			Printer.log("Operating system: " + Intel.os);
			Printer.log("Applocation jar location: " + Intel.appJarLoc);
			Printer.log("Application folder location: " + Intel.appDirPath);
			if (Intel.os.equals("Windows 7") || Intel.os.equals("Windows 8") || Intel.os.equals("Windows 8.1") || Intel.os.equals("Windows 10")){
				Intel.minecraftPath = new File(Intel.defaultWinMcDir);
				if (!Intel.defaultWinAppDir.equals(Intel.appDirPath)){
					Intel.appDirPath = Intel.defaultWinAppDir;
					Printer.log("Application folder changed to: " + Intel.defaultWinAppDir);
				}
			} else {
				Printer.log("Operation system not supported!");
				if (Intel.appJarLoc.exists() && Intel.appJarLoc.isFile() && !Intel.isWindows()) Intel.appJarLoc.deleteOnExit();
				CriticalError.castError(Intel.os + " støttes ikke!", "Klikk OK for å avslutte programmet.");
			}
			Printer.log("Minecraft path: " + Intel.minecraftPath);
			// Shutdown task
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					if (Intel.updateOnExit){
						Updater.updateCheck();
						Updater.updateIfPossible();
					}
					try {
						SocketClient.socketClient.close();
						Printer.log("Socket connection was successfully closed!");

					} catch (Exception e){
						Printer.log("Failed to close socket connection!");
					}
					Printer.log("Successfully shut down.");
				}
			});
			Intel.updateOnExit = true;
			// Load config
			JsonHandler.loadConfig();
			// Load RSA keys
			RSA.publicKey = RSA.loadPublicKey((String) JsonHandler.get("public_key"));
			RSA.privateKey = RSA.loadPrivateKey((String) JsonHandler.get("private_key"));
			// Open window
			Printer.log("Launching window...");
			launch(args);

		} catch (Exception e) {
			Printer.log(e);
			Printer.log("An unknown error was detected on startup!");
			try {
				CriticalError.castError("En feil ble oppdaget under oppstart!", "Programmet vil prøve å oppdatere, og deretter slå seg av.");
			} catch (Exception ex){
				System.exit(0);
			}
		}
	}
}