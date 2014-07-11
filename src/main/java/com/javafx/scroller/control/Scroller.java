/**
 * 
 */
package com.javafx.scroller.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.Control;

import com.sun.javafx.scene.traversal.Direction;

/**
 * Scroller Control Implementation
 * @author Sai.Dandem
 */
public class Scroller<T> extends Control {

	private String DEFAULT_STYLE_CLASS="button-scroller";
	private Double DEFAULT_MIN_HEIGHT = 30d;
	
	//  Configurable Properties
	private ObjectProperty<Direction> menuDirection;
	private SimpleBooleanProperty showMenu;
	private SimpleDoubleProperty spacing;
	private ObservableList<T> items;
	private SimpleIntegerProperty defaultMenuItemsCount;
	
	private ListChangeListener<T> itemsListener = new ListChangeListener<T>(){
		@Override
		public void onChanged(javafx.collections.ListChangeListener.Change<? extends T> paramChange) {
			for (T t : items) {
				if(t instanceof Group){
					throw new IllegalArgumentException("Cannot pass Group as item for the Scroller");
				}
			}
		}};

	public Scroller(){
		this(null);
	}

	public Scroller(ObservableList<T> paramObservableList){
		this.items = FXCollections.observableArrayList();
		this.items.addListener(this.itemsListener);
		
	    if(paramObservableList!=null){
			setItems(paramObservableList);
	    }
		getStyleClass().setAll(new String[] { DEFAULT_STYLE_CLASS });
		setMinHeight(DEFAULT_MIN_HEIGHT);
	}

	/**
	 * Return the path to the CSS file so things are setup right
	 */
	@Override protected String getUserAgentStylesheet()
	{
		return this.getClass().getResource(this.getClass().getSimpleName() + ".css").toString();
	}

	public final ObjectProperty<Direction> menuDirectionProperty()
	{
		if (this.menuDirection == null){
			this.menuDirection = new ObjectPropertyBase<Direction>(Direction.DOWN) {
				@Override
				public Object getBean() {
					return Scroller.this;
				}

				@Override
				public String getName() {
					return "menuDirection";
				}
			};
		}
		return this.menuDirection;
	}

	public final void setMenuDirection(Direction paramMenuDirection)
	{
		if(!(paramMenuDirection.equals(Direction.UP) ||  paramMenuDirection.equals(Direction.DOWN))){
			throw new UnsupportedOperationException();
		}
		menuDirectionProperty().setValue(paramMenuDirection);
	}

	public final Direction getMenuDirection()
	{
		return ((this.menuDirection == null) ? Direction.UP : this.menuDirection.getValue());
	}
	
	public final SimpleBooleanProperty showMenuProperty()
	{
		if (this.showMenu == null){
			this.showMenu = new SimpleBooleanProperty(true);
		}
		return this.showMenu;
	}

	public final void setShowMenu(Boolean paramShowMenu)
	{
		showMenuProperty().setValue(paramShowMenu);
	}

	public final Boolean getShowMenu()
	{
		return ((this.showMenu == null) ? true : this.showMenu.getValue());
	}
	
	public final SimpleDoubleProperty spacingProperty()
	{
		if (this.spacing == null){
			this.spacing = new SimpleDoubleProperty();
		}
		return this.spacing;
	}

	public final void setSpacing(Double paramSpacing)
	{
		spacingProperty().setValue(paramSpacing);
	}

	public final Double getSpacing()
	{
		return ((this.spacing == null) ? 10.0D : this.spacing.getValue());
	}
	
	public final SimpleIntegerProperty defaultMenuItemsCountProperty()
	{
		if (this.defaultMenuItemsCount == null){
			this.defaultMenuItemsCount = new SimpleIntegerProperty();
		}
		return this.defaultMenuItemsCount;
	}

	public final void setDefaultMenuItemsCount(Integer paramCount)
	{
		if(paramCount==null || paramCount.intValue()<1){
			throw new UnsupportedOperationException("Menu Items Count cannot be null or less than 1.");
		}
		defaultMenuItemsCountProperty().setValue(paramCount);
	}

	public final Integer getDefaultMenuItemsCount()
	{
		return ((this.defaultMenuItemsCount == null) ? 10 : this.defaultMenuItemsCount.getValue());
	}
	
	public final void setItems(ObservableList<T> paramObservableList){
		this.items.clear();
		if(paramObservableList!=null && paramObservableList.size()>0){
			this.items.addAll(paramObservableList);
		}
	}

	public final ObservableList<T> getItems(){
		return this.items;
	}
}
