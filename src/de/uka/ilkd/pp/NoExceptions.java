// This file is part of the Java Pretty Printer Library (JPPlib)
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

/** A dummy Exception type parameter.
 * This {@link java.lang.RuntimeException} can be used as a type
 * parameter for the other classes in this package if the backend 
 * does not throw any (checked) exceptions.  For instance,
 * {@link StringBackend} implements 
 * <code>Backend&lt;NoExceptions&gt;</code> and can be written to
 * using a <code>Layouter&lt;NoExceptions&gt;</code>, etc. 
 */
public class NoExceptions extends RuntimeException {
	private static final long serialVersionUID = 1346223574009403876L;

	public NoExceptions() {
		super();
    }
	
    public NoExceptions(String s) {
    	super(s);
    }
}
