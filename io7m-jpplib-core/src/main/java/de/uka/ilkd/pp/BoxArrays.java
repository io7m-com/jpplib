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

/** A utility class to box all elements of an array. 
 * Java's autoboxing applies only to primitive types, not
 * to arrays of primitive values.  The methods in this class
 * can be used to box each element of an array of primitive
 * values, e.g. int[], so as to obtain an array of references,
 * e.g. Integer[].  The <code>boxPrimArray</code> methods
 * may be used to box arrays of known primitive element type.
 * {@link #boxArray(Object)} will box the elements of an
 * arbitrary array, passed in as an <code>Object</code>, 
 * using reflection to find out the element type.
 * 
 * @author Martin Giese
 *
 */
class BoxArrays {
	public static Boolean[] boxPrimArray(boolean[] a) {
		Boolean[] result = new Boolean[a.length];
		for(int i=0;i<a.length;i++) {
			result[i]=a[i];
		}
		return result;
	}

	public static Character[] boxPrimArray(char[] a) {
		Character[] result = new Character[a.length];
		for(int i=0;i<a.length;i++) {
			result[i]=a[i];
		}
		return result;
	}

	public static Byte[] boxPrimArray(byte[] a) {
		Byte[] result = new Byte[a.length];
		for(int i=0;i<a.length;i++) {
			result[i]=a[i];
		}
		return result;
	}

	public static Short[] boxPrimArray(short[] a) {
		Short[] result = new Short[a.length];
		for(int i=0;i<a.length;i++) {
			result[i]=a[i];
		}
		return result;
	}

	public static Integer[] boxPrimArray(int[] a) {
		Integer[] result = new Integer[a.length];
		for(int i=0;i<a.length;i++) {
			result[i]=a[i];
		}
		return result;
	}
	
	public static Long[] boxPrimArray(long[] a) {
		Long[] result = new Long[a.length];
		for(int i=0;i<a.length;i++) {
			result[i]=a[i];
		}
		return result;
	}

	public static Float[] boxPrimArray(float[] a) {
		Float[] result = new Float[a.length];
		for(int i=0;i<a.length;i++) {
			result[i]=a[i];
		}
		return result;
	}

	public static Double[] boxPrimArray(double[] a) {
		Double[] result = new Double[a.length];
		for(int i=0;i<a.length;i++) {
			result[i]=a[i];
		}
		return result;
	}

	/** Box all elements of an array.
	 * This produces an <code>Integer[]</code> from
	 * an <code>int[]</code> and similarly for all other
	 * primitive types.  Arrays of non-primitive types are
	 * returned unchanged.  Any other objects lead to an
	 * AssertionError
	 * 
	 * @param o an object of any array type
	 * @return the boxed array
	 * @throws java.lang.AssertionError if <code>o</code> is not an array
	 */
	public static Object[] boxArray(Object o) {
		assert o.getClass().isArray();
		Class<?> comp = o.getClass().getComponentType();
		if (comp == Boolean.TYPE) {
			return boxPrimArray((boolean[])o);
		} else if (comp == Character.TYPE) {
			return boxPrimArray((char[])o);
		} else if (comp == Byte.TYPE) {
			return boxPrimArray((byte[])o);
		} else if (comp == Short.TYPE) {
			return boxPrimArray((short[])o);
		} else if (comp == Integer.TYPE) {
			return boxPrimArray((int[])o);
		} else if (comp == Long.TYPE) {
			return boxPrimArray((long[])o);
		} else if (comp == Float.TYPE) {
			return boxPrimArray((float[])o);
		} else if (comp == Double.TYPE) {
			return boxPrimArray((double[])o);
		} else {
			return (Object[])o;
		}
    }
}