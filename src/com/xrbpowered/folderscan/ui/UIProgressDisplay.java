package com.xrbpowered.folderscan.ui;

import java.awt.Color;

import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.std.UIButton;
import com.xrbpowered.zoomui.std.UIListBox;
import com.xrbpowered.zoomui.std.UIListItem;

public class UIProgressDisplay extends UIElement {

	public static final Color colorBackground = UIListBox.colorBackground;
	public static final Color colorText = UIListItem.colorText;
	
	private static final Color colorSpinDark = new Color(0x777777);
	private static final Color colorSpinLight = new Color(0xdddddd);
	
	public static final int width = 200;
	
	public String caption = null;
	
	private final SafeThread repaintThread;
	private int spin = 0;
	
	public UIProgressDisplay(UIContainer parent) {
		super(parent);
		
		repaintThread = new SafeThread() {
			@Override
			protected void loop() throws InterruptedException {
				Thread.sleep(100);
				spin = (spin+1)%4;
				safeUIRunAsync();
			}
			@Override
			protected void uiRun() {
				repaint();
			}
		};
		repaintThread.start();
	}
	
	public void dismiss() {
		repaintThread.interrupt();
		getParent().removeChild(this);
	}

	@Override
	public void paint(GraphAssist g) {
		g.fill(this, colorBackground);
		float x = getWidth()/2 - width/2;
		float y = getHeight()/2;
		
		g.fillRect(x-24, y-7, 6, 6, spin==0 ? colorSpinDark : colorSpinLight);
		g.fillRect(x-24+8, y-7, 6, 6, spin==1 ? colorSpinDark : colorSpinLight);
		g.fillRect(x-24+8, y-7+8, 6, 6, spin==2 ? colorSpinDark : colorSpinLight);
		g.fillRect(x-24, y-7+8, 6, 6, spin==3 ? colorSpinDark : colorSpinLight);
		
		if(caption!=null) {
			g.setFont(UIButton.font);
			g.setColor(colorText);
			g.drawString(caption, x, y, GraphAssist.LEFT, GraphAssist.CENTER);
		}
	}

}
