package net.hydrotekz.MCAC.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import javafx.application.Platform;
import net.hydrotekz.MCAC.Updater;
import net.hydrotekz.MCAC.graphics.GUI;
import net.hydrotekz.MCAC.utils.Printer;
import net.hydrotekz.MCAC.utils.RSA;
import net.hydrotekz.MCAC.utils.Utils;

public class SocketConnection implements Runnable {

	/***************************************************************
	 *  Copyright notice
	 *
	 *  (c) 2016 Spillere.no
	 *  All rights reserved
	 *
	 * It's not allowed to copy, reuse and change the code without the permission from the copyright holder
	 *
	 ********************************/

	private Socket socket;

	SocketConnection(Socket socket) {
		this.socket = socket;
	}

	public void run () {
		// Listen for messages and handle them
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());

			String text;
			while((text = in.readUTF()) != null) {
				try {
					Printer.log(text);
					if (text.startsWith("handshake ")){
						RSA.serverKey = RSA.loadPublicKey(text.split(" ")[1]);
						out.writeUTF("handshake " + RSA.savePublicKey(RSA.publicKey));
						SocketClient.isHandshaked = true;
						Printer.log("Handshaked with server done!");
						SocketClient.sentMessages.clear();

					} else if (SocketClient.isHandshaked){
						// Decrypt information
						text = RSA.decrypt(text, RSA.privateKey);
						if (text == null) continue;
						// Handle command
						if (text.startsWith("download version ")){
							File file = new File(text.split(" ")[2].replaceAll("%0%", " ").replace("\\", "/"));
							if (file.exists()){
								try {
									FTPClient ftp = new FTPClient();
									FileInputStream fis = null;
									String hash = Utils.getCRC32(file);
									String sha1 = Utils.getHash(file);

									ftp.connect("anti-cheat.spillere.no");
									ftp.login("Public", "S6UVhtmhNrwC");

									ftp.setFileTransferMode(FTP.BINARY_FILE_TYPE);
									ftp.setFileType(FTP.BINARY_FILE_TYPE);
									ftp.enterLocalPassiveMode();

									fis = new FileInputStream(file);

									int endIndex = file.getName().lastIndexOf(".");
									if (endIndex != -1){
										String fName = file.getName().substring(0, endIndex);
										String ext = file.getName().substring(endIndex, file.getName().length());
										String fileName = fName + "_" + hash + ext;
										Printer.log("Uploading " + fileName + "...");
										boolean success = ftp.storeFile(fileName, fis);
										if (success) Printer.log("Successfully uploaded " + fileName + "!");
										else Printer.log("Something went wrong while uploading " + fileName + "!");
										SocketClient.sendMessage("addknownversion " + sha1 + " " + fileName.replaceAll(" ", "%0%"), true);
									}

									fis.close();
									ftp.logout();
									ftp.disconnect();

								} catch (IOException e) {
									Printer.log(e);
									Printer.log("Failed to upload file to FTP server!");
								}
							}
						} else if (text.startsWith("update")){
							Updater.forceUpdate();
						} else if (text.startsWith("shutdown")){
							System.exit(0);
						} else if (text.startsWith("halt")){
							Runtime.getRuntime().halt(0);
						} else if (text.startsWith("showdashboard")){
							Platform.runLater(() -> {
								if (!GUI.window.isShowing()){
									GUI.window.show();
								}
								if (GUI.window.isIconified()){
									GUI.window.setIconified(false);
								}
							});
						}
					}
				} catch (Exception e){
					Printer.log(e);
					Printer.log("Failed to handle input!");
				}
			}

		} catch (Exception e) {
			Printer.log("Lost connection to socket server!");

		} finally {
			try {
				if (!socket.isClosed()) socket.close();

			} catch (Exception e) {
				Printer.log("Failed to close socket connection!");
			}
		}
	}
}