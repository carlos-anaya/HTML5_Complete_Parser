package com.html5parser.constants;

import java.util.HashMap;
import java.util.Map;

public class ForeignAttributesTable {

	
	/**
	 * Value description:
	 * 
	 * first column is the attibute name
	 * second column is the prefix
	 * third column is the local name
	 * fourth column is the namespace
	 */
	public static final Map<String, String[]> TABLE;
	static {
		TABLE = new HashMap<String, String[]>();
		TABLE.put("xlink:actuate", new String[] { "xlink","actuate",Namespace.XLink});
		TABLE.put("xlink:arcrole", new String[] { "xlink","arcrole",Namespace.XLink});
		TABLE.put("xlink:href", new String[] { "xlink","href",Namespace.XLink});
		TABLE.put("xlink:role", new String[] { "xlink","role",Namespace.XLink});
		TABLE.put("xlink:show", new String[] { "xlink","show",Namespace.XLink});
		TABLE.put("xlink:title", new String[] { "xlink","title",Namespace.XLink});
		TABLE.put("xlink:type", new String[] { "xlink","type",Namespace.XLink});
		TABLE.put("xml:base", new String[] { "xml","base",Namespace.XML});
		TABLE.put("xml:lang", new String[] { "xml","lang",Namespace.XML});
		TABLE.put("xml:space", new String[] { "xml","space",Namespace.XML});
		TABLE.put("xmlns", new String[] { "","xmlns",Namespace.XMLNS});
		TABLE.put("xmlns:xlink", new String[] { "xmlns","xlink",Namespace.XMLNS});
	}

}
