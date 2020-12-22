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
			if(save)
				System.err.println("*** Warning: Not saved because not scanned!");
			if(oldData!=null && oldData.scanTime>newData.scanTime) {
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
	
	private static void help() {
		System.out.println();
		System.out.println("Usage:");
		System.out.println("java -jar folderscan.jar [options]");
		System.out.println();
		System.out.println("Options:");
		System.out.println("-nogui\n\tDo not open GUI window.");
		System.out.println("-cfg filename\n\tLoad config from filename. If not specified, folderscan.cfg will be used.");
		System.out.println("-d path\n\tRecursively scan specified directory. Config is ignored.");
		System.out.println("-out filename\n\tWrite scan snapshot to a file.");
		System.out.println("-save\n\tWrite scan snapshot to folderscan.data. Same as -out folderscan.data.");
		System.out.println("-in filename\n\tLoad snapshot from filename.\n\tBy default, folderscan.data is loaded unless -nocmp is specified.");
		System.out.println("-alt filename\n\tLoad another snapshot from filename.\n\tCan be used to compare between two snapshots.");
		System.out.println("-nocmp\n\tDo not compare.");
		System.out.println("-help\n\tShow this help and quit.");
		System.exit(1);
	}
	
	private static String getArg(String[] args, int i, String prompt) {
		if(i>=args.length || args[i].startsWith("-")) {
			System.err.printf("Expected %s after %s\n", prompt, args[i-1]);
			help();
			return null;
		}
		else {
			return args[i];
		}
	}
	
	public static void main(String[] args) {
		for(int i=0; i<args.length; i++) {
			switch(args[i]) {
				case "-help":
					help();
					break;
					
				case "-cfg":
					config = Config.loadConfig(getArg(args, ++i, "config file name"));
					break;
				case "-d":
					config = Config.forDirectory(getArg(args, ++i, "directory path"));
					break;
				case "-nogui":
					ui = false;
					break;
					
				case "-in":
					dataPath = getArg(args, ++i, "snapshot file name");
					break;
				case "-alt":
					altDataPath = getArg(args, ++i, "snapshot file name");
					break;
				case "-nocmp":
					noCompare = true;
					break;
					
				case "-out":
					save = true;
					savePath = getArg(args, ++i, "output file name");
					break;
				case "-save":
					save = true;
					break;
					
				default:
					System.err.println("Unknown option: "+args[i]);
					help();
			}
		}

		if(ui)
			FolderScanUI.startUI();
		else
			processData(null);
	}

}
