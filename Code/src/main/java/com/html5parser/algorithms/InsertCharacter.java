package com.html5parser.algorithms;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;

public class InsertCharacter {

	/**
	 * 
	 * @param context
	 * @param token
	 * @return the Node in which the characters where added
	 */
	public static Node run(ParserContext context, Token token) {
		return run(context, token.getValue());
	}

	public static Node run(ParserContext context, int character) {
		return run(context, String.valueOf(Character.toChars(character)));
	}

	/**
	 * 
	 * @param context
	 * @param token
	 * @return the Node in which the characters where added
	 */
	public static Node run(ParserContext context, String data) {

		// Let the adjusted insertion location be the appropriate place for
		// inserting a node.
		AdjustedInsertionLocation adjustedInsertionLocation = AppropiatePlaceForInsertingANode
				.run(context);

		// If the adjusted insertion location is in a Document node, then abort
		// these steps.
		if (adjustedInsertionLocation.getParent().getNodeType() == Node.DOCUMENT_NODE) {
			return null;
		}

		// If there is a Text node immediately before the adjusted insertion
		// location, then append data to that Text node's data.
		Node referenceLocation = adjustedInsertionLocation.getReferenceNode();
		Node beforeLocation = null;
		if (referenceLocation != null)
			beforeLocation = referenceLocation.getPreviousSibling();
		// if it will be inserted before location
		if (beforeLocation != null
				&& beforeLocation.getNodeType() == Node.TEXT_NODE) {
			beforeLocation.setNodeValue((beforeLocation.getNodeValue())
					.concat(data));
			return beforeLocation;
		}// if it will be inserted as a last child
		else if (adjustedInsertionLocation.getParent().getLastChild() != null
				&& adjustedInsertionLocation.getParent().getLastChild()
						.getNodeType() == Node.TEXT_NODE) {
			Node location = adjustedInsertionLocation.getParent()
					.getLastChild();
			location.setNodeValue((location.getNodeValue()).concat(data));
			return referenceLocation;
		}

		else {
			// Otherwise, create a new Text node whose data is data and whose
			// node document is the same as that of the element in which the
			// adjusted insertion location finds itself, and insert the newly
			// created node at the adjusted insertion location.
			Document document = adjustedInsertionLocation.getParent()
					.getOwnerDocument();

			// If doc is null it means it is the document
			if (document == null)
				document = ((Document) adjustedInsertionLocation.getParent());

			Node textNode = document.createTextNode(data);
			adjustedInsertionLocation.insertElement(textNode);
			return textNode;
		}
	}
}
