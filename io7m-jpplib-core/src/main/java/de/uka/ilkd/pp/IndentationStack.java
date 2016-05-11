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
