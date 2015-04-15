package com.html5parser.algorithms;

import java.util.Arrays;
import java.util.Stack;

import org.w3c.dom.Element;

import com.html5parser.classes.ParserContext;

public class ElementInScope {

	private enum ScopeType {
		ListItem, Button, Table, Select
	};

	public static Boolean isInScope(ParserContext parserContext,
			String elementName) {
		return isInScope(parserContext, elementName, null);
	}

	public static Boolean isInListItemScope(ParserContext parserContext,
			String elementName) {
		return isInScope(parserContext, elementName, ScopeType.ListItem);
	}

	public static Boolean isInButtonScope(ParserContext parserContext,
			String elementName) {
		return isInScope(parserContext, elementName, ScopeType.Button);
	}

	public static Boolean isInTableScope(ParserContext parserContext,
			String elementName) {
		return isInScope(parserContext, elementName, ScopeType.Table);
	}

	public static Boolean isInSelectScope(ParserContext parserContext,
			String elementName) {
		return isInScope(parserContext, elementName, ScopeType.Select);
	}

	// The stack of open elements is said to have an element target node in a
	// specific scope consisting of a list of element types list when the
	// following algorithm terminates in a match state:
	private static Boolean isInScope(ParserContext parserContext,
			String elementName, ScopeType type) {
		Stack<Element> openElements = new Stack<Element>();
		openElements.addAll(parserContext.getOpenElements());
		do {
			// Initialize node to be the current node (the bottommost node of
			// the stack).
			Element node = openElements.pop();
			// If node is the target node, terminate in a match state.
			if (node.getNodeName().equals(elementName))
				return true;
			// Otherwise, if node is one of the element types in list, terminate
			// in a failure state.
			else {
				if (type == null) {
					if (isInScope(node))
						return false;
				} else {
					switch (type) {
					case ListItem:
						if (isInListItemScope(node))
							return false;
						break;
					case Button:
						if (isInButtonScope(node))
							return false;
						break;
					case Table:
						if (isInTableScope(node))
							return false;
						break;
					case Select:
						if (isInSelectScope(node))
							return false;
						break;
					default:
						if (isInScope(node))
							return false;
						break;
					}
				}
			}
			// Otherwise, set node to the previous entry in the stack of open
			// elements and return to step 2. (This will never fail, since the
			// loop will always terminate in the previous step if the top of the
			// stack — an html element — is reached.)
		} while (!openElements.empty());
		return null;

	}

	// The stack of open elements is said to have a particular element in scope
	// when it has that element in the specific scope consisting of the
	// following element types:
	//
	// applet in the HTML namespace
	// caption in the HTML namespace
	// html in the HTML namespace
	// table in the HTML namespace
	// td in the HTML namespace
	// th in the HTML namespace
	// marquee in the HTML namespace
	// object in the HTML namespace
	// template in the HTML namespace
	// mi in the MathML namespace
	// mo in the MathML namespace
	// mn in the MathML namespace
	// ms in the MathML namespace
	// mtext in the MathML namespace
	// annotation-xml in the MathML namespace
	// foreignObject in the SVG namespace
	// desc in the SVG namespace
	// title in the SVG namespace
	private static Boolean isInScope(Element element) {
		String[] elementsInHTMLns = { "applet", "caption", "html", "table",
				"td", "th", "marquee", "object", "template" };
		String[] elementsInMathMLns = { "mi", "mo", "mn", "ms", "mtext",
				"annotation-xml" };
		String[] elementsInSVGns = { "foreignObject", "desc", "title" };

		if (// element.getNamespaceURI().equals(Namespace.HTML) &&
		Arrays.asList(elementsInHTMLns).contains(element.getNodeName()))
			return true;
		if (// element.getNamespaceURI().equals(Namespace.MathML) &&
		Arrays.asList(elementsInMathMLns).contains(element.getNodeName()))
			return true;
		if (// element.getNamespaceURI().equals(Namespace.SVG) &&
		Arrays.asList(elementsInSVGns).contains(element.getNodeName()))
			return true;
		return false;
	}

	// The stack of open elements is said to have a particular element in list
	// item scope when it has that element in the specific scope consisting of
	// the following element types:
	// All the element types listed above for the has an element in scope
	// algorithm.
	// ol in the HTML namespace
	// ul in the HTML namespace
	private static Boolean isInListItemScope(Element element) {
		String[] elementsInHTMLns = { "ol", "ul" };
		if (// element.getNamespaceURI().equals(Namespace.HTML) &&
		Arrays.asList(elementsInHTMLns).contains(element.getNodeName()))
			return true;
		return isInScope(element);
	}

	// The stack of open elements is said to have a particular element in button
	// scope when it has that element in the specific scope consisting of the
	// following element types:
	//
	// All the element types listed above for the has an element in scope
	// algorithm.
	// button in the HTML namespace
	private static Boolean isInButtonScope(Element element) {
		String[] elementsInHTMLns = { "button" };
		if (// element.getNamespaceURI().equals(Namespace.HTML) &&
		Arrays.asList(elementsInHTMLns).contains(element.getNodeName()))
			return true;
		return isInScope(element);
	}

	// The stack of open elements is said to have a particular element in table
	// scope when it has that element in the specific scope consisting of the
	// following element types:
	//
	// html in the HTML namespace
	// table in the HTML namespace
	// template in the HTML namespace
	private static Boolean isInTableScope(Element element) {
		String[] elementsInHTMLns = { "html", "table", "template" };
		if (// element.getNamespaceURI().equals(Namespace.HTML) &&
		Arrays.asList(elementsInHTMLns).contains(element.getNodeName()))
			return true;
		return false;
	}

	// The stack of open elements is said to have a particular element in select
	// scope when it has that element in the specific scope consisting of all
	// element types except the following:
	//
	// optgroup in the HTML namespace
	// option in the HTML namespace
	private static Boolean isInSelectScope(Element element) {
		String[] elementsInHTMLns = { "optgroup", "option" };
		if (!(// element.getNamespaceURI().equals(Namespace.HTML) &&
		Arrays.asList(elementsInHTMLns).contains(element.getNodeName())))
			return true;
		return false;
	}
}
