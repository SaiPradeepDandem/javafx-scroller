package com.javafx.scroller.control;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import com.sun.javafx.scene.traversal.Direction;

/**
 * 
 * @author Sai.Dandem
 */
public class ScrollerBehavior<Node> extends BehaviorBase<Scroller<Node>> {
	
	private static final String HOME = "Home";
	private static final String END = "End";
	protected static final List<KeyBinding> SCROLLER_BINDINGS = new ArrayList<KeyBinding>();
	  
	public ScrollerBehavior(Scroller<Node> scrollerParam) {
		super(scrollerParam);
	}
	
	@Override
	protected List<KeyBinding> createKeyBindings() {
		return SCROLLER_BINDINGS;
	}
	
	@Override
	protected void callAction(String paramString) {
		if (("TraverseLeft".equals(paramString)))
		{
			if (!(((Scroller<Node>)getControl()).isFocused()))   return;
			scrollPrevious();
		}
		else if (("TraverseRight".equals(paramString)))
		{
			if (!(((Scroller<Node>)getControl()).isFocused()))   return;
			scrollNext();
		}
		else if (("TraverseDown".equals(paramString)))
		{
			Scroller<Node> localScroller = (Scroller<Node>)getControl();
			if (!localScroller.isFocused())   return;
			if(localScroller.getShowMenu() && (localScroller.getMenuDirection() == Direction.DOWN) ){
				showPopup();
			}else{
				super.callAction(paramString);
			}
		}
		else if (("TraverseUp".equals(paramString)))
		{
			Scroller<Node> localScroller = (Scroller<Node>)getControl();
			if (!localScroller.isFocused())   return;
			if(localScroller.getShowMenu() && (localScroller.getMenuDirection() == Direction.UP) ){
				showPopup();
			}else{
				super.callAction(paramString);
			}
		}
		else if ("Home".equals(paramString))
		{
			if (!(((Scroller<Node>)getControl()).isFocused()))   return;
			scrollToFirst();
		}
		else if ("End".equals(paramString))
		{
			if (!(((Scroller<Node>)getControl()).isFocused()))   return;
			scrollToLast();
		}
		else{
			super.callAction(paramString);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent paramMouseEvent) {
		super.mousePressed(paramMouseEvent);
		Scroller<Node> localScroller = (Scroller<Node>)getControl();
		localScroller.requestFocus();
	}
	
	@SuppressWarnings("unchecked")
	public void scrollNext(){
		ScrollerSkin localScrollerSkin = (ScrollerSkin) ((Scroller<Node>)getControl()).getSkin();
		localScrollerSkin.scrollNext();
	}
	
	@SuppressWarnings("unchecked")
	public void scrollPrevious(){
		ScrollerSkin localScrollerSkin = (ScrollerSkin) ((Scroller<Node>)getControl()).getSkin();
		localScrollerSkin.scrollPrevious();
	}
	
	@SuppressWarnings("unchecked")
	public void scrollToFirst(){
		ScrollerSkin localScrollerSkin = (ScrollerSkin) ((Scroller<Node>)getControl()).getSkin();
		localScrollerSkin.scrollToFirst();
	}
	
	@SuppressWarnings("unchecked")
	public void scrollToLast(){
		ScrollerSkin localScrollerSkin = (ScrollerSkin) ((Scroller<Node>)getControl()).getSkin();
		localScrollerSkin.scrollToLast();
	}
	
	@SuppressWarnings("unchecked")
	public void showPopup(){
		ScrollerSkin localScrollerSkin = (ScrollerSkin) ((Scroller<Node>)getControl()).getSkin();
		localScrollerSkin.showPopup();
	}
	
	static
	  {
		SCROLLER_BINDINGS.addAll(TRAVERSAL_BINDINGS);
		SCROLLER_BINDINGS.add(new KeyBinding(KeyCode.HOME, "Home"));
		SCROLLER_BINDINGS.add(new KeyBinding(KeyCode.END, "End"));
	 }
}

