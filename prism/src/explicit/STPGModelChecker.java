//==============================================================================
//	
//	Copyright (c) 2002-
//	Authors:
//	* Dave Parker <david.parker@comlab.ox.ac.uk> (University of Oxford)
//	* Vojtech Forejt <vojtech.forejt@cs.ox.ac.uk> (University of Oxford)
//	
//------------------------------------------------------------------------------
//	
//	This file is part of PRISM.
//	
//	PRISM is free software; you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation; either version 2 of the License, or
//	(at your option) any later version.
//	
//	PRISM is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//	
//	You should have received a copy of the GNU General Public License
//	along with PRISM; if not, write to the Free Software Foundation,
//	Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//	
//==============================================================================

package explicit;

import java.util.*;
import java.util.Map.Entry;

import parser.ast.Expression;
import parser.ast.ExpressionTemporal;
import prism.*;
import explicit.rewards.MDPRewards;
import explicit.rewards.MDPRewardsSimple;
import explicit.rewards.STPGRewards;
import explicit.rewards.STPGRewardsSimple;

/**
 * Explicit-state model checker for two-player stochastic games (STPGs).
 */
public class STPGModelChecker extends ProbModelChecker
{
	// Model checking functions

	/**
	 * Compute probabilities for the contents of a P operator.
	 */
	protected StateValues checkProbPathFormula(Model model, Expression expr, boolean min1, boolean min2) throws PrismException
	{
		// Test whether this is a simple path formula (i.e. PCTL)
		// and then pass control to appropriate method. 
		if (expr.isSimplePathFormula()) {
			return checkProbPathFormulaSimple(model, expr, min1, min2);
		} else {
			throw new PrismException("Explicit engine does not yet handle LTL-style path formulas");
		}
	}

	/**
	 * Compute probabilities for a simple, non-LTL path operator.
	 */
	protected StateValues checkProbPathFormulaSimple(Model model, Expression expr, boolean min1, boolean min2) throws PrismException
	{
		StateValues probs = null;

		// Temporal operators
		if (expr instanceof ExpressionTemporal) {
			ExpressionTemporal exprTemp = (ExpressionTemporal) expr;
			
			// Next
			if(exprTemp.getOperator() == ExpressionTemporal.P_X)
			{
				probs = checkProbNext(model, exprTemp, min1, min2);
			}
			
			// Until
			else if (exprTemp.getOperator() == ExpressionTemporal.P_U) {
				if (exprTemp.hasBounds()) {
					probs = checkProbBoundedUntil(model, exprTemp, min1, min2);
				} else {
					probs = checkProbUntil(model, exprTemp, min1, min2);
				}
			}
			// Anything else - convert to until and recurse
			else {
				probs = checkProbPathFormulaSimple(model, exprTemp.convertToUntilForm(), min1, min2);
			}
		}

		if (probs == null)
			throw new PrismException("Unrecognised path operator in P operator");

		return probs;
	}

	private StateValues checkProbNext(Model model, ExpressionTemporal expr,
			boolean min1, boolean min2) throws PrismException {

		BitSet b;
		int n;
		double soln[], soln2[];
		STPG stpg;

		
		stpg = (STPG) model;
		
		// model check the operand
		b = checkExpression(model, expr.getOperand2()).getBitSet();
		
		// Store num states
		n = model.getNumStates();
		
		// Create solution vector(s)
		soln = new double[n];
		soln2 = new double[n];
		
		for(int i=0; i<n; i++)
			soln[i] = b.get(i)?1.0:0.0;
		
		stpg.mvMultMinMax(soln, min1, min2, soln2, null, false, null);
		
		// Return results
		return StateValues.createFromDoubleArray(soln2, model);
	}

	/**
	 * Compute probabilities for a bounded until operator.
	 */
	protected StateValues checkProbBoundedUntil(Model model, ExpressionTemporal expr, boolean min1, boolean min2) throws PrismException
	{
		int time;
		BitSet b1, b2;
		StateValues probs = null;
		ModelCheckerResult res = null;

		// get info from bounded until
		time = expr.getUpperBound().evaluateInt(constantValues);
		if (expr.upperBoundIsStrict())
			time--;
		if (time < 0) {
			String bound = expr.upperBoundIsStrict() ? "<" + (time + 1) : "<=" + time;
			throw new PrismException("Invalid bound " + bound + " in bounded until formula");
		}

		// model check operands first
		b1 = checkExpression(model, expr.getOperand1()).getBitSet();
		b2 = checkExpression(model, expr.getOperand2()).getBitSet();

		// print out some info about num states
		// mainLog.print("\nb1 = " + JDD.GetNumMintermsString(b1,
		// allDDRowVars.n()));
		// mainLog.print(" states, b2 = " + JDD.GetNumMintermsString(b2,
		// allDDRowVars.n()) + " states\n");

		// Compute probabilities

		// a trivial case: "U<=0"
		if (time == 0) {
			// prob is 1 in b2 states, 0 otherwise
			probs = StateValues.createFromBitSetAsDoubles(b2, model);
		} else {
			res = computeBoundedUntilProbs((STPG) model, b1, b2, time, min1, min2);
			probs = StateValues.createFromDoubleArray(res.soln, model);
		}

		return probs;
	}

	/**
	 * Compute probabilities for an (unbounded) until operator.
	 */
	protected StateValues checkProbUntil(Model model, ExpressionTemporal expr, boolean min1, boolean min2) throws PrismException
	{
		BitSet b1, b2;
		StateValues probs = null;
		ModelCheckerResult res = null;

		// model check operands first
		b1 = checkExpression(model, expr.getOperand1()).getBitSet();
		b2 = checkExpression(model, expr.getOperand2()).getBitSet();

		// print out some info about num states
		// mainLog.print("\nb1 = " + JDD.GetNumMintermsString(b1,
		// allDDRowVars.n()));
		// mainLog.print(" states, b2 = " + JDD.GetNumMintermsString(b2,
		// allDDRowVars.n()) + " states\n");

		res = computeUntilProbs((STPG) model, b1, b2, min1, min2);
		probs = StateValues.createFromDoubleArray(res.soln, model);

		return probs;
	}

	/**
	 * Compute rewards for the contents of an R operator.
	 */
	protected StateValues checkRewardFormula(Model model, STPGRewards modelRewards, Expression expr, boolean min1, boolean min2) throws PrismException
	{
		StateValues rewards = null;
		
		if (expr instanceof ExpressionTemporal) {
			ExpressionTemporal exprTemp = (ExpressionTemporal) expr;
			switch (exprTemp.getOperator()) {
			case ExpressionTemporal.R_F:
				rewards = checkRewardReach(model, modelRewards, exprTemp, min1, min2);
				break;
			default:
				throw new PrismException("Explicit engine does not yet handle the " + exprTemp.getOperatorSymbol() + " operator in the R operator");
			}
		}
		
		if (rewards == null)
			throw new PrismException("Unrecognised operator in R operator");

		return rewards;
	}

	/**
	 * Compute rewards for a reachability reward operator.
	 */
	protected StateValues checkRewardReach(Model model, STPGRewards modelRewards, ExpressionTemporal expr, boolean min1, boolean min2) throws PrismException
	{
		BitSet b;
		StateValues rewards = null;
		ModelCheckerResult res = null;

		// model check operand first
		b = checkExpression(model, expr.getOperand2()).getBitSet();

		// print out some info about num states
		// mainLog.print("\nb = " + JDD.GetNumMintermsString(b1,
		// allDDRowVars.n()));

		res = computeReachRewards((STPG) model, modelRewards, b, min1, min2);
		rewards = StateValues.createFromDoubleArray(res.soln, model);

		return rewards;
	}

	// Numerical computation functions

	/**
	 * Compute reachability probabilities.
	 * i.e. compute the min/max probability of reaching a state in {@code target}.
	 * @param stpg The STPG
	 * @param target Target states
	 * @param min1 Min or max probabilities for player 1 (true=lower bound, false=upper bound)
	 * @param min2 Min or max probabilities for player 2 (true=min, false=max)
	 */
	public ModelCheckerResult computeReachProbs(STPG stpg, BitSet target, boolean min1, boolean min2) throws PrismException
	{
		return computeReachProbs(stpg, null, target, min1, min2, null, null);
	}

	/**
	 * Compute until probabilities.
	 * i.e. compute the min/max probability of reaching a state in {@code target},
	 * while remaining in those in @{code remain}.
	 * @param stpg The STPG
	 * @param remain Remain in these states (optional: null means "all")
	 * @param target Target states
	 * @param min1 Min or max probabilities for player 1 (true=lower bound, false=upper bound)
	 * @param min2 Min or max probabilities for player 2 (true=min, false=max)
	 */
	public ModelCheckerResult computeUntilProbs(STPG stpg, BitSet remain, BitSet target, boolean min1, boolean min2) throws PrismException
	{
		return computeReachProbs(stpg, remain, target, min1, min2, null, null);
	}

	/**
	 * Compute reachability/until probabilities.
	 * i.e. compute the min/max probability of reaching a state in {@code target},
	 * while remaining in those in @{code remain}.
	 * @param stpg The STPG
	 * @param remain Remain in these states (optional: null means "all")
	 * @param target Target states
	 * @param min1 Min or max probabilities for player 1 (true=lower bound, false=upper bound)
	 * @param min2 Min or max probabilities for player 2 (true=min, false=max)
	 * @param init Optionally, an initial solution vector (may be overwritten) 
	 * @param known Optionally, a set of states for which the exact answer is known
	 * Note: if 'known' is specified (i.e. is non-null, 'init' must also be given and is used for the exact values.  
	 */
	public ModelCheckerResult computeReachProbs(STPG stpg, BitSet remain, BitSet target, boolean min1, boolean min2, double init[], BitSet known)
			throws PrismException
	{
		ModelCheckerResult res = null;
		BitSet no, yes;
		int i, n, numYes, numNo;
		long timer, timerProb0, timerProb1;
		boolean genAdv;

		// Check for some unsupported combinations
		if (solnMethod == SolnMethod.VALUE_ITERATION && valIterDir == ValIterDir.ABOVE && !(precomp && prob0)) {
			throw new PrismException("Precomputation (Prob0) must be enabled for value iteration from above");
		}

		// Are we generating an optimal adversary?
		genAdv = exportAdv;

		// Start probabilistic reachability
		timer = System.currentTimeMillis();
		if (verbosity >= 1)
			mainLog.println("Starting probabilistic reachability...");

		// Check for deadlocks in non-target state (because breaks e.g. prob1)
		stpg.checkForDeadlocks(target);

		// Store num states
		n = stpg.getNumStates();

		// Optimise by enlarging target set (if more info is available)
		if (init != null && known != null) {
			BitSet targetNew = new BitSet(n);
			for (i = 0; i < n; i++) {
				targetNew.set(i, target.get(i) || (known.get(i) && init[i] == 1.0));
			}
			target = targetNew;
		}

		// Precomputation
		timerProb0 = System.currentTimeMillis();
		if (precomp && prob0) {
			no = prob0(stpg, remain, target, min1, min2);
		} else {
			no = new BitSet();
		}
		timerProb0 = System.currentTimeMillis() - timerProb0;
		timerProb1 = System.currentTimeMillis();
		if (precomp && prob1 && !genAdv) {
			yes = prob1(stpg, remain, target, min1, min2);
		} else {
			yes = (BitSet) target.clone();
		}
		timerProb1 = System.currentTimeMillis() - timerProb1;

		// Print results of precomputation
		numYes = yes.cardinality();
		numNo = no.cardinality();
		if (verbosity >= 1)
			mainLog.println("target=" + target.cardinality() + ", yes=" + numYes + ", no=" + numNo + ", maybe=" + (n - (numYes + numNo)));

		// Compute probabilities
		switch (solnMethod) {
		case VALUE_ITERATION:
			res = computeReachProbsValIter(stpg, no, yes, min1, min2, init, known);
			break;
		case GAUSS_SEIDEL:
			res = computeReachProbsGaussSeidel(stpg, no, yes, min1, min2, init, known);
			break;
		default:
			throw new PrismException("Unknown STPG solution method " + solnMethod);
		}

		// Finished probabilistic reachability
		timer = System.currentTimeMillis() - timer;
		if (verbosity >= 1)
			mainLog.println("Probabilistic reachability took " + timer / 1000.0 + " seconds.");

		// Update time taken
		res.timeTaken = timer / 1000.0;
		res.timeProb0 = timerProb0 / 1000.0;
		res.timePre = (timerProb0 + timerProb1) / 1000.0;

		return res;
	}

	/**
	 * Prob0 precomputation algorithm.
	 * i.e. determine the states of an STPG which, with min/max probability 0,
	 * reach a state in {@code target}, while remaining in those in @{code remain}.
	 * {@code min}=true gives Prob0E, {@code min}=false gives Prob0A. 
	 * @param stpg The STPG
	 * @param remain Remain in these states (optional: null means "all")
	 * @param target Target states
	 * @param min1 Min or max probabilities for player 1 (true=lower bound, false=upper bound)
	 * @param min2 Min or max probabilities for player 2 (true=min, false=max)
	 */
	public BitSet prob0(STPG stpg, BitSet remain, BitSet target, boolean min1, boolean min2)
	{
		int n, iters;
		BitSet u, soln, unknown;
		boolean u_done;
		long timer;

		// Start precomputation
		timer = System.currentTimeMillis();
		if (verbosity >= 1)
			mainLog.println("Starting Prob0 (" + (min1 ? "min" : "max") + (min2 ? "min" : "max") + ")...");

		// Special case: no target states
		if (target.cardinality() == 0) {
			soln = new BitSet(stpg.getNumStates());
			soln.set(0, stpg.getNumStates());
			return soln;
		}

		// Initialise vectors
		n = stpg.getNumStates();
		u = new BitSet(n);
		soln = new BitSet(n);

		// Determine set of states actually need to perform computation for
		unknown = new BitSet();
		unknown.set(0, n);
		unknown.andNot(target);
		if (remain != null)
			unknown.and(remain);

		// Fixed point loop
		iters = 0;
		u_done = false;
		// Least fixed point - should start from 0 but we optimise by
		// starting from 'target', thus bypassing first iteration
		u.or(target);
		soln.or(target);
		while (!u_done) {
			iters++;
			// Single step of Prob0
			stpg.prob0step(unknown, u, min1, min2, soln);
			// Check termination
			u_done = soln.equals(u);
			// u = soln
			u.clear();
			u.or(soln);
		}

		// Negate
		u.flip(0, n);

		// Finished precomputation
		timer = System.currentTimeMillis() - timer;
		if (verbosity >= 1) {
			mainLog.print("Prob0 (" + (min1 ? "min" : "max") + (min2 ? "min" : "max") + ")");
			mainLog.println(" took " + iters + " iterations and " + timer / 1000.0 + " seconds.");
		}

		return u;
	}

	/**
	 * Prob1 precomputation algorithm.
	 * i.e. determine the states of an STPG which, with min/max probability 1,
	 * reach a state in {@code target}, while remaining in those in @{code remain}.
	 * @param stpg The STPG
	 * @param remain Remain in these states (optional: null means "all")
	 * @param target Target states
	 * @param min1 Min or max probabilities for player 1 (true=lower bound, false=upper bound)
	 * @param min2 Min or max probabilities for player 2 (true=min, false=max)
	 */
	public BitSet prob1(STPG stpg, BitSet remain, BitSet target, boolean min1, boolean min2)
	{
		int n, iters;
		BitSet u, v, soln, unknown;
		boolean u_done, v_done;
		long timer;

		// Start precomputation
		timer = System.currentTimeMillis();
		if (verbosity >= 1)
			mainLog.println("Starting Prob1 (" + (min1 ? "min" : "max") + (min2 ? "min" : "max") + ")...");

		// Special case: no target states
		if (target.cardinality() == 0) {
			return new BitSet(stpg.getNumStates());
		}

		// Initialise vectors
		n = stpg.getNumStates();
		u = new BitSet(n);
		v = new BitSet(n);
		soln = new BitSet(n);

		// Determine set of states actually need to perform computation for
		unknown = new BitSet();
		unknown.set(0, n);
		unknown.andNot(target);
		if (remain != null)
			unknown.and(remain);

		// Nested fixed point loop
		iters = 0;
		u_done = false;
		// Greatest fixed point
		u.set(0, n);
		while (!u_done) {
			v_done = false;
			// Least fixed point - should start from 0 but we optimise by
			// starting from 'target', thus bypassing first iteration
			v.clear();
			v.or(target);
			soln.clear();
			soln.or(target);
			while (!v_done) {
				iters++;
				// Single step of Prob1
				stpg.prob1step(unknown, u, v, min1, min2, soln);
				// Check termination (inner)
				v_done = soln.equals(v);
				// v = soln
				v.clear();
				v.or(soln);
			}
			// Check termination (outer)
			u_done = v.equals(u);
			// u = v
			u.clear();
			u.or(v);
		}

		// Finished precomputation
		timer = System.currentTimeMillis() - timer;
		if (verbosity >= 1) {
			mainLog.print("Prob1 (" + (min1 ? "min" : "max") + (min2 ? "min" : "max") + ")");
			mainLog.println(" took " + iters + " iterations and " + timer / 1000.0 + " seconds.");
		}

		return u;
	}
	
	/**
	 * Zero cummulative reward precomputation algorithm.
	 * i.e. determine the states of an STPG which, with with probability 1 get min/max reward equal to 0.0 
	 * before (possibly) reaching a state in {@code target}, while remaining in those in @{code remain}.
	 * @param stpg The STPG
	 * @param remain Remain in these states (optional: null means "all")
	 * @param target Target states
	 * @param min1 Min or max probabilities for player 1 (true=lower bound, false=upper bound)
	 * @param min2 Min or max probabilities for player 2 (true=min, false=max)
	 */
	public BitSet zeroRewards(STPG stpg, STPGRewards rewards, BitSet remain, BitSet target, boolean min1, boolean min2)
	{
		int n, iters;
		double[] soln1, soln2;
		BitSet unknown;
		boolean done;
		long timer;

		// Start precomputation
		timer = System.currentTimeMillis();
		if (verbosity >= 1)
			mainLog.println("Starting zeroRewards (" + (min1 ? "min" : "max") + (min2 ? "min" : "max") + ")...");


		// Initialise vectors
		n = stpg.getNumStates();
		soln1 = new double[n];
		soln2 = new double[n];

		// Determine set of states actually need to perform computation for
		unknown = new BitSet();
		unknown.set(0, n);
		if(target != null)
			unknown.andNot(target);
		if (remain != null)
			unknown.and(remain);
		
		//initialise the solution so that the forbidden states are penalised
		for(int i = 0; i < n; i++) {
			if (remain != null && !remain.get(i) && target != null && !target.get(i))
				soln1[i] = Double.POSITIVE_INFINITY;
		}
		

		// Nested fixed point loop
		iters = 0;
		done = false;
		while (!done) {
			iters++;
			//at every iter at least one state must go from zero to nonzero, hence we have
			//at most n iterations
			assert iters <= n+1;
			
			stpg.mvMultRewMinMax(soln1, rewards, min1, min2, soln2, unknown, false, null);
			
			// Check termination (outer)
			done = true;
			
			double[] tmp = soln2;
			soln2 = soln1;
			soln1 = tmp;
			
			for(int i = 0; i < n; i++) {
				if (soln1[i] > 0.0 && soln2[i] == 0.0) {
					done = true;
					break;
				}
			}
		}

		// Finished precomputation
		timer = System.currentTimeMillis() - timer;
		if (verbosity >= 1) {
			mainLog.print("Prob1 (" + (min1 ? "min" : "max") + (min2 ? "min" : "max") + ")");
			mainLog.println(" took " + iters + " iterations and " + timer / 1000.0 + " seconds.");
		}

		BitSet result = new BitSet(n);
		for(int i = 0; i < n; i++) {
			if (soln1[i] == 0.0)
				result.set(i);
		}
		
		return result;
	}

	/**
	 * Compute reachability probabilities using value iteration.
	 * @param stpg The STPG
	 * @param no Probability 0 states
	 * @param yes Probability 1 states
	 * @param min1 Min or max probabilities for player 1 (true=lower bound, false=upper bound)
	 * @param min2 Min or max probabilities for player 2 (true=min, false=max)
	 * @param init Optionally, an initial solution vector (will be overwritten) 
	 * @param known Optionally, a set of states for which the exact answer is known
	 * Note: if 'known' is specified (i.e. is non-null, 'init' must also be given and is used for the exact values.  
	 */
	protected ModelCheckerResult computeReachProbsValIter(STPG stpg, BitSet no, BitSet yes, boolean min1, boolean min2, double init[], BitSet known)
			throws PrismException
	{
		ModelCheckerResult res = null;
		BitSet unknown;
		int i, n, iters;
		double soln[], soln2[], tmpsoln[], initVal;
		int adv[] = null;
		boolean genAdv, done;
		long timer;

		// Are we generating an optimal adversary?
		genAdv = exportAdv;

		// Start value iteration
		timer = System.currentTimeMillis();
		if (verbosity >= 1)
			mainLog.println("Starting value iteration (" + (min1 ? "min" : "max") + (min2 ? "min" : "max") + ")...");

		// Store num states
		n = stpg.getNumStates();

		// Create solution vector(s)
		soln = new double[n];
		soln2 = (init == null) ? new double[n] : init;

		// Initialise solution vectors. Use (where available) the following in order of preference:
		// (1) exact answer, if already known; (2) 1.0/0.0 if in yes/no; (3) passed in initial value; (4) initVal
		// where initVal is 0.0 or 1.0, depending on whether we converge from below/above. 
		initVal = (valIterDir == ValIterDir.BELOW) ? 0.0 : 1.0;
		if (init != null) {
			if (known != null) {
				for (i = 0; i < n; i++)
					soln[i] = soln2[i] = known.get(i) ? init[i] : yes.get(i) ? 1.0 : no.get(i) ? 0.0 : init[i];
			} else {
				for (i = 0; i < n; i++)
					soln[i] = soln2[i] = yes.get(i) ? 1.0 : no.get(i) ? 0.0 : init[i];
			}
		} else {
			for (i = 0; i < n; i++)
				soln[i] = soln2[i] = yes.get(i) ? 1.0 : no.get(i) ? 0.0 : initVal;
		}

		// Determine set of states actually need to compute values for
		unknown = new BitSet();
		unknown.set(0, n);
		unknown.andNot(yes);
		unknown.andNot(no);
		if (known != null)
			unknown.andNot(known);

		// Create/initialise adversary storage
		if (genAdv) {
			adv = new int[n];
			for (i = 0; i < n; i++) {
				adv[i] = -1;
			}
		}

		// Start iterations
		iters = 0;
		done = false;
		while (!done && iters < maxIters) {
			iters++;
			// Matrix-vector multiply and min/max ops
			stpg.mvMultMinMax(soln, min1, min2, soln2, unknown, false, genAdv ? adv : null);
			// Check termination
			done = PrismUtils.doublesAreClose(soln, soln2, termCritParam, termCrit == TermCrit.ABSOLUTE);
			// Swap vectors for next iter
			tmpsoln = soln;
			soln = soln2;
			soln2 = tmpsoln;
		}

		// Finished value iteration
		timer = System.currentTimeMillis() - timer;
		if (verbosity >= 1) {
			mainLog.print("Value iteration (" + (min1 ? "min" : "max") + (min2 ? "min" : "max") + ")");
			mainLog.println(" took " + iters + " iterations and " + timer / 1000.0 + " seconds.");
		}

		// Print adversary
		if (genAdv) {
			PrismLog out = new PrismFileLog(exportAdvFilename);
			for (i = 0; i < n; i++) {
				out.println(i + " " + (adv[i] != -1 ? stpg.getAction(i, adv[i]) : "-"));
			}
			out.println();
		}

		// Return results
		res = new ModelCheckerResult();
		res.soln = soln;
		res.numIters = iters;
		res.timeTaken = timer / 1000.0;
		return res;
	}

	/**
	 * Compute reachability probabilities using Gauss-Seidel.
	 * @param stpg The STPG
	 * @param no Probability 0 states
	 * @param yes Probability 1 states
	 * @param min1 Min or max probabilities for player 1 (true=lower bound, false=upper bound)
	 * @param min2 Min or max probabilities for player 2 (true=min, false=max)
	 * @param init Optionally, an initial solution vector (will be overwritten) 
	 * @param known Optionally, a set of states for which the exact answer is known
	 * Note: if 'known' is specified (i.e. is non-null, 'init' must also be given and is used for the exact values.  
	 */
	protected ModelCheckerResult computeReachProbsGaussSeidel(STPG stpg, BitSet no, BitSet yes, boolean min1, boolean min2, double init[], BitSet known)
			throws PrismException
	{
		ModelCheckerResult res;
		BitSet unknown;
		int i, n, iters;
		double soln[], initVal, maxDiff;
		boolean done;
		long timer;

		// Start value iteration
		timer = System.currentTimeMillis();
		if (verbosity >= 1)
			mainLog.println("Starting value iteration (" + (min1 ? "min" : "max") + (min2 ? "min" : "max") + ")...");

		// Store num states
		n = stpg.getNumStates();

		// Create solution vector
		soln = (init == null) ? new double[n] : init;

		// Initialise solution vector. Use (where available) the following in order of preference:
		// (1) exact answer, if already known; (2) 1.0/0.0 if in yes/no; (3) passed in initial value; (4) initVal
		// where initVal is 0.0 or 1.0, depending on whether we converge from below/above. 
		initVal = (valIterDir == ValIterDir.BELOW) ? 0.0 : 1.0;
		if (init != null) {
			if (known != null) {
				for (i = 0; i < n; i++)
					soln[i] = known.get(i) ? init[i] : yes.get(i) ? 1.0 : no.get(i) ? 0.0 : init[i];
			} else {
				for (i = 0; i < n; i++)
					soln[i] = yes.get(i) ? 1.0 : no.get(i) ? 0.0 : init[i];
			}
		} else {
			for (i = 0; i < n; i++)
				soln[i] = yes.get(i) ? 1.0 : no.get(i) ? 0.0 : initVal;
		}

		// Determine set of states actually need to compute values for
		unknown = new BitSet();
		unknown.set(0, n);
		unknown.andNot(yes);
		unknown.andNot(no);
		if (known != null)
			unknown.andNot(known);

		// Start iterations
		iters = 0;
		done = false;
		while (!done && iters < maxIters) {
			iters++;
			// Matrix-vector multiply and min/max ops
			maxDiff = stpg.mvMultGSMinMax(soln, min1, min2, unknown, false, termCrit == TermCrit.ABSOLUTE);
			// Check termination
			done = maxDiff < termCritParam;
		}

		// Finished Gauss-Seidel
		timer = System.currentTimeMillis() - timer;
		if (verbosity >= 1) {
			mainLog.print("Value iteration (" + (min1 ? "min" : "max") + (min2 ? "min" : "max") + ")");
			mainLog.println(" took " + iters + " iterations and " + timer / 1000.0 + " seconds.");
		}

		// Return results
		res = new ModelCheckerResult();
		res.soln = soln;
		res.numIters = iters;
		res.timeTaken = timer / 1000.0;
		return res;
	}

	/**
	 * Construct strategy information for min/max reachability probabilities.
	 * (More precisely, list of indices of player 1 choices resulting in min/max.)
	 * (Note: indices are guaranteed to be sorted in ascending order.)
	 * @param stpg The STPG
	 * @param state The state to generate strategy info for
	 * @param target The set of target states to reach
	 * @param min1 Min or max probabilities for player 1 (true=min, false=max)
	 * @param min2 Min or max probabilities for player 2 (true=min, false=max)
	 * @param lastSoln Vector of probabilities from which to recompute in one iteration 
	 */
	public List<Integer> probReachStrategy(STPG stpg, int state, BitSet target, boolean min1, boolean min2, double lastSoln[]) throws PrismException
	{
		double val = stpg.mvMultMinMaxSingle(state, lastSoln, min1, min2);
		return stpg.mvMultMinMaxSingleChoices(state, lastSoln, min1, min2, val);
	}

	/**
	 * Compute bounded reachability probabilities.
	 * i.e. compute the min/max probability of reaching a state in {@code target} within k steps.
	 * @param stpg The STPG
	 * @param target Target states
	 * @param k Bound
	 * @param min1 Min or max probabilities for player 1 (true=lower bound, false=upper bound)
	 * @param min2 Min or max probabilities for player 2 (true=min, false=max)
	 */
	public ModelCheckerResult computeBoundedReachProbs(STPG stpg, BitSet target, int k, boolean min1, boolean min2) throws PrismException
	{
		return computeBoundedReachProbs(stpg, null, target, k, min1, min2, null, null);
	}

	/**
	 * Compute bounded until probabilities.
	 * i.e. compute the min/max probability of reaching a state in {@code target},
	 * within k steps, and while remaining in states in @{code remain}.
	 * @param stpg The STPG
	 * @param remain Remain in these states (optional: null means "all")
	 * @param target Target states
	 * @param k Bound
	 * @param min1 Min or max probabilities for player 1 (true=lower bound, false=upper bound)
	 * @param min2 Min or max probabilities for player 2 (true=min, false=max)
	 */
	public ModelCheckerResult computeBoundedUntilProbs(STPG stpg, BitSet remain, BitSet target, int k, boolean min1, boolean min2) throws PrismException
	{
		return computeBoundedReachProbs(stpg, remain, target, k, min1, min2, null, null);
	}

	/**
	 * Compute bounded reachability/until probabilities.
	 * i.e. compute the min/max probability of reaching a state in {@code target},
	 * within k steps, and while remaining in states in @{code remain}.
	 * @param stpg The STPG
	 * @param remain Remain in these states (optional: null means "all")
	 * @param target Target states
	 * @param k Bound
	 * @param min1 Min or max probabilities for player 1 (true=lower bound, false=upper bound)
	 * @param min2 Min or max probabilities for player 2 (true=min, false=max)
	 * @param init Initial solution vector - pass null for default
	 * @param results Optional array of size k+1 to store (init state) results for each step (null if unused)
	 */
	public ModelCheckerResult computeBoundedReachProbs(STPG stpg, BitSet remain, BitSet target, int k, boolean min1, boolean min2, double init[],
			double results[]) throws PrismException
	{
		// TODO: implement until

		ModelCheckerResult res = null;
		int i, n, iters;
		double soln[], soln2[], tmpsoln[];
		long timer;

		// Start bounded probabilistic reachability
		timer = System.currentTimeMillis();
		if (verbosity >= 1)
			mainLog.println("Starting bounded probabilistic reachability...");

		// Store num states
		n = stpg.getNumStates();

		// Create solution vector(s)
		soln = new double[n];
		soln2 = (init == null) ? new double[n] : init;

		// Initialise solution vectors. Use passed in initial vector, if present
		if (init != null) {
			for (i = 0; i < n; i++)
				soln[i] = soln2[i] = target.get(i) ? 1.0 : init[i];
		} else {
			for (i = 0; i < n; i++)
				soln[i] = soln2[i] = target.get(i) ? 1.0 : 0.0;
		}
		// Store intermediate results if required
		// (compute min/max value over initial states for first step)
		if (results != null) {
			results[0] = Utils.minMaxOverArraySubset(soln2, stpg.getInitialStates(), min2);
		}

		// Start iterations
		iters = 0;
		while (iters < k) {
			iters++;
			// Matrix-vector multiply and min/max ops
			stpg.mvMultMinMax(soln, min1, min2, soln2, target, true, null);
			// Store intermediate results if required
			// (compute min/max value over initial states for this step)
			if (results != null) {
				results[iters] = Utils.minMaxOverArraySubset(soln2, stpg.getInitialStates(), min2);
			}
			// Swap vectors for next iter
			tmpsoln = soln;
			soln = soln2;
			soln2 = tmpsoln;
		}

		// Print vector (for debugging)
		//mainLog.println(soln);

		// Finished bounded probabilistic reachability
		timer = System.currentTimeMillis() - timer;
		if (verbosity >= 1) {
			mainLog.print("Bounded probabilistic reachability (" + (min1 ? "min" : "max") + (min2 ? "min" : "max") + ")");
			mainLog.println(" took " + iters + " iterations and " + timer / 1000.0 + " seconds.");
		}

		// Return results
		res = new ModelCheckerResult();
		res.soln = soln;
		res.lastSoln = soln2;
		res.numIters = iters;
		res.timeTaken = timer / 1000.0;
		res.timePre = 0.0;
		return res;
	}

	/**
	 * Compute expected reachability rewards.
	 * @param stpg The STPG
	 * @param rewards The rewards
	 * @param target Target states
	 * @param min1 Min or max rewards for player 1 (true=min, false=max)
	 * @param min2 Min or max rewards for player 2 (true=min, false=max)
	 */
	public ModelCheckerResult computeReachRewards(STPG stpg, STPGRewards rewards, BitSet target, boolean min1, boolean min2) throws PrismException
	{
		return computeReachRewards(stpg, rewards, target, min1, min2, null, null);
	}

	/**
	 * Compute expected reachability rewards.
	 * i.e. compute the min/max reward accumulated to reach a state in {@code target}.
	 * @param stpg The STPG
	 * @param rewards The rewards
	 * @param target Target states
	 * @param min1 Min or max rewards for player 1 (true=min, false=max)
	 * @param min2 Min or max rewards for player 2 (true=min, false=max)
	 * @param init Optionally, an initial solution vector (may be overwritten) 
	 * @param known Optionally, a set of states for which the exact answer is known
	 * Note: if 'known' is specified (i.e. is non-null, 'init' must also be given and is used for the exact values.  
	 */
	public ModelCheckerResult computeReachRewards(STPG stpg, STPGRewards rewards, BitSet target, boolean min1, boolean min2, double init[], BitSet known) throws PrismException
	{
		boolean unreachingAsInfinity = true;
		
		ModelCheckerResult res = null;
		BitSet inf;
		int i, n, numTarget, numInf;
		long timer, timerProb1, timerApprox;

		// Start expected reachability
		timer = System.currentTimeMillis();
		if (verbosity >= 1)
			mainLog.println("Starting expected reachability...");

		// Check for deadlocks in non-target state (because breaks e.g. prob1)
		stpg.checkForDeadlocks(target);

		// Store num states
		n = stpg.getNumStates();

		// Optimise by enlarging target set (if more info is available)
		if (init != null && known != null) {
			BitSet targetNew = new BitSet(n);
			for (i = 0; i < n; i++) {
				targetNew.set(i, target.get(i) || (known.get(i) && init[i] == 0.0));
			}
			target = targetNew;
		}
		
		//If staying in zero component forever while not reaching a final state is
		//allowed, add such states to target
		if(!unreachingAsInfinity) {
			//note: by definition the returned bitset contains all target states.
			target = zeroRewards(stpg, rewards, null, target, min1, min2);
		}

		// Precomputation (not optional)
		timerProb1 = System.currentTimeMillis();
		inf = prob1(stpg, null, target, !min1, !min2);
		inf.flip(0, n);
		timerProb1 = System.currentTimeMillis() - timerProb1;

		// Print results of precomputation
		numTarget = target.cardinality();
		numInf = inf.cardinality();
		if (verbosity >= 1)
			mainLog.println("target=" + numTarget + ", inf=" + numInf + ", rest=" + (n - (numTarget + numInf)));
		
		//Compute rewards with epsilon instead of zero. This is used to get the over-approximation
		//of the real result, which deals with the problem of staying in zero components for free
		//when infinity should be gained.
		if (unreachingAsInfinity) {
			//first, get the minimum nonzero reward and maximal reward, will be used as a basis for epsilon
			//also, check if by any chance all rewards are nonzero, then we don't need to precompute
			double minimumReward = Double.POSITIVE_INFINITY;
			double maximumReward = 0.0;
			boolean allNonzero = true;
			double r;
			for (i = 0; i < n; i++) {
				r = rewards.getStateReward(i);
				if (r > 0.0 && r < minimumReward)
					minimumReward = r;
				if (r > maximumReward)
					maximumReward = r;
				allNonzero = allNonzero && r > 0;
				
				for (int j = 0; j < stpg.getNumChoices(i); j++) {
					r = rewards.getTransitionReward(i,j);
					if (r > 0.0  && r < minimumReward)
						minimumReward = r;
					if (r > maximumReward)
						maximumReward = r;
					allNonzero = allNonzero && rewards.getTransitionReward(i,j) > 0;
					
					for (int k= 0; k < stpg.getNumNestedChoices(i, j); k++) {
						r = rewards.getNestedTransitionReward(i, j, k);
						if (r > 0.0 && r < minimumReward)
							minimumReward = r;
						if (r > maximumReward)
							maximumReward = r;
						allNonzero = allNonzero && r > 0;
					}
				}
			}
			
			if (!allNonzero) {
				timerApprox = System.currentTimeMillis();
				//A simple heuristic that gives small epsilon, but still is hopefully safe floating-point-wise
				double epsilon = Math.min(minimumReward, maximumReward * 0.01);;
				
				if (verbosity >= 1) {
					mainLog.println("Computing the upper bound where " + epsilon + " is used instead of 0.0");
				}
				
				//Modify the rewards
				double origZeroReplacement;
				if (rewards instanceof MDPRewardsSimple) {
					origZeroReplacement = ((MDPRewardsSimple) rewards).getZeroReplacement();
					((MDPRewardsSimple) rewards).setZeroReplacement(epsilon);
				} else {
					throw new PrismException("To compute expected reward I need to modify the reward structure. But I don't know how to modify" + rewards.getClass().getName());
				}
				
				//Compute the value when rewards are nonzero
				switch (solnMethod) {
				case VALUE_ITERATION:
					res = computeReachRewardsValIter(stpg, rewards, target, inf, min1, min2, init, known);
					break;
				default:
					throw new PrismException("Unknown STPG solution method " + solnMethod);
				}
				
				//Set the value iteration result to be the initial solution for the next part
				//in which "proper" zero rewards are used
				init = res.soln;
				
				//Return the rewards to the original state
				if (rewards instanceof MDPRewardsSimple) {
					((MDPRewardsSimple)rewards).setZeroReplacement(origZeroReplacement);
				}
				
				timerApprox = System.currentTimeMillis() - timerApprox;
				
				if (verbosity >= 1) {
					mainLog.println("Computed an over-approximation of the solution (in " + timerApprox / 1000 + " seconds), this will now be used to get the solution");
				}
			}
		}
		
		// Compute real rewards
		switch (solnMethod) {
		case VALUE_ITERATION:
			res = computeReachRewardsValIter(stpg, rewards, target, inf, min1, min2, init , known);
			break;
		default:
			throw new PrismException("Unknown STPG solution method " + solnMethod);
		}

		// Finished expected reachability
		timer = System.currentTimeMillis() - timer;
		if (verbosity >= 1)
			mainLog.println("Expected reachability took " + timer / 1000.0 + " seconds.");

		// Update time taken
		res.timeTaken = timer / 1000.0;
		res.timePre = timerProb1 / 1000.0;

		return res;
	}

	/**
	 * Compute expected reachability rewards using value iteration.
	 * @param stpg The STPG
	 * @param rewards The rewards
	 * @param target Target states
	 * @param inf States for which reward is infinite
	 * @param min1 Min or max rewards for player 1 (true=min, false=max)
	 * @param min2 Min or max rewards for player 2 (true=min, false=max)
	 * @param init Optionally, an initial solution vector (will be overwritten) 
	 * @param known Optionally, a set of states for which the exact answer is known
	 * Note: if 'known' is specified (i.e. is non-null, 'init' must also be given and is used for the exact values.
	 */
	protected ModelCheckerResult computeReachRewardsValIter(STPG stpg, STPGRewards rewards, BitSet target, BitSet inf, boolean min1, boolean min2, double init[], BitSet known)
			throws PrismException
	{
		ModelCheckerResult res;
		BitSet unknown;
		int i, n, iters;
		double soln[], soln2[], tmpsoln[];
		int adv[] = null;
		boolean genAdv, done;
		long timer;
		
		
		// Are we generating an optimal adversary?
		genAdv = exportAdv;

		// Start value iteration
		timer = System.currentTimeMillis();
		if (verbosity >= 1)
			mainLog.println("Starting value iteration (" + (min1 ? "min" : "max") + (min2 ? "min" : "max") + ")...");

		// Store num states
		n = stpg.getNumStates();
		
		// Create solution vector(s)
		soln = new double[n];
		soln2 = (init == null) ? new double[n] : init;

		// Initialise solution vectors. Use (where available) the following in order of preference:
		// (1) exact answer, if already known; (2) 0.0/infinity if in target/inf; (3) passed in initial value; (4) 0.0
		if (init != null) {
			if (known != null) {
				for (i = 0; i < n; i++)
					soln[i] = soln2[i] = known.get(i) ? init[i] : target.get(i) ? 0.0 : inf.get(i) ? Double.POSITIVE_INFINITY : init[i];
			} else {
				for (i = 0; i < n; i++)
					soln[i] = soln2[i] = target.get(i) ? 0.0 : inf.get(i) ? Double.POSITIVE_INFINITY : init[i];
			}
		} else {
			for (i = 0; i < n; i++)
				soln[i] = soln2[i] = target.get(i) ? 0.0 : inf.get(i) ? Double.POSITIVE_INFINITY : 10.0;
		}

		// Determine set of states actually need to compute values for
		unknown = new BitSet();
		unknown.set(0, n);
		unknown.andNot(target);
		unknown.andNot(inf);
		if (known != null)
			unknown.andNot(known);

		// Create/initialise adversary storage
		if (genAdv) {
			adv = new int[n];
			for (i = 0; i < n; i++) {
				adv[i] = -1;
			}
		}

		// Start iterations
		iters = 0;
		done = false;
		while (!done && iters < maxIters) {
			//mainLog.println(soln);
			iters++;
			// Matrix-vector multiply and min/max ops
			stpg.mvMultRewMinMax(soln, rewards, min1, min2, soln2, unknown, false, genAdv ? adv : null);
			// Check termination
			done = PrismUtils.doublesAreClose(soln, soln2, termCritParam, termCrit == TermCrit.ABSOLUTE);
			// Swap vectors for next iter
			tmpsoln = soln;
			soln = soln2;
			soln2 = tmpsoln;
		}

		// Finished value iteration
		timer = System.currentTimeMillis() - timer;
		if (verbosity >= 1) {
			mainLog.print("Value iteration (" + (min1 ? "min" : "max") + (min2 ? "min" : "max") + ")");
			mainLog.println(" took " + iters + " iterations and " + timer / 1000.0 + " seconds.");
		}

		// Print adversary
		if (genAdv) {
			/*Iterator<Entry<Integer, Double>> it;
			PrismLog out = new PrismFileLog(settings.getString(PrismSettings.PRISM_EXPORT_ADV_FILENAME));
			//out.print("Adv:");
			out.println(n + " ?");
			for (i = 0; i < n; i++) {
				if (adv[i] == -1)
					continue;
				//out.print(" " + i + ":" + stpg.getStatesList().get(i) + ":");
				//out.println(adv[i] != -1 ? stpg.getAction(i, adv[i]) : "-");
				
				it = stpg.getTransitionsIterator(i, adv[i]);
				if (it == null)
					continue;
				while (it.hasNext()) {
					Entry<Integer, Double> next = it.next();
					out.print(i + " 0 " + next.getKey() + " " + next.getValue() + " ");
					out.println(adv[i] != -1 ? stpg.getAction(i, adv[i]) : "-");
				}
			}
			out.println();*/
		}

		// Return results
		res = new ModelCheckerResult();
		res.soln = soln;
		res.numIters = iters;
		res.timeTaken = timer / 1000.0;
		return res;
	}

	/**
	 * Simple test program.
	 */
	public static void main(String args[])
	{
		STPGModelChecker mc;
		STPGAbstrSimple stpg;
		ModelCheckerResult res;
		BitSet target;
		Map<String, BitSet> labels;
		boolean min1 = true, min2 = true;
		try {
			mc = new STPGModelChecker();
			stpg = new STPGAbstrSimple();
			stpg.buildFromPrismExplicit(args[0]);
			//System.out.println(stpg);
			labels = mc.loadLabelsFile(args[1]);
			//System.out.println(labels);
			target = labels.get(args[2]);
			if (target == null)
				throw new PrismException("Unknown label \"" + args[2] + "\"");
			for (int i = 3; i < args.length; i++) {
				if (args[i].equals("-minmin")) {
					min1 = true;
					min2 = true;
				} else if (args[i].equals("-maxmin")) {
					min1 = false;
					min2 = true;
				} else if (args[i].equals("-minmax")) {
					min1 = true;
					min2 = false;
				} else if (args[i].equals("-maxmax")) {
					min1 = false;
					min2 = false;
				}
			}
			//stpg.exportToDotFile("stpg.dot", target);
			//stpg.exportToPrismExplicit("stpg");
			res = mc.computeReachProbs(stpg, target, min1, min2);
			System.out.println(res.soln[0]);
		} catch (PrismException e) {
			System.out.println(e);
		}
	}
}
