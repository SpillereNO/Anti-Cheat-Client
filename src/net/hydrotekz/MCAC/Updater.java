package net.hydrotekz.MCAC;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import net.hydrotekz.MCAC.graphics.GUI;
import net.hydrotekz.MCAC.handlers.JsonHandler;
import net.hydrotekz.MCAC.utils.Printer;

public class Updater {

	/***************************************************************
	 *  Copyright notice
	 *
	 *  (c) 2016 Spillere.no
	 *  All rights reserved
	 *
	 * It's not allowed to copy, reuse and change the code without the permission from the copyright holder
	 *
	 ********************************/

	private static double latestVersion = 0.00;
	private static String updateLink = "https://dl.dropboxusercontent.com/u/77919443/Applications/Anti-Cheat/";
	private static boolean forceUpdating = false;

	public static void forceUpdate() {
		Runnable r = new Runnable() {
			public void run() {
				try {
					if (!forceUpdating){
						forceUpdating = true;
						Thread.sleep(3000);
						while(GUI.window != null){
							try {
								Printer.log("Force updating...");
								JsonHandler.put("update_restart", true);
								JsonHandler.put("window_location_x", GUI.window.getX());
								JsonHandler.put("window_location_y", GUI.window.getY());
								JsonHandler.put("window_iconified", GUI.window.isIconified());
								JsonHandler.put("window_showing", GUI.window.isShowing());
								JsonHandler.put("reopen_at_last_loc", true);
								JsonHandler.saveConfig();
								Thread.sleep(500);
								updateCheck();
								updateIfPossible();
								Runtime rt = Runtime.getRuntime();
								String cmd = Intel.appDirPath + "\\Anti-Cheat.exe";
								rt.exec(cmd);
								Printer.log("Update finished, executing: " + cmd);
								System.exit(0);

							} catch (Exception e){
								Printer.log(e);
								break;
							}
						}
					}

				} catch (Exception e){
					Printer.log(e);
				}
			}
		};
		new Thread(r).start();
	}

	public static void startupUpdate(){
		try {
			updateCheck();
			if (latestVersion > Client.version){
				Printer.log("Updating...");
				JsonHandler.put("update_restart", true);
				JsonHandler.put("window_location_x", GUI.window.getX());
				JsonHandler.put("window_location_y", GUI.window.getY());
				JsonHandler.put("window_iconified", GUI.window.isIconified());
				JsonHandler.put("window_showing", GUI.window.isShowing());
				JsonHandler.put("reopen_at_last_loc", true);
				JsonHandler.saveConfig();
				Thread.sleep(500);
				updateApplication(latestVersion);
				Runtime rt = Runtime.getRuntime();
				String cmd = Intel.appDirPath + "\\Anti-Cheat.exe";
				rt.exec(cmd);
				Printer.log("Update finished, executing: " + cmd);
			}
		} catch (Exception e){
			Printer.log("Error detected while updating!");
			Printer.log(e);
		}
	}

	public static void updateCheck(){
		double latest = latestVersion;
		updateLatestVersion();
		if (latestVersion > latest){
			Printer.log("Update found! Current version: " + Client.version + " Latest version: " + latestVersion);
		}
	}

	public static void updateIfPossible(){
		try {
			if (latestVersion > Client.version){
				updateApplication(latestVersion);
			}
		} catch (Exception e){
			Printer.log("Error detected while updating!");
			Printer.log(e);
		}
	}

	private static void updateLatestVersion(){
		double latest = 0.00;
		try {
			URL url = new URL(updateLink + "Update.txt");

			Scanner s = new Scanner(url.openStream());
			StringBuilder text = new StringBuilder();
			while (s.hasNext()){
				String append = s.next();
				if (s.hasNext()) append += " ";
				text.append(append);
			}
			s.close();
			String output = text.toString();
			if (output != null && !output.isEmpty() && output.length() > 2){
				latest = Double.parseDouble(output);
			}
		} catch(Exception ex) {}
		if (latest > Client.version) latestVersion = latest;
	}

	private static void updateApplication(double version){
		try {
			Printer.log("Downloading update...");
			URL url = new URL(updateLink + "Anti-Cheat v" + version + ".jar");
			HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
			https.setRequestMethod("GET");
			https.connect();
			int code = https.getResponseCode();
			if (code == 200){
				//				if (Intel.appJarLoc.exists()){
				ReadableByteChannel rbc = Channels.newChannel(url.openStream());
				FileOutputStream fos = new FileOutputStream(Intel.appJarLoc.getAbsolutePath().replaceAll("%20", " ").replaceAll("%40", "@"));
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				https.disconnect();
				fos.close();
				rbc.close();
				Printer.log("Update downloaded!");
				Printer.log("");
				Client.version = version;
				JsonHandler.put("version", version);

				//				} else {
				//					Printer.log("Update aborted: File not found!");
				//				}
			} else {
				Printer.log("Update aborted, unsafe response code: " + code);
			}
		} catch (Exception e){
			Printer.log("Error detected, failed to download update!");
			Printer.log(e);
		}
	}
}