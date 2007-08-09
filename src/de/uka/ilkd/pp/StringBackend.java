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

/** A {@link Backend} which appends all output to a StringBuilder or
 * StringBuffer.
 * The {@link #mark(Object o)} method does nothing in this
 * implementation.  There is a method {@link #count()} which returns
 * the number of characters written by this so far.  
 * The method {@link #getString()} gets the output written so far.
 */
public class StringBackend implements Backend<NoExceptions> {
	/** The StringBuffer or StringBuilder output will be accumulated in.
	 * Some of the implementations rely on this being either a StringBuilder
	 * or StringBuffer, and all implementations in this class guarantee it.
	 * If you subclass StringBackend, beware!
	 */
	protected Appendable out;
	
	/** The maximum width of lines to be written to this backend. */
    protected int lineWidth;
    
    /** The initial number of characters in <code>out</code>, used
     * by the implementation of {@link #count()}.
     */
    protected int initOutLength;
    
    /** Create a new StringBackend.  This will append all output to
     * the given StringBuilder <code>sb</code>.    */
    public StringBackend(StringBuilder sb,int lineWidth) {
    	this.lineWidth = lineWidth;
    	this.out = sb;
    	initOutLength = sb.length();
    }

    /**
	 * Create a new StringBackend. This will append all output to the given
	 * StringBuffer <code>sb</code>.
	 * 
	 * @deprecated consider using the constructor
	 *             {@link #StringBackend(StringBuilder, int)}, as
	 *             StringBuilders are faster, and multi-threaded access to
	 *             <code>sb</code> is probably not going to work anyhow.
	 */
    public StringBackend(StringBuffer sb,int lineWidth) {
    	this.lineWidth = lineWidth;
    	this.out = sb;
    	initOutLength = sb.length();
    }

    /** Create a new StringBackend.  This will accumulate output in
     * a fresh, private StringBuilder. */
    public StringBackend(int lineWidth) {
    	this(new StringBuilder(lineWidth),lineWidth);
    }

    /** Append a String <code>s</code> to the output.  <code>s</code> 
     * contains no newlines. */
    public void print(String s) {
    	try {
    		out.append(s);
    	} catch (java.io.IOException e) {
    		// Cannot happen, since out can only be a 
    		// StringBuffer or StringBuilder
    	}
    }

    /** Start a new line. */
    public void newLine() {
    	try {
			    	out.append('\n');

		} catch (java.io.IOException e) {
    		// Cannot happen, since out can only be a 
    		// StringBuffer or StringBuilder
		}
    }

    /** Closes this backend */
    public void close() {
    	return;
    }

    /** Flushes any buffered output */
    public void flush() {
    	return;
    }

    /** Gets called to record a <code>mark()</code> call in the input. */
    public void mark(Object o) {
    	return;
    }

    /** Returns the number of characters written through this backend.*/
    public int count() {
    	if (out instanceof StringBuilder) {
    		return ((StringBuilder)out).length()-initOutLength;
    	} else if (out instanceof StringBuffer) {
    		return ((StringBuffer)out).length()-initOutLength;    		
    	} else {
    		// Cannot happen, since out can only be a 
    		// StringBuffer or StringBuilder
    		return 0;
        }
    }
    
    /** Returns the available space per line */
    public int lineWidth() {
    	return lineWidth;
    }

    /** Returns the space required to print the String <code>s</code> */
    public int measure(String s) {
    	return s.length();
    }

    /** Returns the accumulated output */
    public String getString() {
    	return out.toString();
    }
}
