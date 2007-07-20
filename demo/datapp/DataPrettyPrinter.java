//This file is part of the Java Pretty Printer Library (JPPlib)
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

package datapp;

import de.uka.ilkd.pp.*;
import java.util.*;

/** Utility class to pretty-print arbitrary objects.
 * There is a static method 
 * {@link #prettyPrint(Object)} which produces a pretty-printed
 * String representation of an arbitrary object, with useful
 * layouts for collections, arrays, and maps.  See the 
 * documentation of that method for details.
 * 
 * @author mgiese
 *
 */
public class DataPrettyPrinter {

	/** The maximum line width */
	public final int DEFAULT_LINE_WIDTH = 80;

	/** The indentation, in particular for Map entries. */
	public final int DEFAULT_INDENTATION = 2;

	/** The layouter used for pretty printing. */
	private Layouter<NoExceptions> out;

	/** The backend where the output can be collected */
	private StringBackend back = null;

	/** Construct a DataPrettyPrinter with given line width and indentation.*/
	private DataPrettyPrinter() {
		back = new StringBackend(DEFAULT_LINE_WIDTH);
		out = new Layouter<NoExceptions>(back, DEFAULT_INDENTATION);
	}

	/** Finalize printing. */
	private void close() {
		out.close();
	}
	
	/** Return the string printed to the backend. */
	private String getString() {
		return back.getString();
	}

	/**
	 * Pretty print an object according to its type. Arrays, collections and
	 * maps recieve special handling. The format for collections and maps is as
	 * described in the Collection Framework, but with possible insertion of
	 * line breaks and indentation.  Arrays are printed like lists.
	 * 
	 * <p>
	 * Objects that implement the {@link PrettyPrintable} interface are printed
	 * using their prettyPrint method. Any other objects are printed using their
	 * standard String representation.
	 * 
	 * <h4>Layout for container types</h4>
	 * A collection ({@link java.util.List} or {@link java.util.Set}) is printed as
	 * <pre>
	 * [xxx, yyy, zzz]
	 * </pre>
	 * if it fits on one line, and as
	 * <pre>
	 * [xxx,
	 *  yyy,
	 *  zzz]
	 * </pre>
	 * otherwise.  The same format is used for arrays.
	 * 
	 * <p>A {@link java.util.Map} is printed as
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
	 * key-value pairs will also be spread over two lines, e.g.
	 * <pre>
	 * {key1=val1,
	 *  key2=
	 *    [long,
	 *     long,
	 *     value],
	 *  key3=val3]
	 * </pre>
	 * This is to prevent long keys from adding too much indentation.
	 *
	 * <p> 
	 * 
	 * @param o
	 *            the object to be pretty printed
	 * @return the pretty-printed String representation of <code>o</code>
	 */
	public static String prettyPrint(Object o) {
		DataPrettyPrinter dpp = new DataPrettyPrinter();
		dpp.prettyPrintObject(o);
		dpp.close();
		return dpp.getString();
	}
	
	/** Print <code>o</code> to this object's Layouter.
	 * Figures out the type of <code>o</code> and delgates
	 * to one of the specialized printing methods.
	 * 
	 * @param o
	 *            the object to be pretty printed
	 */
	private void prettyPrintObject(Object o) {
		if (o instanceof Collection<?>) {
			prettyPrintCollection((Collection<?>) o);
		} else if (o instanceof Map<?, ?>) {
			prettyPrintMap((Map<?, ?>) o);
		} else if (o.getClass().isArray()) {
			prettyPrintArray(o);		
		} else if (o instanceof PrettyPrintable) {
			((PrettyPrintable) o).prettyPrint(out);
		} else {
			prettyPrintDefault(o);
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
	private void prettyPrintCollection(Collection<?> c) {
		out.print("[").beginC(0);
		boolean first = true;
		for (Object o : c) {
			if (!first) {
				out.print(",").brk(1, 0);
			}
			prettyPrintObject(o);
			first = false;
		}
		out.print("]").end();
	}

	/** Pretty prints an array og reference or primitive elements.
	 * The format is the same as for collections.
	 * 
	 * @param o an object, has to be an array!
	 */
	private void prettyPrintArray(Object o) {
		Object[] boxed = BoxArrays.boxArray(o);
		prettyPrintCollection(Arrays.asList(boxed));
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
	 * key-value pairs will also be spread over two lines, e.g.
	 * <pre>
	 * {key1=val1,
	 *  key2=
	 *    [long,
	 *     long,
	 *     value],
	 *  key3=val3]
	 * </pre>
	 */
	private void prettyPrintMap(Map<?, ?> m) {
		out.print("{").beginC(0);
		boolean first = true;
		for (Map.Entry<?, ?> e : m.entrySet()) {
			if (!first) {
				out.print(",").brk(1, 0);
			}
			prettyPrintEntry(e);
			first = false;
		}
		out.print("}").end();
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
	 * otherwise.  This is mailny to prevent key from adding too
	 * much indentation.
	 */
	private void prettyPrintEntry(Map.Entry<?, ?> e) {
		out.beginC();
		prettyPrintObject(e.getKey());
		out.print("=").brk(0, 0);
		prettyPrintObject(e.getValue());
		out.end();
	}

	/** Pretty print a value according to its standard
	 * string representation.
	 * 
	 * @param o the object to print
	 */
	private void prettyPrintDefault(Object o) {
		out.print(String.valueOf(o));
	}

	public static void main(String[] args) {
		System.out.println("A short list\n");
		List imsevimse = Arrays.asList(new String[] { "imse", "vimse",
				"spindel" });
		System.out.println(prettyPrint(imsevimse));

		System.out.println("\nA nested array\n");
		int[][] pas = new int[10][];
		for(int i=0;i<10;i++) {
			pas[i] = new int[i+1];
			pas[i][0] = pas[i][i] = 1;
			for(int j=1;j<i;j++) {
				pas[i][j] = pas[i-1][j] + pas[i-1][j-1];
			}
		}
		System.out.println(prettyPrint(pas));

		System.out.println("\nThe System environment\n");
		System.out.println(prettyPrint(System.getenv()));

		System.out.println("\nA list of maps from Strings to stuff");
		List<Map<String, Object>> l = new ArrayList<Map<String, Object>>();
		for (int n = 1; n <= 15; n++) {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("number", n);
			m.put("square", n * n);
			List<Integer> mult = new ArrayList<Integer>();
			for (int i = 1; i <= 11; i++) {
				mult.add(n * i);
			}
			m.put("some multiples", mult);
			l.add(m);
		}
		System.out.println(prettyPrint(l));
	}
}
