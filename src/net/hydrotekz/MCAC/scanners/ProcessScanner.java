package net.hydrotekz.MCAC.scanners;

import java.io.File;
import java.util.List;

import org.jutils.jprocesses.JProcesses;
import org.jutils.jprocesses.model.ProcessInfo;

import net.hydrotekz.MCAC.Client;
import net.hydrotekz.MCAC.Intel;
import net.hydrotekz.MCAC.handlers.JsonHandler;
import net.hydrotekz.MCAC.net.SocketClient;
import net.hydrotekz.MCAC.utils.Printer;
import net.hydrotekz.MCAC.utils.Utils;

public class ProcessScanner {

	/***************************************************************
	 *  Copyright notice
	 *
	 *  (c) 2016 Spillere.no
	 *  All rights reserved
	 *
	 * It's not allowed to copy, reuse and change the code without the permission from the copyright holder
	 *
	 ********************************/

	public static int updateIntelOfClients(){
		int i = 0;
		try {
			Printer.log("Scanning processes...");
			long time = System.currentTimeMillis();
			List<ProcessInfo> processesList = JProcesses.getProcessList();
			int processes = 0;
			for (final ProcessInfo processInfo : processesList) {
				String cmd = processInfo.getCommand();
				String name = processInfo.getName();
				if (name.startsWith("java")){
					if (cmd.contains("--gameDir")){
						String gameDir = getArgument(cmd, "--gameDir");
						String username = getArgument(cmd, "--username");
						String uuid = getArgument(cmd, "--uuid");
						if (gameDir != null && uuid != null && username != null){
							Intel.uuids.put(uuid, username);
							Intel.addGameDir(gameDir);
							SocketClient.sendMessage("login " + JsonHandler.get("client_id") + " " + Client.version, true);
							SocketClient.sendMessage("auth " + uuid + " " + username, true);
							i++;
						}
					}
				}
				if (cmd.startsWith("\"")){
					String path = cmd.split("\"")[1];
					File file = new File(path);
					String hash = Utils.getHash(file);
					SocketClient.sendMessage("scan process " + file.getAbsolutePath().replaceAll(" ", "%0%") + " " + hash, false);
				}
				processes++;
			}
			Printer.log(processes + " was scanned in " + (System.currentTimeMillis()-time) + "ms!");

		} catch (Exception e){
			Printer.log(e);
		}
		return i;
	}

	private static String getArgument(String cmd, String arg){
		String[] split = cmd.split(" ");
		String value = null;
		for (String s : split){
			if (value == null){
				if (s.equalsIgnoreCase(arg)){
					value = "";
				}
			} else if (s.equalsIgnoreCase("") || !s.startsWith("-")){
				value+=s;
			} else {
				break;
			}
		}
		return value;
	}
}