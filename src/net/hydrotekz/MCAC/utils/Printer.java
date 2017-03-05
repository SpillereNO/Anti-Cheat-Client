package net.hydrotekz.MCAC.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Printer {
	
	/***************************************************************
	*  Copyright notice
	*
	*  (c) 2016 Spillere.no
	*  All rights reserved
	*
	* It's not allowed to copy, reuse and change the code without the permission from the copyright holder
	*
	********************************/

	public static void log(String message) {
		if (message == null) return;
		System.out.println(message);
	}

	public static void log(Exception e){
		Printer.log("--- ERROR START ---");
		log(getErrorText(e));
		Printer.log("--- ERROR STOP ---");
	}

	public static String getErrorText(Exception e){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return(sw.toString());
	}
}