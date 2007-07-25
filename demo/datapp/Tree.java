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

package datapp;

import java.util.*;

public class Tree<E> implements PrettyPrintable {

	private E label;
	private List<Tree<E>> children = new ArrayList<Tree<E>>();
	
	public Tree(E label) {
		this.label = label;
	}
	
	public boolean isLeaf() {
		return children.size() == 0;
	}
	
	E getLabel() {
		return label;
	}
	
	public void addChild(Tree<E> t) {
		children.add(t);
	}
	
	public List<Tree<E>> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	public <Exc extends Exception> void prettyPrint(DataLayouter<Exc> l) 
	throws Exc {
		l.beginC(3).print(label);
		if (!isLeaf()) {
			l.print("(").brk(0, 0);
			boolean first = true;
			for (Tree<E> ch:children) {
				if (!first) {
					l.print(",").brk(1, 0);
				}
				ch.prettyPrint(l);
				// Alternatively: l.print(ch);
				first = false;
			}
			l.print(")");
		}
		l.end();
	}

}
