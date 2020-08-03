package com.xrbpowered.folderscan.ui;

import com.xrbpowered.folderscan.data.FolderInfo;
import com.xrbpowered.zoomui.GraphAssist;

public class UIFolderItem extends UIFileListItemBase {
	
	public UIFolderItem(UIFileList list, FolderInfo info, boolean first) {
		super(list, info, first);
	}
	
	protected boolean isChanged() {
		FolderInfo folder = (FolderInfo)info;
		return super.isChanged() || folder.countAdded>0 || folder.countModified>0 || folder.countRemoved>0;
	}
	
	@Override
	public void paint(GraphAssist g) {
		if(((FolderInfo)info).ignore) {
			g.fill(this, hover ? colorHighlight : colorBackground);
			folderIcon.paint(g.graph, 2, 8, 4, 16, getPixelScale(), true);
			g.setColor(colorAmber);
			g.drawString(info.name, 32, getHeight()/2, GraphAssist.LEFT, GraphAssist.CENTER);
			g.drawString("(ignore)", 780, getHeight()/2, GraphAssist.LEFT, GraphAssist.CENTER);
		}
		else {
			g.fill(this, getBackgroundColor());
			folderIcon.paint(g.graph, 0, 8, 4, 16, getPixelScale(), true);
			g.setColor(colorText);
			paintInfo(g, !hover);
		}
		paintBorder(g);
	}
	
	@Override
	public boolean onMouseDown(float x, float y, Button button, int mods) {
		if(button==Button.left && mods==0) {
			if(!((FolderInfo)info).ignore) {
				list.pushFolder((FolderInfo)info);
				repaint();
			}
			return true;
		}
		else
			return super.onMouseDown(x, y, button, mods);
	}
}