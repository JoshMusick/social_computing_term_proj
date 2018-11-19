/**
 * @author Joshua Musick
 *
 */
public class MatchingResult {
	
	long gs_time_ns = 0; // Gale Shapley man and woman optimal compute time
	long heuristic_time_ns = 0; // Time taken to run through the heuristics
	long equitable_time_ns = 0; // Time taken to calculate the equitable matching
	
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

	public MatchingResult(int num, long gs_time, long heur_time, long eq_time, String filename)
	{
		this.num_people = num;
		this.gs_time_ns = gs_time;
		this.heuristic_time_ns = heur_time;
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
		System.out.println("Num\tTrim-M\tM-Opt\tW-Pes\tT-MO\t|M-Pes\tW-Opt\tT-WO\t|M-Equ\tW-Equ\tTot EQ\t|GS-t(ms)\tH-t(ms)\t\tE-t(ms)");
	}
	
	public void PrintData()
	{
		System.out.println(GetNumPeople() + "\t" + trimMask + "\t|" + man_opt_eq + "\t" + woman_pess_eq + "\t" + (man_opt_eq + woman_pess_eq) + "\t|" +
				man_pess_eq + "\t" + woman_opt_eq + "\t" + (man_pess_eq + woman_opt_eq) + "\t|" + 
				man_equit_sum + "\t" + woman_equit_sum + "\t" + (man_equit_sum + woman_equit_sum) + "\t|" + 
				GetMS(gs_time_ns) + "\t\t" + GetMS(heuristic_time_ns) + "\t\t" + GetMS(equitable_time_ns));
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
