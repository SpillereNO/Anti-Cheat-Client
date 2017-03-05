package net.hydrotekz.MCAC.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Random;
import java.util.zip.CRC32;

import net.hydrotekz.MCAC.Client;
import net.hydrotekz.MCAC.graphics.CriticalError;

public class Utils {

	/***************************************************************
	 *  Copyright notice
	 *
	 *  (c) 2016 Spillere.no
	 *  All rights reserved
	 *
	 * It's not allowed to copy, reuse and change the code without the permission from the copyright holder
	 *
	 ********************************/

	public static File refresh(File f){
		return new File(f.getAbsolutePath());
	}

	public static String getRandomString(int length){
		String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder(length);
		for(int i = 0; i < length; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
		return sb.toString();
	}

	public static String getCRC32(File file){
		try {
			CRC32 hash = new CRC32();
			try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
				final byte[] buffer = new byte[1024];
				for (int read = 0; (read = is.read(buffer)) != -1;) {
					hash.update(buffer, 0, read);
				}
			}
			return Long.toHexString(hash.getValue());

		} catch (Exception e){
			Printer.log("Failed to hash file: " + file.getName());
			Printer.log(e);
		}
		return null;
	}

	public static String getHash(final File file) {
		try {
			final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");

			try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
				final byte[] buffer = new byte[1024];
				for (int read = 0; (read = is.read(buffer)) != -1;) {
					messageDigest.update(buffer, 0, read);
				}
			}

			// Convert the byte to hex format
			try (Formatter formatter = new Formatter()) {
				for (final byte b : messageDigest.digest()) {
					formatter.format("%02x", b);
				}
				return formatter.toString();
			}
		} catch (Exception e){
			Printer.log("Failed to hash file: " + file.getName());
			Printer.log(e);
		}
		return null;
	}

	public static boolean isInUse(File file){
		return !file.renameTo(file);
	}

	public static void restartApplication() {
		try {
			final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
			final File currentJar = new File(Client.class.getProtectionDomain().getCodeSource().getLocation().toURI());

			/* is it a jar file? */
			if(!currentJar.getName().endsWith(".jar"))
				return;

			/* Build command: java -jar application.jar */
			final ArrayList<String> command = new ArrayList<String>();
			command.add(javaBin);
			command.add("-jar");
			command.add(currentJar.getPath());

			final ProcessBuilder builder = new ProcessBuilder(command);
			builder.start();
			System.exit(0);

		} catch (Exception e){
			Printer.log(e);
			Printer.log("Failed to restart application!");
			try {
				CriticalError.castError("En feil ble oppdaget under restart!", "Programmet vil prøve å oppdatere, og deretter slå seg av.");
			} catch (Exception ex){
				System.exit(0);
			}
		}
	}
}