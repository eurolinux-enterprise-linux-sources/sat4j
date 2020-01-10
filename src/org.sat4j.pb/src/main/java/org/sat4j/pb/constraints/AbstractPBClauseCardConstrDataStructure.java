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
package org.sat4j.pb.constraints;

import java.math.BigInteger;

import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.constraints.cnf.Clauses;
import org.sat4j.minisat.core.Constr;
import org.sat4j.pb.constraints.pb.IDataStructurePB;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;

public abstract class AbstractPBClauseCardConstrDataStructure extends
		AbstractPBDataStructureFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static final BigInteger MAX_INT_VALUE = BigInteger
			.valueOf(Integer.MAX_VALUE);

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.sat4j.minisat.constraints.AbstractPBDataStructureFactory#
	 * constraintFactory(org.sat4j.specs.VecInt, org.sat4j.specs.VecInt,
	 * boolean, int)
	 */
	@Override
	protected Constr constraintFactory(int[] literals, BigInteger[] coefs,
			BigInteger degree) throws ContradictionException {
		if (degree.equals(BigInteger.ONE)) {
			IVecInt v = Clauses.sanityCheck(new VecInt(literals),
					getVocabulary(), solver);
			if (v == null)
				return null;
			return constructClause(v);
		}
		if (coefficientsEqualToOne(coefs)) {
			assert degree.compareTo(MAX_INT_VALUE) < 0;
			return constructCard(new VecInt(literals), degree.intValue());
		}
		return constructPB(literals, coefs, degree);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.sat4j.minisat.constraints.AbstractPBDataStructureFactory#
	 * constraintFactory(org.sat4j.specs.VecInt, org.sat4j.specs.VecInt, int)
	 */
	@Override
	protected Constr learntConstraintFactory(IDataStructurePB dspb) {
		if (dspb.getDegree().equals(BigInteger.ONE)) {
			IVecInt literals = new VecInt();
			IVec<BigInteger> resCoefs = new Vec<BigInteger>();
			dspb.buildConstraintFromConflict(literals, resCoefs);
			// then assertive literal must be placed at the first place
			int indLit = dspb.getAssertiveLiteral();
			if (indLit > -1) {
				int tmp = literals.get(indLit);
				literals.set(indLit, literals.get(0));
				literals.set(0, tmp);
			}
			return constructLearntClause(literals);
		}
		if (dspb.isCardinality()) {
			return constructLearntCard(dspb);
		}
		return constructLearntPB(dspb);
	}

	static boolean coefficientsEqualToOne(BigInteger[] coefs) {
		for (int i = 0; i < coefs.length; i++)
			if (!coefs[i].equals(BigInteger.ONE))
				return false;
		return true;
	}

	abstract protected Constr constructClause(IVecInt v);

	abstract protected Constr constructCard(IVecInt theLits, int degree)
			throws ContradictionException;

	abstract protected Constr constructPB(int[] theLits, BigInteger[] coefs,
			BigInteger degree) throws ContradictionException;

	abstract protected Constr constructLearntClause(IVecInt literals);

	abstract protected Constr constructLearntCard(IDataStructurePB dspb);

	abstract protected Constr constructLearntPB(IDataStructurePB dspb);

}
