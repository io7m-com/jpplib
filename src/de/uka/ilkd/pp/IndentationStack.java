// This file is part of the Java™ Pretty Printer Library (JPPlib)
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

import java.util.ArrayList;

class IndentationStack {

	static enum BreakDecision {
		FITS,INCONSISTENT,CONSISTENT;
		
		static BreakDecision fromBreakConsistency(Layouter.BreakConsistency c) {
			return c==Layouter.BreakConsistency.CONSISTENT?
					CONSISTENT:INCONSISTENT;
		}
	}
	
	private ArrayList<Entry> stack = new ArrayList<Entry>(10);
	
	/** Return whether the stack is empty. */
	boolean isEmpty() {
		return stack.isEmpty();
	}

	/** Pop one element from the margin stack. */
	void pop() {
		try {
			stack.remove(stack.size() - 1);
		} catch (IndexOutOfBoundsException e) {
			throw new UnbalancedBlocksException();
		}
	}

	/** push one element,consisting of margin and 
	 * break decision onto the margin stack. */
	void push(int n, BreakDecision dec) {
		stack.add(new Entry(n,dec));
	}

	/** return the topmost element of the margin stack without popping it. */
	private Entry top() {
		try {
			return stack.get(stack.size() - 1);
		} catch (IndexOutOfBoundsException e) {
			throw new UnbalancedBlocksException();
		}
	}

	/** return the margin of the top element of the margin stack. */
	int topIndentation() {
		return top().getIndentation();
	}


	/** return the break type flags of the top element of the margin stack. */
	BreakDecision topDecision() {
		return top().getDecision();
	}

	boolean topInconsistent() {
		return topDecision() == BreakDecision.INCONSISTENT;
	}

	boolean topConsistent() {
		return topDecision() == BreakDecision.CONSISTENT;
	}

	boolean topFits() {
		return topDecision() == BreakDecision.FITS;
	}

	private class Entry {
		private final int indentation;
		private final BreakDecision decision;
		
		Entry(final int indentation, final BreakDecision decision) {
			super();
			this.indentation = indentation;
			this.decision = decision;
		}
		/**
		 * @return the decision
		 */
		BreakDecision getDecision() {
			return decision;
		}
		/**
		 * @return the indentation
		 */
		int getIndentation() {
			return indentation;
		}		
	}
}
