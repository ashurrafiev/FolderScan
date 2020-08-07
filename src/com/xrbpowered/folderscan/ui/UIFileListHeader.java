package com.xrbpowered.folderscan.ui;

import java.awt.Color;

import com.xrbpowered.folderscan.FolderScanUI;
import com.xrbpowered.folderscan.ui.FileSort.SortMode;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.base.UIHoverElement;
import com.xrbpowered.zoomui.std.UIListItem;

public class UIFileListHeader extends UIContainer {

	public static final Color colorBackground = UIFileList.colorBackground;
	public static final Color colorHighlight = UIListItem.colorHighlight;
	public static final Color colorSelected = UIListItem.colorSelection;
	public static final Color colorText = UIListItem.colorText;
	public static final Color colorSelectedText = UIListItem.colorSelectedText;
	
	public static final float height = 32;
	
	public class UIHeaderButton extends UIHoverElement {

		public final String label;
		public final SortMode sortMode;
		public final int align;

		private final float baseTx;
		private final float baseWidth;
		private final float marginLeft;
		private final float marginRight;
		
		private float tx;

		public UIHeaderButton(String label, SortMode sortMode, float width, float tx, int align, float marginLeft, float marginRight) {
			super(UIFileListHeader.this);
			this.label = label;
			this.sortMode = sortMode;
			this.baseTx = tx;
			this.tx = tx;
			this.align = align;
			this.baseWidth = width;
			this.marginLeft = marginLeft;
			this.marginRight = marginRight;
			
			minWidth += baseWidth;
			marginSum += marginLeft+marginRight;
			
			setLocation(0, 0);
			setSize(width, height);
		}

		public UIHeaderButton(String label, SortMode sortMode, float width, float tx, int align) {
			this(label, sortMode, width, tx, align, 0, 0);
		}

		public float getTextX() {
			return getX()+tx;
		}
		
		protected float updateMargins(float marginUnit) {
			float w = baseWidth + (marginLeft+marginRight)*marginUnit;
			tx = baseTx + marginLeft*marginUnit;
			setSize(w, getHeight());
			return w;
		}
		
		protected void drawLabel(GraphAssist g, float tx, float ty) {
			g.drawString(label, tx, ty, align, GraphAssist.BOTTOM);
		}
		
		@Override
		public void paint(GraphAssist g) {
			boolean selected = (FolderScanUI.ui.fileList.sort.mode==sortMode);
			g.fill(this, selected ? colorSelected : hover ? colorHighlight : colorBackground);
			g.setColor(selected ? colorSelectedText : colorText);
			drawLabel(g, tx, getHeight()-8);
		}
		
		@Override
		public boolean onMouseDown(float x, float y, Button button, int mods) {
			if(button==Button.left) {
				UIFileList list = FolderScanUI.ui.fileList;
				list.sort.mode = sortMode;
				list.refresh();
				list.repaint();
			}
			return true;
		}
	}
	
	public final UIHeaderButton headerName;
	public final UIHeaderButton headerSize;
	public final UIHeaderButton headerSizeDiff;
	public final UIHeaderButton headerFiles;
	public final UIHeaderButton headerChanges;
	public final UIHeaderButton headerTime;
	public final UIHeaderButton headerMark;
	
	private float minWidth = 0;
	private float marginSum = 0;
	
	public UIFileListHeader(UIContainer parent) {
		super(parent);
		setSize(0, height);
		
		headerName = new UIHeaderButton("Name", SortMode.name, 256, 32, GraphAssist.LEFT, 0, 5);
		headerSize = new UIHeaderButton("Size", SortMode.size, 64, 60, GraphAssist.RIGHT);
		headerSizeDiff = new UIHeaderButton("(diff)", SortMode.sizeDiff, 64, 4, GraphAssist.LEFT);
		headerFiles = new UIHeaderButton("Files", SortMode.files, 96, 64, GraphAssist.RIGHT, 2, 0);
		headerChanges = new UIHeaderButton(null, SortMode.changes, 150, 142, GraphAssist.RIGHT) {
			@Override
			protected void drawLabel(GraphAssist g, float tx, float ty) {
				g.drawString("Add", tx-100, ty, align, GraphAssist.BOTTOM);
				g.drawString("Mod", tx-50, ty, align, GraphAssist.BOTTOM);
				g.drawString("Rem", tx, ty, align, GraphAssist.BOTTOM);
			}
		};
		headerTime = new UIHeaderButton("Last modified", SortMode.time, 208, 32, GraphAssist.LEFT, 2, 4);
		headerMark = new UIHeaderButton(null, SortMode.mark, 32, 8, GraphAssist.RIGHT, 1, 1) {
			@Override
			protected void drawLabel(GraphAssist g, float tx, float ty) {
				UIFileListItemBase.iconCheckMarked.paint(g.graph, 0, tx, ty-16, 16, getPixelScale(), true);
			}
		};
	}
	
	@Override
	public void layout() {
		float x = getWidth();
		if(x<minWidth) x = minWidth;
		float mu = (x-minWidth)/marginSum;
		
		UIElement c;
		for(int i=children.size()-1; i>0; i--) {
			c = children.get(i);
			x -= ((UIHeaderButton) c).updateMargins(mu);
			c.setLocation(x, c.getY());
		}
		
		c = children.get(0);
		c.setSize(x, c.getHeight());
	}
	
	@Override
	protected void paintSelf(GraphAssist g) {
		g.fill(this, colorBackground);
	}
	
	@Override
	protected void paintChildren(GraphAssist g) {
		g.pushClip(0, 0, getWidth(), getHeight());
		super.paintChildren(g);
		g.popClip();
	}

}
