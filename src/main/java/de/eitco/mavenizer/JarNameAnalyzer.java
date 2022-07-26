package de.eitco.mavenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import de.eitco.mavenizer.Main.Analyzer;
import de.eitco.mavenizer.Main.MavenUidComponent;
import de.eitco.mavenizer.Main.StringValueSource;
import de.eitco.mavenizer.Main.ValueSource;
import de.eitco.mavenizer.Main.ValueCandidate;
import de.eitco.mavenizer.ManifestAnalyzer.ScoredValue;

public class JarNameAnalyzer {
	
	public Map<MavenUidComponent, List<ValueCandidate>> analyze(String jarFilename) {
		
		var result = Map.<MavenUidComponent, List<ValueCandidate>>of(
				MavenUidComponent.GROUP_ID, new ArrayList<ValueCandidate>(),
				MavenUidComponent.ARTIFACT_ID, new ArrayList<ValueCandidate>(),
				MavenUidComponent.VERSION, new ArrayList<ValueCandidate>()
				);
		
		ValueSource valueSource = new StringValueSource("'" + jarFilename + "'");
		var nameWithoutExt = jarFilename.substring(0, jarFilename.lastIndexOf('.'));
		
		Matcher matcher = Helper.Regex.jarFilenameVersionSuffix.matcher(nameWithoutExt);
		if (matcher.find()) {
			String version = matcher.group(Helper.Regex.CAP_GROUP_VERSION);
			if (version != null) {
				int suffixStart = matcher.start();
				var nameWithoutVersion = nameWithoutExt.substring(0, suffixStart);
				{
					var value = new ScoredValue(nameWithoutVersion, 6);
					var candidate = new ValueCandidate(value, Analyzer.JAR_FILENAME, valueSource);
					result.get(MavenUidComponent.ARTIFACT_ID).add(candidate);
				}{
					var value = new ScoredValue(version, 6);
					var candidate = new ValueCandidate(value, Analyzer.JAR_FILENAME, valueSource);
					result.get(MavenUidComponent.VERSION).add(candidate);
				}
				return result;
			}
		}
		
		var value = new ScoredValue(nameWithoutExt, 4);
		var candidate = new ValueCandidate(value, Analyzer.JAR_FILENAME, valueSource);
		result.get(MavenUidComponent.ARTIFACT_ID).add(candidate);

		return result;
	}
}
