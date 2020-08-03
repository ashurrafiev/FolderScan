package com.xrbpowered.folderscan.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Config {

	public ArrayList<String> list = new ArrayList<>();
	public ArrayList<String> ignore = new ArrayList<>();

	private static String regex(String s) {
		return s.replaceAll("([^A-Za-z0-9*])", "\\\\$1").replaceAll("\\*", ".*?")+"$";
	}
	
	public boolean ignorePath(String path) {
		for(String rs : ignore) {
			if(path.matches(rs))
				return true;
		}
		return false;
	}
	
	private Config load(String path) {
		System.out.printf("Loading config ...\n");
		try {
			Scanner in = new Scanner(new File(path));
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
		return this;
	}
	
	public static Config loadConfig(String path) {
		return new Config().load(path);
	}
	
}
