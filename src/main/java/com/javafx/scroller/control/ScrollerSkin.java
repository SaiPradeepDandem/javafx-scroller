package com.javafx.scroller.control;

import java.util.Iterator;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TimelineBuilder;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathBuilder;
import javafx.stage.Popup;
import javafx.util.Duration;

import com.sun.javafx.scene.control.skin.SkinBase;
import com.sun.javafx.scene.traversal.Direction;

/**
 * 
 * @author Sai.Dandem
 */
public class ScrollerSkin<T extends Node> extends SkinBase<Scroller<T>, ScrollerBehavior<T>>{

	// Constant Variables
	private final double MENU_ITEM_MIN_HEIGHT = 26.0D;
	private final double MENU_ITEM_MIN_WIDTH = 200.0D;
	
	// Node Variables
	private Button leftScroller;
    private Button rightScroller;
    private Button menuButton;
    private ScrollPane center;
    private HBox scrollNode;
    private HBox mainLayout;
    private Scroller<T> localScroller;
    private VBox listView;
    private StackPane listViewContainer;
    private Popup popup;
    private ScrollPane menuListScrollPane;
    
    // Observable Variables
    private ObservableList<String> menuItemsList;
    private SimpleDoubleProperty viewPortWidth;
    private SimpleBooleanProperty controlsVisible;
    private SimpleBooleanProperty leftControlDisable;
    private SimpleBooleanProperty rightControlDisable;
    private SimpleIntegerProperty firstVisibleNodePos;
    private SimpleDoubleProperty currentXOffsetValue;
    private Timeline scrollerTimeLine;
    
    // Listener Variables
    private final ListChangeListener<? super Node> nodeListener = new ListChangeListener<Node>(){
		@Override
		public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> paramChange) {
			layoutItems();
		}
    };
    
    private final ChangeListener<Number> offsetListener = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> paramObservableValue,	Number paramT1, Number paramT2) {
			if((paramT2.doubleValue() * -1)<=0){
				leftControlDisable.set(true);
			}
		}
	};
    
	private final ChangeListener<Boolean> focusListener = new ChangeListener<Boolean>() {
		@Override
		public void changed(ObservableValue<? extends Boolean> paramObservableValue,	Boolean paramT1, Boolean paramT2) {
			if(paramT2 && popup!=null){
				popup.hide();
			}
		}
	};
	
	public ScrollerSkin(Scroller<T> control) {
		super(control, new ScrollerBehavior<T>(control));
		this.localScroller = control;
		this.localScroller.focusedProperty().addListener(this.focusListener);
		initialize();
	}
	
	private void initialize(){
		this.mainLayout = new HBox();
		this.firstVisibleNodePos = new SimpleIntegerProperty();
		this.currentXOffsetValue = new SimpleDoubleProperty();
		this.currentXOffsetValue.addListener(this.offsetListener);
		menuItemsList = FXCollections.observableArrayList();
		
		controlsVisible = new SimpleBooleanProperty();
		leftControlDisable = new SimpleBooleanProperty(true);
		rightControlDisable = new SimpleBooleanProperty(true);
		configureControls();
				
		this.scrollNode = new HBox(){	
			@Override
			protected void layoutChildren() {
				super.layoutChildren();
				determineControlsVisibility(viewPortWidth.get());
				checkForEnds(viewPortWidth.get());
			}
		};
		this.scrollNode.setAlignment(Pos.CENTER_LEFT);
		this.scrollNode.setPadding(new Insets(0,5,0,5));
		this.scrollNode.spacingProperty().bind(this.localScroller.spacingProperty());
		this.scrollNode.translateXProperty().bind(currentXOffsetValue);
		this.localScroller.getItems().addListener(nodeListener);
		
		this.center = new ScrollPane();
		this.center.getStyleClass().add("scroller-bg");
		this.center.hbarPolicyProperty().set(ScrollBarPolicy.NEVER);
		this.center.vbarPolicyProperty().set(ScrollBarPolicy.NEVER);
		this.center.prefHeightProperty().bind(this.localScroller.heightProperty());
		this.center.minHeightProperty().bind(this.localScroller.heightProperty());
		this.center.setFitToWidth(true);
		this.center.setFitToHeight(true);
		this.center.setContent(GridPaneBuilder.create()
											  .alignment(Pos.CENTER_LEFT)
											  .children(this.scrollNode).build());
		
		this.viewPortWidth = new SimpleDoubleProperty();
		this.viewPortWidth.bind(this.center.widthProperty());
		this.viewPortWidth.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0,	Number arg1, Number newWidth) {
				checkForEnds(newWidth.doubleValue());
				determineControlsVisibility(newWidth.doubleValue());
			}
		});
		
		mainLayout.getChildren().addAll(this.center);
		HBox.setHgrow(this.center, Priority.ALWAYS);
		
		layoutItems();
		getChildren().addAll(mainLayout);
	}
	
	private void configureControls(){
		final ScrollerShapes shapes = new ScrollerShapes();
		this.leftScroller = new Button();
		this.leftScroller.setGraphic(shapes.getLeftPointedArrow());
		this.leftScroller.getStyleClass().add("left-Scroller");
		this.leftScroller.prefHeightProperty().bind(this.mainLayout.heightProperty());
		this.leftScroller.visibleProperty().bind(this.controlsVisible);
		this.leftScroller.setMaxWidth(20);
		this.leftScroller.setMinWidth(20);
		this.leftScroller.disableProperty().bind(leftControlDisable);
		this.leftScroller.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				computeScrollOffsetValue(firstVisibleNodePos.get()-1);
				ScrollerSkin.this.localScroller.requestFocus();
			}
		});
		
		this.rightScroller = new Button();
		this.rightScroller.setGraphic(shapes.getRighttPointedArrow());
		this.rightScroller.getStyleClass().add("right-Scroller");
		this.rightScroller.prefHeightProperty().bind(this.mainLayout.heightProperty());
		this.rightScroller.visibleProperty().bind(this.controlsVisible);
		this.rightScroller.setMaxWidth(20);
		this.rightScroller.setMinWidth(20);
		this.rightScroller.disableProperty().bind(rightControlDisable);
		this.rightScroller.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				computeScrollOffsetValue(firstVisibleNodePos.get()+1);
				ScrollerSkin.this.localScroller.requestFocus();
			}
		});
		
		this.menuButton = new Button();
		this.menuButton.getStyleClass().add("menu-Button");
		this.menuButton.prefHeightProperty().bind(this.mainLayout.heightProperty());
		this.menuButton.visibleProperty().bind(this.controlsVisible);
		this.menuButton.setMaxWidth(20);
		this.menuButton.setMinWidth(20);
		this.menuButton.graphicProperty().bind( new ObjectBinding<Path>() {
			{
				localScroller.menuDirectionProperty();
			}
			@Override
			protected Path computeValue() {
				if(localScroller.getMenuDirection().equals(Direction.UP)){
					return shapes.getUpArrow();
				}else{
					return shapes.getDownArrow();
				}
			}
		});
		this.menuButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				ScrollerSkin.this.localScroller.requestFocus();
				showPopup();
			}
		});
		configureMenuList();
		
	}
	
	private void configureMenuList() {
		if(this.popup==null){
			this.popup = new Popup();
			this.popup.setAutoHide(true);
			this.popup.setAutoFix(true);
			this.popup.setHideOnEscape(true);
			
			this.listView = new VBox();
			this.menuListScrollPane = new ScrollPane();
			this.menuListScrollPane.minWidthProperty().bind(this.listView.widthProperty());
			this.menuListScrollPane.getStyleClass().add("menuList-scroll-pane");
			this.menuListScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
			
			this.listViewContainer = new StackPane(){
				@Override
				protected void layoutChildren() {
					super.layoutChildren();
					layoutPopup(getWidth(), getHeight());
				}
			};
			this.listViewContainer.getStyleClass().add("menu-list-down");
			this.popup.getContent().add(this.listViewContainer);
		}
	}

	
	private void determineControlsVisibility(double viewPortWidth){
		if(mainLayout.getWidth()<this.scrollNode.getWidth()){
			showControls();
		}else{
			hideControls();
		}
	}
	
	private void showControls(){
		if(!this.controlsVisible.get()){
			this.controlsVisible.set(true);
			leftScroller.setMinWidth(0);
			rightScroller.setMinWidth(0);
			menuButton.setMinHeight(0);
			
			mainLayout.getChildren().add(0,leftScroller);
			mainLayout.getChildren().add(rightScroller);
			if(localScroller.showMenuProperty().get()){
				mainLayout.getChildren().addAll(menuButton);
			}
			mainLayout.requestLayout();
		}
	}
	
	private void hideControls(){
		if(this.controlsVisible.get()){
			mainLayout.getChildren().removeAll(leftScroller,rightScroller);
			if(localScroller.showMenuProperty().get()){
				mainLayout.getChildren().remove(menuButton);
			}
			this.controlsVisible.set(false);
			
			localScroller.requestLayout();
			mainLayout.requestLayout();
			center.requestLayout();
		}
		// If the controls are not visible means, the first node MUST & SHOULD be at starting point.
		this.firstVisibleNodePos.set(0);
		setOffsetValue(0);
	}
	
	private void checkForMenuStyleClass(){
		listViewContainer.getStyleClass().remove("menu-list-down");
		listViewContainer.getStyleClass().remove("menu-list-up");
		if(localScroller.getMenuDirection() == Direction.UP){
			listViewContainer.getStyleClass().add("menu-list-up");
		}else{
			listViewContainer.getStyleClass().add("menu-list-down");
		}
	}
	
	/**
	 * Method to show the pop up.
	 */
	public void showPopup() {
		if(this.controlsVisible.get()){
			refreshMenuList();
			checkForMenuStyleClass();
			layoutPopup(listViewContainer.getWidth(), listViewContainer.getHeight());
		}
	}
	
	private void layoutPopup(double listWidth, double listHeight){
		Parent parent = getParent();
		Bounds childBounds = getBoundsInParent();
		Bounds parentBounds = parent.localToScene(parent.getBoundsInLocal());
		double layoutX = childBounds.getMinX() + parentBounds.getMinX() + parent.getScene().getX() + parent.getScene().getWindow().getX();
		double layoutY = childBounds.getMaxY() + parentBounds.getMinY() + parent.getScene().getY() + parent.getScene().getWindow().getY();
		
		double widthToShift = layoutX+(mainLayout.getWidth()-listWidth);
		double hgtToShift =0;
		if(localScroller.getMenuDirection() == Direction.UP){
			hgtToShift = menuButton.getHeight();
			hgtToShift = hgtToShift + listHeight;
		}
		popup.show(this, widthToShift, layoutY-hgtToShift);
	}
	
	
	private void refreshMenuList() {
		cleanListView();
		
		Iterator<String> localIterator = menuItemsList.iterator();
		ObservableList<ScrollerMenuItem> localObservableList = FXCollections.observableArrayList();
		int i =0;
		while (localIterator.hasNext()){
			final ScrollerMenuItem localMenuItem = new ScrollerMenuItem(localIterator.next(),i);
			localMenuItem.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent paramT) {
					scrollToNode(localMenuItem.getIndex());	
				}
			});
			localObservableList.add(localMenuItem);
			i++;
		}
	    this.listView.getChildren().addAll(localObservableList);
	    
	    this.listViewContainer.getChildren().clear();
	    // Wrapping in the the menu scroll if items are more than the specified count.
		if(listView.getChildren().size()>this.localScroller.getDefaultMenuItemsCount()){
			this.menuListScrollPane.setContent(null);
			this.menuListScrollPane.setPrefHeight(MENU_ITEM_MIN_HEIGHT * this.localScroller.getDefaultMenuItemsCount());
			this.menuListScrollPane.setVvalue(0);
			this.menuListScrollPane.setContent(this.listView);
	    	this.listViewContainer.getChildren().add(this.menuListScrollPane);
			
	    }else{
	    	this.listViewContainer.getChildren().add(this.listView);
		}
	}
	
	private void scrollToNode(int nodeIndex){
		computeScrollOffsetValue(nodeIndex);
		Node node = this.localScroller.getItems().get(nodeIndex);
		if(node instanceof Button){
			((Button)node).fire();
		}else{
			if(node.getOnMouseClicked()!=null){
				node.getOnMouseClicked().handle(null);
			}
		}
		this.popup.hide();
		node.requestFocus();
	}

	@SuppressWarnings("unchecked")
	private void cleanListView(){
		Iterator<Node> localIterator = listView.getChildren().iterator();
		while (localIterator.hasNext()){
			((ScrollerMenuItem)localIterator.next()).setOnMouseClicked(null);
		}
		this.listView.getChildren().clear();
	}
	
	private void layoutItems(){
		scrollNode.getChildren().clear();
		menuItemsList.clear();
		for (Node node : this.localScroller.getItems()) {
			scrollNode.getChildren().add(node);
			menuItemsList.add(node.getUserData().toString());
		}
	}
	
	private void setAndAnimateOffsetValue(double value){
		this.scrollerTimeLine = null;
		this.scrollerTimeLine = TimelineBuilder.create().cycleCount(1).autoReverse(true).build();
		KeyFrame kf = new KeyFrame(Duration.valueOf("300ms"), new KeyValue(currentXOffsetValue,value));
		this.scrollerTimeLine.getKeyFrames().clear();
		this.scrollerTimeLine.getKeyFrames().add(kf);
		this.scrollerTimeLine.play();
	}
	
	private void setOffsetValue(double value){
		this.currentXOffsetValue.set(value);
	}
	
	public void scrollNext() {
		if(this.controlsVisible.get()){
			computeScrollOffsetValue(firstVisibleNodePos.get()+1);
		}
	}

	public void scrollPrevious() {
		if(this.controlsVisible.get()){
			computeScrollOffsetValue(firstVisibleNodePos.get()-1);
		}
	}

	public void scrollToFirst() {
		if(this.controlsVisible.get()){
			computeScrollOffsetValue(0);
		}
	}

	public void scrollToLast() {
		if(this.controlsVisible.get()){
			computeScrollOffsetValue(localScroller.getItems().size()-1);
		}
	}

	private void computeScrollOffsetValue(int nodePosition){
		if(popup!=null){
			popup.hide();
		}
		
		double d1 = 0; 
		double d2 = this.scrollNode.getWidth(); 
		double d3 = this.center.getWidth();
		
		if(nodePosition>0){
			d1 = getLeftIndent();
		}
		for (int i = 0; i < nodePosition; i++) {
			Node node = this.localScroller.getItems().get(i);
			d1 = d1 + getNodeWidth(node) + ((i==nodePosition-1) ? getHalfSpacing() : this.localScroller.getSpacing()) ;
		}
		if(d1==0){
			leftControlDisable.set(true);
			rightControlDisable.set(false);
			
		}else if((d2-d1)<d3){
			d1 = d2-d3;
			leftControlDisable.set(false);
			rightControlDisable.set(true);
			
		}else{
			leftControlDisable.set(false);
			rightControlDisable.set(false);
		}
		
		d1 = (-1) * d1;
		setAndAnimateOffsetValue(d1);
		determineFirstVisibleNodePosition(d1);
	}
	
	private double getNodeWidth(Node node){
		if(node instanceof Region){
			return ((Region)node).getWidth();
		}else if(node instanceof Control){
			return ((Control)node).getWidth();
		}
		return 0;
	}
	
	/**
	 *                       <------------- d3 ---------------->  
	 * 						 ===================================
	 * 	  <------ d1 ------>|									| ViewPort (ScrollPane)
	 *    ----------------------------------------------------------------
	 *   |																  | Scroll Node
	 *    ----------------------------------------------------------------
	 * 						|									|
	 * 						 ===================================
	 * 
	 *    <-------------------------------- d2 -------------------------->
	 *    
	 * @param d3
	 */
	private void checkForEnds(double d3) {
		double d2 = this.scrollNode.getWidth(); 
		double d1 = this.scrollNode.getTranslateX(); 
		double d1P = (d1<0)? -1 * d1 : d1 ;
		
		if(d2>0 && d1<0 && (d2+d1)<d3){
			double nv = (-1)* (d2-d3);
			setOffsetValue(nv);
			rightControlDisable.set(true);
			
			// Determine the firstVisibleNodePos position.
			determineFirstVisibleNodePosition(nv);
		}
		
		if(d1P+d3 == d2){
			// last node reached.
			rightControlDisable.set(true);
		}else if(d1P+d3 < d2){
			rightControlDisable.set(false);
		}
	}

	private void determineFirstVisibleNodePosition(double d1) {
		double d1P = (d1<0)? -1 * d1 : d1 ; // d1 Positive Value
		double spacing = getHalfSpacing();
		double w = getLeftIndent();
		for (int i=0;i<this.localScroller.getItems().size();i++) {
			w = w + getNodeWidth(this.localScroller.getItems().get(i)) + spacing;
			if(d1P<w){
				this.firstVisibleNodePos.set(i);
				break;
			}
		}
	}
	
	private double getLeftIndent(){
		return this.scrollNode.getPadding().getLeft();
	}
	
	private double getHalfSpacing(){
		return this.localScroller.getSpacing()>0 ? this.localScroller.getSpacing() / 2 : 0;
	}
	
	
	/**
	 * Class which renders as item to the menu pop up.
	 * @author Sai.Dandem
	 *
	 */
	class ScrollerMenuItem extends StackPane{
		private SimpleIntegerProperty index;
		//private SimpleBooleanProperty selected = new SimpleBooleanProperty();
		
		public ScrollerMenuItem(String item, int index){
			super();
			setIndex(index);
			
			setMaxHeight(MENU_ITEM_MIN_HEIGHT);
			setMinHeight(MENU_ITEM_MIN_HEIGHT);
			setMinWidth(MENU_ITEM_MIN_WIDTH);
			getStyleClass().add("scroller-menu-item");
			getChildren().add(new Label(item));
		}
		
		public final SimpleIntegerProperty indexProperty()
		{
			if (this.index == null){
				this.index = new SimpleIntegerProperty();
			}
			return this.index;
		}

		public final void setIndex(Integer paramCount)
		{
			indexProperty().setValue(paramCount);
		}

		public final Integer getIndex()
		{
			return ((this.index == null) ? 0 : this.index.getValue());
		}
		
	}
	
	/**
	 * Utility class that provides shapes to the buttons.
	 * @author Sai.Dandem
	 */
	class ScrollerShapes {
		/**
		 * Builds the left pointed arrow shape using path.
		 * @return Path
		 */
		public Path getLeftPointedArrow(){
			return PathBuilder.create()
							  .elements(new MoveTo(0, 6),
							  			new LineTo(0, 7),
							  			new LineTo(6, 13),
							  			new LineTo(8, 11),
							  			new LineTo(4, 7),
							  			new LineTo(4, 6),
							  			new LineTo(8, 2),
							  			new LineTo(6, 0),
							  			new LineTo(0, 6),
							  			new LineTo(0, 7))
							  .stroke(Color.BLACK)
							  .strokeWidth(.9)
							  .fill(Color.WHITE)
							  .build();
		}
		
		/**
		 * Builds the right pointed arrow shape using path. ( Rotates the left pointed arrow by 180 degrees )
		 * @return Path
		 */
		public Path getRighttPointedArrow(){
			Path arrow = getLeftPointedArrow();
			arrow.setRotate(180);
			return arrow;
		}
		
		/**
		 * Builds the down arrow shape using path.
		 * @return Path
		 */
		public Path getDownArrow(){
			return PathBuilder.create()
							  .elements(new MoveTo(0, 0),
							  			new LineTo(5, 5),
							  			new LineTo(10, 0),
							  			new LineTo(0, 0))
							  .stroke(Color.BLACK)
							  .strokeWidth(.9)
							  .fill(Color.WHITE)
							  .build();
		}
		
		/**
		 * Builds the down arrow shape using path. ( Rotates the down arrow by 180 degrees )
		 * @return Path
		 */
		public Path getUpArrow(){
			Path arrow = getDownArrow();
			arrow.setRotate(180);
			return arrow;
		}
	}
}
