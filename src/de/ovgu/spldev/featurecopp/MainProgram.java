package de.ovgu.spldev.featurecopp;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import de.ovgu.spldev.featurecopp.xml.IXMLParser;
import de.ovgu.spldev.featurecopp.xml.IXMLParser.PSPOTCandidate;
import de.ovgu.spldev.featurecopp.xml.IXMLParser.XMLParserException;
import de.ovgu.spldev.featurecopp.xml.stax.FCJStaXParser;

public class MainProgram {
	
	public static void usage() {
		System.out.println("FCJR.jar <FeatureCoPP_report.xml> <csvfile> --pspot=<good|bad|ugly|all|gbg> [<N>]");
		System.out.println("\tCreates CSV file excerpt by given pspot-filter");
		System.out.println("\tN denotes the output of top-n-features based on their occurrences/roles (standard=10)");
	}
	
	/*
	 * <semantics PSPOT="0.52" CS="0.52" ER="0.00"
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
			String pspot_val = split_args[1];
			PSPOTCandidate filter = PSPOTCandidate.GOOD;
			switch(pspot_val) {
			case "good":
				break;
			case "bad":
				filter = PSPOTCandidate.BAD;
				break;
			case "ugly":
				filter = PSPOTCandidate.UGLY;
				break;
			case "all":
				filter = PSPOTCandidate.ALL;
				break;
			case "gbg":
				filter = PSPOTCandidate.GOODBADGOOD;
				break;
			default:
				System.err.println("Invalid pspot (" + pspot_val + ")! Refusing..." );
				usage();
				System.exit(1);
			}
			System.out.println("Searching pspot=" + filter);
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
