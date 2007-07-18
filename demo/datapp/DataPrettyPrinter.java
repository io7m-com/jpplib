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

public class DataPrettyPrinter {

	public final int DEFAULT_LINE_WIDTH = 80;

	public final int DEFAULT_INDENTATION = 2;

	private Layouter<NoExceptions> out;

	private StringBackend back = null;

	public DataPrettyPrinter() {
		back = new StringBackend(DEFAULT_LINE_WIDTH);
		out = new Layouter<NoExceptions>(back, DEFAULT_INDENTATION);
	}

	public String getString() {
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
	 * @param o
	 *            the object to be pretty printed
	 */
	public void prettyPrint(Object o) {
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

	public void prettyPrintCollection(Collection<?> c) {
		out.print("[").beginC(0);
		boolean first = true;
		for (Object o : c) {
			if (!first) {
				out.print(",").brk(1, 0);
			}
			prettyPrint(o);
			first = false;
		}
		out.print("]").end();
	}

	/** Pretty prints an array og reference or primitive elements.
	 * This is private because it would crash if called on anything
	 * except an array.
	 * 
	 * @param o
	 */
	private void prettyPrintArray(Object o) {
		Object[] boxed = BoxArrays.boxArray(o);
		prettyPrintCollection(Arrays.asList(boxed));
	}
	
	public void prettyPrintMap(Map<?, ?> m) {
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

	public void prettyPrintEntry(Map.Entry<?, ?> e) {
		out.beginC();
		prettyPrint(e.getKey());
		out.print("=").brk(0, 0);
		prettyPrint(e.getValue());
		out.end();
	}

	public void prettyPrintDefault(Object o) {
		out.print(String.valueOf(o));
	}

	public static void main(String[] args) {
		DataPrettyPrinter dpp;

		System.out.println("A short list\n");
		List imsevimse = Arrays.asList(new String[] { "imse", "vimse",
				"spindel" });
		dpp = new DataPrettyPrinter();
		dpp.prettyPrint(imsevimse);
		System.out.println(dpp.getString());

		System.out.println("\nA nested array\n");
		int[][] pas = new int[10][];
		for(int i=0;i<10;i++) {
			pas[i] = new int[i+1];
			pas[i][0] = pas[i][i] = 1;
			for(int j=1;j<i;j++) {
				pas[i][j] = pas[i-1][j] + pas[i-1][j-1];
			}
		}
		dpp = new DataPrettyPrinter();
		dpp.prettyPrint(pas);
		System.out.println(dpp.getString());

		System.out.println("\nThe System environment\n");
		dpp = new DataPrettyPrinter();
		dpp.prettyPrint(System.getenv());
		System.out.println(dpp.getString());

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
		dpp = new DataPrettyPrinter();
		dpp.prettyPrint(l);
		System.out.println(dpp.getString());
	}
}
