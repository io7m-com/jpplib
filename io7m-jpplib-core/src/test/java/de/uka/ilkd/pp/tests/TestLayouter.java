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

package de.uka.ilkd.pp.tests;

import de.uka.ilkd.pp.Backend;
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import junit.framework.TestCase;

/** Unit-Test the {@link Layouter} class. */

public class TestLayouter extends TestCase {

	/** A backend to remember the result in */
	StringBackend narrowBack;
	/** A backend to remember the result in */
	StringBackend wideBack;
	/** A backend to remember the result in */
	StringBackend sixBack;
	/** A layouter which breaks everything */
	MarkingBackend markBack;
	/** A layouter which breaks everything */
	Layouter<NoExceptions> narrow;
	/** A layouter which breaks nothing */
	Layouter<NoExceptions> wide;
	/** A layouter which breaks nothing */
	Layouter<NoExceptions> six;
	/** A layouter which breaks nothing */
	Layouter<NoExceptions> marking;

	int[] marks;
	int markPtr;

	public TestLayouter(String name) {
		super(name);
	}

	public void setUp() {
		narrowBack = new StringBackend(1);
		wideBack   = new StringBackend(10000);
		sixBack   = new StringBackend(6);
		markBack = new MarkingBackend(1);
		narrow = new Layouter<NoExceptions>(narrowBack,2);
		wide   = new Layouter<NoExceptions>(wideBack,2);
		six    = new Layouter<NoExceptions>(sixBack,2);
		marking= new Layouter<NoExceptions>(markBack,2);
		marks = new int[100];
		markPtr = 0;
	}



	class MarkingBackend extends StringBackend 
	implements Backend<NoExceptions>
	{

		public MarkingBackend(int lineWidth) {
			super(lineWidth);
		}

		public void mark(Object o) {
			marks[markPtr++] = count();
		}
	}

	public void testNarrowConsistent() {
		narrow.beginC().print("A").beginC()
		.print("B").brk(1,2)
		.print("C").brk(2,3)
		.print("D").end().print("E").end().close();
		assertEquals("break consistent","AB\n     C\n      DE",
				narrowBack.getString());
	}

	public void testWideConsistent() {
		wide.beginC().print("A").beginC()
		.print("B").brk(1,2)
		.print("C").brk(2,3)
		.print("D").end().print("E").end().close();
		assertEquals("no break consistent","AB C  DE",
				wideBack.getString());
	}

	public void testNarrowInconsistent() {
		narrow.beginC().print("A").beginI()
		.print("B").brk(1,2)
		.print("C").brk(2,3)
		.print("D").end().print("E").end().close();
		assertEquals("break inconsistent","AB\n     C\n      DE",
				narrowBack.getString());
	}

	public void testWideInconsistent() {
		wide.beginC().print("A").beginI()
		.print("B").brk(1,2)
		.print("C").brk(2,3)
		.print("D").end().print("E").end().close();
		assertEquals("no break inconsistent","AB C  DE",
				wideBack.getString());
	}

	public void testSixInconsistent() {
		six.beginC().print("A").beginI()
		.print("B").brk(1,2)
		.print("C").brk(2,3)
		.print("D").end().print("E").end().close();
		assertEquals("some breaks inconsistent","AB C\n      DE",
				sixBack.getString());
	}

	public void testNarrowPre() {
		narrow.beginC().print("[")
		.pre("A\nB\nC").print("]").end().close();
		assertEquals("preformatted","[A\n B\n C]",
				narrowBack.getString());

	}

	public void testWidePre() {
		wide.beginC().print("[")
		.pre("A\nB\nC").print("]").end().close();
		assertEquals("preformatted","[A\n B\n C]",
				wideBack.getString());

	}

	public void testNarrowInd() {
		narrow.beginC().print("A").beginC()
		.ind(1,2).print("B").brk(1,2)
		.print("C").ind(3,4).print("D").brk(2,3)
		.print("E").end().print("F").end().close();
		assertEquals("ind consistent","A    B\n     C D\n      EF",
				narrowBack.getString());
	}

	public void testWideInd() {
		wide.beginC().print("A").beginC()
		.ind(1,2).print("B").brk(1,2)
		.print("C").ind(3,4).print("D").brk(2,3)
		.print("E").end().print("F").end().close();
		assertEquals("ind consistent","A B C   D  EF",
				wideBack.getString());
	}

	public void testIndFromPos() {
		six.print("AB").beginC(1).print("DE").brk(0,0);
		six.print("GH").brk(0,-1);
		six.print("I").end().close();
		assertEquals("indent from current pos",
				"ABDE\n   GH\n  I",
				sixBack.getString());
	}

	public void testIndFromLevel() {
		six.print("AB").beginCInd(1).print("DE").brk(0,0);
		six.print("GH").brk(0,-1);
		six.print("I").end().close();
		assertEquals("indent from current pos",
				"ABDE\n GH\nI",
				sixBack.getString());
	}

	public void testMark() {
		marking.
		beginC().mark(null) 
		.print("A").mark(null) 
		.beginC().mark(null) 
		.print("B").mark(null) 
		.brk(1,2).mark(null) 
		.print("C").mark(null) 
		.brk(2,3).mark(null) 
		.print("D").mark(null) 
		.end().mark(null) 
		.print("E").mark(null) 
		.end().mark(null) 
		.close();
		assertEquals("break consistent","AB\n     C\n      DE",
				markBack.getString());
		assertEquals("number marks",11,markPtr);
		assertEquals("marks pos 1",0,marks[0]);
		assertEquals("marks pos 2",1,marks[1]);
		assertEquals("marks pos 3",1,marks[2]);
		assertEquals("marks pos 4",2,marks[3]);
		assertEquals("marks pos 5",8,marks[4]);
		assertEquals("marks pos 6",9,marks[5]);
		assertEquals("marks pos 7",16,marks[6]);
		assertEquals("marks pos 8",17,marks[7]);
		assertEquals("marks pos 9",17,marks[8]);
		assertEquals("marks pos 10",18,marks[9]);
		assertEquals("marks pos 11",18,marks[10]);
	}
}

