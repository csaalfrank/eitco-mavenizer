package de.eitco.mavenizer.analyze.jar;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.eitco.mavenizer.MavenUid.MavenUidComponent;
import de.eitco.mavenizer.StringUtil;
import de.eitco.mavenizer.analyze.JarAnalyzer.JarAnalyzerType;
import de.eitco.mavenizer.analyze.JarAnalyzer.JarEntry;
import de.eitco.mavenizer.analyze.JarAnalyzer.ValueCandidateCollector;

public class ClassTimestampAnalyzer {
	
	public static final DateTimeFormatter dateToVersion = DateTimeFormatter.ofPattern("yyyy.MM.dd");
	public static final DateTimeFormatter datePrinter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	public void analyze(ValueCandidateCollector result, List<JarEntry> classes) {
		
		var datesToOccurence = new HashMap<LocalDate, Integer>();
		var total = 0;
		
		for (var entry : classes) {
			if (entry.timestamp != null) {
				var instant = entry.timestamp.toInstant();
				var dateUtc = LocalDate.ofInstant(instant, ZoneOffset.UTC);
				var count = datesToOccurence.getOrDefault(dateUtc, 0);
				datesToOccurence.put(dateUtc, count + 1);
				total++;
			}
		}
		
		var highestCountEntry = 
				datesToOccurence.entrySet().stream()
			    .sorted(Entry.<LocalDate, Integer>comparingByValue().reversed())
			    .findFirst();
		
		if (highestCountEntry.isPresent()) {
			var highestCountDate = highestCountEntry.get().getKey();
			var highestCount = highestCountEntry.get().getValue();
			
			if (highestCount > 1) {
				var version = highestCountDate.format(dateToVersion);
				int countRatio = (highestCount * 100) / total;
				var countRatioPercent = StringUtil.leftPad(countRatio + "", 3);
				var details = countRatioPercent + "% of classes have created/modified date: " + highestCountDate.format(datePrinter);
				
				result.addCandidate(MavenUidComponent.VERSION, version, (countRatio > 60 ? 1 : 0), details);
			}

			
			var mostRecentEntry = 
					datesToOccurence.entrySet().stream()
				    .sorted(Entry.<LocalDate, Integer>comparingByKey().reversed())
				    .findFirst();
			
			if (mostRecentEntry.isPresent()) {
				var mostRecentDate = mostRecentEntry.get().getKey();
				var mostRecentCount = mostRecentEntry.get().getValue();
				
				if (!mostRecentDate.equals(highestCountDate)) {
					var version = mostRecentDate.format(dateToVersion);
					int countRatio = (mostRecentCount * 100) / total;
					var countRatioPercent = Integer.toString(countRatio);
					var details = "Most recent created/modified date: " + mostRecentDate.format(datePrinter) + " (" + mostRecentCount + " classes = " + countRatioPercent + "%)";
					
					result.addCandidate(MavenUidComponent.VERSION, version, (countRatio > 10 ? 1 : 0), details);
				}

			}
		}
		
	}
	
	public JarAnalyzerType getType() {
		return JarAnalyzerType.CLASS_TIMESTAMP;
	}
}
