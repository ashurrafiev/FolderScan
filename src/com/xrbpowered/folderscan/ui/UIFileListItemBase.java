package com.xrbpowered.folderscan.ui;

import static com.xrbpowered.folderscan.ui.Format.*;

import java.awt.Color;

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
	
	protected void paintInfo(GraphAssist g, boolean canFill) {
		canFill &= !info.marked;
		
		g.drawString(info.name, 32, getHeight()/2, GraphAssist.LEFT, GraphAssist.CENTER);
		if(info.sizeDiff!=0) {
			g.setColor(info.sizeDiff>0 ? colorGreen: colorRed);
			g.drawString(formatLargeNumber(info.sizeDiff, "(%%+.%df%%s)"), 408, getHeight()/2, GraphAssist.LEFT, GraphAssist.CENTER);
		}
		
		g.setColor(isChanged() ? colorText : colorTextDisabled);
		g.drawString(formatDate(info.time), 780, getHeight()/2, GraphAssist.LEFT, GraphAssist.CENTER);
		g.drawString(formatDateDiff(info.time), 848, getHeight()/2, GraphAssist.LEFT, GraphAssist.CENTER);
		g.drawString(formatLargeNumber(info.size), 400, getHeight()/2, GraphAssist.RIGHT, GraphAssist.CENTER);
		
		if(info.isFolder()) {
			if(info.added)
				g.setColor(colorGreen);
			else if(info.removed)
				g.setColor(colorRed);
			g.drawString(formatLargeNumber(info.totalFiles), 550, getHeight()/2, GraphAssist.RIGHT, GraphAssist.CENTER);
			
			FolderInfo folder = (FolderInfo) info;
			if(!info.added && folder.countAdded>0) {
				if(canFill)
					g.fillRect(628-50, 0, 50, getHeight(), colorBgGreen);
				g.setColor(colorGreen);
				g.drawString(formatLargeNumber(folder.countAdded), 620, getHeight()/2, GraphAssist.RIGHT, GraphAssist.CENTER);
			}
			if(!info.modified && folder.countModified>0) {
				if(canFill)
					g.fillRect(678-50, 0, 50, getHeight(), colorBgAmber);
				g.setColor(colorAmber);
				g.drawString(formatLargeNumber(folder.countModified), 670, getHeight()/2, GraphAssist.RIGHT, GraphAssist.CENTER);
			}
			if(!info.removed && folder.countRemoved>0) {
				if(canFill)
					g.fillRect(728-50, 0, 50, getHeight(), colorBgRed);
				g.setColor(colorRed);
				g.drawString(formatLargeNumber(folder.countRemoved), 720, getHeight()/2, GraphAssist.RIGHT, GraphAssist.CENTER);
			}
		}
		
		if(info.added)
			iconCheckAdded.paint(g.graph,0, 608, 4, 16, getPixelScale(), true);
		if(info.modified)
			iconCheckModified.paint(g.graph,0, 658, 4, 16, getPixelScale(), true);
		if(info.removed)
			iconCheckRemoved.paint(g.graph,0, 708, 4, 16, getPixelScale(), true);
		if(info.marked)
			iconCheckMarked.paint(g.graph,0, 968+8, 4, 16, getPixelScale(), true);
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