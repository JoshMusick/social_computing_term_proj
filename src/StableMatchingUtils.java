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
			System.out.println("(" + (i + 1) + ", " + (matchIndex + 1) + ")");
		}
	}
}
