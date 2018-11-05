# social_computing_term_proj
Term project for social computing. This branch implements a brute force algorithm to calculate an equitable stable matching. This is defined as the matching with a lowest cumulative 'equity score' for all participants in the matching, where an individual's equity score is the index of the individual's match in their preference list.

To execute: compile the code and run with an 'inputFile' parameter. Ex:
$> java EquitableMatcher input.txt

There is an additional executable, 'InputGenerator.java' that can be used to generate test cases of a given size. Currently this file will just output some randomized preference lists to the console. Just copy this output and paste into a file that you want to use as an input for the EquitableMatcher.
$> java InputGenerator 5
	Sample Output:
5
3 1 5 4 2
2 4 3 1 5
4 5 3 1 2
2 5 4 1 3
3 1 2 5 4
5 2 4 3 1
3 5 1 4 2
2 3 5 4 1
3 4 5 1 2
4 5 3 1 2

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


