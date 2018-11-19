import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Person implements Cloneable {

	private List<Integer> preferenceList = List.of();
	private Person match;
	private Person lastRejected;
	private int position;
	private Set<Integer> feasibleMatches = Set.of();
	
	private boolean isMale;

	public Person(int position, boolean isMale) {
		this.position = position;
		this.isMale = isMale;
	}
	
	public boolean GetIsMale() {
		return this.isMale;
	}

	public List<Integer> getPreferenceList() {
		return preferenceList;
	}

	public void setPreferenceList(List<Integer> preferenceList) {
		this.preferenceList = preferenceList;
		feasibleMatches = new HashSet<Integer>(preferenceList);
	}

	public void markInfeasible(Integer index) {
		feasibleMatches.remove(index);
	}

	public List<Integer> getFeasiblePreferences() {
		return preferenceList.stream() //
				.filter(i -> feasibleMatches.contains(i)) //
				.collect(Collectors.toList());
	}

	public Person getMatch() {
		return match;
	}

	public void setMatch(Person match) {
		this.match = match;
	}

	public Person getLastRejected() {
		return lastRejected;
	}

	public void setLastRejected(Person lastRejected) {
		this.lastRejected = lastRejected;
	}

	public boolean prefers(Person personA, Person personB) {
		return preferenceList.indexOf(personA.position) < preferenceList.indexOf(personB.position);
	}
	
	/**
	 * Determines the index of person p in this person's preference list
	 * @param p person to be identified in the preference list
	 * @return index of person p in this preference list
	 */
	public int getPreferenceWeight(Person p) {
		int pos = p.getPosition();
		return preferenceList.indexOf(pos);
	}
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Person)) {
			return false;
		}
		Person person = (Person) o;
		return position == person.position;
	}

	@Override
	public int hashCode() {
		return Objects.hash(position);
	}

	public Person clone() {
		Person clone = new Person(this.position, this.isMale);
		clone.feasibleMatches = new HashSet<Integer>(this.feasibleMatches);
		clone.lastRejected = this.lastRejected;
		clone.match = this.match;
		clone.preferenceList = this.preferenceList;
		return clone;
	}
}
