package xmlpp;

/** Some utilities for XML pretty printers */
public class XMLUtils {

	/** Replace critical characters by XML entities. */
	static String quoteCharacterData(char[] ch, int start, int length) {
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

	/** Perform entity-quoting of quotes within attribute values. */
	public static String quoteAttrValue(String s) {
		return "\""
			+s.replaceAll("\"", "&quot;")
			  .replaceAll("\'", "&apos;")
			  .replaceAll("&", "&amp;")+"\"";
	}

}
