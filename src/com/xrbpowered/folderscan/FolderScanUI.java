package com.xrbpowered.folderscan;

import java.awt.Color;

import com.xrbpowered.folderscan.data.Database;
import com.xrbpowered.folderscan.ui.SafeThread;
import com.xrbpowered.folderscan.ui.UIFileList;
import com.xrbpowered.folderscan.ui.UIFileListHeader;
import com.xrbpowered.folderscan.ui.UIProgressDisplay;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.std.UIScrollBar;
import com.xrbpowered.zoomui.swing.SwingFrame;
import com.xrbpowered.zoomui.swing.SwingWindowFactory;

public class FolderScanUI extends UIContainer {

	public static FolderScanUI ui;
	
	public final UIFileListHeader listHeader;
	public final UIFileList fileList;
	
	private UIProgressDisplay progress; 
	
	public FolderScanUI(UIContainer parent) {
		super(parent);
		ui = this;
		listHeader = new UIFileListHeader(this);
		fileList = new UIFileList(this);
		
		progress = new UIProgressDisplay(this);
		
		new SafeThread() {
			public Database data = null;
			@Override
			public void run() {
				data = FolderScan.processData(progress);
				safeUIRunAsync();
			}
			@Override
			protected void uiRun() {
				if(data!=null)
					fileList.reset(data);
				progress.dismiss();
				progress = null;
				repaint();
			}
		}.start();
	}

	@Override
	public void layout() {
		listHeader.setLocation(0, 0);
		listHeader.setSize(getWidth() - UIScrollBar.defaultWidth, listHeader.getHeight());
		fileList.setLocation(0, listHeader.getHeight());
		fileList.setSize(getWidth(), getHeight()-listHeader.getHeight());
		
		if(progress!=null) {
			progress.setLocation(0, 0);
			progress.setSize(getWidth(), getHeight());
		}
		
		super.layout();
	}
	
	@Override
	protected void paintSelf(GraphAssist g) {
		g.fill(this, Color.WHITE);
	}
	
	public static void startUI( ) {
		SwingFrame frame = new SwingFrame(SwingWindowFactory.use(), "FolderScan", 1200, 800, true, false) {
			@Override
			public boolean onClosing() {
				confirmClosing();
				return false;
			}
		};
		new FolderScanUI(frame.getContainer());
		frame.show();
	}
	
	public static void main(String[] args) {
		startUI();
	}

}
