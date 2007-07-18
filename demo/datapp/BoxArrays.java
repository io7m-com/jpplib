//This file is part of the Java Pretty Printer Library (JPPlib)
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

package datapp;

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
 * @author mgiese
 *
 */
public class BoxArrays {
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