import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class InputGenerator {

	public static void main(String[] args) {
		//String generatedInput = generateRandomInput(Integer.valueOf(args[0]));
		//System.out.println(generatedInput);
		
		CreateRandomInput(10, 5);
		System.out.println("**********************************************");
		CreateRandomInput(100, 6);
		System.out.println("**********************************************");
		CreateRandomInput(1000, 7);
		System.out.println("**********************************************");
	}

	public static String generateRandomInput(Integer pairs) {

		System.out.println("Generating sample preference set for " + pairs + " pairs...");
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
	
	public static void CreateRandomInput(Integer numPairs, int num) {
		
		
		List<Integer> list = new ArrayList<Integer>();
		
		for (int i = 1; i <= numPairs; ++i) {
			list.add(i);
		}
	
		int cnt = numPairs * 2;
		
		PrintWriter pwCsv;
		PrintWriter pwPlain;
		
        try {
            pwCsv = new PrintWriter(new File("test_input_" + num + ".csv"));
            pwPlain = new PrintWriter(new File("test_input_" + num + ".txt"));
 
            pwCsv.write(numPairs.toString());
            pwPlain.write(numPairs.toString());
            pwCsv.write(System.lineSeparator());
            pwPlain.write(System.lineSeparator());
            
            for (int j = 0; j < cnt; ++j) {
    			Collections.shuffle(list);
    			
    			StringBuffer csvData = new StringBuffer("");
                StringBuffer plainData = new StringBuffer("");
                
    			for (int i : list) {
    				csvData.append(i);
    				csvData.append(',');
    				
    				plainData.append(i);
    				plainData.append(' ');
    			}
    			  			
    			pwCsv.write(csvData.toString());
    			pwPlain.write(plainData.toString());
    		
    			pwCsv.write(System.lineSeparator());
                pwPlain.write(System.lineSeparator());
    		}
                     
           
            pwCsv.close();
            pwPlain.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }		
		
	}
		
}
