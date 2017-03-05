package net.hydrotekz.MCAC.scanners;

import java.io.File;
import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import net.hydrotekz.MCAC.Client;
import net.hydrotekz.MCAC.Intel;
import net.hydrotekz.MCAC.graphics.GUI;
import net.hydrotekz.MCAC.handlers.JsonHandler;
import net.hydrotekz.MCAC.net.SocketClient;
import net.hydrotekz.MCAC.utils.Printer;

public class DataCollector {

	/***************************************************************
	 *  Copyright notice
	 *
	 *  (c) 2016 Spillere.no
	 *  All rights reserved
	 *
	 * It's not allowed to copy, reuse and change the code without the permission from the copyright holder
	 *
	 ********************************/

	// Update UUID and username selected in the launcher @Deprecated
	public static void updateLauncherIntel(){
		try {
			// Scan json data
			File jsonFile = new File(Intel.minecraftPath + "/launcher_profiles.json");
			if (jsonFile.exists()){
				FileReader reader = new FileReader(jsonFile);
				JSONObject json = (JSONObject) new JSONParser().parse(reader);
				if (json.containsKey("selectedUser") && json.get("selectedUser") instanceof String){
					String user = (String) json.get("selectedUser");
					// Continue
					if (json.containsKey("authenticationDatabase") && json.get("authenticationDatabase") instanceof JSONObject){
						JSONObject authenticationDatabase = (JSONObject) json.get("authenticationDatabase");
						if (authenticationDatabase.containsKey(user) && authenticationDatabase.get(user) instanceof JSONObject){
							JSONObject profile = (JSONObject) authenticationDatabase.get(user);
							if (profile.containsKey("displayName") && profile.get("displayName") instanceof String){
								Intel.username = (String) profile.get("displayName");
								GUI.changeUsername(Intel.username);
								if (profile.containsKey("uuid") && profile.get("uuid") instanceof String){
									String uuid = ((String) profile.get("uuid")).replaceAll("-", "");
									SocketClient.sendMessage("login " + JsonHandler.get("client_id") + " " + Client.version, true);
									SocketClient.sendMessage("auth " + uuid + " " + Intel.username, true);
									Intel.addGameDir(Intel.minecraftPath.getAbsolutePath());
								} else Printer.log("Failed to get uuid!");
							} else Printer.log("Failed to get displayName!");
						} else Printer.log("Failed to get profile!");
					} else Printer.log("Failed to get authenticationDatabase!");
				} else Printer.log("Failed to get selectedUser!");
			} else Printer.log("Failed to find launcher_profiles.json!");

		} catch (Exception e){
			Printer.log(e);
		}
	}
}