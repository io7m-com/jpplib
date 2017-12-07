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

import java.io.IOException;
import java.io.Writer;

/** A {@link Backend} which writes all output to a java.io.Writer.
 * The {@link #mark(Object o)} method does nothing in this implementation.
 * There is a method {@link #count()} which returns the number of characters
 * written by this so far.
 */

public class WriterBackend implements Backend<IOException> {

    protected Writer out;
    protected int lineWidth;
    protected int count=0;

    public WriterBackend(Writer w,int lineWidth) {
	this.out = w;
	this.lineWidth = lineWidth;
    }

    /** Append a String <code>s</code> to the output.  <code>s</code> 
     * contains no newlines. */
    public void print(String s) throws IOException {
	out.write(s);
	count+=measure(s);
    }

    /** Start a new line. */
    public void newLine() throws IOException {
	out.write('\n');
	count++;
    }

    /** Closes this backend */
    public void close() throws IOException {
	out.close();
    }

    /** Flushes any buffered output */
    public void flush() throws IOException {
	out.flush();
    }

    /** Gets called to record a <code>mark()</code> call in the input. */
    public void mark(Object o) {
	return;
    }

    /** Returns the number of characters written through this backend.*/
    public int count() {
	return count;
    }

    /** Returns the available space per line */
    public int lineWidth() {
	return lineWidth;
    }

    /** Returns the space required to print the String <code>s</code> */
    public int measure(String s) {
	return s.length();
    }

}
