import java.util.List;

public class StableMatchingUtils {

	public static boolean checkStableMatching(Matching people) {
		List<Person> men = people.getMen();
		List<Person> women = people.getWomen();
		for (Person man : men) {
			if (hasBlockingPair(man, women)) {
				return false;
			}
		}
		for (Person woman : women) {
			if (hasBlockingPair(woman, men)) {
				return false;
			}
		}
		return true;
	}

	private static boolean hasBlockingPair(Person person, List<Person> possibleMatches) {
		Person currentMatch = person.getMatch();
		if (currentMatch == null) {
			return false;
		}
		for (Integer preferredMatchIndex : person.getPreferenceList()) {
			Person preferredMatch = possibleMatches.get(preferredMatchIndex);
			if (preferredMatch.equals(currentMatch)) {
				break;
			}
			if (preferredMatch.prefers(person, preferredMatch.getMatch())) {
				return true;
			}
		}
		return false;
	}
	
	public static void printOutput(Matching matchedPeople, boolean menFirst) {
		printOutput(matchedPeople, menFirst, true);
	}
	
	public static void printOutput(Matching matchedPeople, boolean menFirst, boolean addOne) {

		int offset = addOne ? 1 : 0;
		List<Person> men = matchedPeople.getMen();
		List<Person> women = matchedPeople.getWomen();
		for (int i = 0; i < men.size(); i++) {
			int matchIndex;
			if (menFirst) {
				Person match = men.get(i).getMatch();
				matchIndex = women.indexOf(match);
			} else {
				Person match = women.get(i).getMatch();
				matchIndex = men.indexOf(match);
			}
			System.out.println("(" + (i + offset) + ", " + (matchIndex + offset) + ")");
		}
	}

	public static void printReducedPreferenceLists(Matching groups) {
		for (int i = 0; i < groups.getMen().size(); i++) {
			Person p = groups.getMen().get(i);
			System.out.println( i + " " + p.getFeasiblePreferences());
		}
		for (int i = 0; i < groups.getWomen().size(); i++) {
			Person p = groups.getWomen().get(i);
			System.out.println( i + " " + p.getFeasiblePreferences());
		}
	}

	public static void printPreferenceLists(Matching groups) {
		for (int i = 0; i < groups.getMen().size(); i++) {
			Person p = groups.getMen().get(i);
			System.out.println( i + " " + p.getPreferenceList());
		}
		for (int i = 0; i < groups.getWomen().size(); i++) {
			Person p = groups.getWomen().get(i);
			System.out.println( i + " " + p.getPreferenceList());
		}
	}

	public static Long calculateEquityScore(Matching match) {
		List<Person> men = match.getMen();
		List<Person> women = match.getWomen();
		Long equity = 0L;
		equity = men.stream() //
				.map(man -> calculatePreferenceScore(man, women)).reduce(0L, (a, b) -> a + b);
		equity += women.stream() //
				.map(woman -> calculatePreferenceScore(woman, men)).reduce(0L, (a, b) -> a + b);
		return equity;
	}

	public static Long calculatePreferenceScore(Person person, List<Person> preferenceGroup) {
		Integer matchIndex = preferenceGroup.indexOf(person.getMatch());
		if (matchIndex < 0) {
			throw new RuntimeException("invalid match");
		}
		Integer preferenceListIndex = person.getPreferenceList().indexOf(matchIndex);
		return preferenceListIndex.longValue();
	}
	
	public static void FindTotalFeasibleOptions(Matching match)
	{
		int cnt = 0;
		List<Person> grp1 = match.getMen();
		List<Person> grp2 = match.getWomen();
		
		for (Person p : grp1) {
			cnt += p.getFeasiblePreferences().size();
		}
		
		for (Person p : grp2) {
			cnt += p.getFeasiblePreferences().size();
		}
		System.out.println("Total number of feasible options possible are " + cnt);
	}
}
