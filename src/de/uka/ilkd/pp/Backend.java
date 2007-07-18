// This file is part of the Java Pretty Printer Library (JPPlib)
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
