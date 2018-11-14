import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EquitableMatcher {

	private Long bestMatchValue = Long.MAX_VALUE;
	private Matching bestMatch = null;
	private Integer numberOfMatchings = 0;
	Set<List<Integer>> uniqueMatchings = new HashSet<List<Integer>>();

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("usage: EquitableMatcher <input>\n");
			return;
		}
		EquitableMatcher em = new EquitableMatcher();
		Matching unPairedMatching = InputParserUtility.ParseInput(args[0]);
		em.match(unPairedMatching);
	}

	public Integer match(Matching unPairedMatching) {

		Matching manOptimalMatch = GaleShapelyAlgorithm.execute(unPairedMatching.clone(), "m");
		Matching womanOptimalMatch = GaleShapelyAlgorithm.execute(unPairedMatching.clone(), "w");

		// TODO: combine these into one method
		List<List<Integer>> nonFeasiblePairs = findNonFeasiblePairs(manOptimalMatch, womanOptimalMatch);
		removeNonFeasiblePairs(nonFeasiblePairs, manOptimalMatch);

		StableMatchingUtils.printReducedPreferenceLists(manOptimalMatch);

		findAllMatchings(manOptimalMatch, true);

		printResults(manOptimalMatch, womanOptimalMatch);
		
		return numberOfMatchings;
	}
	
	public void printResults(Matching manOptimalMatch, Matching womanOptimalMatch )
	{
		Long equityScore = StableMatchingUtils.calculateEquityScore(manOptimalMatch);
		System.out.println("Man-optimal equity score: " + equityScore);
		equityScore = StableMatchingUtils.calculateEquityScore(womanOptimalMatch);
		System.out.println("Woman-optimal equity score: " + equityScore);
		System.out.println("Optimal matching:");
		StableMatchingUtils.printOutput(bestMatch, true);
		System.out.println("Total matchings: " + numberOfMatchings + " Optimal equity score: " + bestMatchValue);
	}

	public void findAllMatchings(Matching baseMatching, boolean printMatching) {

		List<Integer> matchId = baseMatching.getMatchingId();
		if (uniqueMatchings.contains(matchId)) {
			return;
		}

		uniqueMatchings.add(matchId);
		// If we get here, then we've identified a new matching!
		evaluateMatching(baseMatching, printMatching);

		for (Rotation rotation : identifyRotations(baseMatching)) {
			Matching rotatedMatch = getRotatedMatch(baseMatching, rotation);
			findAllMatchings(rotatedMatch, printMatching);
		}
	}

	private Matching getRotatedMatch(Matching matching, Rotation rotation) {
		Matching rotatedMatching = matching.clone();
		List<Person> men = rotatedMatching.getMen();
		List<Person> women = rotatedMatching.getWomen();
		for (Integer index : rotation.getRotation()) {
			Person man = men.get(index);
			List<Integer> reducedPreferenceList = man.getFeasiblePreferences();
			Integer currentMatchIndex = reducedPreferenceList.get(0);
			Integer rotatedMatchIndex = reducedPreferenceList.get(1);
			Person rotatedMatch = women.get(rotatedMatchIndex);
			man.markInfeasible(currentMatchIndex);
			man.setMatch(rotatedMatch);
			rotatedMatch.setMatch(man);
		}
		// rotated matches have improved their matching. Let's make sure to mark any
		// pairs below a womens' current matches as infeasible
		updateFeasibility(rotatedMatching);
		return rotatedMatching;
	}

	private void updateFeasibility(Matching rotatedMatching) {
		List<Person> men = rotatedMatching.getMen();
		List<Person> women = rotatedMatching.getWomen();

		for (Person woman : women) {
			boolean feasible = true;
			for (Integer position : woman.getFeasiblePreferences()) {
				if (!feasible) {
					Person man = men.get(position);
					woman.markInfeasible(position);
					man.markInfeasible(woman.getPosition());
				}
				if (position.equals(woman.getMatch().getPosition())) {
					feasible = false;
				}
			}
		}
	}

	private List<Rotation> identifyRotations(Matching matching) {
		Set<Integer> visitedMatches = new HashSet<Integer>();
		List<Rotation> rotations = new ArrayList<Rotation>();
		List<Person> men = matching.getMen();
		List<Person> women = matching.getWomen();
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
				// we've already checked this index.
				potentialRotation = null;
				break;
			}
			visitedMatches.add(index);
			Person man = men.get(index);
			Integer matchId = man.getMatch().getPosition();
			List<Integer> reducedPreferenceList = man.getFeasiblePreferences();
			Integer currentMatchIndex = reducedPreferenceList.indexOf(matchId);
			if (currentMatchIndex.equals(reducedPreferenceList.size() - 1)) {
				// no other match possibilities for this man. No rotation;
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
		result.setRotation(rotation);
		return result;
	}

	private void removeNonFeasiblePairs(List<List<Integer>> nonFeasiblePairs, Matching match) {
		for (List<Integer> pair : nonFeasiblePairs) {
			Integer manIndex = pair.get(0);
			Integer womanIndex = pair.get(1);
			Person man = match.getMen().get(manIndex);
			Person woman = match.getWomen().get(womanIndex);
			System.out.println("man: " + man.getPosition() + "woman: " + woman.getPosition());
			man.markInfeasible(woman.getPosition());
			woman.markInfeasible(man.getPosition());
		}
	}

	private List<List<Integer>> findNonFeasiblePairs(Matching manOptimalMatch, Matching womanOptimalMatch) {
		List<Person> menOptimal = manOptimalMatch.getMen();
		List<Person> menPessimal = womanOptimalMatch.getMen();
		List<Person> womenOptimal = womanOptimalMatch.getWomen();
		List<Person> womenPessimal = manOptimalMatch.getWomen();

		List<List<Integer>> nonFeasiblePairs = new ArrayList<List<Integer>>();
		nonFeasiblePairs.addAll(findNonFeasiblePairsByGender(menOptimal, menPessimal, womenPessimal, true));
		nonFeasiblePairs.addAll(findNonFeasiblePairsByGender(womenOptimal, womenPessimal, menOptimal, false));
		return nonFeasiblePairs;
	}

	private List<List<Integer>> findNonFeasiblePairsByGender(List<Person> aOptimal, List<Person> aPessimal,
			List<Person> groupB, boolean man) {
		List<List<Integer>> nonFeasiblePairs = new ArrayList<List<Integer>>();
		for (int personIndex = 0; personIndex < aOptimal.size(); personIndex++) {
			Person person = aOptimal.get(personIndex);
			Person optimalMatch = aOptimal.get(personIndex).getMatch();
			Person pessimalMatch = aPessimal.get(personIndex).getMatch();
			Integer optimalMatchIndex = groupB.indexOf(optimalMatch);
			Integer pessimalMatchIndex = groupB.indexOf(pessimalMatch);
			nonFeasiblePairs.addAll(
					findNonFeasiblePairsForIndividual(person, optimalMatchIndex, pessimalMatchIndex, groupB, man));
		}
		return nonFeasiblePairs;
	}

	private List<List<Integer>> findNonFeasiblePairsForIndividual(Person person, Integer optimalMatchIndex,
			Integer pessimalMatchIndex, List<Person> groupB, boolean man) {
		List<List<Integer>> nonFeasiblePairs = new ArrayList<List<Integer>>();
		boolean feasibleRange = false;
		for (Integer preference : person.getPreferenceList()) {
			if (preference.equals(optimalMatchIndex)) {
				feasibleRange = true;
			}
			if (!feasibleRange) {
				Person nonFeasibleMatch = groupB.get(preference);
				if (man) {
					nonFeasiblePairs.add(Arrays.asList(person.getPosition(), nonFeasibleMatch.getPosition()));
				} else {
					nonFeasiblePairs.add(Arrays.asList(nonFeasibleMatch.getPosition(), person.getPosition()));
				}
			}
			if (preference.equals(pessimalMatchIndex)) {
				feasibleRange = false;
			}
		}
		return nonFeasiblePairs;
	}

	private void evaluateMatching(Matching matching, boolean printMatching) {
		numberOfMatchings++;
		Long equityScore = StableMatchingUtils.calculateEquityScore(matching);
		if (printMatching) {
			System.out.println("matching: " + matching.getMatchingId() + " equity score: " + equityScore);
		}		
		if (equityScore < bestMatchValue) {
			bestMatchValue = equityScore;
			bestMatch = matching;
		}
		return;
	}

}
