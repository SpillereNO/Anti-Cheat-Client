package net.hydrotekz.MCAC.handlers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.KeyPair;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import net.hydrotekz.MCAC.Client;
import net.hydrotekz.MCAC.Intel;
import net.hydrotekz.MCAC.utils.Printer;
import net.hydrotekz.MCAC.utils.RSA;
import net.hydrotekz.MCAC.utils.Utils;

public class JsonHandler {

	private static JSONParser parser = new JSONParser();
	private static JSONObject json = new JSONObject();
	private static File file = new File(Intel.appDirPath + File.separator + "Client.json");

	public static void loadConfig(){
		try {
			if (file.exists()){
				Object obj = parser.parse(new FileReader(file));
				json = (JSONObject) obj;
			}

		} catch (Exception e) {
			Printer.log("Failed to load config!");
			Printer.log(e);
		}
		generateConfig();
	}

	public static void saveConfig(){
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(json.toJSONString());
			writer.flush();
			writer.close();

		} catch (Exception e) {
			Printer.log("Failed to save config!");
			Printer.log(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static void generateConfig(){
		if (!json.containsKey("client_id")) json.put("client_id", Utils.getRandomString(16));
		if (!json.containsKey("reopen_at_last_loc")) json.put("reopen_at_last_loc", false);
		if (!json.containsKey("public_key") || !json.containsKey("private_key")){
			generateKeys();
		}
		json.put("version", Client.version);
		saveConfig();
	}

	@SuppressWarnings("unchecked")
	public static void generateKeys(){
		// Load RSA keys
		try {
			long time = System.currentTimeMillis();
			KeyPair keyPair = RSA.generateKeys();
			json.put("public_key", RSA.savePublicKey(keyPair.getPublic()));
			json.put("private_key", RSA.savePrivateKey(keyPair.getPrivate()));
			Printer.log("RSA keys generated in " + (System.currentTimeMillis()-time) + "ms!");
		} catch (Exception e){
			Printer.log("Failed to generate RSA keys!");
		}
	}
	
	public static void regenerateKeys(){
		generateKeys();
		saveConfig();
	}

	public static boolean containsKey(String key){
		return json.containsKey(key);
	}

	@SuppressWarnings("unchecked")
	public static void put(Object key, Object value){
		json.put(key, value);
	}

	public static Object get(String key){
		return json.get(key);
	}
}