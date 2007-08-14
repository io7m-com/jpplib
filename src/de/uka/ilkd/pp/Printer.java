// This file is part of the Java™ Pretty Printer Library (JPPlib)
// Copyright (C) 2007 Martin Giese
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uka.ilkd.pp;

import static de.uka.ilkd.pp.IndentationStack.*;
import static de.uka.ilkd.pp.IndentationStack.BreakDecision.*;

/** The intermediate layer of the pretty printing library.  Using the
 * block size information provided by the {@link Layouter} class, this
 * decides where to insert line breaks.  It tries to break as few
 * blocks as possible.  
 *
 * <p>Exceptions of type {@code Exc} thrown by the backend will get
 * passed through to the Layouter.
 *
 * @param <Exc> The type of exceptions that might be thrown by the backend.
 */

class Printer<Exc extends Exception> {
	
	/** total line length available */
	private final int lineWidth;

	/** position in current line. */
	private int pos;

	/** total chars written */
	private int totalOut = 0;

	/** Back-end for the pretty-printed output */
	private Backend<Exc> back;


	/** stack to remember value of <code>pos</code> and 
	 * breaking decisions in nested blocks */
	private IndentationStack indentStack = new IndentationStack();
	
	/** Create a printer.  It will write its output to <code>writer</code>.
	 * Lines have a maximum width of <code>lineWidth</code>. 
	 * @param back the Backend to write output to
	 * */
	Printer(Backend<Exc> back) {
		this.back = back;
		lineWidth = back.lineWidth();
		pos = 0;
	}

	/** Write the String <code>s</code> to <code>out</code> 
	 * @param s the String to write
	 */
	void print(String s) throws Exc {
		back.print(s);
		pos += back.measure(s);
		totalOut += back.measure(s);
	}

	/** Begin a block.  The parameter <code>followingLength</code> gives
	 * the length of the contents of the block, as determined by Layouter,
	 * or possibly some large number, if the Layouter can determine that 
	 * the block will not fit on one line.  Depending on the amount of
	 * space left on the line, the layouter decides whether this block
	 * should be broken or not.
	 */
	void openBlock(Layouter.BreakConsistency consistent,
			        Layouter.IndentationBase fromPos, 
			        int indent, int followingLength) {
		if (followingLength > space()) {
				indentStack.push(indentBase(fromPos) + indent, 
						         BreakDecision.fromBreakConsistency(consistent));
		} else {
			indentStack.push(0, FITS);
		}
	}

	/** end a block */
	void closeBlock() {
		indentStack.pop();
	}

	/**
	 * Write a break.  The parameter <code>followingLength</code> is 
	 * determined by Layouter and gives the space needed by the 
	 * material up to the next corresponding closeBlock() or printBreak().
	 * It is used to decide whether the current line is continued, 
	 * or a new (indented) line is begun.
	 * 
	 * @param width the number of spaces printed if the line is <em>not</em>
	 * @param offset the amount added to the indentation level if the line <em>is</em> broken
     * @param followingLength space required by text up to next break or close
	 * */
	void printBreak(int width, int offset, int followingLength) throws Exc {
		if (    indentStack.topConsistent()
			 || (   indentStack.topInconsistent() 
				 && followingLength > space())) {

			pos = indentStack.topIndentation() + offset;
			newLine();
		} else {
			writeSpaces(width);
			pos += width;
		}
	}

	/** Mark this position in the text.  This is simply sent 
	 * through to the backend.
	 */
	void mark(Object o) throws Exc {
		back.mark(o);
	}

	/** Add a number of spaces.  The number of spaces 
	 * inserted depends on whether the surrounding block is
	 * broken or not.
	 * @param width number of spaces to insert if surrounding block is not broken
	 * @param offest position relative to current indentation level to advance to*/
	void indent(int width, int offset) throws Exc {
		if (indentStack.topFits()) {
			writeSpaces(width);
			pos += width;
		} else {
			int newMargin = indentStack.topIndentation() + offset;
			if (newMargin > pos) {
				writeSpaces(newMargin - pos);
				pos = newMargin;
			}
		}
	}

	/** Close the output stream. */
	void close() throws Exc {
		back.close();
	}

	/** Flush the output stream. */
	void flush() throws Exc {
		back.flush();
	}

	/** Return the amount of space currently left on this line. */
	int space() {
		return lineWidth - pos;
	}

	/** Return the line width of this Printer. */
	int lineWidth() {
		return lineWidth;
	}

	private int indentBase(Layouter.IndentationBase base) {
		switch(base) {
		case FROM_POS:
			return pos;
		case FROM_IND:
			if (indentStack.isEmpty()) {
				return 0;
			} else {
				return indentStack.topIndentation();
			}
		default:
			return 0;
		}
	}
	
	/** Start a new line and indent according to <code>pos</code>
	 */
	private void newLine() throws Exc {
		back.newLine();
		totalOut++;
		if (pos > 0) {
			writeSpaces(pos);
		}
	}

	/** how many spaces are in SPACES */
	private static final int NR_SPACES = 128;

	/** a String containing <code>NR_SPACES</code> spaces */
	private static final String SPACES;

	/* initialize SPACES */
	static {
		StringBuilder sb = new StringBuilder(NR_SPACES);
		for (int i = 0; i < NR_SPACES; i++) {
			sb.append(' ');
		}
		SPACES = sb.toString();
	}

	private void writeSpaces(int n) throws Exc {
		while (n > NR_SPACES) {
			back.print(SPACES);
			n -= NR_SPACES;
		}
		back.print(SPACES.substring(0, n));
		totalOut += n;
	}
}
