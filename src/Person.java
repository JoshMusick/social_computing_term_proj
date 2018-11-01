import java.util.List;

public class Person {
	private List<Person> preferenceList = List.of();
	private Person match;
	private Person lastRejected;

	public List<Person> getPreferenceList() {
		return preferenceList;
	}

	public void setPreferenceList(List<Person> preferenceList) {
		this.preferenceList = preferenceList;
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
		return preferenceList.indexOf(personA) < preferenceList.indexOf(personB);
	}
}
