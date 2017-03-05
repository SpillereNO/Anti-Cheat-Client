package net.hydrotekz.MCAC;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Intel {

	/***************************************************************
	 *  Copyright notice
	 *
	 *  (c) 2016 Spillere.no
	 *  All rights reserved
	 *
	 * It's not allowed to copy, reuse and change the code without the permission from the copyright holder
	 *
	 ********************************/

	/*
	 * Functions
	 */

	public static void addGameDir(String path){
		File file = new File(path);
		if (file.exists() && !gameDirs.contains(file)){
			gameDirs.add(file);
		}
	}
	
	/*
	 * Settings
	 */
	
	public static boolean updateOnExit = false;

	/*
	 * Essentials
	 */

	public static List<File> gameDirs = new ArrayList<File>();
	public static HashMap<String, String> uuids = new HashMap<String, String>();
	public static File appJarLoc = null;
	public static String appDirPath = null;

	/*
	 * Needed
	 */

	public static String username = "Ukjent";
	public static File minecraftPath = null;
	public static String os = null;
	public static String javaHome;

	/*
	 * Temporary
	 */

	public static String defaultWinAppDir = System.getenv("APPDATA") + File.separator + "SAC";

	public static String defaultUnixMcDir = System.getProperty("user.home") + File.separator + ".minecraft";
	public static String defaultWinMcDir = System.getenv("APPDATA") + File.separator + ".minecraft";
	public static String defaultMacMcDir = System.getProperty("user.home") + File.separator + "Library" + File.separator
			+ "Application Support" + File.separator + "minecraft";

	/*
	 * Operating systems
	 */

	public static boolean isWindows() {
		return (os.toLowerCase().indexOf("win") >= 0);
	}

	public static boolean isMac() {
		return (os.toLowerCase().indexOf("mac") >= 0);
	}

	public static boolean isUnix() {
		return (os.toLowerCase().indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0 );
	}

	public static boolean isSolaris() {
		return (os.toLowerCase().indexOf("sunos") >= 0);
	}
}