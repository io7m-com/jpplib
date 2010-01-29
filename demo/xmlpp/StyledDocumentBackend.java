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

package xmlpp;

import de.uka.ilkd.pp.Backend;
import de.uka.ilkd.pp.NoExceptions;

import javax.swing.text.*;

/** A {@link de.uka.ilkd.pp.Backend} which appends all output to a 
 * {@link javax.swing.text.StyledDocument}.
 * If the parameter to the {@link #mark(Object o)} 
 * method is an instance of {@link javax.swing.text.AttributeSet}, following
 * characters sent to {@link #print(String)} will be printed using these attributes.
 * Initially, an empty set of attributes is used.
 * 
 * @author Martin Giese
 *
 */
public class StyledDocumentBackend implements Backend<NoExceptions> {
	protected StyledDocument out;
    protected int lineWidth;
    protected AttributeSet currentAttributes = SimpleAttributeSet.EMPTY;
    
    /** Create a new StyledDocumentBackend.  This will append all output to
     * the given StyledDocument <code>sb</code>.    */
    public StyledDocumentBackend(StyledDocument sd,int lineWidth) {
    	this.lineWidth = lineWidth;
    	this.out = sd;
    }

    /** Create a new StyledDocumentBackend.  This will accumulate output in
     * a fresh, private DefaultStyledDocument. */
    public StyledDocumentBackend(int lineWidth) {
    	this(new DefaultStyledDocument(),lineWidth);
    }

    /** Append a String <code>s</code> to the output.  <code>s</code> 
     * contains no newlines. */
    public void print(String s) {
    	try {
    		out.insertString(out.getLength(),s,currentAttributes);
    	} catch (BadLocationException e) {
    		System.err.println(e);
    		System.exit(1);
    	}
    }

    /** Start a new line. */
    public void newLine() {
    	print("\n");
    }

    /** Closes this backend */
    public void close() {
    	return;
    }

    /** Flushes any buffered output */
    public void flush() {
    	return;
    }

    /** Gets called to record a <code>mark()</code> call in the input. 
     * If <code>o</code> is an instance of {@link javax.swing.text.AttributeSet},
     * any further text is printed with these attributes. */
    public void mark(Object o) {
    	if (o instanceof AttributeSet) {
    		currentAttributes = (AttributeSet) o;
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
    public StyledDocument getDocument() {
    	return out;
    }
}
