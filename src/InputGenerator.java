import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InputGenerator {

	public static void main(String[] args) {
		String generatedInput = generateRandomInput(Integer.valueOf(args[0]));
		System.out.println(generatedInput);
	}

	public static String generateRandomInput(Integer pairs) {

		String output = pairs.toString() + System.lineSeparator();
		Integer size = pairs;
		List<Integer> preferenceList = IntStream.rangeClosed(1, size) //
				.boxed() //
				.collect(Collectors.toList());

		for (int i = 0; i < size * 2; i++) {
			Collections.shuffle(preferenceList);
			String shuffledPreferenceList = preferenceList.stream() //
					.map(num -> num.toString()) //
					.collect(Collectors.joining(" "));
			output += shuffledPreferenceList + System.lineSeparator();
		}
		return output;
	}
}
