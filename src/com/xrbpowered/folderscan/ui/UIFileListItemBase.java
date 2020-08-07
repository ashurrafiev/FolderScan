package com.xrbpowered.folderscan.ui;

import static com.xrbpowered.folderscan.ui.Format.*;

import java.awt.Color;

import com.xrbpowered.folderscan.FolderScanUI;
import com.xrbpowered.folderscan.data.FileInfo;
import com.xrbpowered.folderscan.data.FolderInfo;
import com.xrbpowered.folderscan.ui.FileSort.SortMode;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.base.UIHoverElement;
import com.xrbpowered.zoomui.icons.IconPalette;
import com.xrbpowered.zoomui.icons.SvgIcon;
import com.xrbpowered.zoomui.std.UIListBox;
import com.xrbpowered.zoomui.std.UIListItem;
import com.xrbpowered.zoomui.std.UIToolButton;

public abstract class UIFileListItemBase extends UIHoverElement {
	
	public static final Color colorBackground = UIFileList.colorBackground;
	public static final Color colorHighlight = UIListItem.colorHighlight;
	public static final Color colorText = Color.BLACK;
	public static final Color colorTextDisabled = new Color(0x999999);
	public static final Color colorNavBackground = new Color(0xf7f7f7);
	public static final Color colorNavHighlight = new Color(0xeeeeee);
	
	public static float itemHeight = 24f;
	
	public static final SvgIcon fileIcon = new SvgIcon(UIToolButton.iconPath+"file.svg", 160, UIToolButton.palette);
	public static final SvgIcon folderIcon = new SvgIcon(UIToolButton.iconPath+"folder.svg", 160, UIToolButton.palette);
	public static final SvgIcon navIcon = new SvgIcon(UIToolButton.iconPath+"up.svg", 160, UIToolButton.palette);
	public static final SvgIcon homeIcon = new SvgIcon(UIToolButton.iconPath+"home.svg", 160, UIToolButton.palette);

	public static final SvgIcon iconCheckRemoved = new SvgIcon(UIToolButton.iconPath+"ok.svg", 160, new IconPalette(new Color[][] {
		{new Color(0xeeeeee), new Color(0xeecccc), new Color(0xaa0000), Color.RED}
	}));
	public static final SvgIcon iconCheckModified = new SvgIcon(UIToolButton.iconPath+"ok.svg", 160, new IconPalette(new Color[][] {
		{new Color(0xeeeeee), new Color(0xeeddbb), new Color(0xdd5500), new Color(0xffaa00)}
	}));
	public static final SvgIcon iconCheckMarked = new SvgIcon(UIToolButton.iconPath+"ok.svg", 160, new IconPalette(new Color[][] {
		{new Color(0xeeeeee), new Color(0xccddee), new Color(0x0077dd), new Color(0x00bbff)}
	}));
	public static final SvgIcon iconCheckAdded = new SvgIcon(UIToolButton.iconPath+"ok.svg", 160, new IconPalette(new Color[][] {
		{new Color(0xeeeeee), new Color(0xcceecc), new Color(0x007700), new Color(0x00ee00)}
	}));

	public static final Color colorRed = new Color(0xdd0000);
	public static final Color colorAmber = new Color(0xdd9900);
	public static final Color colorGreen = new Color(0x009900);

	public static final Color colorBgRed = new Color(0xfff7f0);
	public static final Color colorBgAmber = new Color(0xfffdee);
	public static final Color colorBgGreen = new Color(0xeeffee);
	public static final Color colorBgBlue = new Color(0xeef9ff);

	public final UIFileList list;
	
	public final FileInfo info;
	public final boolean first;
	
	public UIFileListItemBase(UIFileList list, FileInfo info, boolean first) {
		super(list.getView());
		this.list = list;
		this.info = info;
		this.first = first;
		setSize(0, itemHeight);
	}
	
	protected Color getBackgroundColor() {
		if(hover)
			return colorHighlight;
		else if(info.marked)
			return colorBgBlue;
		else if(info.added)
			return colorBgGreen;
		else if(info.modified)
			return colorBgAmber;
		else if(info.removed)
			return colorBgRed;
		else
			return colorBackground;
	}
	
	protected boolean isChanged() {
		return info.added || info.modified || info.removed;
	}
	
	protected void drawString(GraphAssist g, String s, UIFileListHeader.UIHeaderButton hdr) {
		drawString(g, s, hdr, 0);
	}

	protected void drawString(GraphAssist g, String s, UIFileListHeader.UIHeaderButton hdr, float tx) {
		g.drawString(s, hdr.getTextX()+tx, getHeight()/2, hdr.align, GraphAssist.CENTER);
	}

	protected void paintInfo(GraphAssist g, boolean canFill) {
		canFill &= !info.marked;
		UIFileListHeader hdr = FolderScanUI.ui.listHeader;
		
		drawString(g, info.name, hdr.headerName);
		if(info.sizeDiff!=0) {
			g.setColor(info.sizeDiff>0 ? colorGreen: colorRed);
			drawString(g, formatLargeNumber(info.sizeDiff, "(%%+.%df%%s)"), hdr.headerSizeDiff);
		}
		
		g.setColor(isChanged() ? colorText : colorTextDisabled);
		drawString(g, formatDate(info.time), hdr.headerTime);
		drawString(g, formatDateDiff(info.time), hdr.headerTime, 68);
		drawString(g, formatLargeNumber(info.size), hdr.headerSize);
		
		if(info.isFolder()) {
			if(info.added)
				g.setColor(colorGreen);
			else if(info.removed)
				g.setColor(colorRed);
			drawString(g, formatLargeNumber(info.totalFiles), hdr.headerFiles);
			
			FolderInfo folder = (FolderInfo) info;
			if(!info.added && folder.countAdded>0) {
				if(canFill)
					g.fillRect(hdr.headerChanges.getX(), 0, 50, getHeight(), colorBgGreen);
				g.setColor(colorGreen);
				drawString(g, formatLargeNumber(folder.countAdded), hdr.headerChanges, -100);
			}
			if(!info.modified && folder.countModified>0) {
				if(canFill)
					g.fillRect(hdr.headerChanges.getX()+50, 0, 50, getHeight(), colorBgAmber);
				g.setColor(colorAmber);
				drawString(g, formatLargeNumber(folder.countModified), hdr.headerChanges, -50);
			}
			if(!info.removed && folder.countRemoved>0) {
				if(canFill)
					g.fillRect(hdr.headerChanges.getX()+100, 0, 50, getHeight(), colorBgRed);
				g.setColor(colorRed);
				drawString(g, formatLargeNumber(folder.countRemoved), hdr.headerChanges, 0);
			}
		}
		
		float x = hdr.headerChanges.getTextX()-14;
		if(info.added)
			iconCheckAdded.paint(g.graph, 0, x-100, 4, 16, getPixelScale(), true);
		if(info.modified)
			iconCheckModified.paint(g.graph, 0, x-50, 4, 16, getPixelScale(), true);
		if(info.removed)
			iconCheckRemoved.paint(g.graph, 0, x, 4, 16, getPixelScale(), true);
		if(info.marked)
			iconCheckMarked.paint(g.graph, 0, hdr.headerMark.getTextX(), 4, 16, getPixelScale(), true);
	}
	
	protected void paintBorder(GraphAssist g) {
		if(first) {
			g.resetStroke();
			g.line(0,0, getWidth(), 0, UIListBox.colorBorder);
		}
	}
	
	@Override
	public boolean onMouseDown(float x, float y, Button button, int mods) {
		if(button==Button.middle || button==Button.left && mods==modCtrlMask) {
			info.marked = !info.marked;
			if(list.sort.mode==SortMode.mark)
				list.refresh();
			repaint();
			return true;
		}
		else
			return super.onMouseDown(x, y, button, mods);
	}
}