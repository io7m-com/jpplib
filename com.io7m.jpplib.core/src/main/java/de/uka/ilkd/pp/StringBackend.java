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
