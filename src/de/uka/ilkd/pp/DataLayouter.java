//This file is part of the Java™ Pretty Printer Library (JPPlib)
//Copyright (C) 2007 Martin Giese

//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.

//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.

//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uka.ilkd.pp;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/** An extension of {@link de.uka.ilkd.pp.Layouter} to print
 * arbitrary Java data.  There is a {@link #print(Object)} method
 * that prints objects according to their type.  In particular,
 * there are special layouts for (nested) collections, maps, and
 * arrays.  Classes implementing the interface {@link PrettyPrintable}
 * provide their own method for printing themselves to a DataLayouter.
 * 
 * <p>Like most methods of {@link Layouter}, the methods of this
 * class return <code>this</code>, so calls can be sequenced as in
 * <code>l.beginC().print(...).brk(...)</code>.
 * 
 * <p>The intended main use of this class is to produce more readable
 * debugging output than what <code>System.err.println()</code> would
 * give, or for data output that should be human-readable.  It produces
 * standard representations of data, like {@link Object#toString()}.
 * Application-specific types will usually wish to define their own
 * pretty printing, using the methods of {@link Layouter}.
 * 
 * @author Martin Giese
 *
 * @param <Exc> The type of exceptions that might be thrown by the backend.
 */
public class DataLayouter<Exc extends Exception> extends Layouter<Exc> {

	/**
	 * Construts a newly allocated DataLayouter which will send output to
	 * the given {@link Backend} and has the given default indentation.
	 *
	 * @param back the Backend
	 * @param indentation the default indentation
	 *
	 */
	public DataLayouter(Backend<Exc> back,int indentation) {
		super(back, indentation);
	}
	
	// STATIC FACTORY METHODS ----------------------------------------

	/** Factory method for a DataLayouter with a {@link WriterBackend}.
	 * The line width is taken to be {@link #DEFAULT_LINE_WIDTH}, and the
	 * default indentation {@link #DEFAULT_INDENTATION}. 
	 *
	 * @param writer the {@link java.io.Writer} the Backend is going to use
	 */
	public static DataLayouter<IOException> 
	getWriterDataLayouter(java.io.Writer writer) {
		return getWriterDataLayouter(writer,DEFAULT_LINE_WIDTH);
	}

	/** Factory method for a DataLayouter with a {@link WriterBackend}.
	 * The default indentation is taken from {@link #DEFAULT_INDENTATION}. 
	 *
	 * @param writer the {@link java.io.Writer} the Backend is going to use
	 * @param lineWidth the maximum lineWidth the Backend is going to use
	 */
	public static DataLayouter<IOException> 
	getWriterDataLayouter(java.io.Writer writer,int lineWidth) {
		return getWriterDataLayouter(writer,lineWidth,DEFAULT_INDENTATION);
	}

	/** Factory method for a DataLayouter with a {@link WriterBackend}.
	 *
	 * @param writer the {@link java.io.Writer} the Backend is going to use
	 * @param lineWidth the maximum lineWidth the Backend is going to use
	 * @param indentation the default indentation
	 */
	public static DataLayouter<IOException> 
	getWriterDataLayouter(java.io.Writer writer,int lineWidth,int indentation) {
		return new DataLayouter<IOException>(
				   new WriterBackend(writer,lineWidth),
				   indentation);
	}

	// DATA PRINTING METHODS ----------------------------------------

	/** Print <code>o</code> to this DataLayouter.
     * If <code>o</code> is an instance of {@link PrettyPrintable},
     * it is printed using its <code>prettyPrint</code> method.
     * Otherwise, if <code>o</code> a is a collection, and array,
     * or a map, it is printed as descibed in the methods
     * {@link #print(Collection)}, {@link #printArray(Object)},
     * and {@link #print(Map)}, respectively.  If everything else
     * fails, <code>o.toString()</code> is used.
	 * 
	 * @param o
	 *            the object to be pretty printed
	 */
	public DataLayouter<Exc> print(Object o) throws Exc {
		if (o instanceof PrettyPrintable) {
			((PrettyPrintable) o).prettyPrint(this);
			return this;
		} else if (o instanceof Collection<?>) {
			return print((Collection<?>) o);
		} else if (o instanceof Map<?, ?>) {
			return print((Map<?, ?>) o);
		} else if (o.getClass().isArray()) {
			return printArray(o);		
		} else {
			return print(String.valueOf(o));
		}
	}

	/** Print a collection.
	 * This is printed as
	 * <pre>
	 * [xxx, yyy, zzz]
	 * </pre>
	 * if it fits on one line, and as
	 * <pre>
	 * [xxx,
	 *  yyy,
	 *  zzz]
	 * </pre>
	 * otherwise.
	 * 
	 * @param c A collection
	 */
	public DataLayouter<Exc> print(Collection<?> c) throws Exc {
		print("[").beginC(0);
		boolean first = true;
		for (Object o : c) {
			if (!first) {
				print(",").brk(1, 0);
			}
			print(o);
			first = false;
		}
		print("]").end();
		return this;
	}

	/** Print an array of reference or primitive elements.
	 * The produced layout is the same as for collections.
	 * 
	 * @param o an object, has to be an array!
	 */
	public DataLayouter<Exc> printArray(Object o) throws Exc {
		Object[] boxed = BoxArrays.boxArray(o);
		print(Arrays.asList(boxed));
		return this;
	}
	
	/** Print a map.
	 * This is printed as
	 * <pre>
	 * {k1=v1, k2=v2, k3=v3]
	 * </pre>
	 * if it fits on one line, and as
	 * <pre>
	 * {key1=val1,
	 *  key2=val2,
	 *  key3=val3]
	 * </pre>
	 * otherwise.  If values don't fit on one line, the
	 * key-value pairs will also be spread over two lines, as
	 * indicated for {@link #printEntry(java.util.Map.Entry)}.
	 * </pre>
	 */
	public DataLayouter<Exc> print(Map<?, ?> m) throws Exc {
		print("{").beginC(0);
		boolean first = true;
		for (Map.Entry<?, ?> e : m.entrySet()) {
			if (!first) {
				print(",").brk(1, 0);
			}
			printEntry(e);
			first = false;
		}
		print("}").end();
		return this;
	}

	/** Print a map entry.
	 * This is printed as
	 * <pre>
	 * key=val
	 * </pre>
	 * if it fits on one line, and as
	 * <pre>
	 * key=
	 *   val
	 * </pre>
	 * otherwise.  This is mainly to prevent key from adding too
	 * much indentation.
	 */
	public DataLayouter<Exc> printEntry(Map.Entry<?, ?> e) throws Exc {
		beginC();
		print(e.getKey());
		print("=").brk(0, 0);
		print(e.getValue());
		end();
		return this;
	}

	// OVERRIDES OF INHERITED METHODS --------------------------------------

	/* The point here is the covariant refinement of the return types
	 * so that it remains possible to say l.beginC().print(...).brk(...)
	 * even for a DataLayouter.  If only Java had a self-type we 
	 * could declare as return type for these mehtods...
	 */
	
	@Override
	public DataLayouter<Exc> begin(boolean consistent, int indent) {
		super.begin(consistent, indent);
		return this;
	}

	@Override
	public DataLayouter<Exc> begin(boolean consistent) {
		super.begin(consistent);
		return this;
	}

	@Override
	public DataLayouter<Exc> beginC() {
		super.beginC();
		return this;
	}

	@Override
	public DataLayouter<Exc> beginC(int indent) {
		super.beginC(indent);
		return this;
	}

	@Override
	public DataLayouter<Exc> beginI() {
		super.beginI();
		return this;
	}

	@Override
	public DataLayouter<Exc> beginI(int indent) {
		super.beginI(indent);
		return this;
	}

	@Override
	public DataLayouter<Exc> brk() throws Exc {
		super.brk();
		return this;
	}

	@Override
	public DataLayouter<Exc> brk(int width, int offset) throws Exc {
		super.brk(width, offset);
		return this;
	}

	@Override
	public DataLayouter<Exc> brk(int width) throws Exc {
		super.brk(width);
		return this;
	}

	@Override
	public DataLayouter<Exc> end() throws Exc {
		super.end();
		return this;
	}

	@Override
	public DataLayouter<Exc> flush() throws Exc {
		super.flush();
		return this;
	}

	@Override
	public DataLayouter<Exc> ind() throws Exc {
		super.ind();
		return this;
	}

	@Override
	public DataLayouter<Exc> ind(int width, int offset) throws Exc {
		super.ind(width, offset);
		return this;
	}

	@Override
	public DataLayouter<Exc> mark(Object o) throws Exc {
		super.mark(o);
		return this;
	}

	@Override
	public DataLayouter<Exc> nl() throws Exc {
		super.nl();
		return this;
	}

	@Override
	public DataLayouter<Exc> pre(String s) throws Exc {
		super.pre(s);
		return this;
	}

	@Override
	public DataLayouter<Exc> print(String s) throws Exc {
		super.print(s);
		return this;
	}
	
}
