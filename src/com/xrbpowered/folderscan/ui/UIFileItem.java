package com.xrbpowered.folderscan.ui;

import com.xrbpowered.folderscan.data.FileInfo;
import com.xrbpowered.zoomui.GraphAssist;

public class UIFileItem extends UIFileListItemBase {
	
	public UIFileItem(UIFileList list, FileInfo info, boolean first) {
		super(list, info, first);
	}
	
	@Override
	public void paint(GraphAssist g) {
		g.fill(this, getBackgroundColor());
		fileIcon.paint(g.graph, 0, 8, 4, 16, getPixelScale(), true);
		g.setColor(colorText);
		paintInfo(g, !hover);
		paintBorder(g);
	}
}