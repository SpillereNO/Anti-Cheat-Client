package net.hydrotekz.MCAC.scanners;

import java.io.File;

import net.hydrotekz.MCAC.Intel;
import net.hydrotekz.MCAC.graphics.GUI;
import net.hydrotekz.MCAC.net.SocketClient;
import net.hydrotekz.MCAC.utils.Printer;
import net.hydrotekz.MCAC.utils.Utils;

public class FileScanner {

	/***************************************************************
	 *  Copyright notice
	 *
	 *  (c) 2016 Spillere.no
	 *  All rights reserved
	 *
	 * It's not allowed to copy, reuse and change the code without the permission from the copyright holder
	 *
	 ********************************/

	// Analyzer
	public static void analyzer(File file, File gameDir){
		if (file.isFile() && !file.isDirectory() && file.getName().endsWith(".jar")){
			try {
				String hash = Utils.getHash(file);
				File versions = new File(gameDir.getAbsolutePath() + "/versions");
				if (file.getAbsolutePath().startsWith(versions.getAbsolutePath())){
					SocketClient.sendMessage("scan version " + file.getAbsolutePath().replaceAll(" ", "%0%") + " " + hash + " " + Utils.isInUse(file), true);

				} else {
					SocketClient.sendMessage("scan other " + file.getAbsolutePath().replaceAll(" ", "%0%") + " " + hash + " " + Utils.isInUse(file), false);
				}

			} catch (Exception e){
				Printer.log("Failed to hash file: " + file.getName());
				Printer.log(e);
			}

		} else if (file.isDirectory()){
			for (File f : file.listFiles()){
				analyzer(Utils.refresh(f), gameDir);
			}
		}
	}


	// Scan for files
	public static void startFileScanner(){
		try {
			GUI.setStauts(true);
			Runnable r = new Runnable() {
				public void run() {
					fileScanner();
				}
			};
			new Thread(r).start();

		} catch (Exception e){
			Printer.log("Failed to start file scanner!");
			Printer.log(e);
			GUI.setStauts(false);
		}
	}

	public static void fileScanner(){
		try {
			Thread.sleep(5000);
			Printer.log("");
			Printer.log("Updating game directories...");

			long time = System.currentTimeMillis();
			DataCollector.updateLauncherIntel();
			int i = ProcessScanner.updateIntelOfClients();

			Printer.log("Game directories updated, time: " + (System.currentTimeMillis()-time)/1000 + " seconds!");
			Printer.log("");
			if (i <= 0) Thread.sleep(45000);
			else Thread.sleep(15000);
			Printer.log("Scanning and hashing files...");

			time = System.currentTimeMillis();
			for (File dir : Intel.gameDirs){
				analyzer(dir, dir);
				ResourceScanner.checkResourcepacks(dir);
			}

			Printer.log("Scan finished, time: " + (System.currentTimeMillis()-time)/1000 + " seconds!");
			Printer.log("");
			Thread.sleep(15000);

		} catch (Exception e){
			Printer.log(e);
		}
		fileScanner();
	}
}