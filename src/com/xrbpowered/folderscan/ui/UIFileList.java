package com.xrbpowered.folderscan.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;

import com.xrbpowered.folderscan.data.FileInfo;
import com.xrbpowered.folderscan.data.FolderInfo;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.base.UIScrollContainerBase;
import com.xrbpowered.zoomui.std.UIButton;
import com.xrbpowered.zoomui.std.UIListBox;
import com.xrbpowered.zoomui.std.UIScrollBar;
import com.xrbpowered.zoomui.std.UIScrollContainer;

public class UIFileList extends UIScrollContainerBase {

	public static final Color colorBackground = Color.WHITE;

	private LinkedList<UINavItem> nav = new LinkedList<>();
	private ArrayList<UIFolderItem> folders = new ArrayList<>();
	private ArrayList<UIFileItem> files = new ArrayList<>();

	public FileSort sort = new FileSort();
	
	public UIFileList(UIContainer parent) {
		super(parent);
	}

	@Override
	protected UIScrollBar createScroll() {
		return UIScrollContainer.createScroll(this);
	}

	public void reset(FolderInfo info) {
		getView().removeAllChildren();
		nav.clear();
		nav.add(new UINavItem(this, info, nav.isEmpty()));
		setFolder(info);
	}

	public void setFolder(FolderInfo info) {
		for(UIFolderItem item : folders)
			getView().removeChild(item);
		folders.clear();
		for(UIFileItem item : files)
			getView().removeChild(item);
		files.clear();
		
		boolean first = true;
		
		ArrayList<FolderInfo> folderInfo = new ArrayList<>(info.folders.values());
		folderInfo.sort(sort);
		for(FolderInfo folder : folderInfo) {
			folders.add(new UIFolderItem(this, folder, first));
			first = false;
		}
		
		ArrayList<FileInfo> fileInfo = new ArrayList<>(info.files.values());
		fileInfo.sort(sort);
		for(FileInfo file : fileInfo) {
			files.add(new UIFileItem(this, file, first));
			first = false;
		}
	}
	
	public void pushFolder(FolderInfo info) {
		if(nav.isEmpty() || nav.getLast().info!=info) {
			nav.add(new UINavItem(this, info, nav.isEmpty()));
			setFolder(info);
		}
	}
	
	public void refresh() {
		setFolder((FolderInfo)nav.getLast().info);
	}
	
	public boolean isCurrent(FileInfo info) {
		return nav.getLast().info==info;
	}
	
	public void navigate(UINavItem ni) {
		if(isCurrent(ni.info))
			return;
		
		while(nav.getLast().info!=ni.info) {
			getView().removeChild(nav.removeLast());
		}
		setFolder((FolderInfo)ni.info);
		repaint();
	}

	@Override
	protected void paintSelf(GraphAssist g) {
		Format.now = System.currentTimeMillis();
		
		g.fill(this, colorBackground);
		g.setFont(UIButton.font);
	}
	
	@Override
	protected void paintBorder(GraphAssist g) {
		g.hborder(this, GraphAssist.TOP, UIListBox.colorBorder);
	}
	
	private float layout(UIElement item, float w, float y) {
		item.setLocation(0, y);
		float h = item.getHeight();
		item.setSize(w, h);
		return y+h;
	}
	
	@Override
	protected float layoutView() {
		float w = getView().getWidth();
		float y = 0;
		for(UINavItem item : nav)
			y = layout(item, w, y);
		for(UIFolderItem item : folders)
			y = layout(item, w, y);
		for(UIFileItem item : files)
			y = layout(item, w, y);
		return y;
	}

}
