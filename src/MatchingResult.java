/**
 * @author Joshua Musick
 *
 */
public class MatchingResult {
	
	long gs_time_ns = 0; // Gale Shapley man and woman optimal compute time
	long heuristic_time_ns = 0; // Time taken to run through the heuristics
	long equitable_prune_ns = 0; // Time taken to prune all infeasible pairs by equitable matcher
	long equitable_time_ns = 0; // Time taken to calculate the equitable matching (using rotations)
	
	int num_people = 0; // Number of men and women in the matching
	
	int man_opt_eq = 0; 	// man optimal solution equitable sum
	int man_pess_eq = 0; 	// man pessimal solution equitable sum
	int man_equit_sum = 0; 	// man equitable sum
	
	int woman_opt_eq = 0; 		// woman optimal solution equitable sum
	int woman_pess_eq = 0; 		// woman pessimal solution equitable sum
	int woman_equit_sum = 0; 	// woman equitable sum
	
	int trimMask = 0;	// trim settings used
	
	public void SetTrimMask(int mask)
	{
		this.trimMask = mask;
	}
	
	String fileName = "";

	public MatchingResult(int num, long gs_time, long heur_time, long eq_prune, long eq_time, String filename)
	{
		this.num_people = num;
		this.gs_time_ns = gs_time;
		this.heuristic_time_ns = heur_time;
		this.equitable_prune_ns = eq_prune;
		this.equitable_time_ns = eq_time;
		this.fileName = filename;
	}
	
	public String GetFilename()
	{
		return fileName;
	}
	
	public void SetManValues(int optEq, int pessEq, int equitable)
	{
		this.man_opt_eq = optEq;
		this.man_pess_eq = pessEq;
		this.man_equit_sum = equitable;
	}
	
	public void SetWomanValues(int optEq, int pessEq, int equitable)
	{
		this.woman_opt_eq = optEq;
		this.woman_pess_eq = pessEq;
		this.woman_equit_sum = equitable;
	}
	
	public void PrintHeader()
	{
		System.out.println("Legend:");
		System.out.println("Num: Number of men / women in matching");
		//System.out.println("Trim-M: Trim Mask - 0: No trimming, 1: Single Feasible, 2: Mutual Feasible, 3: Single and Mutual Feasible");
		System.out.println("M-Opt: Man Optimal Match -- Man Equitable Score (man optimal)");
		System.out.println("W-Pes: Man Optimal Match -- Woman Equitable Score (woman pessimal)");
		System.out.println("T-MO: Man Optimal Match -- Total Equitable Score (man + woman)");
		System.out.println("M-Pes: Woman Optimal Match -- Man Equitable Score (man pessimal)");
		System.out.println("W-Opt: Woman Optimal Match -- Woman Equitable Score (woman optimal)");
		System.out.println("T-WO: Woman Optimal Match -- Total Equitable Score (man + woman)");
		
		System.out.println("M-Equ: Most Fair Match -- Man Equitable Score");
		System.out.println("W-Equ: Most Fair Match -- Woman Equitable Score");
		System.out.println("Tot EQ: Most Fair Match -- Total Equitable Score (man + woman)");
		System.out.println("%OpM: Equitable Percent of Optimal - Man");
		System.out.println("%OpW: Equitable Percent of Optimal - Woman");
		
		
		System.out.println("GS(ms): Time to run Gale-Shapley for both man-optimal and woman-optimal solution in milliseconds");
		System.out.println("Hu(ms): Time to run Heuristics in milliseconds");
		System.out.println("GS(ms): Time to run Equitable Matching Pruning in milliseconds");
		System.out.println("GS(ms): Time to run Equitable Matching in milliseconds");
		System.out.println("-----------------------------------------------------------");
		System.out.println("Num\t|\tM-Opt\tW-Pes\tT-MO\t|\tM-Pes\tW-Opt\tT-WO\t|\tM-Equ\tW-Equ\tTot EQ\t|\t%OpM\t%OptW\t|\tGS(ms)\tHu(ms)\tEP(ms)\tEM(ms)");
	}
	
	public void PrintData()
	{
		int mOptPer = (int)(1000.0 * (double)(man_equit_sum - this.man_opt_eq) / (double)(man_pess_eq - man_opt_eq));
		int wOptPer = (int)(1000.0 * (double)(woman_equit_sum - this.woman_opt_eq) / (double)(woman_pess_eq - woman_opt_eq));
		
		// Prune the numbers reported to only include 4 digits...
		float mOptPerf = (float) mOptPer / 1000.f;
		float wOptPerf = (float) wOptPer / 1000.f;
		
		System.out.println(GetNumPeople() + "\t|\t" + man_opt_eq + "\t" + woman_pess_eq + "\t" + (man_opt_eq + woman_pess_eq) + "\t|\t" +
				man_pess_eq + "\t" + woman_opt_eq + "\t" + (man_pess_eq + woman_opt_eq) + "\t|\t" + 
				man_equit_sum + "\t" + woman_equit_sum + "\t" + (man_equit_sum + woman_equit_sum) + "\t|\t" + 
				mOptPerf + "\t" + wOptPerf + "\t|\t" +
				GetMS(gs_time_ns) + "\t" + GetMS(heuristic_time_ns) + "\t" + GetMS(equitable_prune_ns)  + "\t" + GetMS(equitable_time_ns));
	}
	
	public Integer GetNumPeople()
	{
		return num_people;
	}
	
	private double GetMS(long t_ns) {
		double t = 0;
		
		long t_us = t_ns / 10000; // remove 3 lower significant digits
		t = (double)t_us / 100.0;
		
		return t;
	}
	
	
}
