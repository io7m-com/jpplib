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

package xmlpp;

import de.uka.ilkd.pp.*;
import java.io.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/** Pretty-prints outline of an XML document.
 * This class is a demo for the JPPLib classes.  It reads
 * an XML file using SAX and pretty-prints its outline,
 * i.e. just the start and end tags.
 * 
 * <p>The Layout is like
 * <pre>
 * &lt;doc&gt;&lt;head&gt;&lt;/head&gt;&lt;body&gt;&lt;/body&gt;&lt;/doc&gt;
 * </pre>
 * as long as everything fits on one line.  Otherwise, elements
 * are broken, and sub-elements are indented, e.g.
 * <pre>
 * &lt;doc&gt;
 *     &lt;head&gt;&lt;/head&gt;
 *     &lt;body&gt;&lt;/body&gt;
 * &lt;/doc&gt;
 * </pre>
 * 
 * @author Martin Giese
 *
 */
public class XMLPrettyPrinter extends DefaultHandler {
 
	private PrintStream out;
	private Layouter<java.io.IOException> pp;
	private boolean insertBreak;
	
	public XMLPrettyPrinter(PrintStream out) {
		this.out = out;
		pp = Layouter.getWriterLayouter(new BufferedWriter(new OutputStreamWriter(this.out)));
	}

	@Override
	public void startElement(String namespace, 
			String localName, 
			String qName,
			Attributes atts) 
	throws SAXException {
		try {
			if (insertBreak) {
				pp.brk(0,0);
			}
			pp.beginC(4).print("<"+localName+">");
			insertBreak = true;
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) 
	throws SAXException {
		try {
			if (insertBreak) {
				pp.brk(0,-4);
			}
			pp.print("</"+localName+">").end();
			insertBreak = true;
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	public void process(String urlString) 
	throws Exception {
		SAXParserFactory spf = 
			SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		ParserAdapter pa = 
			new ParserAdapter(sp.getParser());
		pa.setContentHandler(this);
        pp.beginC(0);
        insertBreak = false;
		pa.parse(urlString);
		if (insertBreak) {
			pp.brk(0,0);
		}
		pp.end().close();
	}

	public static void main(String[] args) {
		try {
            XMLPrettyPrinter xpp = new XMLPrettyPrinter(System.out);
            xpp.process(args[0]);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
	}

}