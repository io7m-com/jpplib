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

package xmlpp;

import de.uka.ilkd.pp.*;
import java.io.*;

import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;  

import java.io.File;
import java.io.IOException;

import org.w3c.dom.*;


public class DOMXMLPrettyPrinter {

	public static final int INDENTATION= 3;

	private Layouter<IOException> pp;
	private Document document;
	
	/** A call to break is required before printing
	 * the next item.  This is needed because breaks are to
	 * be printed before every item, except text, but not
	 * at the beginning.
	 */
	private boolean insertBreak;

	public DOMXMLPrettyPrinter(Document document) {
		super();
		this.document = document;
		pp = Layouter.getWriterLayouter(new BufferedWriter(new OutputStreamWriter(System.out)));	
	}

	void prettyPrint() throws IOException {
		pp.beginC(0);
		insertBreak = false;
		prettyPrint(document);
		pp.end();
		pp.close();
	}
	
	void prettyPrint(Node node) throws IOException {
		switch(node.getNodeType()) {
		case Node.DOCUMENT_NODE: 
			prettyPrint(((Document)node).getDocumentElement() );
			break;
		case Node.ELEMENT_NODE: 
			if (insertBreak) {
				pp.brk(0,0);
			}
			pp.beginC(INDENTATION);
			pp.print("<");
			pp.print(node.getNodeName());
			NamedNodeMap attrs = node.getAttributes();
			for (int i = 0; i < attrs.getLength(); i++)
			{
				Node attr = attrs.item(i);
				pp.print(" " + attr.getNodeName() + 
						"=\"" + attr.getNodeValue() + 
				"\"");
			}
			pp.print(">");

			NodeList children = node.getChildNodes();
			if (children != null)
			{
				int len = children.getLength();
				for (int i = 0; i < len; i++) {
					prettyPrint(children.item(i));
				}
			}

			pp.brk(0,-INDENTATION);
		    pp.print("</");
		    pp.print(node.getNodeName());
		    pp.print(">").end();
			break;

		case Node.ENTITY_REFERENCE_NODE: 
			pp.print("&");
			pp.print(node.getNodeName());
			pp.print(";").nl();
			break;
		case Node.CDATA_SECTION_NODE: 
			pp.print("<![CDATA[");
			pp.print(node.getNodeValue());
			pp.print("]]>").nl();
			break;
		case Node.TEXT_NODE: 
			// collect words
			String quotedText = node.getNodeValue().trim();
			String[] words=quotedText.split("\\s+");
			// if last element was arleady characters, continue with a
			// separating brk, otherwise, start new inconsisten block.
			pp.beginIInd(0);
			// output words separated by blanks
			boolean brk = false;
			for(String word:words) {
				if (brk) {
					pp.brk(1,0);
				}
				pp.print(word);
				brk = true;
			}
			pp.end();
			break;
		case Node.PROCESSING_INSTRUCTION_NODE: 
			pp.print("<?").nl();
			pp.print(node.getNodeName());
			String data = node.getNodeValue();
			pp.print(" ");
			pp.print(data);
			pp.print("?>").nl();
			break;
		}
		insertBreak = true;
	}
	
	public static void main(String argv[])
    {
        if (argv.length != 1) {
            System.err.println("Usage: java xmlpp.DOMXMLPrettyPrinter filename");
            System.exit(1);
        }
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setCoalescing(true);
        try {
        	DocumentBuilder builder = factory.newDocumentBuilder();
        	Document document = builder.parse( new File(argv[0]) );
        	
        	new DOMXMLPrettyPrinter(document).prettyPrint();
        } catch (SAXException sxe) {
           // Error generated during parsing
           Exception  x = sxe;
           if (sxe.getException() != null)
               x = sxe.getException();
           x.printStackTrace();

        } catch (ParserConfigurationException pce) {
           // Parser with specified options can't be built
           pce.printStackTrace();

        } catch (IOException ioe) {
           // I/O error
           ioe.printStackTrace();
        }
    }
}
