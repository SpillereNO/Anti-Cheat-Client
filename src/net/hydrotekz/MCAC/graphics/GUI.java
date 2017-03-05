package net.hydrotekz.MCAC.graphics;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class GUI {

	/***************************************************************
	 *  Copyright notice
	 *
	 *  (c) 2016 Spillere.no
	 *  All rights reserved
	 *
	 * It's not allowed to copy, reuse and change the code without the permission from the copyright holder
	 *
	 ********************************/

	public static Stage window = null;
	public static boolean auto = false;
	public static Scene dashboard;
	public static Label label3, label4;
	public static StringProperty status, username;
	public static ImageView warningView1, warningView2, logoView;

	public static void setStauts(boolean good){
		if (good){
			label3 = new Label("Kjører");
			status = new Label("Kjører").textProperty();
			label3.textProperty().bind(status);
			label3.getStyleClass().add("goodstatus");
			Image warning1 = new Image("check.png", 32, 32, true, true, true);
			warningView1 = new ImageView();
			warningView1.setImage(warning1);

		} else {
			label3 = new Label("Inaktiv");
			status = new Label("Inaktiv").textProperty();
			label3.textProperty().bind(status);
			label3.getStyleClass().add("badstatus");
			Image warning1 = new Image("triangle.png", 32, 32, true, true, true);
			warningView1 = new ImageView();
			warningView1.setImage(warning1);
		}
	}

	public static void setUsername(String to){
		if (to.equalsIgnoreCase("Ukjent")){
			label4 = new Label("Ukjent");
			username = new Label(to).textProperty();
			label4.textProperty().bind(username);
			label4.getStyleClass().add("badusername");
			Image warning2 = new Image("triangle.png", 32, 32, true, true, true);
			warningView2 = new ImageView();
			warningView2.setImage(warning2);

		} else {
			label4 = new Label(to);
			username = new Label(to).textProperty();
			label4.textProperty().bind(username);
			label4.getStyleClass().add("goodusername");
			Image warning2 = new Image("check.png", 32, 32, true, true, true);
			warningView2 = new ImageView();
			warningView2.setImage(warning2);
		}
	}

	public static void changeUsername(String to){
		Platform.runLater(() -> {
			username.set(to);
		});
	}
}