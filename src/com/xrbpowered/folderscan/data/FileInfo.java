package com.xrbpowered.folderscan.data;

import java.io.File;

public class FileInfo {

	public String name;
	public long size = 0;
	public long time = 0;
	public int totalFiles = 1;
	
	public boolean added = false;
	public boolean modified = false;
	public boolean removed = false;
	public long sizeDiff = 0;
	
	public boolean marked = false;
	
	public FileInfo() {
	}
	
	public FileInfo(File file) {
		name = file.getName();
		size = file.length();
		time = file.lastModified();
	}
	
	public boolean isFolder() {
		return false;
	}

}
