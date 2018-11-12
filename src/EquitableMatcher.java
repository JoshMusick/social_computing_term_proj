import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EquitableMatcher {

	private Long bestMatchValue = Long.MAX_VALUE;
	private List<List<Person>> bestMatch = null;
	private Integer numberOfMatchings = 0;
	Set<List<Integer>> matchLattice = new HashSet<List<Integer>>();

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

		List<List<Person>> nonFeasiblePairs = findNonFeasiblePairs(manOptimalMatch, manOptimalMatch, womanOptimalMatch);
		removeNonFeasiblePairs(manOptimalMatch, nonFeasiblePairs);

		StableMatchingUtils.printReducedPreferenceLists(manOptimalMatch);

//		matchings.forEach(System.out::println);
		StableMatchingUtils.printOutput(manOptimalMatch, true);
		matchLattice = findAllMatchings(manOptimalMatch);


//		findMostEquitableMatch(groups);
//
//		equityScore = calculateEquityScore(bestMatch);
//		System.out.println("Optimal matching:");
//		StableMatchingUtils.printOutput(bestMatch, true);
//		System.out.println("Total matchings: " + numberOfMatchings + " Optimal equity score: " + equityScore);

	}

	private Set<List<Integer>> findAllMatchings(List<List<Person>> matching) {
		Set<List<Integer>> returnVal = new HashSet<List<Integer>>();

		List<Integer> matchId = identifyMatching(matching);
		if (matchLattice.contains(matchId)) {
			return returnVal;
		}

		for (Rotation rotation : identifyRotations(matching)) {
//			List<List<Person>> rotatedMatch = getRotatedMatch(matching, rotation);
//			returnVal.addAll(findAllMatchings(rotatedMatch));
		}
		return null;
	}

	private List<Rotation> identifyRotations(List<List<Person>> matching) {
		Set<Integer> visitedMatches = new HashSet<Integer>();
		List<Rotation> rotations = new ArrayList<Rotation>();
		List<Person> men = matching.get(0);
		List<Person> women = matching.get(1);
		for (int i = 0; i < men.size(); i++) {
			if (visitedMatches.contains(i)) {
				continue;
			}
			Rotation rotation = findRotation(i, men, women, visitedMatches);
			if (rotation != null) {
				rotations.add(rotation);
			}
		}
		return rotations;
	}

	private Rotation findRotation(int index, List<Person> men, List<Person> women, Set<Integer> visitedMatches) {
		List<Integer> potentialRotation = new ArrayList<Integer>();
		potentialRotation.add(index);
		boolean rotationFound = false;
		while (!rotationFound) {
			if (visitedMatches.contains(index)) {
				//we've already checked this index.
				potentialRotation = null;
				break;
			}
			visitedMatches.add(index);
			Person man = men.get(index);
			Integer matchId = man.getMatch().getPosition();
			List<Integer> reducedPreferenceList = man.getFeasiblePreferences();
			Integer currentMatchIndex = reducedPreferenceList.indexOf(matchId);
			if (currentMatchIndex.equals(reducedPreferenceList.size() - 1)) {
				//no other match possibilities for this man. No rotation;
				potentialRotation = null;
				break;
			}
			Integer nextMatchId = reducedPreferenceList.get(currentMatchIndex + 1);
			Person nextMatch = women.get(nextMatchId);
			index = nextMatch.getMatch().getPosition();

			if (potentialRotation.contains(index)) {
				rotationFound = true;
			}
			potentialRotation.add(index);

		}
		System.out.println("potential: " + potentialRotation);
		return rotationOf(potentialRotation);
	}

	private Rotation rotationOf(List<Integer> potentialRotation) {
		if (potentialRotation == null) {
			return null;
		}
		Rotation result = new Rotation();
		Integer cycle = potentialRotation.get(potentialRotation.size() - 1);
		Integer firstOccurance = potentialRotation.indexOf(cycle);
		List<Integer> rotation = potentialRotation.subList(firstOccurance, potentialRotation.size() - 1);
		System.out.println("rotation: " + rotation);
		
		return result;
	}

	/**
	 * For a man M, matched with woman W, let p(M,W) be the index W in the
	 * preference list of M. Let a matching be identified as a list of p(M,W). Each
	 * matching has a unique identification in this way and the set of these
	 * matchings form a poset.
	 */
	private List<Integer> identifyMatching(List<List<Person>> manOptimalMatch) {
		List<Person> men = manOptimalMatch.get(0);
		List<Integer> matching = new ArrayList<Integer>();
		for (Person man : men) {
			Person match = man.getMatch();
			Integer matchPosition = man.getPreferenceList().indexOf(match.getPosition());
			matching.add(matchPosition);
		}
		return matching;
	}

	private void removeNonFeasiblePairs(List<List<Person>> groups, List<List<Person>> nonFeasiblePairs) {
		List<Person> men = groups.get(0);
		List<Person> women = groups.get(1);
		for (List<Person> pair : nonFeasiblePairs) {
			Person man = pair.get(0);
			Person woman = pair.get(1);
			man.markInfeasible(woman.getPosition());
			woman.markInfeasible(man.getPosition());
		}
	}

	private void findMostEquitableMatch(List<List<Person>> input) {
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

	private List<List<Person>> findNonFeasiblePairs(List<List<Person>> groups, List<List<Person>> manOptimalMatch,
			List<List<Person>> womanOptimalMatch) {
		List<Person> men = groups.get(0);
		List<Person> women = groups.get(1);
		List<Person> menOptimal = manOptimalMatch.get(0);
		List<Person> menPessimal = womanOptimalMatch.get(0);
		List<Person> womenOptimal = womanOptimalMatch.get(1);
		List<Person> womenPessimal = manOptimalMatch.get(1);

		List<List<Person>>nonFeasiblePairs = new ArrayList<List<Person>>();
		nonFeasiblePairs.addAll(findNonFeasiblePairsByGender(men, women, menOptimal, menPessimal, true));
		nonFeasiblePairs.addAll(findNonFeasiblePairsByGender(women, men, womenOptimal, womenPessimal, false));
		return nonFeasiblePairs;
	}

	private List<List<Person>> findNonFeasiblePairsByGender(List<Person> groupA, List<Person> groupB, List<Person> aOptimal,
			List<Person> aPessimal, boolean man) {
		List<List<Person>>nonFeasiblePairs = new ArrayList<List<Person>>();
		for (int personIndex = 0; personIndex < groupA.size(); personIndex++) {
			Person person = groupA.get(personIndex);
			Person optimalMatch = aOptimal.get(personIndex).getMatch();
			Person pessimalMatch = aPessimal.get(personIndex).getMatch();
			Integer optimalMatchIndex = groupB.indexOf(optimalMatch);
			Integer pessimalMatchIndex = groupB.indexOf(pessimalMatch);
			nonFeasiblePairs.addAll(findNonFeasiblePairsForIndividual(person, optimalMatchIndex, pessimalMatchIndex, groupB, man));
		}
		return nonFeasiblePairs;
	}

	private List<List<Person>> findNonFeasiblePairsForIndividual(Person person, Integer optimalMatchIndex,
			Integer pessimalMatchIndex, List<Person> groupB, boolean man) {
		List<List<Person>>nonFeasiblePairs = new ArrayList<List<Person>>();
		boolean feasibleRange = false;
		for (Integer preference : person.getPreferenceList()) {
			if (preference.equals(optimalMatchIndex)) {
				feasibleRange = true;
			}
			if (!feasibleRange) {
				Person nonFeasibleMatch = groupB.get(preference);
				if (man) {
					nonFeasiblePairs.add(Arrays.asList(person, nonFeasibleMatch));
				} else {
					nonFeasiblePairs.add(Arrays.asList(nonFeasibleMatch, person));
				}
			}
			if (preference.equals(pessimalMatchIndex)) {
				feasibleRange = false;
			}
		}
		return nonFeasiblePairs;
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
