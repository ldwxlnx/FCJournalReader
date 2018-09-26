package de.ovgu.spldev.featurecopp;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import de.ovgu.spldev.featurecopp.xml.IXMLParser;
import de.ovgu.spldev.featurecopp.xml.IXMLParser.SVCandidate;
import de.ovgu.spldev.featurecopp.xml.IXMLParser.XMLParserException;
import de.ovgu.spldev.featurecopp.xml.stax.FCJStaXParser;

public class MainProgram {
	
	public static void usage() {
		System.out.println("FCJR.jar <FeatureCoPP_report.xml> <csvfile> --sv=<good|bad|ugly|all|gbg> [<N>]");
		System.out.println("\tCreates CSV file excerpt by given sv-filter");
		System.out.println("\tN denotes the output of top-n-features based on their occurrences/roles (standard=10)");
	}
	
	/*
	 * <semantics SV="0.52" ER="0.00"
	 * funcdefs="0.0" totaldecls="2.0" funcdecls="1.0"
	 * structdecls="0.0" vardecls="1.0" symtotal="0.0"
	 * symbound="0.0" symunbound="0.0" stmts="0.0"
	 * exprs="0.0" funcalls="0.0" cppdir="0.0"
	 * include="0.0" comments="0.0"/>
	 */
	public static void main(String[] args) {
		if(args.length < 3 || 4 < args.length) {
			usage();
			return;
		}
		try {
			if(args.length == 4) {
				long top_n = Long.parseLong(args[3], 10);
				Configuration.SHOW_TOP_N_FEATURES = top_n;
			}
			String[] split_args = args[2].split("=", 2);
			if(split_args.length != 2) {
				System.err.println("Invalid pspot argument! Refusing..." );
				usage();
				System.exit(1);
			}
			if(! "--sv".equals(split_args[0])) {
				System.out.println("Unknown option " + split_args[0] + "! Refusing...");
				usage();
				System.exit(1);
			}
			String sv_val = split_args[1];
			SVCandidate filter = SVCandidate.GOOD;
			switch(sv_val) {
			case "good":
				break;
			case "bad":
				filter = SVCandidate.BAD;
				break;
			case "ugly":
				filter = SVCandidate.UGLY;
				break;
			case "all":
				filter = SVCandidate.ALL;
				break;
			case "gbg":
				filter = SVCandidate.GOODBADGOOD;
				break;
			default:
				System.err.println("Invalid sv (" + sv_val + ")! Refusing..." );
				usage();
				System.exit(1);
			}
			System.out.println("Searching sv=" + filter);
			PrintStream csvStrm = new PrintStream(args[1]);
			IXMLParser xmlParser = IXMLParser.createXMLParser(args[0], csvStrm, filter);
			xmlParser.parse();
			System.out.println(xmlParser.psStatsToString());
			csvStrm.close();			
		} catch (XMLParserException | FileNotFoundException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

	}

}
