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

/** Thrown by many methods of {@link Layouter} to indicate illegal
 * usage.  This exception indicates that the sequence of
 * method calls was illegal, i.e.  more blocks were ended than begun,
 * the Layouter is closed before all blocks are ended, a break occurs
 * outside of any block, etc.
 *
 * <p>This is a RuntimeException, and if it is ever thrown, it means
 * that there is a mistake in the program using the {@code Layouter} 
 * class.
 */

public class UnbalancedBlocksException extends IllegalStateException {
	private static final long serialVersionUID = 5086204740022528272L;

	public UnbalancedBlocksException() {
	super();
    }
	
    public UnbalancedBlocksException(String s) {
	super(s);
    }
}
