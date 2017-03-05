package net.hydrotekz.MCAC.net;

import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import net.hydrotekz.MCAC.Client;
import net.hydrotekz.MCAC.handlers.JsonHandler;
import net.hydrotekz.MCAC.scanners.DataCollector;
import net.hydrotekz.MCAC.utils.Printer;
import net.hydrotekz.MCAC.utils.RSA;

public class SocketClient {

	/***************************************************************
	 *  Copyright notice
	 *
	 *  (c) 2016 Spillere.no
	 *  All rights reserved
	 *
	 * It's not allowed to copy, reuse and change the code without the permission from the copyright holder
	 *
	 ********************************/

	public static Socket socketClient;
	public static boolean isHandshaked = false;
	private static String ip = "anti-cheat.spillere.no";
	private static int port = 443;

	private static long recon = 0;

	public static List<String> sentMessages = new ArrayList<String>();

	public static void connect(){
		try {
			socketClient = new Socket();
			socketClient.connect(new InetSocketAddress(ip, port), 30000);
			SocketConnection connection = new SocketConnection(socketClient);
			Thread t = new Thread(connection);
			t.start();
			sentMessages.clear();
			sendMessage("login " + JsonHandler.get("client_id") + " " + Client.version, true);
			Printer.log("Successfully connected to socket server!");
			DataCollector.updateLauncherIntel();

		} catch (Exception e) {
			Printer.log("Failed to connect to socket server!");
		}
	}

	public static void reconnect(){
		try {
			if (recon == 0 || System.currentTimeMillis() > recon){
				socketClient = new Socket();
				socketClient.connect(new InetSocketAddress(ip, port), 10000);
				SocketConnection connection = new SocketConnection(socketClient);
				Thread t = new Thread(connection);
				t.start();
				sentMessages.clear();
				sendMessage("login " + JsonHandler.get("client_id") + " " + Client.version, true);
				Printer.log("Successfully reconnected to socket server!");
				DataCollector.updateLauncherIntel();
				sentMessages.clear();
				recon = System.currentTimeMillis()+30000;
			}

		} catch (Exception e) {
			Printer.log("Failed to reconnect to socket server!");
		}
	}

	public static boolean sendMessage(String msg, boolean print){
		try {
			if (socketClient != null && !socketClient.isClosed() && socketClient.isConnected()){
				DataOutputStream os = new DataOutputStream(socketClient.getOutputStream());
				if (os != null && !socketClient.isInputShutdown()){
					if (isHandshaked){
						if (!sentMessages.contains(msg)){
							String encrypted = RSA.encrypt(msg, RSA.serverKey);
							os.writeUTF(encrypted);
							sentMessages.add(msg);
							if (print) Printer.log("Socket message successfully sent: " + msg);
							return true;
						} else if (print){
							Printer.log("Socket message already sent: " + msg);
						}
					} else {
						Printer.log("Could not send message, wait for the handshake to complete.");
					}
				} else {
					Printer.log("Failed to send socket to message: " + msg);
					reconnect();
				}
			} else {
				Printer.log("Failed to send socket to message: " + msg);
				reconnect();
			}
		} catch (Exception e){
			Printer.log("Failed to send socket to message: " + msg);
			reconnect();
		}
		return false;
	}
}