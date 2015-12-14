package org.aksw.mandolin.model;

import org.aksw.mandolin.inference.ProbKBGibbsSampling;

import com.googlecode.rockit.app.sampler.gibbs.GIBBSLiteral;
import com.googlecode.rockit.javaAPI.HerbrandUniverse;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class PredictionLiteral implements Comparable<PredictionLiteral> {
	
	private static HerbrandUniverse u = HerbrandUniverse.getInstance();
	private String p, x, y;
	private String id;
	private double prob;
	
	public PredictionLiteral(String input, double prob) {
		String[] name = input.split("\\|");
		p = name[0];
		x = u.getConstant(name[1]);
		y = u.getConstant(name[2]);
		id = p + "(" + x + ", " + y + ")";
		this.prob = prob;
	}

	public PredictionLiteral(GIBBSLiteral l) {
		this(l.getName(), l.return_my_probability(ProbKBGibbsSampling.ITERATIONS));
	}

	public String getP() {
		return p;
	}

	public String getX() {
		return x;
	}

	public String getY() {
		return y;
	}
	
	public double getProb() {
		return prob;
	}

	public String toString() {
		return "P[ " + id + " = true ] = " + prob;
	}

	@Override
	public int compareTo(PredictionLiteral o) {
		return this.id.compareTo(o.id);
	}
	
}