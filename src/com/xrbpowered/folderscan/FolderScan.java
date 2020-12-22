package com.xrbpowered.folderscan;

import com.xrbpowered.folderscan.data.Config;
import com.xrbpowered.folderscan.data.Database;
import com.xrbpowered.folderscan.ui.UIProgressDisplay;

public class FolderScan {

	public static final String defaultDataPath = "./folderscan.data";
	public static final String defaultConfigPath = "./folderscan.cfg";
	
	public static Config config = null;
	
	public static boolean ui = true;
	
	public static String dataPath = null;
	public static String altDataPath = null;
	public static boolean noCompare = false;
	
	public static boolean save = false;
	public static String savePath = defaultDataPath;
	
	public static Database processData(UIProgressDisplay progress) {
		if(config==null)
			config = Config.loadConfig(defaultConfigPath);

		Database newData = null;
		Database oldData = null;
		if(dataPath==null && altDataPath==null) {
			if(!noCompare)
				dataPath = defaultDataPath;
		}
		if(dataPath==null && altDataPath!=null) {
			dataPath = altDataPath;
			altDataPath = null;
		}
		if(noCompare) {
			if(dataPath!=null)
				newData = new Database().loadData(dataPath, progress);
		}
		else {
			if(altDataPath!=null) {
				oldData = new Database().loadData(altDataPath, progress);
				newData = new Database().loadData(dataPath, progress);
			}
			else {
				oldData = new Database().loadData(dataPath, progress);
			}
		}
		
		if(newData==null) {
			newData = new Database().scanFolders(config, progress);
			if(oldData!=null && oldData.scanTime>newData.scanTime)
				System.err.println("*** Warning: Loaded snapshot is from the future!");
			if(save)
				newData.saveData(savePath);
		}
		else {
			if(oldData.scanTime>newData.scanTime) {
				Database d = oldData;
				oldData = newData;
				newData = d;
			}
		}
		
		if(oldData==null)
			return newData;
		else
			return new Database().compareData(newData, oldData, config, progress);
	}
	
	public static void main(String[] args) {
		for(int i=0; i<args.length; i++) {
			switch(args[i]) {
				case "-cfg":
					config = Config.loadConfig(args[++i]);
					break;
				case "-nogui":
					ui = false;
					break;
					
				case "-in":
					dataPath = args[++i];
					break;
				case "-alt":
					altDataPath = args[++i];
					break;
				case "-nocmp":
					noCompare = true;
					break;
					
				case "-out":
					save = true;
					savePath = args[++i];
					break;
				case "-save":
					save = true;
					break;
					
				default:
					System.err.println("Unknown option: "+args[i]);
					// TODO print usage
					System.exit(1);
			}
		}

		if(ui)
			FolderScanUI.startUI();
		else
			processData(null);
	}

}
