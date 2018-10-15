package de.ovgu.spldev.featurecopp;

import java.io.File;

public class Configuration {
	public final static String LINESEP = System.lineSeparator();
	public static long SHOW_TOP_N_FEATURES = Long.MAX_VALUE;
	public static File LOGFILE = new File("fcjournalreader.log");
	public static File PROJCSV = new File("fcproj.txt");
	public static File FEATCSV = new File("fcfeat.txt");
	public static void setOutfiles(File outdir) {
		if(outdir.isDirectory()) {
			LOGFILE = new File(outdir, LOGFILE.toString());
			PROJCSV = new File(outdir, PROJCSV.toString());
			FEATCSV = new File(outdir, FEATCSV.toString());
		} 
	}
}
