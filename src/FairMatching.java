import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FairMatching {
	
	String m_filename;
	private void SetFilename(String name) {
		m_filename = name;
	}
	
	
	// Timer variables for tracking algorithm performance
	long startTime = 0;
	long endTime = 0;
	
	private void SetStart() { startTime = System.nanoTime(); }
	private void SetEnd() { endTime = System.nanoTime(); }
	
	public long GetTimeNS() {
		return endTime - startTime;
	}
	
	public double GetTimeMS() {
		long time_ns = GetTimeNS();
		double time_ms = (double)time_ns / 1000000.0;
		return time_ms;
	}
	
	// Results collection
//	private List<MatchingResult> results = new ArrayList<MatchingResult>();
	
	// Debugging display bitmask for controlling output
	public static final int DEBUG_PRINT_NONE = 0;
	public static final int DEBUG_PRINT_PREF_LIST = 1 << 0; // Displays the original preference list at various stages
	public static final int DEBUG_PRINT_FEAS_LIST = 1 << 1; // Displays the feasible preference list at various steps
	public static final int DEBUG_PRINT_MAN_OPT_MATCHING = 1 << 2; // Displays the man optimal matching from Gale Shapley
	public static final int DEBUG_PRINT_WOMAN_OPT_MATCHING = 1 << 3; // Displays the woman optimal matching from Gale Shapley
	public static final int DEBUG_PRINT_FEASIBLE_RANGE = 1 << 4; // Displays the initial feasible preference index range after optimal / pessimal trim
	public static final int DEBUG_PRINT_FEASIBLE_FROM_TRIM = 1 << 5; // Displays the feasible pref list after doing the optimal / pessimal trim
	
	public static final int DEBUG_PRINT_FEASIBLE_AFTER_UNIQUE_TRIM = 1 << 6; // Displays the feasible pref list after doing the Unique Feasible trim #1
	//public static final int DEBUG_PRINT_FEASIBLE_AFTER_UNIQUE_TRIM_2 = 1 << 7; // Displays the feasible pref list after doing the Unique Feasible trim #2
	
	public static final int DEBUG_PRINT_FEASIBLE_AFTER_MUTUAL_PREF_TRIM = 1 << 8; // Displays the feasible pref list after performing the mutual feasible trim
	
	public static final int DEBUG_PRINT_FEASIBLE_BEFORE_EQ_MATCHER = 1 << 9; // Displays the feasible pref list after doing the optimal / pessimal trim
	public static final int DEBUG_DISPLAY_BEST_MATCHING = 1 << 10; // display final matching
	public static final int DEBUG_PRINT_ALL = 0xFFFFFFFF;
		
	
	// Heuristic Step Bitmask, indicates steps to be used for finding optimal solution
	public static final int TRIM_NONE 	= 			0;		// do no pruning of input set
	public static final int TRIM_SINGLE_FEASIBLE = 1 << 0; // If a man or woman has a single feasible option, trim all others from having them as an option
	public static final int TRIM_MUTUAL_FEASIBLE = 1 << 1; // trim feasible options that are not mutual between all men and women
	public static final int TRIM_ALL = TRIM_SINGLE_FEASIBLE | TRIM_MUTUAL_FEASIBLE;
	
	
	
	//private List<List<Person>> groups = List.of();
	private List<Person> menGroup = List.of();
	private List<Person> womenGroup = List.of();
	
	/** 
	 * This function will reset any class data, it is intended to be called before doing a new matching
	 */
	public void ResetData()
	{
		startTime = 0;
		endTime = 0;
		menGroup = List.of();
		womenGroup = List.of();
		m_filename = "";
	}

	public static void main(String[] args) {
		// This will take a list of files, and run the algorithm on each of them
		List<String> inputs = List.of(
			//	"input.txt" 
			//	, "input3.txt" 
			//	, "input4.txt"
				"test_input_5.txt"
			//	, "test3.txt"
				);
		
		List<Integer> trimVals = List.of(
		//		  TRIM_NONE
		//		, TRIM_SINGLE_FEASIBLE
		//		, TRIM_MUTUAL_FEASIBLE
				 TRIM_ALL
				);		
		
		
		List<MatchingResult> results = new ArrayList<MatchingResult>();
		
		System.out.println("Starting Fair Matching...");
		int index = 0;
		for (Integer trim : trimVals) {
			
			// This is running 10 iterations for averages
			for (int i = 0; i < 2; ++i) {
				for (String fileN : inputs) {
					System.out.println("*************************************");
					System.out.println("Loading input file " + fileN);
					
					FairMatching fm = new FairMatching();
					
					MatchingResult res = fm.PerformMatching(fileN, trim, 
							//DEBUG_PRINT_NONE);
							//DEBUG_PRINT_ALL);
							DEBUG_PRINT_FEASIBLE_BEFORE_EQ_MATCHER | 
							DEBUG_DISPLAY_BEST_MATCHING
							);
					//| DEBUG_PRINT_FEASIBLE_BEFORE_EQ_MATCHER );// DEBUG_PRINT_NONE);	
					System.out.println("*************************************");
					index++;
					if (i != 0) {
						results.add(res);	
					}					
				}				
			}		
		}
		
		System.out.println("*************************************");
		
		PrintResults(results);		
		System.out.println("Fair Matching complete...");

	}

	public MatchingResult PerformMatching(String filename, int trimMask, int debugMask) {
		
		//ResetData();
		
		SetFilename(filename);
		
		Matching unPairedMatching = InputParserUtility.ParseInput(filename);
		
		SetStart();
		
		Matching manOptimalMatch = GaleShapelyAlgorithm.execute(unPairedMatching.clone(), "m");
		Matching womanOptimalMatch = GaleShapelyAlgorithm.execute(unPairedMatching.clone(), "w");
		
		SetEnd();
		long gs_time_ns = GetTimeNS();
		
		menGroup = unPairedMatching.getMen();
		womenGroup = unPairedMatching.getWomen();
		
		boolean printFullPreference = ((debugMask & DEBUG_PRINT_PREF_LIST) > 0);
		
		if ((debugMask & DEBUG_PRINT_MAN_OPT_MATCHING) > 0) {
			PrintMatching(manOptimalMatch, "Man Optimal Matching", true);
		}
		if ((debugMask & DEBUG_PRINT_WOMAN_OPT_MATCHING) > 0) {
			PrintMatching(womanOptimalMatch, "Woman Optimal Matching", false);
		}
		
		System.out.println("-----------------\nTrimming has begun...");
		
		SetStart();
		// trim the entries which are outside the optimal and pessimistic "bounds"
		trimAllFeasiblePreferences(manOptimalMatch, womanOptimalMatch, debugMask);
		
		boolean reTrim = true;
		int loopCnt = 0;
		while (reTrim) {
			reTrim = false;
			loopCnt++;
			
			if ((trimMask & TRIM_SINGLE_FEASIBLE) > 0) {
				// Remove matches that have only a single "possible" match
				if (trimSingleFeasible() > 0) {
					reTrim = true;
				}
				
				if ((debugMask & DEBUG_PRINT_FEASIBLE_AFTER_UNIQUE_TRIM) > 0) {
					// Print the currently possible Preference pairs
					PrintPrefPossible(printFullPreference);
				}
				
			}
			
			if ((trimMask & TRIM_MUTUAL_FEASIBLE) > 0) {
				// Trim options which are not feasible from the opposite group
				if(trimMutualFeasible() > 0) {
					reTrim = true;
				}
				
				if ((debugMask & DEBUG_PRINT_FEASIBLE_AFTER_MUTUAL_PREF_TRIM) > 0) {
					// Print the curretly possible Preference pairs
					PrintPrefPossible(printFullPreference);
				}
			
			}			
		}
		
		SetEnd();
		long heuristic_time = GetTimeNS();
		System.out.println("Trimming complete after " + loopCnt + " loops");
				
				
		// Print the preference list after the single feasible trim or final feasible list prior to using the EquitableMatcher
		if ((debugMask & ( DEBUG_PRINT_FEASIBLE_BEFORE_EQ_MATCHER)) > 0) {
			// Print the currently possible Preference pairs
			PrintPrefPossible(printFullPreference);
		}
						
		System.out.println("Starting Equitable Matcher ...");
		
		
		
		
		EquitableMatcher em = new EquitableMatcher();
		
		
		Matching m = CreateMatching(manOptimalMatch);
		
		System.out.println("-------------------");
		
		PrintMatching(m, "ManOptimal Matching - clone", false);
		
		PrintPrefPossible(m.getMen(), m.getWomen(), true);
		
		System.out.println("-------------------");
		
		StableMatchingUtils.FindTotalFeasibleOptions(m);
		
		SetStart();
		
		em.findAllMatchings(m, false);
		
		SetEnd();
		
		long equitable_time = GetTimeNS();
				
//		em.printResults(manOptimalMatch, womanOptimalMatch);
		
		long manEquityScore = StableMatchingUtils.calculateEquityScore(manOptimalMatch);
		long womanEquityScore = StableMatchingUtils.calculateEquityScore(womanOptimalMatch);
		
		if ((debugMask & DEBUG_DISPLAY_BEST_MATCHING) > 0) {
			System.out.println("The Best match is:...");
			Matching bestMatch = em.GetBestMatching();
			StableMatchingUtils.printOutput(bestMatch, true);		
		}
		
		
		System.out.println("Man Optimal Equitable Score - " + manEquityScore + 
				" :: Woman Optimal Equitable Score - " + womanEquityScore);
		
		return StoreMatchingResults(em.GetBestMatching(), manOptimalMatch, womanOptimalMatch, gs_time_ns, heuristic_time, equitable_time, trimMask );

	}
	
	private Matching CreateMatching(Matching mOpt)
	{
		Matching m = new Matching(menGroup, womenGroup);
		
		List<Person> grp = mOpt.getMen();
		for (Person p : grp) {		
			int matIndx = p.getMatch().getPosition();
			m.getMen().get(p.getPosition()).setMatch(m.getWomen().get(matIndx));
		}
		grp = mOpt.getWomen();
		for (Person p : grp) {
			int matIndx = p.getMatch().getPosition();
			m.getWomen().get(p.getPosition()).setMatch(m.getMen().get(matIndx));
		}
		
		return m;
	}
	
	private MatchingResult StoreMatchingResults(Matching bestMatch, Matching manOptMatch, Matching womanOptMatch, 
			long GS_time_ns, long heur_time_ns, long equityTime_ns, int trimMask)
	{
		
		int cnt = manOptMatch.men.size();
		
		int manManOpt = GetEquityScore(manOptMatch, true);
		int womanManOpt = GetEquityScore(manOptMatch, false);
		
		int manWomanOpt = GetEquityScore(womanOptMatch, true);
		int womanWomanOpt = GetEquityScore(womanOptMatch, false);
		
		int manEquit = GetEquityScore(bestMatch, true);
		int womanEquit = GetEquityScore(bestMatch, false);
		
		System.out.println("For Man Optimal - the man equity score is " + manManOpt + " and woman equity score is " + 
		womanManOpt + " :: with a total equity score of " + (manManOpt + womanManOpt));
		System.out.println("For Woman Optimal - the man equity score is " + manWomanOpt + " and woman equity score is " + 
				womanWomanOpt + " :: with a total equity score of " + (manWomanOpt + womanWomanOpt));
		
		MatchingResult res = new MatchingResult(cnt, GS_time_ns, heur_time_ns, equityTime_ns, m_filename);
		res.SetManValues(manManOpt, manWomanOpt, manEquit);
		res.SetWomanValues(womanWomanOpt, womanManOpt, womanEquit);
		res.SetTrimMask(trimMask);
		
		return res;
		//results.add(res);
		
	}
	
	public static void PrintResults(List<MatchingResult> results)
	{
		System.out.println(" results...");
		
		List<MatchingResult> resList = GetSortedResults(results);
		boolean first = true;
		for (MatchingResult r : resList)
		{
			if (first) {
				first = false;
				r.PrintHeader();
			}
			r.PrintData();
		}
		
		
	}

	public static List<MatchingResult> GetSortedResults(List<MatchingResult> results)
	{
		return results.stream()
				.sorted(Comparator.comparing(MatchingResult::GetNumPeople)).collect(Collectors.toList()); //.compareTo(o2.GetNumPeople()));
	}
	
	/**
	 * This method will compute a specific gender equity score
	 * @param match The matching to be evaluated
	 * @param getMan determines if the man equity or woman equity score should be calculated
	 * @return Sum of the man or woman equity score
	 */
	private int GetEquityScore(Matching match, boolean getMan) 
	{
		int score = 0;
		List<Person> grp = (getMan ? match.getMen() : match.getWomen());
		for (Person p : grp) {
		//	System.out.println("Person " + p.getPosition() + " has match of " + p.getMatch().getPosition());
			score += p.getPreferenceWeight(p.getMatch());
		}		
		return score;
	}
	
	public int trimMutualFeasible()
	{
		int cnt = 0;
		System.out.println("-----\nPerforming Mutual Feasible Trim...");
		cnt += trimMutualFeasibleLists(menGroup, womenGroup);
		cnt += trimMutualFeasibleLists(womenGroup, menGroup);
		return cnt;
	}
	
	/**
	 * This function will go through all members of grpA, and for each member of grpA, person p, test 
	 * that every person in p's list of feasible options (fp) also contains p.  If p is not in fp's list
	 * of feasible options, fp is removed from f's list. of trim non-mutually feasible options from each member of grpA, relative to all grpB 
	 * @param grpA - Group of persons that will be iterated over, testing for mutual feasible options in grpB
	 * @param grpB - Group of persons that will be checked for feasible options
	 * @return Number of non-feasible options that were removed during this check
	 */
	private int trimMutualFeasibleLists(List<Person> grpA, List<Person> grpB)
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
		return count;
	}
	
	public int trimSingleFeasible()
	{
		int cnt = 0;
		System.out.println("-----\nPerforming Single Feasible Trim...");
		cnt += trimUniqueMatch(menGroup, womenGroup);
		cnt += trimUniqueMatch(womenGroup, menGroup);
		System.out.println("Removed " + cnt + " single-feasible matches ...");
		return cnt;
	}
	
	public int trimUniqueMatch(List<Person> grpA, List<Person> grpB) 
	{
		int numReduced = 0;
		for (Person p : grpA) {
			List<Integer> prefs = p.getFeasiblePreferences();
			int cnt = prefs.size();
			if (cnt == 1) {
				// Person p only has 1 possible match, and their position is "matchNum"
				int matchNum = prefs.get(0);
			//	System.out.println("Unique Match found: " + p.getPosition() + ", " + matchNum);
				for (Person pers : grpA) 
				{
					// Ensure all other members of grpA have matchNum marked as infeasible
					if (!pers.equals(p)) {
						if (pers.IsFeasible(matchNum)) {
							pers.markInfeasible(matchNum);
							numReduced++;
						}						
					}
				}			
				
			}				
		}
		return numReduced;
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
	
	public void PrintPrefPossible(List<Person> men, List<Person> women, boolean printFullPrefs) 
	{
		PrintGroupPrefPossible(men, "M", printFullPrefs);
		PrintGroupPrefPossible(women, "F", printFullPrefs);
	}

	public void PrintPrefPossible(boolean printFullPrefs)
	{
		PrintPrefPossible(menGroup, womenGroup, printFullPrefs);
	}
	
	private void PrintGroupPrefPossible(List<Person> grp, String gend, boolean printFullPrefs)
	{
		System.out.println("Feasible Preferences for all " + gend);
		for (Person p : grp) {
			int index = p.getPosition();
			System.out.println(gend + index + (printFullPrefs ? " pref: " + p.getPreferenceList() : "" ) +
					" feas: " + p.getFeasiblePreferences());
		}
	}
	
	private void trimAllFeasiblePreferences(Matching manOptimalMatch, Matching womanOptimalMatch, 
			int debugMask) {
		List<Person> menOptimal = manOptimalMatch.getMen();
		List<Person> menPessimal = womanOptimalMatch.getMen();
		List<Person> womenOptimal = womanOptimalMatch.getWomen();
		List<Person> womenPessimal = manOptimalMatch.getWomen();
		
		System.out.println("Trimming Feasible Preferences for each gender, per man/woman optimal solutions...");
			
		trimFeasiblePreferencesByGender(menGroup, womenGroup, menOptimal, menPessimal, debugMask);
		trimFeasiblePreferencesByGender(womenGroup, menGroup, womenOptimal, womenPessimal, debugMask);
	}

	private void trimFeasiblePreferencesByGender(List<Person> groupA, List<Person> groupB, 
			List<Person> aOptimal, List<Person> aPessimal, int debugMask) {
		boolean isMale = true;
		if (groupA.size() > 0) {
			isMale = groupA.get(0).GetIsMale();
		}
		
		for (int personIndex = 0; personIndex < groupA.size(); personIndex++) {
			Person person = groupA.get(personIndex);
			Person optimalMatch = aOptimal.get(personIndex).getMatch();
			Person pessimalMatch = aPessimal.get(personIndex).getMatch();
			
			Integer optimalMatchPos = optimalMatch.getPosition();
			Integer pessimalMatchPos = pessimalMatch.getPosition();
			
			
			//Integer optimalMatchIndex = optimalMatch.getPosition();// groupB.indexOf(optimalMatch);
			//Integer pessimalMatchIndex = pessimalMatch.getPosition();// groupB.indexOf(pessimalMatch);
			trimFeasiblePreferencesForIndividual(person, optimalMatchPos, pessimalMatchPos);
			
			System.out.println(person.getPosition() + "--- Opt: " + optimalMatchPos + " Pess: " + pessimalMatchPos);
			
			if ((debugMask & (DEBUG_PRINT_FEASIBLE_RANGE | DEBUG_PRINT_PREF_LIST | DEBUG_PRINT_FEASIBLE_FROM_TRIM )) > 0 ) {
				System.out.println("Feasible Preferences for all " + (isMale ? "Males" : "Females"));
				String debugString = (isMale ? "M" : "F") + personIndex;
				if ((debugMask & DEBUG_PRINT_FEASIBLE_RANGE) > 0) {
					debugString += " Opt/Pess [ " + aOptimal.get(personIndex).getMatch().getPosition() +
							" -> " + aPessimal.get(personIndex).getMatch().getPosition() + " ]";
				}
				if ((debugMask & DEBUG_PRINT_PREF_LIST) > 0) {
					debugString += " pref: " + person.getPreferenceList();				
				}
				
				if ((debugMask & DEBUG_PRINT_FEASIBLE_FROM_TRIM) > 0) {
					debugString += " feas: " + person.getFeasiblePreferences();				
				}			
				System.out.println(debugString);				
			}
			
		}
	}
	
	private void trimFeasiblePreferencesForIndividual(Person person, Integer optimalMatch,
			Integer pessimalMatch) {
		boolean feasibleRange = false;
		for (Integer preference : person.getPreferenceList()) {
			if (preference.equals(optimalMatch)) {
				feasibleRange = true;
			}
			if (!feasibleRange) {
				person.markInfeasible(preference);
			}
			if (preference.equals(pessimalMatch)) {
				feasibleRange = false;
			}
		}
	}
	
	
	public void PrintMatching(Matching matching, String title, boolean menFirst) {
		System.out.println("Results of " + title);
		StableMatchingUtils.printOutput(matching, menFirst);
	}
	
//	public void PrintPreferences(List<Person> group, String gender)
//	{
//		System.out.println("Preference List for " + gender);
//		for (Person p : group) 
//		{
//			System.out.println((p.GetIsMale() ? "M" : "F") + p.getPosition() + " pref: " +
//					 p.getPreferenceList());
//		}
//	}
	
}
