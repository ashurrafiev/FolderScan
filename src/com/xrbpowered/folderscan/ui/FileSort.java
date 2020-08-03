package com.xrbpowered.folderscan.ui;

import java.util.Comparator;

import com.xrbpowered.folderscan.data.FileInfo;
import com.xrbpowered.folderscan.data.FolderInfo;

public class FileSort implements Comparator<FileInfo> {

	public enum SortMode {
		name, size, sizeDiff, files, changes, time, mark
	}
	
	public SortMode mode = SortMode.changes;
	
	private int compareIgnore(FileInfo o1, FileInfo o2) {
		if(o1.isFolder() && o2.isFolder()) {
			FolderInfo f1 = (FolderInfo)o1;
			FolderInfo f2 = (FolderInfo)o2;
			return Boolean.compare(f1.ignore, f2.ignore);
		}
		else
			return 0;
	}
	
	@Override
	public int compare(FileInfo o1, FileInfo o2) {
		int res;
		switch(mode) {
			case size:
				res = -Long.compare(o1.size, o2.size);
				if(res==0)
					res = compareIgnore(o1, o2);
				break;
			case sizeDiff:
				res = compareIgnore(o1, o2);
				if(res==0)
					res = -Long.compare(Math.abs(o1.sizeDiff), Math.abs(o2.sizeDiff));
				break;
			case files:
				res = -Integer.compare(o1.totalFiles, o2.totalFiles);
				if(res==0)
					res = compareIgnore(o1, o2);
				break;
			case changes:
				res = compareIgnore(o1, o2);
				if(res==0)
					res = -Boolean.compare(o1.added, o2.added);
				if(res==0)
					res = -Boolean.compare(o1.modified, o2.modified);
				if(res==0)
					res = -Boolean.compare(o1.removed, o2.removed);
				if(res==0 && o1.isFolder() && o2.isFolder()) {
					FolderInfo f1 = (FolderInfo)o1;
					FolderInfo f2 = (FolderInfo)o2;
					res = -Integer.compare(
							f1.countAdded+f1.countModified+f1.countRemoved,
							f2.countAdded+f2.countModified+f2.countRemoved
						);
				}
				break;
			case time:
				res = -Long.compare(o1.time, o2.time);
				break;
			case mark:
				res = -Boolean.compare(o1.marked, o2.marked);
				break;
			default:
				res = 0;
		}
		if(res==0)
			res = o1.name.compareToIgnoreCase(o2.name);
		return res;
	}
}
