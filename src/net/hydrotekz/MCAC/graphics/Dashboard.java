package net.hydrotekz.MCAC.graphics;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import net.hydrotekz.MCAC.Intel;
import net.hydrotekz.MCAC.scanners.DataCollector;
import net.hydrotekz.MCAC.scanners.FileScanner;

public class Dashboard {

	/***************************************************************
	 *  Copyright notice
	 *
	 *  (c) 2016 Spillere.no
	 *  All rights reserved
	 *
	 * It's not allowed to copy, reuse and change the code without the permission from the copyright holder
	 *
	 ********************************/

	public static BorderPane setupDashboard(){
		// GridPane
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 0, 10, 10));
		grid.setVgap(8);
		grid.setHgap(10);

		//HBox
		HBox buttomMenu = new HBox();
		buttomMenu.setPadding(new Insets(10, 10, 0, 10));

		Image logo = new Image("logo.png");
		GUI.logoView = new ImageView();
		GUI.logoView.setImage(logo);

		HBox.setMargin(GUI.logoView, new Insets(10, 10, 0, 10));

		// Items
		Label label1 = new Label("	Anti-Cheat status:");
		Label label2 = new Label("	Ditt brukernavn:");

		// GUI
		FileScanner.startFileScanner();
		DataCollector.updateLauncherIntel();
		GUI.setUsername(Intel.username);

		// Placements
		GridPane.setConstraints(label1, 0, 4);
		GridPane.setConstraints(GUI.label3, 2, 4);
		GridPane.setConstraints(GUI.warningView1, 3, 4);
		GridPane.setConstraints(label2, 0, 8);
		GridPane.setConstraints(GUI.label4, 2, 8);
		GridPane.setConstraints(GUI.warningView2, 3, 8);

		// Return
		grid.getChildren().addAll(label1, label2, GUI.label3, GUI.label4, GUI.warningView1, GUI.warningView2);
		buttomMenu.getChildren().addAll(GUI.logoView);
		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(grid);
		borderPane.setTop(buttomMenu);
		return borderPane;
	}
}