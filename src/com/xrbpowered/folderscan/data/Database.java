package com.xrbpowered.folderscan.data;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipInputStream;

import com.xrbpowered.folderscan.ui.UIProgressDisplay;

public class Database extends FolderInfo {

	public long scanTime = 0L;
	
	public Database() {
		super("[Home]");
	}
	
	private static String trimPath(String path) {
		if(path.endsWith("/") || path.endsWith("\\"))
			return path.substring(0, path.length()-1);
		else
			return path;
	}

	private void printProgress(UIProgressDisplay progress, String format, Object... args) {
		String s = String.format(format, args);
		if(progress!=null)
			progress.caption = s;
		System.out.println(s);
	}
	
	public Database scanFolders(Config config, UIProgressDisplay progress) {
		for(String s : config.list) {
			printProgress(progress, "Scanning %s ...", s);
			FolderInfo info = scanFolder(trimPath(s), new File(s), config);
			info.name = s;
			add(info);
		}
		scanTime = System.currentTimeMillis();
		return this;
	}
	
	public Database loadData(String dataPath, UIProgressDisplay progress) {
		printProgress(progress, "Loading data from %s ...", dataPath);
		try {
			File file = new File(dataPath);
			ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
			zip.getNextEntry();
			DataInputStream in = new DataInputStream(zip);
			
			int n = in.readInt();
			for(int i=0; i<n; i++) {
				String s = in.readUTF();
				FolderInfo info = FolderInfo.loadFolder(in);
				info.name = s;
				add(info);
			}

			zip.close();
			scanTime = file.lastModified();
			return this;
		}
		catch(Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
	
	public Database compareData(Database newData, Database oldData, Config config, UIProgressDisplay progress) {
		printProgress(progress, "Comparing ...");
		for(String s : config.list) {
			FolderInfo info = newData.folders.get(s);
			FolderInfo old = oldData.folders.get(s);
			if(old==null) {
				System.out.printf("No saved data for %s\n", s);
				info.flagAdded();
				info.sizeDiff = info.size;
				countAdded += info.countAdded;
				add(info);
			}
			else {
				FolderInfo res = FolderInfo.compareFolder(trimPath(s), info, old, config);
				res.name = s;
				res.sizeDiff = info.size - old.size;
				countAdded += res.countAdded;
				countModified += res.countModified;
				countRemoved += res.countRemoved;
				add(res);
			}
		}
		return this;
	}

}
