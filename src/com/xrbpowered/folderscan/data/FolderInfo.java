package com.xrbpowered.folderscan.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class FolderInfo extends FileInfo {

	public boolean ignore = false;
	
	public HashMap<String, FolderInfo> folders = new HashMap<>();
	public HashMap<String, FileInfo> files = new HashMap<>();
	
	public int countAdded = 0;
	public int countModified = 0;
	public int countRemoved = 0;
	
	public FolderInfo(String name) {
		this.name = name;
		totalFiles = 0;
	}

	@Override
	public boolean isFolder() {
		return true;
	}
	
	public void add(FileInfo info) {
		if(info==null)
			return;
		size += info.size;
		time = Math.max(time, info.time);
		if(info.isFolder()) {
			FolderInfo sub = (FolderInfo)info;
			totalFiles += sub.totalFiles;
			folders.put(info.name, sub);
		}
		else {
			totalFiles++;
			files.put(info.name, info);
		}
	}
	
	public void flagAdded() {
		added = true;
		countAdded = totalFiles;
		sizeDiff = size;
		for(FolderInfo dir : folders.values())
			dir.flagAdded();
		for(FileInfo file: files.values()) {
			file.added = true;
			file.sizeDiff = file.size;
		}
	}

	public void flagRemoved() {
		removed = true;
		countRemoved = totalFiles;
		sizeDiff = -size;
		for(FolderInfo dir : folders.values())
			dir.flagRemoved();
		for(FileInfo file: files.values()) {
			file.removed = true;
			file.sizeDiff = -file.size;
		}
	}
	
	public void save(DataOutputStream out) throws IOException {
		out.writeUTF(name);
		out.writeInt(folders.size());
		for(FolderInfo info : folders.values()) {
			info.save(out);
		}
		out.writeInt(files.size());
		for(FileInfo f : files.values()) {
			out.writeUTF(f.name);
			out.writeLong(f.size);
			out.writeLong(f.time);
		}
	}

	public static FolderInfo scanFolder(String path, File dir, Config config) {
		FolderInfo info = new FolderInfo(dir.getName());
		if(config.ignorePath(path)) {
			info.ignore = true;
			return info;
		}
		File[] files = dir.listFiles();
		if(files!=null) {
			for(File file : files) {
				String name = file.getName();
				if(name.equals(".") || name.equals(".."))
					continue;
				if(file.isDirectory()) {
					info.add(scanFolder(path+"/"+name, file, config));
				}
				else {
					info.add(new FileInfo(file));
				}
			}
		}
		return info;
	}
	
	public static FolderInfo loadFolder(DataInputStream in) throws IOException {
		String name = in.readUTF();
		FolderInfo info = new FolderInfo(name);
		int nFolders = in.readInt();
		for(int i=0; i<nFolders; i++) {
			info.add(loadFolder(in));
		}
		int nFiles = in.readInt();
		for(int i=0; i<nFiles; i++) {
			FileInfo f = new FileInfo();
			f.name = in.readUTF();
			f.size = in.readLong();
			f.time = in.readLong();
			info.add(f);
		}
		return info;
	}
	
	public static FolderInfo compareFolder(String path, FolderInfo info, FolderInfo old, Config config) {
		FolderInfo res = new FolderInfo(info.name);
		if(config.ignorePath(path)) {
			info.ignore = true;
			return info;
		}
		
		for(FolderInfo dir : info.folders.values()) {
			FolderInfo dirOld = old.folders.get(dir.name);
			if(dirOld!=null) {
				FolderInfo r = compareFolder(path+"/"+dir.name, dir, dirOld, config);
				r.sizeDiff = dir.size - dirOld.size;
				res.countAdded += r.countAdded;
				res.countModified += r.countModified;
				res.countRemoved += r.countRemoved;
				res.add(r);
			}
			else {
				dir.flagAdded();
				res.countAdded += dir.countAdded;
				res.add(dir);
			}
		}
		for(FolderInfo dir : old.folders.values()) {
			if(!info.folders.containsKey(dir.name)) {
				dir.flagRemoved();
				res.countRemoved += dir.countRemoved;
				res.add(dir);
			}
		}
		
		for(FileInfo file : info.files.values()) {
			FileInfo fileOld = old.files.get(file.name);
			if(fileOld!=null) {
				if(file.time!=fileOld.time || file.size!=fileOld.size) {
					file.modified = true;
					file.sizeDiff = file.size - fileOld.size;
					res.countModified++;
				}
			}
			else {
				file.added = true;
				file.sizeDiff = file.size;
				res.countAdded++;
			}
			res.add(file);
		}
		for(FileInfo file : old.files.values()) {
			if(!info.files.containsKey(file.name)) {
				file.removed = true;
				file.sizeDiff = -file.size;
				res.countRemoved++;
				res.add(file);
			}
		}
		
		return res;
	}
	
}
