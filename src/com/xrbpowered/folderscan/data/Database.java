package com.xrbpowered.folderscan.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.xrbpowered.folderscan.ui.Format;
import com.xrbpowered.folderscan.ui.UIProgressDisplay;

public class Database extends FolderInfo {

	public static final String versionId = "FolderScan-v1.1";
	
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

	public void saveData(String dataPath) {
		saveData(dataPath, false);
	}

	public void saveData(String dataPath, boolean fallback) {
		try {
			ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(new File(dataPath)));
			zip.putNextEntry(new ZipEntry("data"));
			DataOutputStream out = new DataOutputStream(zip);
			
			if(!fallback) {
				out.writeUTF(versionId);
				out.writeLong(scanTime);
			}
			
			out.writeInt(folders.size());
			for(Entry<String, FolderInfo> e : folders.entrySet()) {
				out.writeUTF(e.getKey());
				e.getValue().save(out);
			}
			
			zip.closeEntry();
			zip.close();
			System.out.println("Data saved");
		}
		catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public Database loadData(String dataPath, UIProgressDisplay progress) {
		return loadData(dataPath, progress, false);
	}

	public Database loadData(String dataPath, UIProgressDisplay progress, boolean fallback) {
		if(!fallback)
			printProgress(progress, "Loading data from %s ...", dataPath);
		else
			System.out.println("... attempting to load old version format ...");
		try {
			File file = new File(dataPath);
			ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
			zip.getNextEntry();
			DataInputStream in = new DataInputStream(zip);

			scanTime = file.lastModified();
			if(!fallback) {
				boolean fallbackVersion = true;
				try {
					if(in.readUTF().equals(versionId)) {
						scanTime = in.readLong();
						fallbackVersion = false;
					}
				}
				catch(Exception e) {
				}
				if(fallbackVersion) {
					zip.close();
					return loadData(dataPath, progress, true);
				}
			}
			
			int n = in.readInt();
			for(int i=0; i<n; i++) {
				String s = in.readUTF();
				FolderInfo info = FolderInfo.loadFolder(in);
				info.name = s;
				add(info);
			}

			zip.close();
			System.out.printf("Scan date: %s\n", Format.formatDate(scanTime));
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
