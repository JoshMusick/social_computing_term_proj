import java.util.Arrays;
import java.util.List;

public class FindTestSets {

	public static void main(String[] args) throws Exception {
		int trials = 100000;
		int maxMatchings = 0;
		for (int i = 0; i < trials; i++) {
			String testSet = InputGenerator.generateRandomInput(5);
			List<String> lines = Arrays.asList(testSet.split("\\n"));
			Matching matching = InputParserUtility.parseInput(lines);
			EquitableMatcher em = new EquitableMatcher();
			Integer matchings = em.match(matching);
			maxMatchings = matchings > maxMatchings? matchings : maxMatchings;
			if (maxMatchings > 7) {
				break;
			}
		}
		System.out.println("Max matchings: " + maxMatchings);
	}
}
