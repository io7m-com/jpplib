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

/**
 * The backend for a {@link Layouter}.  An object satisfying this
 * interface can act as a receiver for the layed out text produced by
 * a {@link Layouter}.  A <code>Backend</code> must also provide the
 * maximum line width available through the {@link #lineWidth()}
 * method.  Finally, it is responsible for calculating (with {@link
 * #measure(String)} the amount of space it actually needs to print a
 * given string.  For instance, if strings printed through a {@link
 * Layouter} are actually known to be in HTML, {@link
 * #measure(String)} can return the size of the text, not including
 * markup.
 *
 * <P>This interface has a type parameter <code>Exc</code> which
 * denotes the type of exceptions that might be thrown when output is
 * sent to a backend.  For instance, a backend like {@link
 * WriterBackend}, which ultimately writes output to a {@link
 * java.io.Writer} might produce <code>IOException</code>s which
 * should not be handled by the Backend, but passed through to the
 * program calling the {@link Layouter}.  So it implements the
 * interface <code>Backend&lt;java.io.IOException&gt;</code>.  There is a dummy
 * exception class {@link NoExceptions} that may be used as parameter
 * if no checked exceptions are thrown by a Backend.
 *
 * <P>There is currently no provision to handle proportional fonts,
 * and there might never be.
 *
 * @param <Exc> The type of exceptions that might be thrown by 
 * this backend.
 *
 * @author Martin Giese
 * @see Layouter
 *
 */

public interface Backend<Exc extends Exception> {
    /** Append a String <code>s</code> to the output.  <code>s</code> 
     * contains no newlines. */
    void print(String s) throws Exc;

    /** Start a new line. */
    void newLine() throws Exc;

    /** Closes this backend */
    void close() throws Exc;

    /** Flushes any buffered output */
    void flush() throws Exc;

    /** Gets called to record a <code>mark()</code> call in the input. */
    void mark(Object o) throws Exc;

    /** Returns the available space per line */
    int lineWidth();

    /** Returns the space required to print the String <code>s</code> */
    int measure(String s);

}
