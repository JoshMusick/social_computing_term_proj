import java.util.List;
import java.util.stream.Collectors;


public class FairMatching {
	
	//private List<List<Person>> groups = List.of();
	private List<Person> menGroup = List.of();
	private List<Person> womenGroup = List.of();
	
	private List<List<Person>> finalMatching = List.of();
	
	/** 
	 * This function will reset any class data, it is intended to be called before doing a new matching
	 */
	public void ResetData()
	{
		menGroup = List.of();
		womenGroup = List.of();
		finalMatching = List.of();
	}

	public static void main(String[] args) {
		// This will take a list of files, and run the algorithm on each of them
		List<String> inputs = List.of("input.txt", "input3.txt", "input4.txt", "test3.txt");
		
		FairMatching fm = new FairMatching();
		
		System.out.println("Starting Fair Matching...");
		for (String fileN : inputs) {
			System.out.println("*************************************");
			System.out.println("Loading input file " + fileN);
			
			fm.PerformMatching(fileN);	
			
		}
		System.out.println("*************************************");
		System.out.println("Fair Matching complete...");

	}

	public void PerformMatching(String filename) {
		
		ResetData();
		
		List<List<Person>> groups = InputParserUtility.ParseInput(filename);
		List<List<Person>> manOptimalMatch = GaleShapelyAlgorithm.execute(cloneGroups(groups), "m");
		List<List<Person>> womanOptimalMatch = GaleShapelyAlgorithm.execute(cloneGroups(groups), "w");
		
		menGroup = groups.get(0);
		womenGroup = groups.get(1);
		
		//PrintPreferences(menGroup, "Men");	
		PrintMatching(manOptimalMatch, "Man Optimal Matching", true);
		
		//PrintPreferences(womenGroup, "Women");
		PrintMatching(womanOptimalMatch, "Woman Optimal Matching", false);
	
		// trim the entries which are outside the optimal and pessimistic "bounds"
		trimAllFeasiblePreferences(manOptimalMatch, womanOptimalMatch);
		
		// Remove matches that have only a single "possible" match
		trimSingleFeasible();
		
		// Print the curretly possible Preference pairs
		PrintPrefPossible(true);
		
		// Trim options which are not feasible from the opposite group
		trimOppositeGroupFeasible();
		
		// Print the curretly possible Preference pairs
		PrintPrefPossible(false);
	}
	
	public void trimOppositeGroupFeasible()
	{
		System.out.println("***************************************************************");
		System.out.println("Removing possible matches based on non-mutual feasible lists...");
		trimMutualFeasibleLists(menGroup, womenGroup);
		trimMutualFeasibleLists(womenGroup, menGroup);
		
	}
	
	private void trimMutualFeasibleLists(List<Person> grpA, List<Person> grpB)
	{
		int count = 0;
		// Go through each person in group A
		for (Person p : grpA) {
			List<Integer> feasList = p.getFeasiblePreferences();
			// Go through each feasible match, and ensure that person also has person "p"
			//  	in their feasible list as well
			for (Integer f : feasList) {
				// If they don't have p in their list, mark them as infeasible also
				if (!grpB.get(f).getFeasiblePreferences().contains(p.getPosition())) {
					p.markInfeasible(f);
					count++;
				}
			}
		}
		System.out.println("Removed " + count + " infeasible non-mutual matches ...");
	}
	
	public void trimSingleFeasible()
	{
		System.out.println("Trimming Unique Matches from feasible lists...");
		trimUniqueMatch(menGroup, womenGroup);
		trimUniqueMatch(womenGroup, menGroup);
	}
	
	public void trimUniqueMatch(List<Person> grpA, List<Person> grpB) 
	{
		for (Person p : grpA) {
			List<Integer> prefs = p.getFeasiblePreferences();
			int cnt = prefs.size();
			if (cnt == 1) {
				// Person p only has 1 possible match, and their position is "matchNum"
				int matchNum = prefs.get(0);
				System.out.println("Unique Match found: " + p.getPosition() + ", " + matchNum);
				for (Person pers : grpA) 
				{
					// Ensure all other members of grpA have matchNum marked as infeasible
					if (!pers.equals(p)) {
						pers.markInfeasible(matchNum);
					}
				}
								
			}				
		}
		
	}
	
	private void PrintPrefPossible(List<Person> groupA, List<Person> groupB, List<Person> aOptimal,
			List<Person> aPessimal) {
		
		boolean isMale = true;
		if (groupA.size() > 0) {
			isMale = groupA.get(0).GetIsMale();
		}
		
		int cnt = groupA.size();
		
		// Loop through each person
		for (Person p : groupA) {
			int index = p.getPosition();
			int optNum = aOptimal.get(index).getMatch().getPosition();
			int pesNum = aPessimal.get(index).getMatch().getPosition();
			
			List<Integer> prefList = p.getPreferenceList();
			String options = "";
			boolean append = false;
			for (int i = 0; i < cnt; ++i) {
				int pr = prefList.get(i);
				if (pr == optNum) {
					append = true;
				}
				if (append) {
					options += " " + pr + "(" + i + ") ";
					
				}
				if (pr == pesNum) {
					break;
				}
			}
			
			System.out.println((isMale ? "M" : "W") + index + ": Optimal: " + optNum + " Pessimal: " + pesNum + 
					" ---- [ " + options + " ]");
			
		}
		
		
	}

	private void PrintPrefPossible(boolean printFullPrefs)
	{
		PrintGroupPrefPossible(menGroup, "M", printFullPrefs);
		PrintGroupPrefPossible(womenGroup, "F", printFullPrefs);
	}
	
	private void PrintGroupPrefPossible(List<Person> grp, String gend, boolean printFullPrefs)
	{
		System.out.println("Feasible Preferences for all " + gend);
		for (Person p : grp) {
			int index = p.getPosition();
			System.out.println(gend + index + (printFullPrefs ? " pref: " + p.getPreferenceList() : "" ) + " feas: " + p.getFeasiblePreferences());
		}
	}
	
	private void trimAllFeasiblePreferences(List<List<Person>> manOptimalMatch,	List<List<Person>> womanOptimalMatch) {
		List<Person> men = menGroup; // groups.get(0);
		List<Person> women = womenGroup; // groups.get(1);
		List<Person> menOptimal = manOptimalMatch.get(0);
		List<Person> menPessimal = womanOptimalMatch.get(0);
		List<Person> womenOptimal = womanOptimalMatch.get(1);
		List<Person> womenPessimal = manOptimalMatch.get(1);

		
//		PrintPrefPossible(men, women, menOptimal, menPessimal);
//		PrintPrefPossible(women, men, womenOptimal, womenPessimal);
		
		trimFeasiblePreferencesByGender(men, women, menOptimal, menPessimal);
		trimFeasiblePreferencesByGender(women, men, womenOptimal, womenPessimal);
	}

	private void trimFeasiblePreferencesByGender(List<Person> groupA, List<Person> groupB, List<Person> aOptimal,
			List<Person> aPessimal) {
		boolean isMale = true;
		if (groupA.size() > 0) {
			isMale = groupA.get(0).GetIsMale();
		}
		String gender = isMale ? "Males" : "Females";
		System.out.println("Feasible Preferences for all " + gender);
		for (int personIndex = 0; personIndex < groupA.size(); personIndex++) {
			Person person = groupA.get(personIndex);
			Person optimalMatch = aOptimal.get(personIndex).getMatch();
			Person pessimalMatch = aPessimal.get(personIndex).getMatch();
			Integer optimalMatchIndex = groupB.indexOf(optimalMatch);
			Integer pessimalMatchIndex = groupB.indexOf(pessimalMatch);
			trimFeasiblePreferencesForIndividual(person, optimalMatchIndex, pessimalMatchIndex);
			System.out.println((isMale ? "M" : "F") + personIndex + " Opt/Pess [ " + 
					aOptimal.get(personIndex).getMatch().getPosition() +
					" -> " + aPessimal.get(personIndex).getMatch().getPosition() + " ] pref: " +
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
	
	
	public void PrintMatching(List<List<Person>> matching, String title, boolean menFirst) {
		System.out.println("Results of " + title);
		StableMatchingUtils.printOutput(matching, menFirst);
	}
	
	public void PrintPreferences(List<Person> group, String gender)
	{
		System.out.println("Preference List for " + gender);
		for (Person p : group) 
		{
			System.out.println((p.GetIsMale() ? "M" : "F") + p.getPosition() + " pref: " +
					 p.getPreferenceList());
		}
	}
	
	private List<List<Person>> cloneGroups(List<List<Person>> groups) {
		return groups.stream() //
				.map(personList -> personList.stream() //
						.map(person -> person.clone(person)) //
						.collect(Collectors.toList())) //
				.collect(Collectors.toList());
	}

	
}
