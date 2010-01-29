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

package datapp;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import de.uka.ilkd.pp.DataLayouter;
import de.uka.ilkd.pp.PrettyPrintable;

/** An variable-arity tree.
 * A simple representation where each node has a label of type 
 * <code>E</code>, and may have arbitrarily many children.  Trees
 * can be created using the constructor, and modified by adding
 * new children.
 * 
 * @author Martin Giese
 *
 * @param <E> type of (internal and leaf) nodes
 */
public class Tree<E> implements PrettyPrintable {

	private E label;
	private List<Tree<E>> children = new ArrayList<Tree<E>>();
	
	/** Construct a new Tree without children.
	 * There is currently no way to change the label of the node later,
	 * but children can be added from left to right.
	 * 
	 * @param label the label of the single node of the constructed tree
	 */
	public Tree(E label) {
		this.label = label;
	}
	
	/** Answer whether this is a leaf.
	 * This means the same as that this is a node without children.
	 * 
	 * @return <code>true</code> if this is a leaf.
	 */
	public boolean isLeaf() {
		return children.size() == 0;
	}
	
	/** Return the label of this node. */
	E getLabel() {
		return label;
	}
	
	/** Add a new rightmost child. */
	public void addChild(Tree<E> t) {
		children.add(t);
	}
	
	/** Get the children of this node.
	 * This is returned as an unmodifiable list.
	 * 
	 * @return this node's list of children
	 */
	public List<Tree<E>> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	/** Pretty-print the tree rooted at this node.
	 * The layout for trees is 
	 * <pre>
	 * root-label(child1-label, child2-label, ...)
	 * </pre>
	 * if it fits on one line, and
	 * <pre>
	 * root-label(
	 *    child1-label,
	 *    child2-label, 
	 *    ...)
	 * </pre>
	 * otherwise, where the labels are printed using the 
	 * {@link DataLayouter#print(Object)} method of <code>l</code>.
	 * 
	 * @param l the DataLayouter to which the tree will be printed
	 */
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
				//Alternatively: l.print(ch);
				first = false;
			}
			l.print(")");
		}
		l.end();
	}

}
