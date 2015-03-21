package com.html5parser.algorithms;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.constants.Namespace;

public class ElementInsertionAlgorithm {

	public static Element insertHTMLElement(ParserContext context, Token token) {
		return insertForeignElement(context, token, Namespace.HTML);
	}

	public static Element insertForeignElement(ParserContext context,
			Token token, String namespace) {
		Node adjustedInsertionLocation = getAppropiatePlaceForInsertingANode(
				context, null);

		// TODO: implement: Create an element for the token algorithm and
		// replace the following
		Element element = context.getDocument().createElement(token.getValue());

		adjustedInsertionLocation.appendChild(element);

		context.getOpenElements().push(element);

		return element;
	}

	/**
	 * 
	 * @param context
	 *            parser context
	 * @param overrideTarget
	 *            set null to not override
	 * @return node
	 */
	private static Node getAppropiatePlaceForInsertingANode(
			ParserContext context, Node overrideTarget) {
		Node target;

		if (overrideTarget != null)
			target = overrideTarget;
		else
			target = context.getOpenElements().peek();

		// if(fosterParenting)
		// TODO: foster parenting handling

		// else
		Node adjustedInsertionLocation = target;

		if (adjustedInsertionLocation.getNodeName().equals("template")) {
			throw new UnsupportedOperationException(
					"getAppropiatePlaceForInsertingANode() in a template node  not implemented yet (ForeingElementInsertionAlgorithm)");
		}

		return adjustedInsertionLocation;
	}
}
