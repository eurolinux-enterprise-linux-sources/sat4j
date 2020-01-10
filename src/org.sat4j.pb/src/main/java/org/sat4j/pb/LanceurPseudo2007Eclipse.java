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

import static java.lang.System.out;

import java.util.Collection;

import org.sat4j.AbstractLauncher;
import org.sat4j.ExitCode;
import org.sat4j.pb.reader.OPBEclipseReader2007;
import org.sat4j.pb.tools.XplainPB;
import org.sat4j.reader.Reader;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class LanceurPseudo2007Eclipse extends LanceurPseudo2007 {

	XplainPB quickxplain;

	@Override
	protected ISolver configureSolver(String[] args) {
		IPBSolver theSolver;
		if (args.length > 1) {
			theSolver = SolverFactory.instance().createSolverByName(args[0]);
		} else {
			theSolver = SolverFactory.newDefault();
		}
		quickxplain = new XplainPB(theSolver);
		theSolver = new PseudoOptDecorator(quickxplain);
		if (args.length == 3) {
			theSolver.setTimeout(Integer.valueOf(args[1]));
		}
		out.println(theSolver.toString(COMMENT_PREFIX)); //$NON-NLS-1$
		return theSolver;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LanceurPseudo2007Eclipse() {
	}

	@Override
	protected Reader createReader(ISolver theSolver, String problemname) {
		return new OPBEclipseReader2007((IPBSolver) theSolver);
	}

	/**
	 * Lance le prouveur sur un fichier Dimacs
	 * 
	 * @param args
	 *            doit contenir le nom d'un fichier Dimacs, eventuellement
	 *            compress?.
	 */
	public static void main(final String[] args) {
		final AbstractLauncher lanceur = new LanceurPseudo2007Eclipse();
		if (args.length == 0 || args.length > 2) {
			lanceur.usage();
			return;
		}
		lanceur.run(args);
		System.exit(lanceur.getExitCode().value());
	}

	@Override
	protected void displayAnswer() {
		super.displayAnswer();
		ExitCode exitCode = getExitCode();

		if (exitCode == ExitCode.UNSATISFIABLE) {
			try {

				Collection<IConstr> explanation = quickxplain.explain();
				log("Explanation for inconsistency: " + explanation);
			} catch (TimeoutException e) {
				log("Timeout ! Need more time to complete");
			}
		}

	}

}
