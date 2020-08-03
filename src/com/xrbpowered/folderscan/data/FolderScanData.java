package com.xrbpowered.folderscan.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FolderScanData {

	public static ArrayList<String> list = new ArrayList<>();
	public static ArrayList<String> ignore = new ArrayList<>();

	public static String cfgPath = "./folderscan.cfg";
	public static String dataPath = "./folderscan.data";
	
	public static String regex(String s) {
		return s.replaceAll("([^A-Za-z0-9*])", "\\\\$1").replaceAll("\\*", ".*?")+"$";
	}
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String formatDate(long date) {
		return dateFormat.format(new Date(date));
	}
	
	public static void loadConfig() {
		System.out.printf("Loading config ...\n");
		try {
			Scanner in = new Scanner(new File(cfgPath));
			boolean ignores = false;
			while(in.hasNextLine()) {
				String line = in.nextLine().trim();
				if(!line.isEmpty()) {
					if(line.equalsIgnoreCase("ignore:"))
						ignores = true;
					else if(ignores)
						ignore.add(regex(line));
					else
						list.add(line);
				}
			}
			in.close();
			
			if(list.isEmpty()) {
				System.err.println("No items");
				System.exit(1);
			}
		}
		catch(Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	
	public static HashMap<String, FolderInfo> oldInfo = new HashMap<>();
	public static HashMap<String, FolderInfo> newInfo = new HashMap<>();

	public static class FileInfo {
		public String name;
		public long size;
		public long modified;

		public FileInfo() {
		}
		
		public FileInfo(File file) {
			name = file.getName();
			size = file.length();
			modified = file.lastModified();
		}
	}

	public static class FolderInfo {
		public String name;
		public HashMap<String, FolderInfo> folders = new HashMap<>();
		public HashMap<String, FileInfo> files = new HashMap<>();
		
		public FolderInfo(File dir) {
			name = dir.getName();
		}

		public FolderInfo(DataInputStream in) throws IOException {
			name = in.readUTF();
			int nFolders = in.readInt();
			for(int i=0; i<nFolders; i++) {
				add(new FolderInfo(in));
			}
			int nFiles = in.readInt();
			for(int i=0; i<nFiles; i++) {
				FileInfo f = new FileInfo();
				f.name = in.readUTF();
				f.size = in.readLong();
				f.modified = in.readLong();
				add(f);
			}
		}

		public void add(FolderInfo info) {
			if(info!=null)
				folders.put(info.name, info);
		}
		
		public void add(FileInfo info) {
			files.put(info.name, info);
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
				out.writeLong(f.modified);
			}
		}
	}
	
	public static boolean ignorePath(String path) {
		for(String rs : ignore) {
			if(path.matches(rs))
				return true;
		}
		return false;
	}
	
	public static FolderInfo scanFolder(String path, File dir) {
		if(ignorePath(path))
			return null;
		FolderInfo info = new FolderInfo(dir);
		File[] files = dir.listFiles();
		if(files!=null) {
			for(File file : files) {
				String name = file.getName();
				if(name.equals(".") || name.equals(".."))
					continue;
				if(file.isDirectory()) {
					info.add(scanFolder(path+"/"+name, file));
				}
				else {
					info.add(new FileInfo(file));
				}
			}
		}
		return info;
	}

	private static boolean report(String msg, String path, FolderInfo dir) {
		System.out.printf("%s: %s/%s\n", msg, path, dir.name);
		return true;
	}

	private static boolean report(String msg, boolean prompt, String path, FileInfo file) {
		if(!prompt)
			System.out.printf("In %s:\n", path);
		System.out.printf("   %s: %s\n", msg, file.name);
		return true;
	}
	
	public static void compare(String path, FolderInfo info, FolderInfo old) {
		for(String rs : ignore) {
			if(path.matches(rs))
				return;
		}
		for(FolderInfo dir : info.folders.values()) {
			FolderInfo dirOld = old.folders.get(dir.name);
			if(dirOld!=null)
				compare(path+"/"+dir.name, dir, dirOld);
			else
				report("Added folder", path, dir);
		}
		for(FolderInfo dir : old.folders.values()) {
			if(!info.folders.containsKey(dir.name))
				report("Removed folder", path, dir);
		}
		boolean prompt = false;
		for(FileInfo file : info.files.values()) {
			FileInfo fileOld = old.files.get(file.name);
			if(fileOld!=null) {
				if(file.modified!=fileOld.modified || file.size!=fileOld.size)
					prompt = report("Modified on "+formatDate(file.modified), prompt, path, file);
			}
			else
				prompt = report("Added", prompt, path, file);
		}
		for(FileInfo file : old.files.values()) {
			if(!info.files.containsKey(file.name))
				prompt = report("Removed", prompt, path, file);
		}
	}
	
	public static void saveData() {
		System.out.printf("Saving data ...\n");
		try {
			ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(new File(dataPath)));
			zip.putNextEntry(new ZipEntry("data"));
			DataOutputStream out = new DataOutputStream(zip);
			
			out.writeInt(newInfo.size());
			for(Entry<String, FolderInfo> e : newInfo.entrySet()) {
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
	
	public static void loadData(String dataPath) {
		System.out.printf("Loading data ...\n");
		try {
			File file = new File(dataPath);
			long total = file.length();
			FileInputStream fin = new FileInputStream(file);
			ZipInputStream zip = new ZipInputStream(fin);
			zip.getNextEntry();
			DataInputStream in = new DataInputStream(zip);
			
			int n = in.readInt();
			for(int i=0; i<n; i++) {
				String s = in.readUTF();
				FolderInfo info = new FolderInfo(in);
				oldInfo.put(s, info);
				System.out.printf("%d / %d\n", fin.getChannel().position(), total);
			}

			zip.close();
			long modified = file.lastModified();
			System.out.println("Looking for modifications since "+formatDate(modified));
		}
		catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	private static String trimPath(String path) {
		if(path.endsWith("/") || path.endsWith("\\"))
			return path.substring(0, path.length()-1);
		else
			return path;
	}

	public static FolderInfo scanPath(String s) {
		return scanFolder(trimPath(s), new File(s));
	}
	
	/*public static void main(String[] args) {
		String data = dataPath;
		boolean save = false;
		for(int i=0; i<args.length; i++) {
			if(args[i].equals("-save"))
				save = true;
			else if(args[i].equals("-in"))
				data = args[++i];
		}
		
		loadConfig();
		loadData(data);
		
		for(String s : list) {
			System.out.printf("Scanning %s ...\n", s);
			FolderInfo info = scanFolder(trimPath(s), new File(s));
			newInfo.put(s, info);
		}
		if(save) saveData();

		System.out.printf("Comparing ...\n");
		for(String s : list) {
			FolderInfo info = newInfo.get(s);
			FolderInfo old = oldInfo.get(s);
			if(old==null)
				System.out.printf("No saved data for %s\n", s);
			else
				compare(trimPath(s), info, old);
		}
		System.out.printf("Done\n");
	}*/

}
