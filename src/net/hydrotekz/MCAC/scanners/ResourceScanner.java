package net.hydrotekz.MCAC.scanners;

import java.io.File;

import net.hydrotekz.MCAC.net.SocketClient;
import net.hydrotekz.MCAC.utils.Printer;

public class ResourceScanner {

	/***************************************************************
	 *  Copyright notice
	 *
	 *  (c) 2016 Spillere.no
	 *  All rights reserved
	 *
	 * It's not allowed to copy, reuse and change the code without the permission from the copyright holder
	 *
	 ********************************/

	public static void checkResourcepacks(File dir){
		try {
			File folder = new File(dir + "/resourcepacks");
			if (folder.exists()){
				File[] elements = folder.listFiles();
				if (elements != null && elements.length > 0){
					for (File f : elements){
						SocketClient.sendMessage("scan resourcepack " + f.getAbsolutePath().replaceAll(" ", "%0%"), true);
					}
				}
			}

		} catch (Exception e) {
			Printer.log(e);
		}
	}
}