package com.javafx.scroller.demo;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class ItemType2 extends StackPane{
	public ItemType2(String str){
		super();
		if(str==null){
			throw new UnsupportedOperationException("Name cannot be null for the item");
		}
		setUserData(str); // **Mandatory
		setMinSize(100, 80);
		setAlignment(Pos.CENTER);
		getStyleClass().add("item-type-2");
		getChildren().addAll(new Label(str));
    }
}
