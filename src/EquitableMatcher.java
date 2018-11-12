import java.util.List;
import java.util.stream.Collectors;

public class EquitableMatcher {

	private Long bestMatchValue = Long.MAX_VALUE;
	private List<List<Person>> bestMatch = null;
	private Integer numberOfMatchings = 0;

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("usage: EquitableMatcher <input>\n");
			return;
		}
		EquitableMatcher em = new EquitableMatcher();
		em.match(args[0]);
	}

	private void match(String filename) {

		List<List<Person>> groups = InputParserUtility.ParseInput(filename);
		List<List<Person>> manOptimalMatch = GaleShapelyAlgorithm.execute(cloneGroups(groups), "m");
		List<List<Person>> womanOptimalMatch = GaleShapelyAlgorithm.execute(cloneGroups(groups), "w");

		Long equityScore = calculateEquityScore(manOptimalMatch);
		System.out.println("Man-optimal equity score: " + equityScore);
		equityScore = calculateEquityScore(womanOptimalMatch);
		System.out.println("Woman-optimal equity score: " + equityScore);

		trimAllFeasiblePreferences(groups, manOptimalMatch, womanOptimalMatch);

		findMostEquitableMatch(groups);

		equityScore = calculateEquityScore(bestMatch);
		System.out.println("Optimal matching:");
		StableMatchingUtils.printOutput(bestMatch, true);
		System.out.println("Total matchings: " + numberOfMatchings + " Optimal equity score: " + equityScore);

	}

	public void findMostEquitableMatch(List<List<Person>> input) {
		bestMatchValue = Long.MAX_VALUE;
		bestMatch = null;
		numberOfMatchings = 0;
		findMostEquitableMatch(input.get(0).size(), input);
	}

	private void findMostEquitableMatch(Integer unmatchedCount, List<List<Person>> input) {
		// base case: unmatched list is empty:
		if (unmatchedCount == 0) {
			if (!StableMatchingUtils.checkStableMatching(input)) {
//				System.out.println("non stable!");
				return;
			 }
			numberOfMatchings++;
			Long equityScore = calculateEquityScore(input);
			System.out.println("equity score: " + equityScore);
			if (equityScore < bestMatchValue) {
				bestMatchValue = equityScore;
				bestMatch = input;
			}
			return;
		}

		// recursive case:
		List<List<Person>> traversal = cloneGroups(input);
		List<Person> availableMen = traversal.get(0);
		List<Person> availableWomen = traversal.get(1);
		Person availableMan = availableMen.get(availableMen.size() - unmatchedCount);
		List<Integer> feasibleMatches = availableMan.getFeasiblePreferences();
		for (Integer feasibleMatchIndex : feasibleMatches) {
			Person feasibleWoman = availableWomen.get(feasibleMatchIndex);
			// is this match still in the list of available options?
			if (feasibleWoman.getMatch() != null) {
				continue;
			}
			availableMan.setMatch(feasibleWoman);
			feasibleWoman.setMatch(availableMan);
			findMostEquitableMatch(unmatchedCount - 1, cloneGroups(traversal));
			availableMan.setMatch(null);
			feasibleWoman.setMatch(null);
		}

	}

	private void trimAllFeasiblePreferences(List<List<Person>> groups, List<List<Person>> manOptimalMatch,
			List<List<Person>> womanOptimalMatch) {
		List<Person> men = groups.get(0);
		List<Person> women = groups.get(1);
		List<Person> menOptimal = manOptimalMatch.get(0);
		List<Person> menPessimal = womanOptimalMatch.get(0);
		List<Person> womenOptimal = womanOptimalMatch.get(1);
		List<Person> womenPessimal = manOptimalMatch.get(1);

		trimFeasiblePreferencesByGender(men, women, menOptimal, menPessimal);
		trimFeasiblePreferencesByGender(women, men, womenOptimal, womenPessimal);
	}

	private void trimFeasiblePreferencesByGender(List<Person> groupA, List<Person> groupB, List<Person> aOptimal,
			List<Person> aPessimal) {
		for (int personIndex = 0; personIndex < groupA.size(); personIndex++) {
			Person person = groupA.get(personIndex);
			Person optimalMatch = aOptimal.get(personIndex).getMatch();
			Person pessimalMatch = aPessimal.get(personIndex).getMatch();
			Integer optimalMatchIndex = groupB.indexOf(optimalMatch);
			Integer pessimalMatchIndex = groupB.indexOf(pessimalMatch);
			trimFeasiblePreferencesForIndividual(person, optimalMatchIndex, pessimalMatchIndex);
			 System.out.println("person: " + personIndex + " pref: " +
			 person.getPreferenceList() + " feas: " + person.getFeasiblePreferences());
		}
	}

	private void trimFeasiblePreferencesForIndividual(Person person, Integer optimalMatchIndex,
			Integer pessimalMatchIndex) {
		boolean feasibleRange = false;
		for (Integer preference : person.getPreferenceList()) {
			if (preference.equals(optimalMatchIndex)) {
				feasibleRange = true;
			}
			if (!feasibleRange) {
				person.markInfeasible(preference);
			}
			if (preference.equals(pessimalMatchIndex)) {
				feasibleRange = false;
			}
		}
	}

	private Long calculateEquityScore(List<List<Person>> match) {
		List<Person> men = match.get(0);
		List<Person> women = match.get(1);
		Long equity = 0L;
		equity = men.stream() //
				.map(man -> calculatePreferenceScore(man, women)).reduce(0L, (a, b) -> a + b);
		equity += women.stream() //
				.map(woman -> calculatePreferenceScore(woman, men)).reduce(0L, (a, b) -> a + b);
		return equity;
	}

	private Long calculatePreferenceScore(Person person, List<Person> preferenceGroup) {
		Integer matchIndex = preferenceGroup.indexOf(person.getMatch());
		if (matchIndex < 0) {
			throw new RuntimeException("invalid match");
		}
		Integer preferenceListIndex = person.getPreferenceList().indexOf(matchIndex);
		return preferenceListIndex.longValue();
	}

	private List<List<Person>> cloneGroups(List<List<Person>> groups) {
		return groups.stream() //
				.map(personList -> personList.stream() //
						.map(person -> person.clone(person)) //
						.collect(Collectors.toList())) //
				.collect(Collectors.toList());
	}

}
