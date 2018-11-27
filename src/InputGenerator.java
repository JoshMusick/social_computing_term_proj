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
		
		int len = args.length;
		if (len != 3) {
			System.out.println("Invalid arguments... Use the following args");
			System.out.println("generator <num people> <filename root> <number of different inputs to create>");
			return;
		}
		
		Integer n = Integer.parseInt(args[0]);
		String filenameRoot = args[1];
		Integer num = Integer.parseInt(args[2]);
		
		CreateRandomTestCases(n, filenameRoot, num);
		return;
		
		/*
		String generatedInput = generateRandomInput(Integer.valueOf(args[0]));
		System.out.println(generatedInput);
		
		if (false) {
			CreateRandomInput(10, "tests/test_10_1");
			System.out.println("**********************************************");
			CreateRandomInput(100, "tests/test_100_1");
			CreateRandomInput(100, "tests/test_100_2");
			CreateRandomInput(100, "tests/test_100_3");
			CreateRandomInput(100, "tests/test_100_4");
			CreateRandomInput(100, "tests/test_100_5");
			CreateRandomInput(100, "tests/test_100_6");
			CreateRandomInput(100, "tests/test_100_7");
			System.out.println("**********************************************");
			CreateRandomInput(1000, "tests/test_1000_1");
			CreateRandomInput(1000, "tests/test_1000_2");
			CreateRandomInput(1000, "tests/test_1000_3");
			CreateRandomInput(1000, "tests/test_1000_4");
			System.out.println("**********************************************");
				
		}
		if (false) {
			CreateRandomInput(2000, "tests/test_2000_1");
			CreateRandomInput(2000, "tests/test_2000_2");
			CreateRandomInput(2000, "tests/test_2000_3");
			CreateRandomInput(2000, "tests/test_2000_4");
			System.out.println("**********************************************");	
		}
		
		if (false) {
			CreateRandomInput(5000, "tests/test_5000_1");
			CreateRandomInput(5000, "tests/test_5000_2");
			CreateRandomInput(5000, "tests/test_5000_3");
			CreateRandomInput(5000, "tests/test_5000_4");
			System.out.println("**********************************************");	
		}
		*/
	}
	
	public static void CreateRandomTestCases(Integer n, String name_root, Integer numCases)
	{
		if (numCases <= 0) {
			System.out.print("numCases must be > 0");
		}
		for (int i = 0; i < numCases; ++i) 
		{
			String name = name_root + (i+1);
			System.out.println("Creating test case file " + name + " with n == " + n + "...");
			
			CreateRandomInput(n, name);
			System.out.println(name + " created");
		}
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
	
	public static void CreateRandomInput(Integer numPairs, String name) {
		
		
		List<Integer> list = new ArrayList<Integer>();
		
		for (int i = 1; i <= numPairs; ++i) {
			list.add(i);
		}
	
		int cnt = numPairs * 2;
		
		PrintWriter pwCsv;
		PrintWriter pwPlain;
		
        try {
            pwCsv = new PrintWriter(new File(name + ".csv"));
            pwPlain = new PrintWriter(new File(name + ".txt"));
 
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
