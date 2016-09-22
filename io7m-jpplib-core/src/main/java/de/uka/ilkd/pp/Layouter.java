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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Pretty-print information formatted using line breaks and indentation. For
 * instance, this class can be used to print
 * 
 * <pre>
 * while (i &gt; 0) {
 *   i--;
 *   j++;
 * }
 * </pre>
 * 
 * instead of
 * 
 * <pre>
 * while (i &gt; 0) { i
 * --; j++;}
 * </pre>
 * 
 * if a maximum line width of 15 characters is chosen.
 * 
 * <P>
 * The formatted output is directed to a <em>backend</em> which might write it
 * to an I/O stream, append it to the text of a GUI componenet or store it in a
 * string. The {@link Backend} interface encapsulates the concept of backend.
 * Apart from handling the output, the backend is also asked for the available
 * line width and for the amount of space needed to print a string. This makes
 * it possible to include e.g. HTML markup in the output which does not take up
 * any space. There are two convenience implementations {@link WriterBackend}
 * and {@link StringBackend}, which write the output to a
 * {@link java.io.Writer}, resp. a {@link java.lang.String}.
 * 
 * <P>
 * The layouter internally keeps track of a current <em>indentation
 * level</em>.
 * Think of nicely indented Java source code. Then the indentation level at any
 * point is the number of blank columns to be inserted at the begining of the
 * next line if you inserted a line break. To increase the indentation level of
 * parts of the text, the input to the layouter is separated into
 * <em>blocks</em>. The indentation level changes when a block is begun, and
 * it is reset to its previous value when a block is ended. Of course, blocks
 * maybe nested.
 * 
 * In order to break text among several lines, the layouter needs to be told
 * where line breaks are allowed. A <em>break</em> is a position in the text
 * where there is either a line break (with appropriate indentation) or a number
 * of spaces, if enough material fits in one line. In order to handle the
 * indentation level properly, breaks should only occur inside blocks. There are
 * in fact two kinds of blocks: <em>consistent</em> and <em>inconsistent</em>
 * ones. In a consistent block, lines are broken either at all or at none of the
 * breaks. In an inconsistent block, as much material as possible is put on one
 * line before it is broken.
 * 
 * <P>
 * Consider the program above. It should be printed either as
 * 
 * <pre>
 * while (i &gt; 0) { i--; j++; }
 * </pre>
 * 
 * or, if there is not enough space on the line, as
 * 
 * <pre>
 * while (i &gt; 0) {
 *   i--;
 *   j++;
 * }
 * </pre>
 * 
 * Given a Layouter object <code>l</code>, we could say:
 * 
 * <pre>
 * l.beginC(2).print("while (i&gt;0) {").brk(1,0)
 *  .print("i--;").brk(1,0)
 *  .print("j++;").brk(1,-2)
 *  .print("}").end();
 * </pre>
 * 
 * The call to {@link #beginC(int)} starts a consistent block, advancing the
 * indentation level by 2, relative to the current position in the line.
 * The {@link #print(String)} methods gives some actual
 * text to be output. The call to {@link #brk(int,int)} inserts a break. The
 * first argument means that one space should be printed at this position if the
 * line is <em>not</em> broken. The second argument is an offset to be added
 * to the indentation level for the next line, if the line <em>is</em> broken.
 * The effect of this parameter can be seen in the call <code>brk(1,-2)</code>.
 * The offset of <code>-2</code> outdents the last line by 2 positions, which
 * aligns the closing brace with the <code>while</code>.
 * 
 * <p>
 * If the lines in a block are broken, one sometimes wants to insert spaces up
 * to the current indentation level at a certain position without allowing a
 * line break there. This can be done using the {@link #ind(int,int)} method.
 * For instance, one wants to output either
 * 
 * <pre>
 *   ...[Good and Bad and Ugly]...
 * </pre>
 * 
 * or
 * 
 * <pre>
 *   ...[    Good
 *       and Bad
 *       and Ugly]...
 * </pre>
 * 
 * Note the four spaces required before <code>Good</code>. We do this by
 * opening a block which sets the indentation level to the column where the
 * <code>G</code> ends up and outdenting the lines with the <code>and</code>:
 * 
 * <pre>
 * l.print(&quot;...[&quot;).beginC(4).ind(0, 0).print(&quot;Good&quot;).brk(1, -4).print(&quot;and &quot;)
 * 		.print(&quot;Bad&quot;).brk(1, -4).print(&quot;and &quot;).print(&quot;Ugly&quot;).end()
 * 		.print(&quot;]...&quot;);
 * </pre>
 * 
 * Again, the first argument to {@link #ind(int,int)} is a number of spaces to
 * print if the block we are in is printed on one line. The second argument is
 * an offset to be added to the current indentation level to determine the
 * column to which we should skip.
 * 
 * <p>
 * When all text has been sent to a Layouter and all blocks have been ended, the
 * {@link #close()} method should be closed. This sends all pending output to
 * the backend and invokes the {@link Backend#close()} method, which usually
 * closes I/O streams, etc.
 * 
 * <P>The indentation level of a new block can be increased either relative to
 * the current position on the line, or relative to the surrounding block's
 * indentation level.  Using the current position is useful e.g. to produce output 
 * like
 * <pre>
 * if x&gt;=2 then x := x-2
 *         else x := x+2
 * </pre>
 * where beginning a block in front of the <code>then</code> can set the 
 * indentation level to the current position in the line without knowing how
 * much space the condition took up.  Indenting by 3 relative to the surrounding
 * block's indentation level would produce
 * <pre>
 * if x&gt;=2 then x := x-2
 *    else x := x+2
 * </pre>
 * no matter where in the first line the block was opened.
 * <p>
 * Some applications need to keep track of where certain parts of the input text
 * end up in the output. For this purpose, the Layouter class provides the
 * {@link #mark(Object)} method.
 * 
 * <P>
 * The public methods of this class may be divided into two categories: A small
 * number of <em>primitive</em> methods, as described above, and a host of
 * <em>convenience</em> methods which simplify calling the primitive ones for
 * often-used arguments. For instance, a call to {@link #beginC()} is shorthand
 * for <code>beginC(ind)</code>, where <code>ind</code> is the default
 * indentation selected when the Layouter was constructed.
 * 
 * <P>
 * Most of the methods can throw an {@link UnbalancedBlocksException}, which
 * indicates that the sequence of method calls was illegal, i.e. more blocks
 * were ended than begun, the Layouter is closed before all blocks are ended, a
 * break occurs outside of any block, etc.
 * 
 * <p>
 * The backend might throw exceptions of the type indicated by the type
 * parameter {@code Exc}. Such exceptions get passed through to the caller of
 * the Layouter. Note that since text usually gets buffered before it is sent to
 * the backend, exceptions thrown by calls to Layouter methods might not be
 * caused directly by that method call but by an earlier one getting forwarded
 * to the backend.
 * 
 * <p>
 * The algorithm used is essentially the classical one from Derek C. Oppen:
 * <i>Prettyprinting</i>, TOPLAS volume 2 number 4, ACM, 1980, pp. 465-483,
 * with some minor extensions. It has the property that if the input contains
 * enough actual text, i.e. not just arbitrarily long sequences of calls to
 * <code>beginC</code>, <code>mark</code>, etc., then pretty-printing uses
 * constant space, and time linear in the size of the input. In fact, output
 * will begin before the whole input has been given, so this class can be used
 * to pretty-print a stream of data.
 * 
 * @param <Exc>
 *            The type of exceptions that might be thrown by the backend.
 * 
 * @author Martin Giese
 * @see Backend
 */

/*
 * Implementation note: The name of this class is actually a lie. What this
 * class does is calculate the space needed by blocks and portions of blocks
 * between breaks if they are to be printed in a single line. The actual
 * laying-out, that is choosing whether to break lines or not is done by a
 * Printer object, which in turn sends its output to the Backend.
 * 
 */

public class Layouter<Exc extends Exception> {

	private static final Logger LOG;

	static {
		LOG = LoggerFactory.getLogger(Layouter.class);
	}

	private boolean finished;

	/** An enum type to distinguish consistent and inconsistent blocks. */
	public static enum BreakConsistency {CONSISTENT,INCONSISTENT}
	
	/** An enum type to distinguish indentation relative to the current position
	 * or relative to the surrounding block's indentation level */
	public static enum IndentationBase {FROM_POS,FROM_IND}
	
	/** The backend */
	private Backend<Exc> back;

	/** The Printer used for output. */
	private Printer<Exc> out;

	/** The list of scanned tokens not yet output. */
	private List<StreamToken> stream = new java.util.LinkedList<StreamToken>();

	/**
	 * A stack of <code>OpenBlockToken</code>s and <code>BreakToken</code>s
	 * in <code>stream</code>, waiting for their size to be determined.
	 */
	private List<StreamToken> delimStack = new java.util.LinkedList<StreamToken>();

	/*
	 * Some Invariants:
	 * 
	 * delimStack.isEmpty() implies stream.isEmpty()
	 * 
	 * Any OpenBlockToken in stream is also on the demlimStack. The latest
	 * BreakToken of any open block in the stream is also on the delim stack.
	 * 
	 */

	/**
	 * Total size of received strings and blanks, if they were printed in one
	 * line. The difference of this between two states says how much space would
	 * be needed to print the intervening stuff without line breaks.
	 */
	private int totalSize = 0;

	/**
	 * Total size of strings and blanks sent to the Printer <code>out</code>.
	 * Subtract this from <code>totalOutput</code> and you get the space
	 * needed to print what is still buffered in <code>stream</code>
	 */
	private int totalOutput = 0;

	/**
	 * The size assigned to things which are guaranteed not to fit on a line.
	 * For good measure, this is intitialized to twice the line width by the
	 * constructors.
	 */
	private int largeSize;

	/** A default indentation value used for blocks. */
	private int defaultInd;

	// PRIMITIVE CONSTRUCTOR -------------------------------------------

	/**
	 * Construts a newly allocated Layouter which will send output to the given
	 * {@link Backend} and has the given default indentation.
	 * 
	 * @param back
	 *            the Backend
	 * @param indentation
	 *            the default indentation
	 * 
	 */

	public Layouter(Backend<Exc> back, int indentation) {
		this.back = back;
		out = new Printer<Exc>(back);
		largeSize = 2 * back.lineWidth();
		this.defaultInd = indentation;
	}

	// STATIC FACTORY METHODS ----------------------------------------

	/** = 80 : The line width for some of the convenience factories. */
	public static final int DEFAULT_LINE_WIDTH = 80;

	/**
	 * = 2 : The default indentation for some of the convenience constructors
	 */
	public static final int DEFAULT_INDENTATION = 2;

	/**
	 * Factory method for a Layouter with a {@link WriterBackend}. The line
	 * width is taken to be {@link #DEFAULT_LINE_WIDTH}, and the default
	 * indentation {@link #DEFAULT_INDENTATION}.
	 * 
	 * @param writer
	 *            the {@link java.io.Writer} the Backend is going to use
	 */
	public static Layouter<IOException> getWriterLayouter(java.io.Writer writer) {
		return getWriterLayouter(writer, DEFAULT_LINE_WIDTH);
	}

	/**
	 * Factory method for a Layouter with a {@link WriterBackend}. The default
	 * indentation is taken from {@link #DEFAULT_INDENTATION}.
	 * 
	 * @param writer
	 *            the {@link java.io.Writer} the Backend is going to use
	 * @param lineWidth
	 *            the maximum lineWidth the Backend is going to use
	 */
	public static Layouter<IOException> getWriterLayouter(
			java.io.Writer writer, int lineWidth) {
		return getWriterLayouter(writer, lineWidth, DEFAULT_INDENTATION);
	}

	/**
	 * Factory method for a Layouter with a {@link WriterBackend}.
	 * 
	 * @param writer
	 *            the {@link java.io.Writer} the Backend is going to use
	 * @param lineWidth
	 *            the maximum lineWidth the Backend is going to use
	 * @param indentation
	 *            the default indentation
	 */
	public static Layouter<IOException> getWriterLayouter(
			java.io.Writer writer, int lineWidth, int indentation) {
		return new Layouter<IOException>(new WriterBackend(writer, lineWidth),
				indentation);
	}

	/**
	 * Factory method for a Layouter with a {@link StringBackend}. The line
	 * width is taken to be {@link #DEFAULT_LINE_WIDTH}, and the default
	 * indentation {@link #DEFAULT_INDENTATION}.
	 * 
	 * @param sb
	 *            the {@link java.lang.StringBuilder} the Backend is going to
	 *            use
	 */
	public static Layouter<NoExceptions> getStringLayouter(StringBuilder sb) {
		return getStringLayouter(sb, DEFAULT_LINE_WIDTH);
	}

	/**
	 * Factory method for a Layouter with a {@link StringBackend}. The default
	 * indentation is taken from {@link #DEFAULT_INDENTATION}.
	 * 
	 * @param sb
	 *            the {@link StringBuilder} the Backend is going to use
	 * @param lineWidth
	 *            the maximum lineWidth the Backend is going to use
	 */
	public static Layouter<NoExceptions> getStringLayouter(StringBuilder sb,
			int lineWidth) {
		return getStringLayouter(sb, lineWidth, DEFAULT_INDENTATION);
	}

	/**
	 * Factory method for a Layouter with a {@link StringBackend}.
	 * 
	 * @param sb
	 *            the {@link StringBuilder} the Backend is going to use
	 * @param lineWidth
	 *            the maximum lineWidth the Backend is going to use
	 * @param indentation
	 *            the default indentation
	 */
	public static Layouter<NoExceptions> getStringLayouter(StringBuilder sb,
			int lineWidth, int indentation) {
		return new Layouter<NoExceptions>(new StringBackend(sb, lineWidth),
				indentation);
	}

	// PROPERTY GETTERS ------------------------------------

	/**
	 * Gets default indentation for this block
	 *
	 * @return default indentation
	 */
	public int getDefaultIndentation() {
		return defaultInd;
	}

	// PRIMITIVE STREAM OPERATIONS ------------------------------------

	/**
	 * Output text material. The string <code>s</code> should not contain
	 * newline characters. If you have a string with newline characters, and
	 * want to retain its formatting, consider using the {@link #pre(String s)}
	 * method. The Layouter will not insert any line breaks in such a string.
	 * 
	 * @param s
	 *            the String to print.
	 * @return this
	 */
	public Layouter<Exc> print(String s) throws Exc {
		LOG.trace("print: {}", s);

		checkNotFinished();

		if (delimStack.isEmpty()) {
			out.print(s);
			totalSize += back.measure(s);
			totalOutput += back.measure(s);
		} else {
			enqueue(new StringToken(s));
			totalSize += back.measure(s);

			while (totalSize - totalOutput > out.space()
					&& !delimStack.isEmpty()) {
				popBottom().setInfiniteSize();
				advanceLeft();
			}
		}
		return this;
	}

	/**
	 * Begin a block. Parameter <code>cons</code> indicates whether this is a
	 * consistent block or an inconsistent one. In consistent blocks, breaks are
	 * either all broken or none is broken. The indentation level is increased
	 * by <code>indent</code>, either relative to the current position,
	 * or relative to the surrounding block's indentation level, depending on
	 * the parameter <code>indBase</code>.
	 * 
	 * @param cons
	 *            <code>true</code> for consistent block
	 * @param indBase
	 *            increment relative to current pos, not indentation
	 * @param indent
	 *            increment to indentation level
	 * @return this
	 */
	public Layouter<Exc> begin(BreakConsistency cons, 
								 IndentationBase indBase, 
								 int indent) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("begin: {} {} {}", cons, indBase, Integer.valueOf(indent));
		}

		checkNotFinished();

		StreamToken t = new OpenBlockToken(cons, indBase, indent);
		enqueue(t);
		push(t);
		return this;
	}

	private void checkNotFinished()
	{
		if (finished) {
			throw new IllegalStateException("Layouter is already finished");
		}
	}

	/**
	 * Ends the innermost block.
	 * 
	 * @return this
	 */

	public Layouter<Exc> end() throws Exc {
		LOG.trace("end");

		checkNotFinished();

		if (delimStack.isEmpty()) {
			/* then stream is also empty, so output */
			out.closeBlock();
		} else {
			enqueue(new CloseBlockToken());

			StreamToken topDelim = pop();
			topDelim.setEnd();
			if (topDelim.isBreakToken() && !delimStack.isEmpty()) {
				/* This must be the matching OpenBlockToken */
				StreamToken topOpen = pop();
				topOpen.setEnd();
			}

			if (delimStack.isEmpty()) {
				/* preserve invariant */
				advanceLeft();
			}
		}
		return this;
	}

	/**
	 * Print a break. This will print <code>width</code> spaces if the line is
	 * <em>not</em> broken at this point. If it <em>is</em> broken,
	 * indentation is added to the current indentation level, plus the value of
	 * <code>offset</code>.
	 * 
	 * @param width
	 *            space to insert if not broken
	 * @param offset
	 *            offset relative to current indentation level
	 * @return this
	 */
	public Layouter<Exc> brk(int width, int offset) throws Exc {
		if (LOG.isTraceEnabled()) {
			LOG.trace("brk: {} {}", Integer.valueOf(width), Integer.valueOf(offset));
		}

		checkNotFinished();

		if (!delimStack.isEmpty()) {
			StreamToken s = top();
			if (s.isBreakToken()) {
				pop();
				s.setEnd();
			}
		}

		StreamToken t = new BreakToken(width, offset);
		enqueue(t);
		push(t);
		totalSize += width;
		return this;
	}

	/**
	 * Indent relative to the indentation level if surrounding block is broken. If
	 * the surrounding block fits on one line, insert <code>width</code>
	 * spaces. Otherwise, indent to the current indentation level, plus
	 * <code>offset</code>, unless that position has already been exceeded on
	 * the current line. If that is the case, nothing is printed. No line break
	 * is possible at this point.
	 * 
	 * @param width
	 *            space to insert if not broken
	 * @param offset
	 *            offset relative to current indentation level
	 * @return this
	 */
	public Layouter<Exc> ind(int width, int offset) throws Exc {
		if (LOG.isTraceEnabled()) {
			LOG.trace("ind: {} {}", Integer.valueOf(width), Integer.valueOf(offset));
		}

		checkNotFinished();

		if (delimStack.isEmpty()) {
			out.indent(width, offset);
			totalSize += width;
			totalOutput += width;
		} else {
			enqueue(new IndentationToken(width, offset));
			totalSize += width;
		}
		return this;
	}

	/**
	 * This leads to a call of the {@link Backend#mark(Object)} method of the
	 * backend, when the material preceding the call to <code>mark</code> has
	 * been printed to the backend, including any inserted line breaks and
	 * indentation. The {@link Object} argument to <code>mark</code> is passed
	 * through unchanged to the backend and may be used by the application to
	 * pass information about the purpose of the mark.
	 * 
	 * @param o
	 *            an object to be passed through to the backend.
	 * @return this
	 * 
	 */
	public Layouter<Exc> mark(Object o) throws Exc {
		if (LOG.isTraceEnabled()) {
			LOG.trace("mark: {}", o);
		}

		checkNotFinished();

		if (delimStack.isEmpty()) {
			out.mark(o);
		} else {
			enqueue(new MarkToken(o));
		}
		return this;
	}

	/**
	 * Output any information currently kept in buffers. This is essentially
	 * passed on to the backend. Note that material in blocks begun but not
	 * ended cannot be forced to the output by this method. Finish all blocks
	 * and call <code>flush</code> or {@link #close()} then.
	 * 
	 * @return this
	 */
	public Layouter<Exc> flush() throws Exc {
		if (LOG.isTraceEnabled()) {
			LOG.trace("flush");
		}

		checkNotFinished();

		out.flush();
		return this;
	}

	/**
	 * @see #finish()
	 * @return {@code true} if the Layouter has finished
	 * @since 0.7.1
   */

	public boolean isFinished()
	{
		return this.finished;
	}

	/**
	 * Finish (if not already finished) and lose the Layouter. No more methods
	 * should be called after this. All
	 * blocks begun must have been ended by this point. Any pending material is
	 * written to the backend, before the {@link Backend#close()} method of the
	 * backend is called, which closes any open I/O streams, etc.
	 *
	 * @see #finish()
	 */
	public void close() throws Exc {
		LOG.trace("close");

		if (!finished) {
			finish();
		}

		out.close();
	}

	/**
	 * Finish the Layouter. No more methods should be called after this. All
	 * blocks begun must have been ended by this point. Any pending material is
	 * written to the backend.
	 *
	 * @since 0.7.1
   */

	public void finish()
		throws Exc
	{
		try {
			checkNotFinished();

			if (!delimStack.isEmpty()) {
				throw new UnbalancedBlocksException();
			} else {
				advanceLeft();
			}
		} finally {
			this.finished = true;
		}
	}

	// CONVENIENCE STREAM OPERATIONS ---------------------------------

	/**
	 * Begin a block. If <code>consistent</code> is set, breaks are either all
	 * broken or all not broken. The indentation level is increased by
	 * <code>indent</code>, relative to the current position.
	 * 
	 * @deprecated use {@link #begin(de.uka.ilkd.pp.Layouter.BreakConsistency, de.uka.ilkd.pp.Layouter.IndentationBase, int)}
	 * 
	 * @param consistent
	 *            <code>true</code> for consistent block
	 * @param indent
	 *            increment to indentation level
	 * @return this
	 */
	public Layouter<Exc> begin(boolean consistent, int indent) {
		if (LOG.isTraceEnabled()) {
			LOG.trace(
				"begin: {} {}",
				Boolean.valueOf(consistent),
				Integer.valueOf(indent));
		}

		return begin(consistent?BreakConsistency.CONSISTENT:BreakConsistency.INCONSISTENT, 
				IndentationBase.FROM_POS, indent);
	}

	/**
	 * Begin an inconsistent block. Increment the indentation level by this
	 * layouter's default indentation, relative to the current position.
	 * 
	 * @return this
	 */
	public Layouter<Exc> beginI() {
		return begin(BreakConsistency.INCONSISTENT,  IndentationBase.FROM_POS, defaultInd);
	}

	/**
	 * Begin a consistent block. Increment the indentation level by this
	 * layouter's default indentation, relative to the current position.
	 * 
	 * @return this
	 */
	public Layouter<Exc> beginC() {
		return begin(BreakConsistency.CONSISTENT, IndentationBase.FROM_POS, defaultInd);
	}

	/**
	 * Begin an inconsistent block. Add <code>indent</code> to the indentation
	 * level, relative to the current position.
	 * 
	 * @param indent
	 *            the indentation for this block
	 * @return this
	 */
	public Layouter<Exc> beginI(int indent) {
		return begin(BreakConsistency.INCONSISTENT,  IndentationBase.FROM_POS, indent);
	}

	/**
	 * Begin a consistent block. Add <code>indent</code> to the indentation
	 * level, relative to the current position.
	 * 
	 * @param indent
	 *            the indentation for this block
	 * @return this
	 */
	public Layouter<Exc> beginC(int indent) {
		return begin(BreakConsistency.CONSISTENT, IndentationBase.FROM_POS, indent);
	}

	/**
	 * Begin a block with default indentation. Add this Layouter's default
	 * indentation to the indentation level, relative to the current position.
 	 *
	 * @deprecated use {@link #begin(de.uka.ilkd.pp.Layouter.BreakConsistency, de.uka.ilkd.pp.Layouter.IndentationBase, int)}
	 * 
	 * @param consistent
	 *            <code>true</code> for consistent block
	 * @return this
	 */
	public Layouter<Exc> begin(boolean consistent) {
		return begin(consistent,defaultInd);
	}

	/**
	 * Begin an inconsistent block. Increment the indentation level by this
	 * layouter's default indentation, relative to the surrounding block's
	 * indentation level.
	 * 
	 * @return this
	 */
	public Layouter<Exc> beginIInd() {
		return begin(BreakConsistency.INCONSISTENT, IndentationBase.FROM_IND, defaultInd);
	}

	/**
	 * Begin a consistent block. Increment the indentation level by this
	 * layouter's default indentation, relative to the surrounding block's
	 * indentation level.
	 * 
	 * @return this
	 */
	public Layouter<Exc> beginCInd() {
		return begin(BreakConsistency.CONSISTENT, IndentationBase.FROM_IND, defaultInd);
	}

	/**
	 * Begin an inconsistent block. Increment the indentation level by
	 * <code>indent</code>, relative to the surrounding block's indentation
	 * level.
	 * 
	 * @param indent
	 *            the indentation for this block
	 * @return this
	 */
	public Layouter<Exc> beginIInd(int indent) {
		return begin(BreakConsistency.INCONSISTENT, IndentationBase.FROM_IND, indent);
	}

	/**
	 * Begin a consistent block. Increment the indentation level by
	 * <code>indent</code>, relative to the surrounding block's indentation
	 * level.
	 * 
	 * @param indent
	 *            the indentation for this block
	 * @return this
	 */
	public Layouter<Exc> beginCInd(int indent) {
		return begin(BreakConsistency.CONSISTENT, IndentationBase.FROM_IND, indent);
	}


	/**
	 * Print a break with zero offset.
	 * 
	 * @param width
	 *            space to insert if not broken
	 * @return this
	 */
	public Layouter<Exc> brk(int width) throws Exc {
		return brk(width, 0);
	}

	/**
	 * Print a break with zero offset and width one.
	 * 
	 * @return this
	 */
	public Layouter<Exc> brk() throws Exc {
		return brk(1);
	}

	/**
	 * Print a break with zero offset and large width. As the large number of
	 * spaces will never fit into one line, this amounts to a forced line break.
	 * 
	 * @return this
	 */
	public Layouter<Exc> nl() throws Exc {
		return brk(largeSize);
	}

	/**
	 * Indent with zero offset and zero width. Just indents to the current
	 * indentation level if the block is broken, and prints nothing otherwise.
	 * 
	 * @return this
	 */
	public Layouter<Exc> ind() throws Exc {
		return this.ind(0, 0);
	}

	/**
	 * Layout prefromated text. This amounts to a (consistent) block with
	 * indentation 0, where each line of <code>s</code> (separated by \n) gets
	 * printed as a string and newlines become forced breaks.
	 * 
	 * @param s
	 *            the pre-formatted string
	 * @return this
	 */
	public Layouter<Exc> pre(String s) throws Exc {
		if (LOG.isTraceEnabled()) {
			LOG.trace("pre: {}", s);
		}

		StringTokenizer st = new StringTokenizer(s, "\n", true);
		beginC(0);
		while (st.hasMoreTokens()) {
			String line = st.nextToken();
			if ("\n".equals(line)) {
				nl();
			} else {
				print(line);
			}
		}
		end();

		return this;
	}

	// PRIVATE METHODS -----------------------------------------------

	/* Delimiter Stack handling */

	/** Push an OpenBlockToken or BreakToken onto the delimStack */
	private void push(StreamToken t) {
		delimStack.add(t);
	}

	/** Pop the topmost Token from the delimStack */
	private StreamToken pop() {
		try {
			return (StreamToken) (delimStack.remove(delimStack.size() - 1));
		} catch (IndexOutOfBoundsException e) {
			throw new UnbalancedBlocksException();
		}
	}

	/**
	 * Remove and return the token from the <em>bottom</em> of the delimStack
	 */
	private StreamToken popBottom() {
		try {
			return (StreamToken) (delimStack.remove(0));
		} catch (IndexOutOfBoundsException e) {
			throw new UnbalancedBlocksException();
		}
	}

	/** Return the top of the delimStack, without popping it. */
	private StreamToken top() {
		try {
			return (StreamToken) delimStack.get(delimStack.size() - 1);
		} catch (IndexOutOfBoundsException e) {
			throw new UnbalancedBlocksException();
		}
	}

	/* stream handling */

	/** Put a StreamToken into the stream (at the end). */
	private void enqueue(StreamToken t) {
		stream.add(t);
	}

	/**
	 * Send tokens from <code>stream<code> to <code>out</code> as long
	 * as there are tokens left and their size is known.
	 */
	private void advanceLeft() throws Exc {
		StreamToken t;
		while (!stream.isEmpty()
				&& ((t = (StreamToken) stream.get(0)).followingSizeKnown())) {
			t.print();
			stream.remove(0);
			totalOutput += t.size();
		}
	}

	// STREAM TOKEN CLASSES -----------------------------------------

	/**
	 * A stream token.
	 */
	private abstract class StreamToken {
		/** Send this token to the Printer {@link #out}. */
		abstract void print() throws Exc;

		/** Return the size of this token if the block is not broken. */
		abstract int size();

		/**
		 * Return the `section' size. For an OpenBlockToken, this is the size of
		 * the whole block, if it is not broken. For a BreakToken, it is the
		 * size of the material up to the next corresponding BreakToken or
		 * CloseBlockToken. Otherwise it is the same as size(). This might only
		 * be known after several more tokens have been read. If the value is
		 * guaranteed to be larger than what fits on a line, some large value
		 * might be returned instead of the precise size.
		 */
		
		/* This is actually called only in classes under SizeCalculatingToken, which
		 * overrides it.  But it's nicer to think of it in conjunction with 
		 * followingSizeKnown() below.*/
		@SuppressWarnings("unused")
		int followingSize() {
			return size();
		}

		/**
		 * Returns whether the followingSize is already known. That is the case
		 * if either a corresponding next BreakToken or CloseBlockToken has been
		 * encountered, or if the material is known not to fit on a line.
		 */
		boolean followingSizeKnown() {
			return true;
		}

		/**
		 * Indicate that the corresponding next BreakToken or CloseBlockToken
		 * has been encountered. After this, followingSizeKnown() will return
		 * the correct value.
		 */
		void setEnd() {
			throw new UnsupportedOperationException();
		}

		/**
		 * Indicate that the followingSize is guaranteed to be larger than the
		 * line width, and that it can thus be set to some large value.
		 */
		void setInfiniteSize() {
			throw new UnsupportedOperationException();
		}

		/**
		 * Returns whether this is a <code>BreakToken</code>. It returns
		 * <code>false</code>, and is overriden by <code>BreakToken</code>
		 * to return true.
		 */
		boolean isBreakToken() {
			return false;
		}
	}

	/** A token corresponding to a <code>print</code> call. */
	private class StringToken extends StreamToken {
		String s;

		StringToken(String s) {
			this.s = s;
		}

		void print() throws Exc {
			out.print(s);
		}

		int size() {
			return back.measure(s);
		}
	}

	/** A token corresponding to an <code>ind</code> call. */
	private class IndentationToken extends StreamToken {
		protected int width;

		protected int offset;

		IndentationToken(int width, int offset) {
			this.width = width;
			this.offset = offset;
		}

		void print() throws Exc {
			out.indent(width, offset);
		}

		int size() {
			return width;
		}
	}

	/** Superclass of tokens which calculate their followingSize. */
	private abstract class SizeCalculatingToken extends StreamToken {
		protected int begin = 0;

		/** negative means that end has not been set yet. */
		protected int end = -1;

		SizeCalculatingToken() {
			begin = totalSize;
		}

		int followingSize() {
			return end - begin;
		}

		boolean followingSizeKnown() {
			return end >= 0;
		}

		void setEnd() {
			this.end = totalSize;
		}

		void setInfiniteSize() {
			end = begin + largeSize;
		}
	}

	/** A token corresponding to a <code>brk</code> call. */
	private class BreakToken extends SizeCalculatingToken {
		protected int width;

		protected int offset;

		BreakToken(int width, int offset) {
			this.width = width;
			this.offset = offset;
		}

		int size() {
			return width;
		}

		void print() throws Exc {
			out.printBreak(width, offset, followingSize());
		}

		boolean isBreakToken() {
			return true;
		}
	}

	/** A token corresponding to a <code>begin</code> call. */
	private class OpenBlockToken extends SizeCalculatingToken {
		protected BreakConsistency cons;

		protected IndentationBase indBase;

		protected int indent;
		
		OpenBlockToken(BreakConsistency consistent, IndentationBase fromPos, int indent) {
			this.cons = consistent;
			this.indBase = fromPos;
			this.indent = indent;
		}

		int size() {
			return 0;
		}

		void print() throws Exc {
			out.openBlock(cons,indBase, 
						  indent, followingSize());
		}
	}

	/** A token corresponding to an <code>end</code> call. */
	private class CloseBlockToken extends StreamToken {

		CloseBlockToken() {
		}

		void print() throws Exc {
			out.closeBlock();
		}

		int size() {
			return 0;
		}

	}

	/** A token corresponding to a <code>mark</code> call. */
	private class MarkToken extends StreamToken {
		protected Object o;

		MarkToken(Object o) {
			this.o = o;
		}

		int size() {
			return 0;
		}

		void print() throws Exc {
			out.mark(o);
		}
	}

}
