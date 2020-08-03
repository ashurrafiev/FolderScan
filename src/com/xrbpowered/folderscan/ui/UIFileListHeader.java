package com.xrbpowered.folderscan.ui;

import java.awt.Color;

import com.xrbpowered.folderscan.FolderScanUI;
import com.xrbpowered.folderscan.ui.FileSort.SortMode;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
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
		protected final float tx;
		protected final int align;
		
		public UIHeaderButton(String label, SortMode sortMode, float x, float width, float tx, int align) {
			super(UIFileListHeader.this);
			this.label = label;
			this.sortMode = sortMode;
			this.tx = tx-x;
			this.align = align;
			setLocation(x, 0);
			setSize(width, height);
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
	
	public UIFileListHeader(UIContainer parent) {
		super(parent);
		setSize(0, height);
		
		new UIHeaderButton("Name", SortMode.name, 0, 340, 32, GraphAssist.LEFT);
		new UIHeaderButton("Size", SortMode.size, 340, 64, 400, GraphAssist.RIGHT);
		new UIHeaderButton("(diff)", SortMode.sizeDiff, 404, 64, 408, GraphAssist.LEFT);
		new UIHeaderButton("Files", SortMode.files, 404+64, 628-50-404-64, 550, GraphAssist.RIGHT);
		new UIHeaderButton(null, SortMode.changes, 628-50, 150, 720, GraphAssist.RIGHT) {
			@Override
			protected void drawLabel(GraphAssist g, float tx, float ty) {
				g.drawString("Add", tx-100, ty, align, GraphAssist.BOTTOM);
				g.drawString("Mod", tx-50, ty, align, GraphAssist.BOTTOM);
				g.drawString("Rem", tx, ty, align, GraphAssist.BOTTOM);
			}
		};
		new UIHeaderButton("Last modified", SortMode.time, 728, 240, 780, GraphAssist.LEFT);
		new UIHeaderButton(null, SortMode.mark, 968, 32, 968+8, GraphAssist.RIGHT) {
			@Override
			protected void drawLabel(GraphAssist g, float tx, float ty) {
				UIFileListItemBase.iconCheckMarked.paint(g.graph, 0, tx, ty-16, 16, getPixelScale(), true);
			}
		};
	}
	
	@Override
	protected void paintSelf(GraphAssist g) {
		g.fill(this, colorBackground);
	}

}
