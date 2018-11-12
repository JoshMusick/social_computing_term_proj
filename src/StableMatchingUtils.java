import java.util.List;

public class StableMatchingUtils {

	public static boolean checkStableMatching(List<List<Person>> people) {
		List<Person> men = people.get(0);
		List<Person> women = people.get(1);
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
	
	public static void printOutput(List<List<Person>> matchedPeople, boolean menFirst) {
		printOutput(matchedPeople, menFirst, true);
	}
	
	public static void printOutput(List<List<Person>> matchedPeople, boolean menFirst, boolean addOne) {

		int offset = addOne ? 1 : 0;
		List<Person> men = matchedPeople.get(0);
		List<Person> women = matchedPeople.get(1);
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

	public static void printReducedPreferenceLists(List<List<Person>> groups) {
		for (List<Person> group : groups) {
			for (int i = 0; i < group.size(); i++) {
				Person p = group.get(i);
				System.out.println( i + " " + p.getFeasiblePreferences());
			}
		}
	}
}
