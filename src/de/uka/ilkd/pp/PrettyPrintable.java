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
