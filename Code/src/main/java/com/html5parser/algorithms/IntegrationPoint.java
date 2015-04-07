package com.html5parser.algorithms;

import org.w3c.dom.Element;

import com.html5parser.constants.Namespace;

public class IntegrationPoint {
	/*
	 * A node is a MathML text integration point if it is one of the following
	 * elements: An mi element in the MathML namespace An mo element in the
	 * MathML namespace An mn element in the MathML namespace An ms element in
	 * the MathML namespace An mtext element in the MathML namespace
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

	// A node is an HTML integration point if it is one of the following
	// elements:
	//
	// An annotation-xml element in the MathML namespace whose start tag token
	// had an attribute with the name "encoding" whose value was an ASCII
	// case-insensitive match for the string "text/html"
	// An annotation-xml element in the MathML namespace whose start tag token
	// had an attribute with the name "encoding" whose value was an ASCII
	// case-insensitive match for the string "application/xhtml+xml"
	// A foreignObject element in the SVG namespace
	// A desc element in the SVG namespace
	// A title element in the SVG namespace

	public static Boolean isHtmlIntegrationPoint(Element e) {
		if ((e.getNodeName().equals("annotation-xml")
				&& e.getNamespaceURI().equals(Namespace.MathML)
				&& e.hasAttribute("encoding") && (e.getAttribute("encoding")
				.equalsIgnoreCase("text/html") || e.getAttribute("encoding")
				.equalsIgnoreCase("application/xhtml+xml")))
				|| (e.getNamespaceURI().equals(Namespace.SVG) && (e
						.getNodeName().equals("foreignObject")
						|| e.getNodeName().equals("desc") || e.getNodeName()
						.equals("title")))

		) {
			return true;
		}
		throw new UnsupportedOperationException();

	}
}
