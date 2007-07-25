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

import java.util.ArrayList;

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

    /** Mask for break type flags.  These flags are logically or-ed
     * onto the margins in the marginStack to remember what happens
     * with the block in question. */
    private static final int BREAK_MASK   = 0x70000000;
    /** Flag to indicate this block fits into the current line */
    private static final int FITS         = 0x00000000;
    /** Flag to indicate this block will be broken consistently */
    private static final int CONSISTENT   = 0x10000000;
    /** Flag to indicate this block will be broken inconsistently */
    private static final int INCONSISTENT = 0x20000000;

    /** total line length available */
    private final int lineWidth;
    
    /** position in current line. */
    private int pos;

    /** total chars written */
    private int totalOut=0;

    /** Back-end for the pretty-printed output */
    private Backend<Exc> back;

    /** stack to remember value of <code>pos</code> in nested blocks */
    private ArrayList<Integer> marginStack 
	= new ArrayList<Integer>(10);


    /** Create a printer.  It will write its output to <code>writer</code>.
     * Lines have a maximum width of <code>lineWidth</code>. */
    Printer(Backend<Exc> back) {
	this.back = back;
	lineWidth = back.lineWidth();
	pos = 0;
    }


    /** write the String <code>s</code> to <code>out</code> */
    void print(String s) throws Exc {
	back.print(s);
	pos+=back.measure(s);
	totalOut+=back.measure(s);
    }

    /** begin a block */
    void openBlock(boolean consistent,int indent,
		   int followingLength) {
	if (followingLength + pos > lineWidth) {
	    push(pos+indent,consistent?CONSISTENT:INCONSISTENT);
	} else {
	    push(0,FITS);
	}
    }

    /** end a block */
    void closeBlock() {
	pop();
    }

    /**
     * write a break.  <code>followingLength</code> should be the
     * space needed by the material up to the next corresponding
     * closeBlock() or printBreak(), and is used to decide whether the
     * current line is continues, or a new (indented) line is begun.
     * */
    void printBreak(int width,int offset, int followingLength) 
	throws Exc {

	if (topBreak()==CONSISTENT || 
	    (topBreak()==INCONSISTENT 
	     && followingLength > (lineWidth-pos)) ) {
	    
	    pos = topMargin() + offset;

	    newLine();
	} else {
	    writeSpaces(width);
	    pos+=width;
	}
    }

    void mark(Object o) throws Exc {
	back.mark(o);
    }

    void indent(int width, int offset) throws Exc
    {
	int newMargin = topMargin()+offset;
	if (topBreak() != FITS) {
	    if(newMargin > pos) {
		writeSpaces(newMargin-pos);
		pos=newMargin;
	    }
	} else {
	    writeSpaces(width);
	    pos+=width;
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
	return lineWidth-pos;
    }

    /** Return the line width of this Printer. */
    int lineWidth() {
	return lineWidth;
    }

    /** push one element,consisting of margin and 
     * break type flags onto the space stack. */
    private void push(int n,int breaks) {
	marginStack.add(n|breaks);
    }

    /** Pop one element from the space stack. */
    private void pop() {
	try {
	    marginStack.remove(marginStack.size()-1);
	} catch (IndexOutOfBoundsException e) {
	    throw new UnbalancedBlocksException();
	}
    }

    /** return the topmost element of the space stack without popping it. */
    private int top() {
	try {
	    return marginStack.get(marginStack.size()-1);
	} catch (IndexOutOfBoundsException e) {
	    throw new UnbalancedBlocksException();
	}
    }

    /** return the margin of the top element of the space stack. */
    private int topMargin() {
	return top() & ~BREAK_MASK;
    }

    /** return the break type flags of the top element of the space stack. */
    private int topBreak() {
	return top() & BREAK_MASK;
    }

    /** Start a new line and indent according to <code>pos</code>
     */
    private void newLine() throws Exc {
	back.newLine();
	totalOut++;
	if (pos>0) {
	    writeSpaces(pos);
	}
    }

    /** how many spaces are in SPACES */
    private static final int NR_SPACES = 128;

    /** a String containing <code>NR_SPACES</code> spaces */
    private static final String SPACES;

    /* initialize SPACES */
    static {
	StringBuffer sb = new StringBuffer(NR_SPACES);
	for(int i=0;i<NR_SPACES;i++) {
	    sb.append(' ');
	}
	SPACES = sb.toString();	
    }

    private void writeSpaces(int n) throws Exc {
	while (n>NR_SPACES) {
	    back.print(SPACES);
	    n-=NR_SPACES;
	}
	back.print(SPACES.substring(0,n));
	totalOut+=n;
    }
}
