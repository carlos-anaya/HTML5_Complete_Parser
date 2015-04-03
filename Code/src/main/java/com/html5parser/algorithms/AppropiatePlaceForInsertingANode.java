package com.html5parser.algorithms;

import org.w3c.dom.Node;

import com.html5parser.classes.ParserContext;


public class AppropiatePlaceForInsertingANode {
	public static Node run(
			ParserContext context) {
		return AppropiatePlaceForInsertingANode.run(context, null);
	}
	
	public static Node run(
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
