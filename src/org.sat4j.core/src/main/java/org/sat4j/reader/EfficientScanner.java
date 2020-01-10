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
 * Based on the original MiniSat specification from:
 * 
 * An extensible SAT solver. Niklas Een and Niklas Sorensson. Proceedings of the
 * Sixth International Conference on Theory and Applications of Satisfiability
 * Testing, LNCS 2919, pp 502-518, 2003.
 *
 * See www.minisat.se for the original solver in C++.
 * 
 *******************************************************************************/
package org.sat4j.reader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * Efficient scanner based on the LecteurDimacs class written by Frederic
 * Laihem. It is much faster than Java Scanner class because it does not split
 * the input file into strings but reads and interpret the input char by char.
 * Furthermore, it is based on stream (char in ASCII) and not reader (word in
 * UTF).
 * 
 * @author laihem
 * @author leberre
 * @since 2.1
 */
public class EfficientScanner implements Serializable {

	private static final long serialVersionUID = 1L;

	/* taille du buffer */
	private final static int TAILLE_BUF = 16384;

	private transient final BufferedInputStream in;

	private static final char EOF = (char) -1;

	/*
	 * nomFichier repr?sente le nom du fichier ? lire
	 */
	public EfficientScanner(final InputStream input) {
		this.in = new BufferedInputStream(input, EfficientScanner.TAILLE_BUF);
	}

	public void close() throws IOException {
		in.close();
	}

	/** on passe les commentaires et on lit le nombre de literaux */
	public void skipComments() throws IOException {
		char currentChar;
		for (;;) {
			currentChar = currentChar();
			if (currentChar != 'c') {
				break;
			}
			skipRestOfLine();
			if (currentChar == EOF)
				break;
		}
	}

	public int nextInt() throws IOException, ParseFormatException {
		int val = 0;
		boolean neg = false;
		char currentChar = skipSpaces();
		if (currentChar == '-') {
			neg = true;
			currentChar = (char) in.read();
		} else if (currentChar == '+') {
			currentChar = (char) in.read();
		} else if (currentChar >= '0' && currentChar <= '9') {
			val = currentChar - '0';
			currentChar = (char) in.read();
		} else {
			throw new ParseFormatException("Unknown character " + currentChar);
		}
		/* on lit la suite du literal */
		while (currentChar >= '0' && currentChar <= '9') {
			val = (val * 10) + currentChar - '0';
			currentChar = (char) in.read();
		}
		if (currentChar == '\r') {
			in.read(); // skip \r\n on windows.
		}
		return neg ? -val : val;
	}

	public BigInteger nextBigInteger() throws IOException, ParseFormatException {
		StringBuffer stb = new StringBuffer();
		char currentChar = skipSpaces();
		if (currentChar == '-') {
			stb.append(currentChar);
			currentChar = (char) in.read();
		} else if (currentChar == '+') {
			currentChar = (char) in.read();
		} else if (currentChar >= '0' && currentChar <= '9') {
			stb.append(currentChar);
			currentChar = (char) in.read();
		} else {
			throw new ParseFormatException("Unknown character " + currentChar);
		}
		while (currentChar >= '0' && currentChar <= '9') {
			stb.append(currentChar);
			currentChar = (char) in.read();
		}
		return new BigInteger(stb.toString());
	}

	public String next() throws IOException, ParseFormatException {
		StringBuffer stb = new StringBuffer();
		char currentChar = skipSpaces();
		while (currentChar != ' ' && currentChar != '\n') {
			stb.append(currentChar);
			currentChar = (char) in.read();
		}
		return stb.toString();
	}

	public char skipSpaces() throws IOException {
		char car;

		while ((car = (char) in.read()) == ' ' || car == '\n')
			;
		return car;
	}

	public String nextLine() throws IOException {
		StringBuffer stb = new StringBuffer();
		char car;
		do {
			car = (char) in.read();
			stb.append(car);
		} while ((car != '\n') && (car != EOF));
		return stb.toString();
	}

	public void skipRestOfLine() throws IOException {
		char car;
		do {
			car = (char) in.read();
		} while ((car != '\n') && (car != EOF));
	}

	public boolean eof() throws IOException {
		return currentChar() == EOF;
	}

	public char currentChar() throws IOException {
		in.mark(10);
		char car = (char) in.read();
		in.reset();
		return car;
	}
}
