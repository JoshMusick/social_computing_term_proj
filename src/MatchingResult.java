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
	
	String fileName = "";

	public MatchingResult(int num, long gs_time, long eq_time, String filename)
	{
		this.num_people = num;
		this.gs_time_ns = gs_time;
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
		
	}
	
	public void PrintData()
	{
		
	}
	
	public Integer GetNumPeople()
	{
		return num_people;
	}
}
