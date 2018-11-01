import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InputParserUtility {

	public static List<List<Person>> ParseInput(String filename) {
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

	private static List<List<Person>> parseInput(final List<String> inputLines) throws Exception {

		List<String> lines = inputLines.stream() //
				.map(line -> line.replaceAll("^\\s+", "")) // remove leading spaces
				.filter(line -> (line.matches("^\\d+.*$"))) // filter lines that don't start with a digit
				.map(line -> line.replaceAll("[^0-9 ].*$", "")) // remove nondigit values
				.map(line -> line.replaceAll("\\s+$", "")) // remove trailing spaces
				.collect(Collectors.toList()); //

		lines.remove(0); // we don't need the list size; we'll infer it.

		List<Person> men = new ArrayList<Person>();
		List<Person> women = new ArrayList<Person>();
		for (int i = 0; i < lines.size(); i = i + 2) {
			men.add(new Person());
			women.add(new Person());
		}


		List<List<Integer>> preferenceLists = lines.stream() //
				.map(line -> Arrays.stream(line.split("\\s+")) //
						.map(Integer::valueOf) //convert Strings to Integers
						.collect(Collectors.toList())) //
				.collect(Collectors.toList());

		for (Person man : men) {
			List<Person> preferenceList = preferenceLists.remove(0).stream() //
					.map(index -> women.get(index - 1)) //
					.collect(Collectors.toList());
			man.setPreferenceList(preferenceList);
		}

		for (Person woman : women) {
			List<Person> preferenceList = preferenceLists.remove(0).stream() //
					.map(index -> men.get(index - 1)) //
					.collect(Collectors.toList());
			woman.setPreferenceList(preferenceList);
		}

		List<List<Person>> groups = List.of(men, women);

		return groups;
	}
}
