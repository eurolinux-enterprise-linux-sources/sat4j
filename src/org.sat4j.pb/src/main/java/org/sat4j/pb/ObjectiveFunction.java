/*******************************************************************************
 * SAT4J: a SATisfiability library for Java Copyright (C) 2004-2008 Daniel Le Berre
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU Lesser General Public License Version 2.1 or later (the
 * "LGPL"), in which case the provisions of the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL, and not to allow others to use your version of
 * this file under the terms of the EPL, indicate your decision by deleting
 * the provisions above and replace them with the notice and other provisions
 * required by the LGPL. If you do not delete the provisions above, a recipient
 * may use your version of this file under the terms of the EPL or the LGPL.
 * 
 * Based on the pseudo boolean algorithms described in:
 * A fast pseudo-Boolean constraint solver Chai, D.; Kuehlmann, A.
 * Computer-Aided Design of Integrated Circuits and Systems, IEEE Transactions on
 * Volume 24, Issue 3, March 2005 Page(s): 305 - 317
 * 
 * and 
 * Heidi E. Dixon, 2004. Automating Pseudo-Boolean Inference within a DPLL 
 * Framework. Ph.D. Dissertation, University of Oregon.
 *******************************************************************************/
package org.sat4j.pb;

import java.io.Serializable;
import java.math.BigInteger;

import org.sat4j.core.ReadOnlyVec;
import org.sat4j.core.ReadOnlyVecInt;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;

/**
 * Abstraction for an Objective Function for Pseudo Boolean Optimization.
 * 
 * May be generalized in the future to deal with other optimization functions.
 * 
 * @author leberre
 * 
 */
public class ObjectiveFunction implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	// contains the coeffs of the objective function for each variable
	private final IVec<BigInteger> coeffs;

	private final IVecInt vars;

	public ObjectiveFunction(IVecInt vars, IVec<BigInteger> coeffs) {
		this.vars = new ReadOnlyVecInt(vars);
		this.coeffs = new ReadOnlyVec<BigInteger>(coeffs);
	}

	// calculate the degree of the objective function
	public BigInteger calculateDegree(int[] model) {
		BigInteger tempDegree = BigInteger.ZERO;

		for (int i = 0; i < vars.size(); i++) {
			BigInteger coeff = coeffs.get(i);
			if (varInModel(vars.get(i), model))
				tempDegree = tempDegree.add(coeff);
			else if ((coeff.signum() < 0) && !varInModel(-vars.get(i), model)) {
				// the variable does not appear in the model: it can be assigned
				// either way
				// System.out.println("c special optimisation obj. function for var "+i);
				tempDegree = tempDegree.add(coeff);
			}
		}
		return tempDegree;
	}

	private boolean varInModel(int var, int[] model) {
		for (int i = 0; i < model.length; i++)
			if (var == model[i])
				return true;
		return false;
	}

	public IVec<BigInteger> getCoeffs() {
		return coeffs;
	}

	public IVecInt getVars() {
		return vars;
	}

	@Override
	public String toString() {
		StringBuffer stb = new StringBuffer();
		IVecInt lits = getVars();
		IVec<BigInteger> coeffs = getCoeffs();
		for (int i = 0; i < lits.size(); i++)
			stb.append(coeffs.get(i) + " x" + lits.get(i) + " ");
		return stb.toString();
	}

}
