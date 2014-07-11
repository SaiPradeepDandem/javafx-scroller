package com.javafx.scroller.demo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPaneBuilder;
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.StackPaneBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.Stage;

import com.javafx.scroller.control.Scroller;
import com.sun.javafx.scene.traversal.Direction;

public class ScrollerDemoApplication extends Application {

	Stage primaryStage;
	Scene scene;
	BorderPane root;
	VBox layout;
	int itemCnt =1;
	int count_1 = 15;
	int count_2 = 15;
	int count_ItemType1 = 15;
	int count_ItemType2 = 15;
	
	@Override
	public void start(Stage arg0) throws Exception {
		this.primaryStage = arg0;
		this.root = new BorderPane();
		this.scene = new Scene(root, 1140.0D, 650.0D);
		this.scene.getStylesheets().add("com/javafx/scroller/demo/scrollerdemo.css");
		this.primaryStage.setScene(this.scene);
		
		configureHeader();
		configureFooter();
		configureCenter();
		
		configureBasicScroller();
		configureBasicScrollerWithDownMenu();
		configureBasicScrollerWithTopMenu();
		configureItemType1();
		configureItemType2();
		
		this.primaryStage.show();
	}

	private void configureHeader() {
		ImageView bgImage = new ImageView(new Image(getClass().getResourceAsStream("/com/javafx/scroller/demo/background.png")));
		Text header = TextBuilder.create().text("JavaFX Scroller")
								   .styleClass("app-header-text")
								   .translateX(10)
								   .translateY(25).build();
		Bloom bloom = new Bloom();
        bloom.setThreshold(0.3);
        header.setEffect(bloom);
        
		StackPane sp = StackPaneBuilder.create()
									   .children(bgImage,header)
									   .styleClass("app-header")
									   .prefHeight(100).build();
		root.setTop(sp);
	}

	private void configureFooter() {
		Label footer = LabelBuilder.create().text("Developed by a JavaFX Enthusiast :)")
								   .styleClass("app-footer-text").build();

		StackPane sp = StackPaneBuilder.create()
									   .styleClass("app-footer")
									   .children(footer)
									   .prefHeight(20).build();
		root.setBottom(sp);
	}
	
	private void configureCenter() {
		this.layout = VBoxBuilder.create().spacing(10).padding(new Insets(10)).build();
		ScrollPane scrollPane = ScrollPaneBuilder.create().styleClass("center-node")
												 .content(this.layout)
												 .styleClass("center-bg")
												 .fitToHeight(true)
												 .fitToWidth(true).build();
		scrollPane.setContent(layout);
		root.setCenter(scrollPane);
	}

	/**
	 * Method to configure the basic Scroller without menu.
	 */
	private void configureBasicScroller(){
		VBox vb = VBoxBuilder.create().spacing(10).padding(new Insets(10,0,10,0)).build();
		AppLabel lbl = new AppLabel("#"+(itemCnt++)+" : Basic Scroller with no menu button");
		lbl.getStyleClass().add("head-label");
		
		final Scroller<Button> scroller = new Scroller<Button>();
		scroller.setShowMenu(false);
		scroller.setSpacing(10D);
		scroller.setItems(getButtonMockData(count_1,125));
		
		Button add = new Button("Add Button");
		add.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent paramT) {
				String txt = "Demo Button "+(count_1++);
				scroller.getItems().add(ButtonBuilder.create().text(txt).userData(txt).minWidth(125).build());
			}
		});
		
		Button delete = new Button("Delete Button");
		delete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent paramT) {
				if(scroller.getItems().size()>0){
					scroller.getItems().remove(scroller.getItems().get(scroller.getItems().size()-1));
				}
			}
		});
		
		HBox hb = HBoxBuilder.create().spacing(20).children(add, delete).build();
		vb.getChildren().addAll(lbl, hb,scroller);
		this.layout.getChildren().add(vb);
	}
	
	/**
	 * Method to configure the Scroller with menu direction as "DOWN".
	 */
	private void configureBasicScrollerWithDownMenu(){
		VBox vb = VBoxBuilder.create().spacing(10).padding(new Insets(10,0,10,0)).build();
		AppLabel lbl = new AppLabel("#"+(itemCnt++)+" : Basic Scroller with menu direction to bottom");
		lbl.getStyleClass().add("head-label");
		
		final Scroller<Button> scroller = new Scroller<Button>();
		scroller.setSpacing(10D);
		scroller.setDefaultMenuItemsCount(5);
		scroller.setItems(getButtonMockData(count_2,125));
		
		Button add = new Button("Add Button");
		add.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent paramT) {
				String txt = "Demo Button "+(count_2++);
				scroller.getItems().add(ButtonBuilder.create().text(txt).userData(txt).minWidth(125).build());
			}
		});
		
		Button delete = new Button("Delete Button");
		delete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent paramT) {
				if(scroller.getItems().size()>0){
					scroller.getItems().remove(scroller.getItems().get(scroller.getItems().size()-1));
				}
			}
		});
		
		HBox hb = HBoxBuilder.create().spacing(20).children(add, delete).build();
		vb.getChildren().addAll(lbl, hb,scroller);
		this.layout.getChildren().add(vb);
	}
	
	/**
	 * Method to configure the Scroller with menu direction as "UP".
	 */
	private void configureBasicScrollerWithTopMenu(){
		VBox vb = VBoxBuilder.create().spacing(10).padding(new Insets(10,0,10,0)).build();
		AppLabel lbl = new AppLabel("#"+(itemCnt++)+" : Basic Scroller with menu direction to top and setting some maxwidth");
		lbl.getStyleClass().add("head-label");
		
		final Scroller<Button> scroller = new Scroller<Button>();
		scroller.setMaxWidth(550);
		scroller.setSpacing(10D);
		scroller.setMenuDirection(Direction.UP);
		scroller.setItems(getButtonMockData(count_2,125));
		
		Button add = new Button("Add Button");
		add.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent paramT) {
				String txt = "Demo Button "+(count_2++);
				scroller.getItems().add(ButtonBuilder.create().text(txt).userData(txt).minWidth(125).build());
			}
		});
		
		Button delete = new Button("Delete Button");
		delete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent paramT) {
				if(scroller.getItems().size()>0){
					scroller.getItems().remove(scroller.getItems().get(scroller.getItems().size()-1));
				}
			}
		});
		
		HBox hb = HBoxBuilder.create().spacing(20).children(add, delete).build();
		vb.getChildren().addAll(lbl, hb,scroller);
		this.layout.getChildren().add(vb);
	}
	
	/**
	 * Returns the list of Buttons for the Scroller.
	 * @param cnt - no of buttons need to be added.
	 * @param width - Minimum width of the button.
	 * @return ObservableList<Node>
	 */
	private ObservableList<Button> getButtonMockData(int cnt, double width){
		ObservableList<Button> list = FXCollections.observableArrayList();
		for (int i = 1; i < cnt; i++) {
			String txt = "Demo Button "+i;
			final Button btn =ButtonBuilder.create().text(txt).userData(txt).minWidth(width).build();
			btn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent paramT) {
					System.out.println(btn.getText()+" is clicked.");
				}
			});
			list.add(btn);
		}
		return list;
	}
	
	/**
	 * Method to configure the Scroller by adding the "ItemType1" as its items.
	 */
	private void configureItemType1(){
		VBox vb = VBoxBuilder.create().spacing(10).padding(new Insets(10,0,10,0)).build();
		AppLabel lbl = new AppLabel("#"+(itemCnt++)+" : Customised Scroller with menu direction to top");
		lbl.getStyleClass().add("head-label");
		
		final Scroller<Node> scroller = new Scroller<Node>();
		scroller.setSpacing(6D);
		scroller.setMenuDirection(Direction.UP);
		scroller.setItems(getItemType1MockData(count_ItemType1, scroller));
		scroller.getStyleClass().add("itemType1-scroller");
		
		Button add = new Button("Add Button");
		add.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent paramT) {
				String txt = "Demo Button "+(count_ItemType1++);
				final ItemType1 btn = new ItemType1(txt);
				btn.setDeleteAction(new EventHandler<ActionEvent>(){
		            public void handle(ActionEvent event)
		            {
		            	scroller.getItems().remove(btn);
		            }
		        });
				scroller.getItems().add(btn);
			}
		});
		
		HBox hb = HBoxBuilder.create().spacing(20).children(add).build();
		vb.getChildren().addAll(lbl, hb,scroller);
		this.layout.getChildren().add(vb);
	}
	
	/**
	 * Returns the list of items of type "ItemType1" for the Scroller.
	 * @param cnt - no of items need to be added.
	 * @param scroller - Scroller to which this items need be added.
	 * @return ObservableList<Node>
	 */
	private ObservableList<Node> getItemType1MockData(int cnt, final Scroller<Node> scroller){
		ObservableList<Node> list = FXCollections.observableArrayList();
		for (int i = 1; i < cnt; i++) {
			final ItemType1 btn = new ItemType1("Demo Button "+i);
			btn.setDeleteAction(new EventHandler<ActionEvent>(){
	            public void handle(ActionEvent event)
	            {
	            	scroller.getItems().remove(btn);
	            }
	        });
			list.add(btn);
		}
		return list;
	}
	
	/**
	 * Method to configure the Scroller by adding the "ItemType2" as its items.
	 */
	private void configureItemType2(){
		VBox vb = VBoxBuilder.create().spacing(10).padding(new Insets(10,0,10,0)).build();
		AppLabel lbl = new AppLabel("#"+(itemCnt++)+" : Customised Scroller with menu direction to top and pref height");
		lbl.getStyleClass().add("head-label");
		
		final Scroller<Node> scroller = new Scroller<Node>();
		scroller.setSpacing(6D);
		scroller.setMinHeight(84);
		scroller.setMenuDirection(Direction.UP);
		scroller.setItems(getItemType2MockData(count_ItemType2, scroller));
		scroller.getStyleClass().add("itemType2-scroller");
		
		Button add = new Button("Add Button");
		add.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent paramT) {
				String txt = "Demo Button "+(count_ItemType2++);
				final ItemType2 btn = new ItemType2(txt);
				scroller.getItems().add(btn);
			}
		});
		
		HBox hb = HBoxBuilder.create().spacing(20).children(add).build();
		vb.getChildren().addAll(lbl, hb,scroller);
		this.layout.getChildren().add(vb);
	}
	
	/**
	 * Returns the list of items of type "ItemType2" for the Scroller.
	 * @param cnt - no of items need to be added.
	 * @param scroller - Scroller to which this items need be added.
	 * @return ObservableList<Node>
	 */
	private ObservableList<Node> getItemType2MockData(int cnt, final Scroller<Node> scroller){
		ObservableList<Node> list = FXCollections.observableArrayList();
		for (int i = 1; i < cnt; i++) {
			final ItemType2 btn = new ItemType2("Demo Button "+i);
			list.add(btn);
		}
		return list;
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}
}
