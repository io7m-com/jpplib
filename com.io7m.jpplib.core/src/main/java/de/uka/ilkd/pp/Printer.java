//This file is part of the Java™ Pretty Printer Library (JPPlib)
//Copyright (c) 2009, Martin Giese
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without 
//modification, are permitted provided that the following conditions are met:
// 
// * Redistributions of source code must retain the above copyright 
//   notice, this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright 
//   notice, this list of conditions and the following disclaimer in the 
//   documentation and/or other materials provided with the distribution.
// * Neither the name of the author nor the names of his contributors 
//   may be used to endorse or promote products derived from this 
//   software without specific prior written permission.
// 
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
//IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
//ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
//LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
//CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
//SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
//INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
//CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
//ARISING IN ANY WAY OUT OF THE USE OF THIS  SOFTWARE, EVEN IF ADVISED OF 
//THE POSSIBILITY OF SUCH DAMAGE.

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
	void openBlock(Layouter.BreakConsistency cons,
			        Layouter.IndentationBase indBase, 
			        int indent, int followingLength) {
		if (followingLength > space()) {
				indentStack.push(indentBase(indBase) + indent, 
						         BreakDecision.fromBreakConsistency(cons));
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
