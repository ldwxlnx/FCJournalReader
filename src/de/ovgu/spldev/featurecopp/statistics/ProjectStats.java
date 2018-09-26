package de.ovgu.spldev.featurecopp.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.ovgu.spldev.featurecopp.Configuration;

public class ProjectStats {
	public ProjectStats() {
		initUniqueHeaderList();
		initUniqueImplList();
	}
	public String toString() {
		StringBuilder featureRank = new StringBuilder();
		topNFeaturesToString(featureRank, Configuration.SHOW_TOP_N_FEATURES);
		// @formatter:off
		return "SV DIST:" + Configuration.LINESEP
				+ "\t[10,Inf]=" + good_count + ";(0,10)=" + bad_count + ";0="
				+ ugly_count + ";missed=" + missed_count + ";total=" + (good_count + bad_count + ugly_count) + Configuration.LINESEP
				+ "CONDITIONALS: " + Configuration.LINESEP
				+ "\tif=" + if_count + ";ifdef=" + ifdef_count + ";ifndef=" + ifndef_count + ";total=" + (if_count + ifdef_count + ifndef_count) + Configuration.LINESEP
				+ "\telif=" + elif_count + Configuration.LINESEP
				+ "\telse=" + else_count + Configuration.LINESEP
				+ "\tendif=" + endif_count + Configuration.LINESEP
				+ "\ttotal(#*if*,#else)=" + (if_count + ifdef_count + ifndef_count + elif_count + else_count) + Configuration.LINESEP
				+ "FEATURES: " + Configuration.LINESEP
				+ "\trequested=" + features_requested_count + ";total=" + feature_count + Configuration.LINESEP
				+ "Top " + Configuration.SHOW_TOP_N_FEATURES + ":" + Configuration.LINESEP
				+ featureRank.toString()
				+ "ROLES:" + Configuration.LINESEP
				+ "\trequested=" + role_requested_count + ";unrequested=" + role_unrequested_count + ";total=" + (role_requested_count + role_unrequested_count) + Configuration.LINESEP
				+ "\tvalid=" + valid_count + ";dead=" + dead_count + ";total=" + role_count + Configuration.LINESEP
				+ "PROJECT:" + Configuration.LINESEP
				+ "\tunique variable headers (.h)=" + unique_headers.size() + ";unique variable impls(.c)=" + unique_impls.size() + ";total=" + (unique_headers.size() + unique_impls.size()) + Configuration.LINESEP
				+ "\ttext (bytes/MiB)=" + text_size + "/" + String.format(Locale.ENGLISH, "%.2f", (text_size * 1.0 / (1024 * 1024)));
		// @formatter:on
	}

	public void initFeatureList() {
		features = new ArrayList<Role.Feature>(feature_count);
	}

	public void addFeature(Role.Feature f) {
		if (features != null) {
			if (!features.contains(f)) {
				features.add(f);
			}
		}
	}
	
	public void initUniqueHeaderList() {
		unique_headers = new ArrayList<String>();
	}
	
	public void initUniqueImplList() {
		unique_impls = new ArrayList<String>();
	}
	
	public void addUniqueFile(String filename) {
		if(filename != null) {
			if(filename.matches(".*\\.h$")) {
				addUniqueHeader(filename);
			}
			else if(filename.matches(".*\\.c$")) {
				addUniqueImpl(filename);
			}
		}
	}
	
	protected void addUniqueHeader(String filename) {
		if(filename != null) {
			if(!unique_headers.contains(filename)) {
				unique_headers.add(filename);
			}
		}
	}
	
	protected void addUniqueImpl(String filename) {
		if(filename != null) {
			if(!unique_impls.contains(filename)) {
				unique_impls.add(filename);
			}
		}
	}

	private void topNFeaturesToString(StringBuilder sb, long n) {
		Collections.sort(features);
		Collections.reverse(features);
		int max_places_rank = Long.toString(n, 10).length();
		int max_places_role_count = Long.toString(features.get(0).role_count)
				.length();
		sb.append("Abbreviations: #=rank, R=# of roles, .h/.c=occurrences in header/implementation files, D=dead, V=valid, ID=role id, r=requested, td=tangling degree, ndavg=average nesting depth, expr=feature expression");
		sb.append(Configuration.LINESEP);
		sb.append(String.format(Locale.US, "%" + max_places_rank + "s %"
				+ max_places_role_count + "s[%" + max_places_role_count + "s/%"
				+ max_places_role_count + "s/%" + max_places_role_count + "s/%"
				+ max_places_role_count + "s] %6s r td ndavg [expr]"
				+ Configuration.LINESEP, "#", "R", ".h", ".c", "D", "V", "ID"));

		for (int i = 0; i < features.size(); i++) {
			if (i == n) {
				return;
			}

			Role.Feature curr = features.get(i);
			sb.append(String.format(Locale.US, "%" + max_places_rank + "d %"
					+ max_places_role_count + "d[%" + max_places_role_count
					+ "d/%" + max_places_role_count + "d/%"
					+ max_places_role_count + "d/%" + max_places_role_count
					+ "d] %6d %c %2d %.3f [%s]" + Configuration.LINESEP, (i + 1),
					curr.role_count, curr.header_occs, curr.impl_occs, curr.dead_role_count, curr.valid_role_count,
					curr.fuid, curr.isRequested ? 'Y' : 'N', curr.tanglingDegree, curr.ndAVG, curr.featureExpr));
		}
	}

	/*
	 * <stats if="638" ifdef="859" ifndef="1009" ifXtotal="2506" elif="51"
	 * else="261" endif="2506" textsize="50000775"/><features count="1191"
	 * requested="1191" roles="2818">
	 */
	public int good_count;
	public int bad_count;
	public int ugly_count;
	public int missed_count;
	public int dead_count;
	public int valid_count;
	public int if_count;
	public int ifdef_count;
	public int ifndef_count;
	public int elif_count;
	public int else_count;
	public int endif_count;
	public long text_size;
	public int feature_count;
	public int features_requested_count;
	public long role_count;
	public long role_requested_count;
	public long role_unrequested_count;
	private List<Role.Feature> features;
	private List<String> unique_headers;
	private List<String> unique_impls;
}
