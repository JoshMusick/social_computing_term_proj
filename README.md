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

### FairMatching

The FairMatching application is intended to run a batch of input files, perform some heuristic analysis and also perform the rotation algorithm for finding the fair matching.  It will then output the performance results of all the runs performed.

To execute: compile he code and run it with the following parameters:
```
$ java FairMatching .....
```


The strategy for this algorithm is:
1) Given the input file, build two lists of people(men and women) and populate their preference lists with the provided input values

2) Using the Gale-Shapely algorithm, calculate the man-optimal and woman-optimal matching.

3) Use those matchings to filter out non-feasible matchings that occur at preference lists indices outside the range of the man-optimal and woman-optimal matches. For instance, given the input above, the 'feasible' range of matchings is reduced to this:

man 1 preference list: [3, 1, 5, 4, 2] trimmed list: [5, 4, 2]
man 2 preference list: [2, 4, 3, 1, 5] trimmed list: [3]
man 3 preference list: [4, 5, 3, 1, 2] trimmed list: [4]
man 4 preference list: [2, 5, 2, 1, 3] trimmed list: [2, 5]
man 5 preference list: [3, 1, 2, 5, 4] trimmed list: [1]

woman 1 preference list: [5, 2, 4, 3, 1] trimmed list: [5]
woman 2 preference list: [3, 5, 1, 4, 2] trimmed list: [1, 4]
woman 3 preference list: [2, 3, 5, 4, 1] trimmed list: [2]
woman 4 preference list: [3, 4, 5, 1, 2] trimmed list: [3]
woman 5 preference list: [4, 5, 3, 1, 2] trimmed list: [4, 5, 3, 1]

Note that simply trimming down the list this way doesn't remove all of the infeasible values. For example, woman 5 can never be matched with man 3 or 5 because other women are always matched with those men. We should be able to improve execution times by filtering more values from these trimmed down lists.

4) Recurse over all possible matchings. For each matching:
	i) Determine whether the matching is stable (many matchings found this way are not stable)
	ii) Calculate the score of the matching, maintaining a global copy of the matching that currently has the lowest score

5) Output the optimal matching along with its 'equity score'.


