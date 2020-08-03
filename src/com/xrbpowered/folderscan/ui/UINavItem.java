package com.xrbpowered.folderscan.ui;

import com.xrbpowered.folderscan.data.FolderInfo;
import com.xrbpowered.zoomui.GraphAssist;

public class UINavItem extends UIFolderItem {
	
	public final boolean top;
	
	public UINavItem(UIFileList list, FolderInfo info, boolean top) {
		super(list, info, false);
		this.top = top;
	}
	
	@Override
	public void paint(GraphAssist g) {
		boolean last = list.isCurrent(info);
		g.fill(this, hover || last ? colorNavHighlight : colorNavBackground);
		(top ? homeIcon : last ? folderIcon : navIcon).paint(g.graph, last ? 2 : 0, 8, 4, 16, getPixelScale(), true);
		g.setColor(last ? colorTextDisabled : colorText);
		paintInfo(g, false);
	}
	
	@Override
	public boolean onMouseDown(float x, float y, Button button, int mods) {
		if(button==Button.left && mods==0) {
			list.navigate(this);
			return true;
		}
		else
			return super.onMouseDown(x, y, button, mods);
	}
}