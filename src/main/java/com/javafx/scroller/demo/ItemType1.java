package com.javafx.scroller.demo;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.StackPaneBuilder;

public class ItemType1 extends HBox{
	private Hyperlink deleteButton;
	public ItemType1(String str){
		super();
		if(str==null){
			throw new UnsupportedOperationException("Name cannot be null for the item");
		}
		setUserData(str); // **Mandatory
		setMinWidth(150);
		setMinHeight(26);
		setAlignment(Pos.CENTER_LEFT);
		getStyleClass().add("item-type-1");
		
		deleteButton = new Hyperlink("X");
        deleteButton.setStyle("-fx-padding: 0; -fx-text-fill: blue");
        StackPane sp = StackPaneBuilder.create().alignment(Pos.CENTER_RIGHT).children(deleteButton).build();
		getChildren().addAll(new Label(str),sp);
        HBox.setHgrow(sp, Priority.ALWAYS);
	}
	
	public void setDeleteAction(EventHandler<ActionEvent> event){
		deleteButton.setOnAction(event);
	}
}
