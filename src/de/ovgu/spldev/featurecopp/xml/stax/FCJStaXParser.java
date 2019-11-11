package de.ovgu.spldev.featurecopp.xml.stax;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import de.ovgu.spldev.featurecopp.statistics.Role;
import de.ovgu.spldev.featurecopp.statistics.Role.Feature;
import de.ovgu.spldev.featurecopp.xml.IXMLParser;

public class FCJStaXParser extends IXMLParser {

	public FCJStaXParser(final String fileName, final PrintStream strm,
			IXMLParser.SVCandidate filter)
			throws FileNotFoundException, XMLStreamException {
		super(strm, filter);
		// stored for reparse
		this.fileName = fileName;
		init(fileName);
	}

	public void parse() throws XMLParserException {
		try {
			strm.println(Role.OUT_HEAD);
			Role.Feature currFeature = null;
			Role currRole = null;
			long role_n = 0;
			while (xmlParser.hasNext()) {
				switch (xmlParser.getEventType()) {
				case XMLStreamConstants.START_DOCUMENT:
					if (xmlParser == null) {
						init(fileName);
					}
					break;
				case XMLStreamConstants.END_DOCUMENT:
					xmlParser.close();
					xmlParser = null;
					break;
				case XMLStreamConstants.NAMESPACE:
					break;
				// case XMLStreamConstants.ATTRIBUTE:
				case XMLStreamConstants.START_ELEMENT: {
					final String currElement = xmlParser.getLocalName();
					int attribCount = xmlParser.getAttributeCount();
					switch (currElement) {
					// PROJ
					case "stats":
						parseStats(attribCount);
						break;
					// FEATURES
					case "features":
						parseFeatures(attribCount);
						break;
					// FEATURE
					case "feature":
						currFeature = parseFeature(attribCount);
						break;
					case "occs":
						parseRoles(currFeature, attribCount);
						break;
					case "td":
						parseTanglingDegree(currFeature);
						break;
					case "ndavg":
						parseAVGNestingDepth(currFeature);
						break;
					case "sem_avg":
						parseSemanticAVG(currFeature, attribCount);
						break;
					case "sem_dev":
						parseSemanticSTDDEV(currFeature, attribCount);
						break;
					// ROLE
					case "occ": {
						IXMLParser.overwriteProgressLine(++role_n,
								super.projStats.role_count, System.err);
						currRole = parseRole(currFeature, attribCount);
						break;
					}
					// SEMANTICS
					case "semantics":
						parseSemantics(currRole, attribCount);
						break;
					default:
						break;
					} // switch (currElement)
					break;
				} // case XMLStreamConstants.START_ELEMENT:
				case XMLStreamConstants.END_ELEMENT: {
					final String currElement = xmlParser.getLocalName();
					switch (currElement) {
					case "occ": {
						// role completed -> write back
						if (currRole.feature.isRequested) {
							strm.println(currRole.toString());
							strm.flush();
						}
						break;
					}
					default:
						break;
					}					
					break;
				}
				// FEATURE EXPR //TODO not longer working since TD was added
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.CHARACTERS:
					parseFeatureExpr(currFeature);
					break;
				default:
					break;
				} // switch (xmlParser.getEventType())
				xmlParser.next();
			} // while
			System.out.println();
		} catch (NumberFormatException | FileNotFoundException
				| XMLStreamException e) {
			throw new IXMLParser.XMLParserException(e.getMessage());
		}
	}

	private void parseStats(int attribCount) {
		for (int i = 0; i < attribCount; i++) {
			String key = xmlParser.getAttributeLocalName(i);
			String val = xmlParser.getAttributeValue(i);
			if (key == "if") {
				super.projStats.if_count = Integer.parseInt(val);
			} else if (key == "ifdef") {
				super.projStats.ifdef_count = Integer.parseInt(val);
			} else if (key == "ifndef") {
				super.projStats.ifndef_count = Integer.parseInt(val);
			} else if (key == "elif") {
				super.projStats.elif_count = Integer.parseInt(val);
			} else if (key == "else") {
				super.projStats.else_count = Integer.parseInt(val);
			} else if (key == "endif") {
				super.projStats.endif_count = Integer.parseInt(val);
			} else if (key == "textsize") {
				super.projStats.text_size = Long.parseLong(val);
			}

		}
	}

	private void parseFeatures(int attribCount) {
		for (int i = 0; i < attribCount; i++) {
			String key = xmlParser.getAttributeLocalName(i);
			String val = xmlParser.getAttributeValue(i);
			if (key == "roles") {
				super.projStats.role_count = Long.parseLong(val, 10);
			} else if (key == "count") {
				super.projStats.feature_count = Integer.parseInt(val);
				super.projStats.initFeatureList();
			} else if (key == "requested") {
				super.projStats.features_requested_count = Integer
						.parseInt(val);

			}
		}
	}

	private void parseSemanticAVG(Feature currFeature, int attribCount) {
		for (int i = 0; i < attribCount; i++) {
			String key = xmlParser.getAttributeLocalName(i);
			String val = xmlParser.getAttributeValue(i);
			if (key == "CS") {
				currFeature.csAVG = Double.parseDouble(val);
			} else if (key == "ER") {
				currFeature.erAVG = Double.parseDouble(val);
			}
		}
	}

	private void parseSemanticSTDDEV(Feature currFeature, int attribCount) {
		for (int i = 0; i < attribCount; i++) {
			String key = xmlParser.getAttributeLocalName(i);
			String val = xmlParser.getAttributeValue(i);
			if (key == "CS") {
				currFeature.csSTDDEV = Double.parseDouble(val);
			} else if (key == "ER") {
				currFeature.erSTDDEV = Double.parseDouble(val);
			}
		}
	}

	private Role.Feature parseFeature(int attribCount) {
		Role.Feature currFeature = new Role.Feature();
		for (int i = 0; i < attribCount; i++) {
			String key = xmlParser.getAttributeLocalName(i);
			String val = xmlParser.getAttributeValue(i);
			if (key == "uid") {
				currFeature.fuid = Long.parseLong(val, 10);
			} else if (key == "requested") {
				currFeature.isRequested = Boolean.parseBoolean(val);
			}
		}
		if (currFeature.isRequested) {
			projStats.addFeature(currFeature);
		}
		return currFeature;
	}

	private void parseFeatureExpr(Feature currFeature) {
		if (!xmlParser.isWhiteSpace()) {
			currFeature.featureExpr = xmlParser.getText();
		}
	}

	private void parseTanglingDegree(Feature currFeature)
			throws NumberFormatException, XMLStreamException {
		if (!xmlParser.isWhiteSpace()) {
			currFeature.tanglingDegree = Integer
					.parseInt(xmlParser.getElementText());
		}
	}

	private void parseAVGNestingDepth(Feature currFeature)
			throws NumberFormatException, XMLStreamException {
		if (!xmlParser.isWhiteSpace()) {
			currFeature.ndAVG = Double.parseDouble(xmlParser.getElementText());
		}
	}

	private void parseRoles(Role.Feature currFeature, int attribCount) {
		for (int i = 0; i < attribCount; i++) {
			String key = xmlParser.getAttributeLocalName(i);
			String val = xmlParser.getAttributeValue(i);
			if (key == "count") {
				currFeature.role_count = Long.parseLong(val, 10);
			} else if (key == "dead") {
				currFeature.dead_role_count = Long.parseLong(val, 10);
			} else if (key == "valid") {
				currFeature.valid_role_count = Long.parseLong(val, 10);
			}
		}
		if(currFeature.dead_role_count == 1 && currFeature.valid_role_count == 0) {
			
		}
	}

	private Role parseRole(Role.Feature currFeature, int attribCount) {
		Role currRole = new Role(currFeature);
		if (currFeature.isRequested) {
			super.projStats.role_requested_count++;
		} else {
			super.projStats.role_unrequested_count++;
		}
		for (int i = 0; i < attribCount; i++) {
			String key = xmlParser.getAttributeLocalName(i);
			String val = xmlParser.getAttributeValue(i);
			if (key == "status") {
				if ("dead".equals(val)) {
					super.projStats.dead_count++;
				} else if ("valid".equals(val)) {
					super.projStats.valid_count++;
				}
			}
			if (key == "id") {
				currRole.uid = Long.parseLong(val, 10);
			} else if (key == "file") {
				currRole.srcFile = val;
				currFeature.countOccByFileType(val);
				super.projStats.addUniqueFile(val);
			} else if (key == "keyword") {
				currRole.keyword = val;
			} else if (key == "nd") {
				currRole.nd = Integer.parseInt(val);
			} else if (key == "begin") {
				currRole.beginLine = Integer.parseInt(val);
			} else if (key == "end") {
				currRole.endLine = Integer.parseInt(val);
			}
		}
		return currRole;
	}

	private void parseSemantics(Role currRole, int attribCount) {
		for (int i = 0; i < attribCount; i++) {
			String key = xmlParser.getAttributeLocalName(i);
			String val = xmlParser.getAttributeValue(i);
			switch (key) {
			case "PSPOT": {
				Double pspot = Double.parseDouble(val);
				if (pspot == 0) {
					super.projStats.ugly_count++;
				} else if (0 < pspot && pspot < 1) {
					super.projStats.bad_count++;
				} else if (1 <= pspot && pspot <= 2) {
					super.projStats.good_count++;
				}
				// roles are collected only if meeting interval criteria of
				// pspot
				switch (super.filter) {
				case GOOD:
					if (pspot < 1) {
						return;
					}
					break;
				case GOODBADGOOD:
					if (pspot < 0) {
						return;
					}
					break;
				case BAD:
					if (0 == pspot || 1 <= pspot) {
						return;
					}
					break;
				case UGLY:
					if (pspot != 0) {
						return;
					}
					break;
				case ALL:
				default:
				}
				currRole.sem.pspot = pspot;
				break;
			}
			case "CS":
				currRole.sem.cs = Double.parseDouble(val);
				break;
			case "funcdefs":
				currRole.sem.funcdefs = Double.parseDouble(val);
				break;
			case "structdecls":
				currRole.sem.structdecls = Double.parseDouble(val);
				break;
			case "funcdecls":
				currRole.sem.funcdecls = Double.parseDouble(val);
				break;
			case "vardecls":
				currRole.sem.vardecls = Double.parseDouble(val);
				break;
			case "stmts":
				currRole.sem.stmts = Double.parseDouble(val);
				break;
			case "exprs":
				currRole.sem.exprs = Double.parseDouble(val);
				break;
			case "comments":
				currRole.sem.comments = Double.parseDouble(val);
				break;
			// ER
			case "ER":
				currRole.sem.er = Double.parseDouble(val);
				break;
			case "symtotal":
				currRole.sem.symTotal = Double.parseDouble(val);
				break;
			case "symbound":
				currRole.sem.symBound = Double.parseDouble(val);
				break;
			case "funcalls":
				currRole.sem.funcalls = Double.parseDouble(val);
				break;
			// MISC
			case "include":
				currRole.sem.include = Double.parseDouble(val);
				break;
			case "cppdir":
				currRole.sem.cppdir = Double.parseDouble(val);
				break;
			default:
				break;
			} // switch(key)
		} // for
	}

	private void init(final String fileName)
			throws FileNotFoundException, XMLStreamException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
		xmlParser = factory
				.createXMLStreamReader(new FileInputStream(fileName));
		super.resetStatistics();
	}

	private String fileName;
	private XMLStreamReader xmlParser;
}
