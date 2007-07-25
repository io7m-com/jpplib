package de.uka.ilkd.pp;

import java.util.*;
import junit.framework.TestCase;


public class TestDataLayouter extends TestCase {

	/** A backend to remember the result in */
    StringBackend narrowBack;
    /** A backend to remember the result in */
    StringBackend wideBack;
    /** A backend to remember the result in */
    StringBackend tenBack;
    /** A layouter which breaks everything */
    DataLayouter<NoExceptions> narrow;
    /** A layouter which breaks nothing */
    DataLayouter<NoExceptions> wide;
    /** A layouter with line length ten. */
    DataLayouter<NoExceptions> ten;

    public TestDataLayouter(String name) {
	super(name);
    }

    public void setUp() {
	narrowBack = new StringBackend(1);
	wideBack   = new StringBackend(10000);
	tenBack   = new StringBackend(10);
	narrow = new DataLayouter<NoExceptions>(narrowBack,2);
	wide   = new DataLayouter<NoExceptions>(wideBack,2);
	ten    = new DataLayouter<NoExceptions>(tenBack,2);
    }
    
    public void testNarrowList() {
    	List<String> l = new ArrayList<String>();
    	l.add("a");
    	l.add("b");
    	l.add("c");
    	Object o = l;
    	narrow.print(o);
    	assertEquals("List narrow","[a,\n b,\n c]",narrowBack.getString());
    }

    public void testWideList() {
    	List<String> l = new ArrayList<String>();
    	l.add("a");
    	l.add("b");
    	l.add("c");
    	Object o = l;
    	wide.print(o);
    	assertEquals("List wide","[a, b, c]",wideBack.getString());
    }

    public void testNarrowArray() {
    	int[] a = new int[]{1,2,3};
    	Object o = a;
    	narrow.print(o);
    	assertEquals("Array narrow","[1,\n 2,\n 3]",narrowBack.getString());
    }

    public void testWideArray() {
       	int[] a = new int[]{1,2,3};
    	Object o = a;
    	wide.print(o);
    	assertEquals("Array wide","[1, 2, 3]",wideBack.getString());
    }

    public void testNarrowMap() {
    	SortedMap<String,Integer> m = new TreeMap<String,Integer>();
    	m.put("a",1);
    	m.put("b",2);
    	m.put("c",3);
    	Object o = m;
    	narrow.print(o);
    	assertEquals("Map narrow","{a=\n   1,\n b=\n   2,\n c=\n   3}",narrowBack.getString());
    }

    public void testTenMap() {
    	SortedMap<String,Integer> m = new TreeMap<String,Integer>();
    	m.put("a",1);
    	m.put("b",2);
    	m.put("c",3);
    	Object o = m;
    	ten.print(o);
    	assertEquals("Map ten","{a=1,\n b=2,\n c=3}",tenBack.getString());
    }
    
    public void testWideMap() {
    	SortedMap<String,Integer> m = new TreeMap<String,Integer>();
    	m.put("a",1);
    	m.put("b",2);
    	m.put("c",3);
    	Object o = m;
    	wide.print(o);
    	assertEquals("Map wide","{a=1, b=2, c=3}",wideBack.getString());
    }
   
    public class Expr implements PrettyPrintable {
    	Object a;
    	String op;
    	Object b;
    	
    	public Expr(Object a, String op, Object b) {
    		this.a = a;
    		this.op = op;
    		this.b = b;
    	}
    	
    	public <Exc extends Exception> void prettyPrint(DataLayouter<Exc> l) 
    	throws Exc {
    		int ind = op.length()+1;
    		l.beginC(ind).ind(0,0).print(a).brk(1,-ind)
    			.print(op).print(" ").print(b).end();
    	}
    }
    
    public void testNarrowExpr() {
    	Expr e = new Expr(new Expr("a","and","b"),
    					   "or",
    					   new Expr("c","and","d"));
    	narrow.print(e);
    	assertEquals("Expr narrow","       a\n   and b\nor     c\n   and d",narrowBack.getString());
    }

    public void testTenExpr() {
    	Expr e = new Expr(new Expr("a","and","b"),
    					   "or",
    					   new Expr("c","and","d"));
    	ten.print(e);
    	assertEquals("Expr narrow","   a and b\nor c and d",tenBack.getString());
    }

    public void testWideExpr() {
    	Expr e = new Expr(new Expr("a","and","b"),
    					   "or",
    					   new Expr("c","and","d"));
    	wide.print(e);
    	assertEquals("Expr wide","a and b or c and d",wideBack.getString());
    }

}
