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

man 1 preference list: [3, 1, 5, 4, 2] trimmed list: [5, 2]
man 2 preference list: [2, 4, 3, 1, 5] trimmed list: [3]
man 3 preference list: [4, 5, 3, 1, 2] trimmed list: [4]
man 4 preference list: [2, 5, 2, 1, 3] trimmed list: [2, 5]
man 5 preference list: [3, 1, 2, 5, 4] trimmed list: [1]

woman 1 preference list: [5, 2, 4, 3, 1] trimmed list: [5]
woman 2 preference list: [3, 5, 1, 4, 2] trimmed list: [1, 4]
woman 3 preference list: [2, 3, 5, 4, 1] trimmed list: [2]
woman 4 preference list: [3, 4, 5, 1, 2] trimmed list: [3]
woman 5 preference list: [4, 5, 3, 1, 2] trimmed list: [4, 1]

4) Starting with the man-optimal match:
	a) Calculate the matching as the match vector for men (ex: [0,2,0,1])
	b) Store this vector in a set of vectors representing stable matchings that have already been visited. If the vector is already present in that set, then return.
	c) Calculate the cumulative score of the match. If the score is the lowest found so far, record the matching as the optimal.
	d) Identify rotations in the data. For each rotation: repeat step #4 on this rotation

5) Output the optimal matching along with its 'equity score'.


