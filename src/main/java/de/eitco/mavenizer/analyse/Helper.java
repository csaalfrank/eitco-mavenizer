package de.eitco.mavenizer.analyse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Helper {

	public static class Regex {
		
		// capture group names
		public final static String CAP_GROUP_PACKAGE = "package";
		public final static String CAP_GROUP_CLASS = "class";
		public final static String CAP_GROUP_ARTIFACT_ID = "artifactId";
		public final static String CAP_GROUP_VERSION = "version";
		
		// reusable patterns
		public final static String PATTERN_CLASS = "[A-Z]\\w*";
		public final static String PATTERN_SUBPACKAGE = "[a-z_][a-z0-9_]*";
		public final static String PATTERN_ARTIFACT_ID = "[a-z_][a-z0-9_\\-]*";
		public final static String PATTERN_PACKAGE = "(" + PATTERN_SUBPACKAGE + "\\.)*(" + PATTERN_SUBPACKAGE + ")";
		public final static String PATTERN_PACKAGE_2_OR_MORE = "(" + PATTERN_SUBPACKAGE + "\\.)+(" + PATTERN_SUBPACKAGE + ")";
		public final static String PATTERN_CLASSIFIER = "(([0-9]+)|([a-zA-Z]+))";
		
		public final static String PATTERN_VERSION = "[0-9]+(\\.[0-9]+)*(\\.[A-Z]+)?";// yes, "3.1.SONATYPE" is a version used in reality
		public final static String PATTERN_CLASSIFIERS = "(" + PATTERN_CLASSIFIER + ")(\\-" + PATTERN_CLASSIFIER + ")?";// more than 2 classifiers is unrealistic
		
		// specific patterns to test values with, using capture groups to extract substrings
		public final static String PACKAGE_WITH_OPTIONAL_CLASS =
				"^(?<" + CAP_GROUP_PACKAGE + ">" + PATTERN_PACKAGE + ")(\\.(?<" + CAP_GROUP_CLASS + ">" + PATTERN_CLASS + "))?$";
		
		public final static String PACKAGE_2_OR_MORE_WITH_OPTIONAL_CLASS =
				"^(?<" + CAP_GROUP_PACKAGE + ">" + PATTERN_PACKAGE_2_OR_MORE + ")(\\.(?<" + CAP_GROUP_CLASS + ">" + PATTERN_CLASS + "))?$";
		
		public final static String ARTIFACT_ID =
				"^(?<" + CAP_GROUP_ARTIFACT_ID + ">" + PATTERN_ARTIFACT_ID + ")$";
		
		public final static String OPTIONAL_PACKAGE_WITH_ARTIFACT_ID_AS_LEAF =
				"^(?<" + CAP_GROUP_PACKAGE + ">" + "(" + PATTERN_SUBPACKAGE + "\\.)*(" + PATTERN_ARTIFACT_ID + ")" + ")$";
		
		public final static String JAR_FILENAME_VERSION_SUFFIX =
				"\\-(?<" + CAP_GROUP_VERSION + ">" + PATTERN_VERSION + ")([\\-\\.]" + PATTERN_CLASSIFIERS + ")?$";
		
		public final static String ATTRIBUTE_VERSION =
				"^(?<" + CAP_GROUP_VERSION + ">" + PATTERN_VERSION + ")([\\-\\.]" + PATTERN_CLASSIFIERS + ")?$";
		
		// precompiled
		public final static Pattern packageWithOptionalClass = Pattern.compile(PACKAGE_WITH_OPTIONAL_CLASS);
		public final static Pattern packageStrictWithOptionalClass = Pattern.compile(PACKAGE_2_OR_MORE_WITH_OPTIONAL_CLASS);
		public final static Pattern artifactId = Pattern.compile(ARTIFACT_ID);
		public final static Pattern optionalPackageWithArtifactIdAsLeaf = Pattern.compile(OPTIONAL_PACKAGE_WITH_ARTIFACT_ID_AS_LEAF);
		public final static Pattern jarFilenameVersionSuffix = Pattern.compile(JAR_FILENAME_VERSION_SUFFIX);
		public final static Pattern attributeVersion = Pattern.compile(ATTRIBUTE_VERSION);
	}

	public static class CandidateExtractionHelper {
		
		/**
		 * Returns the first 2, 3 or 4 parts of the given package name.
		 */
		public static List<String> getPackageCandidates(String pakkage) {
			String[] parts = pakkage.split("\\.");
			if (parts.length < 2) {
				return List.of();
			}
			var result = new ArrayList<String>(3);
			result.add(parts[0] + "." + parts[1]);
			if (parts.length > 2) {
				result.add(parts[0] + "." + parts[1] + "." + parts[2]);
				if (parts.length > 3) {
					result.add(parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3]);
				}
			}
			return result;
		}
		
		public static String getPackageLeaf(String pakkage) {
			String[] parts = pakkage.split("\\.");
			return parts[parts.length - 1];
		}
	}

}