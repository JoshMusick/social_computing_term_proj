import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InputParserUtility {

	public static Matching ParseInput(String filename) {
		File inputFile = new File(filename);
		if (!inputFile.canRead()) {
			System.out.println("cannot read input file: " + inputFile.getName());
		}
		try {
			List<String> lines = Files.readAllLines(inputFile.toPath());
			return parseInput(lines);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("fail to parse input file: " + e.getMessage());
			return null;
		}
	}

	public static Matching parseInput(final List<String> inputLines) throws Exception {

		List<String> lines = inputLines.stream() //
				.map(line -> line.replaceAll("^\\s+", "")) // remove leading spaces
				.filter(line -> (line.matches("^\\d+.*$"))) // filter lines that don't start with a digit
				.map(line -> line.replaceAll("[^0-9 ].*$", "")) // remove nondigit values
				.map(line -> line.replaceAll("\\s+$", "")) // remove trailing spaces
				.collect(Collectors.toList()); //

		lines.remove(0); // we don't need the list size; we'll infer it.

		List<Person> men = new ArrayList<Person>();
		List<Person> women = new ArrayList<Person>();
		for (int i = 0; i < lines.size() / 2; i++) {
			men.add(new Person(i, true));
			women.add(new Person(i, false));
		}

		List<List<Integer>> preferenceLists = lines.stream() //
				.map(line -> Arrays.stream(line.split("\\s+")) //
						.map(Integer::valueOf) // convert Strings to Integers
						.map(val -> val - 1) // switch to zero based index
						.collect(Collectors.toList())) //
				.collect(Collectors.toList());

		for (Person man : men) {
			List<Integer> preferenceList = preferenceLists.remove(0);
			man.setPreferenceList(preferenceList);
		}

		for (Person woman : women) {
			List<Integer> preferenceList = preferenceLists.remove(0);
			woman.setPreferenceList(preferenceList);
		}

		return new Matching(men, women);
	}
}
