package com.xrbpowered.folderscan.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Format {

	private static String[] units = {"", "K", "M", "G"};
	
	public static String formatLargeNumber(double size, String template) {
		int unit = 0;
		double sgn = size<0 ? -1.0 : 1.0;
		size = Math.abs(size);
		while(size>=1000 && unit<units.length) {
			size /= 1000.0;
			unit++;
		}
		int prec = (size>=100 || unit==0) ? 0 : size>=10 ? 1 : 2;
		if(prec==2 && Math.round(size*100)==Math.round(size*10)*10)
			prec = 1;
		if(prec==1 && Math.round(size*10)==Math.round(size)*10)
			prec = 0;
		String fmt = String.format(template, prec);
		return String.format(fmt, size*sgn, units[unit]);
	}
	public static String formatLargeNumber(double size) {
		return formatLargeNumber(size, "%%.%df%%s");
	}
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String formatDate(long date) {
		if(date==0L)
			return "";
		return dateFormat.format(new Date(date));
	}

	public static long now = 0L;
	
	public static String formatDateDiff(long date) {
		if(date==0L)
			return "";
		long d = 24L*3600L*1000L;
		long days = (now-date)/d;
		if(days>=365) {
			int yrs = (int)Math.round(days/365.0);
			return yrs==1 ? "(1 year ago)" : String.format("(%d years ago)", yrs);
		}
		return days<1 ? "(today)" : days==1 ? "(1 day ago)" : String.format("(%d days ago)", days);
	}

}
