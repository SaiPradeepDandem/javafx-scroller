package com.javafx.scroller.demo;

import com.sun.javafx.Utils;

import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class AppLabel extends Label{
	 public AppLabel(){
		 super();
		 configure();
	 }
	 public AppLabel(String str){
		 super(str);
		 configure();
	 }
	 private void configure(){
		 String file = Utils.isMac()? "handwriting_mac.ttf" : "handwriting_win.ttf";
		 super.setFont(Font.loadFont(getClass().getResourceAsStream("/com/javafx/scroller/demo/"+file), 14));
	 }
}
