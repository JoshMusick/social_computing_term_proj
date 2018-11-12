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
}
