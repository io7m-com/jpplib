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

/** An interface for objects that can pretty print themselves.
 * The {@link #prettyPrint(DataLayouter)} method will be called
 * by {@link DataLayouter#print(Object)} for objects that implement
 * this interface.  According to the intended use of {@link DataLayouter},
 * the produced output should correspond to what {@link Object#toString()}
 * returns, except for added indentation and whitespace.
 * 
 * @author Martin Giese
 * 
 * */
public interface PrettyPrintable {
	/**
	 * Pretty prints <code>this</code> to the
	 * {@link de.uka.ilkd.pp.DataLayouter} <code>l</code>. Will be called by
	 * {@link DataLayouter#print(Object)} for objects that implement this
	 * interface. According to the intended use of {@link DataLayouter}, the
	 * produced output should correspond to what {@link Object#toString()}
	 * returns, except for added indentation and whitespace. 
	 * 
	 * <p>The implementation must end any blocks it begins.  It must not
	 * close <code>l</code>.  It may pass through any exceptions of type 
	 * <code>Exc</code> that are thrown by calls to methods of <code>l</code>
	 * 
	 * @param l the DataLayouter the object will be printed to.
	 */ 
	public <Exc extends Exception> void prettyPrint(DataLayouter<Exc> l) throws Exc;
}
