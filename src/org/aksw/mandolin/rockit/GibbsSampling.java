package org.aksw.mandolin.rockit;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.app.sampler.gibbs.GIBBSLiteral;
import com.googlecode.rockit.app.sampler.gibbs.GIBBSSampler;
import com.googlecode.rockit.app.solver.StandardSolver;
import com.googlecode.rockit.app.solver.pojo.Clause;
import com.googlecode.rockit.app.solver.pojo.Literal;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.ReadOrWriteToFileException;
import com.googlecode.rockit.exception.SolveException;
import com.googlecode.rockit.javaAPI.HerbrandUniverse;
import com.googlecode.rockit.javaAPI.Model;
import com.googlecode.rockit.parser.SyntaxReader;

/**
 * Manager for the Gibbs-Sampling inference. Ground rules can be extracted from
 * the Postgre database after being generated by ProbKB (faster) or generated
 * through standard grounding by RockIt (slower).
 * 
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class GibbsSampling {

	/**
	 * MLN file.
	 */
	private String input;

	/**
	 * DB file.
	 */
	private String groundings;

	private static SyntaxReader reader;
	private Model model;

	// Sampling only
	/**
	 * The number of iterations for sampling.
	 */
	private int iterations = 5000000;
	private GIBBSSampler gibbsSampler;

	public static void main(String[] args) {

		// launch test
		try {
			new GibbsSampling("eval/11_publi-mln/prog.mln",
					"eval/11_publi-mln/evidence.db")
					.inferAfterGroundingByRockIt();
		} catch (ParseException | SolveException | SQLException
				| ReadOrWriteToFileException | IOException e) {
			e.printStackTrace();
		}

	}

	public GibbsSampling(String input, String groundings)
			throws ReadOrWriteToFileException, ParseException, IOException {

		this.input = input;
		this.groundings = groundings;

		Parameters.readPropertyFile();
		Parameters.USE_CUTTING_PLANE_AGGREGATION = false;
		Parameters.USE_CUTTING_PLANE_INFERENCE = false;
		reader = new SyntaxReader();

	}

	/**
	 * Call ProbKB for grounding and preprocess its input for Gibbs sampling by
	 * RockIt.
	 * 
	 * @throws ParseException
	 * @throws SolveException
	 * @throws SQLException
	 */
	public void inferAfterGroundingByProbKB() throws ParseException,
			SolveException, SQLException {

		System.out.println("Input: " + this.input);

		// TODO make the following three collections out of ProbKB input
		ArrayList<String> consistentStartingPoints = null;
		ArrayList<Clause> clauses = null;
		Collection<Literal> evidence = null;

		// call Gibbs sampler
		gibbsSampling(consistentStartingPoints, clauses, evidence);

	}

	/**
	 * Call RockIt for both standard grounding and Gibbs-sampling inference.
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws SolveException
	 * @throws SQLException
	 */
	public void inferAfterGroundingByRockIt() throws ParseException,
			SolveException, SQLException, IOException {

		model = reader.getModel(input, groundings);

		// standard grounding...
		System.out.println("Input: " + this.input);
		StandardSolver solver = new StandardSolver(model);
		// ground MLN and retrieve Clauses
		ArrayList<String> consistentStartingPoints = solver.solve();
		ArrayList<Clause> clauses = solver.getAllClauses();
		Collection<Literal> evidence = solver.getEvidenceAxioms();
		solver = null; // free memory

		// call Gibbs sampler
		gibbsSampling(consistentStartingPoints, clauses, evidence);

	}

	/**
	 * Gibbs Sampling by RockIt.
	 * 
	 * @param consistentStartingPoints
	 * @param clauses
	 * @param evidence
	 * @throws SQLException
	 * @throws SolveException
	 * @throws ParseException
	 */
	public void gibbsSampling(ArrayList<String> consistentStartingPoints,
			ArrayList<Clause> clauses, Collection<Literal> evidence)
			throws SQLException, SolveException, ParseException {

		gibbsSampler = new GIBBSSampler();
		ArrayList<GIBBSLiteral> gibbsOutput = gibbsSampler.sample(iterations,
				clauses, evidence, consistentStartingPoints);

		// TODO remove me later
		System.out.println("");
		for (Clause c : clauses) {
			System.out.println(c);
		}

		HerbrandUniverse u = HerbrandUniverse.getInstance();
		for (GIBBSLiteral l : gibbsOutput) {
			String[] name = l.getName().split("\\|");
			String p = name[0];
			String x = u.getConstant(name[1]);
			String y = u.getConstant(name[2]);
			System.out.println(p + "(" + x + ", " + y + ") = "
					+ l.return_my_probability(iterations));
			System.out.println("\tswap? " + l.is_it_possible_to_swap_me());
		}

		// TODO return something evaluable

	}

}
