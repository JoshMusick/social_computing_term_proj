import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Matching implements Cloneable{

	List<Person> men = new ArrayList<Person>();
	List<Person> women = new ArrayList<Person>();

	public Matching() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Matching(List<Person> men, List<Person> women) {
		super();
		this.men = men;
		this.women = women;
	}

	/**
	 * @return the men
	 */
	public List<Person> getMen() {
		return men;
	}

	/**
	 * @param men the men to set
	 */
	public void setMen(List<Person> men) {
		this.men = men;
	}

	/**
	 * @return the women
	 */
	public List<Person> getWomen() {
		return women;
	}

	/**
	 * @param women the women to set
	 */
	public void setWomen(List<Person> women) {
		this.women = women;
	}

	/**
	 * For a man M, matched with woman W, let p(M,W) be the index W in the
	 * preference list of M. Let a matching be identified as a list of p(M,W). Each
	 * matching has a unique identification in this way and the set of these
	 * matchings form a poset.
	 * 
	 * @return the matchingId
	 */
	public List<Integer> getMatchingId() {
		List<Integer> menOptimality = new ArrayList<Integer>();
		for (Person man : men) {
			Person match = man.getMatch();
			Integer matchPosition = man.getPreferenceList().indexOf(match.getPosition());
			menOptimality.add(matchPosition);
		}
		return menOptimality;
	}

	public Matching clone() {
		Matching clone = new Matching();
		List<Person> men = this.men.stream() //
				.map(man -> man.clone())
				.collect(Collectors.toList());
		List<Person> women = this.women.stream() //
				.map(woman -> woman.clone()) //
				.collect(Collectors.toList());
		clone.setMen(men);
		clone.setWomen(women);
		return clone;
	}
	
	/**
	 * This function will determine if the feasible options of this are identical to "m"
	 * @param m - Matching to be compared with this
	 * @return - True if feasible matchings are identical for both men and women
	 */
	public boolean IsFeasibleSetEqual(Matching m) {
	
		List<Person> m1 = this.getMen();
		List<Person> m2 = m.getMen();
		
		List<Person> w1 = this.getWomen();
		List<Person> w2 = m.getWomen();
		
		if ((m1.size() != m2.size()) || (w1.size() != w2.size()) || (m1.size() != w1.size())) {
			System.out.println("Group sizes are not equal");
			return false;
		}
		
		for (int i = 0; i < m1.size(); ++i) {
			List<Integer> pref1 = m1.get(i).getFeasiblePreferences();
			List<Integer> pref2 = m2.get(i).getFeasiblePreferences();
			
			if (pref1.size() != pref2.size()) {
				System.out.println("Man preference feasible lists not equal for person " + i );
				System.out.println("P1 has size " + pref1.size() + " and P2 has size " + pref2.size()); 
				return false;
			}
			for (int j = 0; j < pref1.size(); ++j) {
				if (!pref1.get(j).equals(pref2.get(j))) {
					System.out.println("Man preference not equal for person " + i + " at pref " + j );
					return false;
				}
			}
			
			pref1 = w1.get(i).getFeasiblePreferences();
			pref2 = w2.get(i).getFeasiblePreferences();
			
			if (pref1.size() != pref2.size()) {
				System.out.println("Woman preference feasible lists not equal for person " + i );
				return false;
			}
			for (int j = 0; j < pref1.size(); ++j) {
				if (!pref1.get(j).equals(pref2.get(j))) {
					System.out.println("Woman preference not equal for person " + i + " at pref " + j );
					return false;
				}
			}	
		}		
		return true;
	}
}
