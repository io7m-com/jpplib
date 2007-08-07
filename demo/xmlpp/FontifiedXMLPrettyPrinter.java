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

package xmlpp;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.ParserAdapter;

import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;

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
public class FontifiedXMLPrettyPrinter extends DefaultHandler {
 
	public static final int INDENTATION = 3;
	
	private Layouter<NoExceptions> pp;
	private boolean insertBreak;
	private boolean lastSawCharacters = false;
	
	public static final AttributeSet ATTR_EMPTY;
	public static final AttributeSet ATTR_BLUE;	
	public static final AttributeSet ATTR_GREEN;	
	public static final AttributeSet ATTR_GRAY;	

	static {
		MutableAttributeSet as;
		ATTR_EMPTY = SimpleAttributeSet.EMPTY;
		as = new SimpleAttributeSet();
		StyleConstants.setForeground(as,java.awt.Color.BLUE);
		ATTR_BLUE = as;
		as = new SimpleAttributeSet();
		StyleConstants.setForeground(as,java.awt.Color.GREEN);
		ATTR_GREEN = as;
		as = new SimpleAttributeSet();
		StyleConstants.setForeground(as,java.awt.Color.GRAY);
		ATTR_GRAY = as;
	}
	
	public FontifiedXMLPrettyPrinter(StyledDocumentBackend back) {
		pp = new Layouter<NoExceptions>(back,INDENTATION);
	}

	@Override
	public void characters(char[] ch, int start, int length) 
	throws SAXException {

			// collect words
			String quotedText = quoteCharacterData(ch, start, length).trim();
			String[] words=quotedText.split("\\s+");
			// if last element was arleady characters, continue with a
			// separating brk, otherwise, start new inconsisten block.
			if (lastSawCharacters) {
				pp.brk(1,0);
			} else {
				pp.beginI(0);
			}
			// output words separated by blanks
			boolean brk = false;
			for(String word:words) {
				if (brk) {
					pp.brk(1,0);
				}
				pp.print(word);
				brk = true;
			}
			lastSawCharacters = true;

	}

	private String quoteCharacterData(char[] ch, int start, int length) {
		StringBuilder sb = new StringBuilder();
		for(int i=start;i<start+length;i++) {
			char c;
			switch (c=ch[i]) {
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '&':
				sb.append("&amp;");
				break;
			default:
				sb.append(c);
			break;
			}
		}
		return sb.toString();
	}

	private void wrapUpCharacters() 
	throws IOException {
		if (lastSawCharacters) {
			pp.end();
			lastSawCharacters = false;
		}
	}
	
	@Override
	public void startElement(String namespace, 
			String localName, 
			String qName,
			Attributes atts) 
	throws SAXException {
		try {
			wrapUpCharacters();
			if (insertBreak) {
				pp.brk(0,0);
			}
			pp.mark(ATTR_BLUE);
			pp.beginC(INDENTATION).print("<"+localName);
			printAttributes(atts);
			pp.print(">");
			pp.mark(ATTR_EMPTY);
			insertBreak = true;
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	public void printAttributes(Attributes atts)
	throws IOException {
		if (atts.getLength()>0) {
			pp.print(" ").beginC(0);
			for (int i=0;i<atts.getLength();i++) {
				pp.print(atts.getLocalName(i)
						+"=");
				pp.mark(ATTR_GREEN);
				pp.print(quoteAttrValue(atts.getValue(i)));
				pp.mark(ATTR_EMPTY);
				if (i!=atts.getLength()-1) {
					pp.brk(1,0);
				}
			}
			pp.end();
		}
	}
	
	public String quoteAttrValue(String s) {
		return "\""
			+s.replaceAll("\"", "&quot;")
			  .replaceAll("\'", "&apos;")+"\"";
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) 
	throws SAXException {
		try {
			wrapUpCharacters();
			if (insertBreak) {
				pp.brk(0,-INDENTATION);
			}
			pp.mark(ATTR_BLUE);
			pp.print("</"+localName+">").mark(ATTR_EMPTY).end();
			insertBreak = true;
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	@Override
	public void processingInstruction(String target, String data) 
	throws SAXException {
		pp.mark(ATTR_GRAY);	
		pp.print("<?"+target+" "+data+"?>").mark(ATTR_EMPTY).nl();
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
        pp.mark(ATTR_GRAY).print("<?xml version=\"1.0\"?>").mark(ATTR_EMPTY).nl();
        insertBreak = false;
		pa.parse(urlString);
		if (insertBreak) {
			pp.brk(0,0);
		}
		wrapUpCharacters();
		pp.end().close();
	}

    public static void createAndShowGUI(String input) {
    	try {
    		JFrame frame = new JFrame(input);
    		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    		StyledDocumentBackend output = new StyledDocumentBackend(80);
    		JTextPane textPane = new JTextPane(output.getDocument());
    		textPane.setFont(new java.awt.Font("Monospaced",0,12));
    		textPane.setEditable(false);
    		textPane.setMargin(new java.awt.Insets(5, 5, 5, 5));
    		JScrollPane scrollPane = new JScrollPane(textPane);
    		scrollPane.setPreferredSize(new java.awt.Dimension(200, 200));
    		frame.getContentPane().add(scrollPane);

    		FontifiedXMLPrettyPrinter xpp = new FontifiedXMLPrettyPrinter(output);
    		xpp.process(input);

    		frame.pack();
    		frame.setVisible(true);
    	} catch (Exception e) {
    		System.err.println(e);
    		System.exit(1);
    	}
    }

	public static void main(String[] args) {
		if (args.length!=1) {
			System.err.println("usage: java xmlpp.XMLPrettyPrinter input.xml");
			System.exit(1);
		}
		final String input = args[0];
		 javax.swing.SwingUtilities.invokeLater(new Runnable() {
		      public void run() {
		        createAndShowGUI(input);
		      }
		    });
		
		}


}
