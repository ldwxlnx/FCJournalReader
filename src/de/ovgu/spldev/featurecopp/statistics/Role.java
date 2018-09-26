package de.ovgu.spldev.featurecopp.statistics;


public class Role {
	public Role(Feature feature) {
		this.sem = new Semantics();
		this.feature = feature;
	}
	public Role() {
		this.sem = new Semantics();
		this.feature = new Feature();
	}
	public static final char CSV_DELIM = ';';
	public static class Feature implements Comparable<Feature> {
		// @formatter:off
		public static final String OUT_HEAD = "EXPR" + CSV_DELIM + "TD" + CSV_DELIM + "NDavg" + CSV_DELIM + "FID" + CSV_DELIM + "REQ";
		// @formatter:on
		public String toString() {
			return featureExpr + CSV_DELIM + tanglingDegree + CSV_DELIM + ndAVG + CSV_DELIM + fuid + CSV_DELIM + (isRequested ? 1 : 0);
		}
		// FEATURE
		public String featureExpr;
		public int tanglingDegree;
		public double ndAVG;
		public long fuid;
		public boolean isRequested;
		public long role_count;
		public long dead_role_count;
		public long valid_role_count;
		public long header_occs;
		public long impl_occs;
		public void countOccByFileType(String filename) {
			if(filename != null) {
				if(filename.matches(".*\\.h$")) {
					header_occs++;
				}
				else if(filename.matches(".*\\.c$")) {
					impl_occs++;
				}
			}
		}
		@Override
		public int compareTo(Feature f) {
			int comp = 0;
			if(role_count < f.role_count) {
				comp = -1;
			}
			else if(role_count > f.role_count) {
				comp = 1;
			}
			return comp;
		}
	}
	public static class Semantics {
		// @formatter:off
		public static final String OUT_HEAD = "PSPOT"
				+ CSV_DELIM
				// CS
				+ "CS" + CSV_DELIM + "FDEF" + CSV_DELIM + "SDECL" + CSV_DELIM + "FDECL" + CSV_DELIM + "VDECL" + CSV_DELIM + "STMT" + CSV_DELIM + "COMM" + CSV_DELIM
				// ER
				+ "ER" + CSV_DELIM + "SYMB" + CSV_DELIM + "SYMT" + CSV_DELIM + "FCALL" + CSV_DELIM
				// MISC
				+ "INC" + CSV_DELIM + "CPP";
		// @formatter:on
		@Override
		public String toString() {
			// @formatter:off
			return pspot + ";"
					// CS
					+ cs + CSV_DELIM + funcdefs + CSV_DELIM + structdecls + CSV_DELIM + funcdecls + CSV_DELIM + vardecls + CSV_DELIM + stmts + CSV_DELIM + comments + CSV_DELIM
					// ER
					+ er + CSV_DELIM + symBound + CSV_DELIM + symTotal + CSV_DELIM + funcalls + CSV_DELIM
					// MISC
					+ include + CSV_DELIM + cppdir;
			// @formatter:on
		}

		public double pspot;
		public double cs;
		public double funcdefs;
		public double structdecls;
		public double funcdecls;
		public double vardecls;
		public double stmts;
		public double comments;
		public double er;
		public double symTotal;
		public double symBound;
		public double funcalls;
		public double include;
		public double cppdir;
	}
	// @formatter:off
	public static final String OUT_HEAD = Semantics.OUT_HEAD + CSV_DELIM
			// ROLE
			+ "SRC" + CSV_DELIM + "RID" + CSV_DELIM + "ND" + CSV_DELIM + "BEGIN" + CSV_DELIM + "END" + CSV_DELIM
			// FEATURE
			+ Feature.OUT_HEAD;
	// @formatter:on
	
	@Override
	public String toString() {
		// @formatter:off
		return sem.toString() + CSV_DELIM
				// ROLE
				+ "\"" + srcFile + "\""+ CSV_DELIM + uid + CSV_DELIM + nd + CSV_DELIM + beginLine + CSV_DELIM + endLine + CSV_DELIM
				// FEATURE
				+ feature.toString();
		// @formatter:on
	}
	// SEMANTICS
	public Semantics sem;
	// ROLE
	public String srcFile;
	public double uid;
	public int nd;
	public double beginLine;
	public double endLine;	
	// FEATURE
	public Feature feature;
}
