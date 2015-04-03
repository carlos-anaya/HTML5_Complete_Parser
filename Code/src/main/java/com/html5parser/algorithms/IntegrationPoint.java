package com.html5parser.algorithms;

import org.w3c.dom.Element;

import com.html5parser.constants.Namespace;

public class IntegrationPoint {
	/*
	 * A node is a MathML text integration point if it is one of the following
	 * elements:
	 * An mi element in the MathML namespace 
	 * An mo element in the MathML namespace 
	 * An mn element in the MathML namespace 
	 * An ms element in the MathML namespace 
	 * An mtext element in the MathML namespace
	 */
	public static Boolean isMathMLTextIntegrationPoint(Element e) {

		if (e.getNamespaceURI().equals(Namespace.MathML)) {
			if (e.getNodeName().equals("mi") || e.getNodeName().equals("mo")
					|| e.getNodeName().equals("mn")
					|| e.getNodeName().equals("ms")
					|| e.getNodeName().equals("mtext")) {
				return true;
			}
		}
		return false;

	}
	
	public static Boolean isHtmlIntegrationPoint(Element e) {
		//TODO
		throw new UnsupportedOperationException();

	}
}
