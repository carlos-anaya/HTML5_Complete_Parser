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
	public static Boolean isMathMLTextIntegrationPoint(Element element) {

		if (element != null
				&& element.getNamespaceURI().equals(Namespace.MathML)) {
			if (element.getNodeName().equals("mi")
					|| element.getNodeName().equals("mo")
					|| element.getNodeName().equals("mn")
					|| element.getNodeName().equals("ms")
					|| element.getNodeName().equals("mtext")) {
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

	public static Boolean isHtmlIntegrationPoint(Element element) {
		if (element != null
				&& ((element.getNodeName().equals("annotation-xml")
						&& element.getNamespaceURI().equals(Namespace.MathML)
						&& element.hasAttribute("encoding") && (element
						.getAttribute("encoding").equalsIgnoreCase("text/html") || element
						.getAttribute("encoding").equalsIgnoreCase(
								"application/xhtml+xml"))) || (element
						.getNamespaceURI().equals(Namespace.SVG) && (element
						.getNodeName().equals("foreignObject")
						|| element.getNodeName().equals("desc") || element
						.getNodeName().equals("title"))))) {
			return true;
		}
		return false;

	}
}
