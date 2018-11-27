# Fair Stable Marriage - Social Computing Term Project Fall 2018
Authors: Swapna Mukrappilly, Jason Trout, Zach Southwell, and Josh Musick

Term project for social computing. This branch implements an algorithm to calculate the optimal stable matching based on the concept of Rotations presented in the Gusfield/Irving book and outlined in the included term project report. 'Optimal' is defined as the matching with a lowest cumulative 'equity score' for all participants in the matching, where an individual's equity score is the index of the individual's match in their preference list.


This repo contains the source code, test cases, and report documents for a Fair Stable Matching implementation of the Stable Marriage Problem.  This repo contains multiple programs.  One for generating randomized test cases, and another for taking a set of test cases and calculating the *fair* stable match.

This project implements a number of heuristics and a rotation algorithm to calculate a fair stable matching. This is defined as the matching with a lowest cumulative 'fairness score' for all participants in the matching, where an individual's fairness score is the index of the individual's match in their preference list.

## Input Generator
The input generator will create a randomized preference list in the format used for this course's previous homework problems.  The program takes 3 arguments, the number of men / women (n), the test case output filename, and the number of test cases to create using the first two settings.

The program would be ran as follows:

```
$ java InputGenerator <n> <filename_root> <num_cases>
...
```

If the program was ran with the following arguments:
```
$ java InputGenerator 100 tests/prefs_100_ 20
...
```

The program would create 20 different input preference lists for 100 men / women.  Those files would be created in the "tests" directory.  For each preference list created, there will be a plain text file (with space and new line delimiters) and a csv file for other types of viewing / manipulating.  The index of the file created will also be appended to the filename. The files created would be:

```
prefs_100_1.txt
prefs_100_1.csv
prefs_100_2.txt
prefs_100_2.csv
...
prefs_100_19.txt
prefs_100_19.csv
prefs_100_20.txt
prefs_100_20.csv
```

## Fair Matching
There are also two other programs that are included, for calculating the fair matching given an input file set.

### EquitableMatcher

The EquitableMatcher will take a single input file and calculate the fair match.

To execute: compile the code and run with an 'inputFile' parameter. Ex:
```
$> java EquitableMatcher input.txt
```

This program will output the fairness / equitable scores for the man-optimal, woman-optimal, and fair matching, as well as the matching itself.

### FairMatching

The FairMatching application is intended to run a batch of input files, perform some heuristic analysis and also perform the rotation algorithm for finding the fair matching.  It will then output the performance results of all the runs performed.

To execute: compile the code and run it with the following parameters:
```
$ java FairMatching <filename_root> <num_cases> <num_iterations (optional)>
```

This application is expecting input files in a similar format as output by the InputGenerator.  Therefore, if you rset of files that you wish to run have the following filenames in the "tests" directory:

```
test_input_1000_1.txt
test_input_1000_2.txt
test_input_1000_3.txt
test_input_1000_4.txt
test_input_1000_5.txt
```

You would lauch the application with the following command:
``` 
$ java FairMatching tests/test_input_1000_ 5
...
```

If you want run multiple iterations of each input file (for profiling / timing of a specific input), you can provide a third argument, which will indicate how many times each input should be "solved".
