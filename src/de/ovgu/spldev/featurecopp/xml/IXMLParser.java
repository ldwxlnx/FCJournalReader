package de.ovgu.spldev.featurecopp.xml;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Locale;

import javax.xml.stream.XMLStreamException;

import de.ovgu.spldev.featurecopp.statistics.ProjectStats;
import de.ovgu.spldev.featurecopp.xml.stax.FCJStaXParser;

public abstract class IXMLParser {

	public static enum SVCandidate {
		GOOD, BAD, UGLY, ALL, GOODBADGOOD//[0.75,2.0]
	};

	public static IXMLParser createXMLParser(String xmlFile, PrintStream strm,
			SVCandidate filter) throws XMLParserException {
		IXMLParser xmlParser = null;
		try {
			xmlParser = new FCJStaXParser(xmlFile, strm, filter);
		} catch (FileNotFoundException | XMLStreamException e) {
			throw new XMLParserException(e.getMessage());
		}
		return xmlParser;
	}

	public static void overwriteProgressLine(long role_n, long roleCount,
			PrintStream strm) {
		int decimalPlaces = Long.toString(roleCount).length();
		double percent = role_n * 1.0 / roleCount * 100.0;
		strm.print("\r");
		strm.print(String.format(Locale.ENGLISH, "Processing role: %"
				+ decimalPlaces + "d/%" + decimalPlaces + "d (%5.1f%%)",
				role_n, roleCount, percent));
	}

	public static class XMLParserException extends Exception {
		/**
		 * gen uid
		 */
		private static final long serialVersionUID = 2982059023161402791L;

		public XMLParserException(final String msg) {
			super(msg);
		}
	}

	public IXMLParser(PrintStream strm, SVCandidate filter) {
		this.strm = strm == null ? System.out : strm;
		this.filter = filter == null ? SVCandidate.GOOD : filter;
		this.projStats = new ProjectStats();
	}

	public abstract void parse() throws XMLParserException;

	public String psStatsToString() {
		return projStats.toString();
	}

	protected void resetStatistics() {
		projStats = new ProjectStats();
	}
	protected ProjectStats projStats;
	protected SVCandidate filter;
	protected PrintStream strm;
}
