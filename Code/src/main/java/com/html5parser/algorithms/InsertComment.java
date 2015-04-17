package com.html5parser.algorithms;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;

public class InsertComment {

	/**
	 * 
	 * @param context
	 * @param token
	 * @return the Node in which the characters where added
	 */
	public static Node run(ParserContext context, Token token) {
		return run(context, token, null);
	}

	/**
	 * 
	 * @param context
	 * @param token
	 * @return the Node in which the characters where added
	 */
	public static Node run(ParserContext context, Token token, Node position) {

		// Let data be the data given in the comment token being processed.
		String data = token.getValue();

		// If position was specified, then let the adjusted insertion location
		// be position. Otherwise, let adjusted insertion location be the
		// appropriate place for inserting a node.
		AdjustedInsertionLocation adjustedInsertionLocation;
		if (position != null) {
			adjustedInsertionLocation = new AdjustedInsertionLocation(position,
					null);
		} else {
			adjustedInsertionLocation = AppropiatePlaceForInsertingANode
					.run(context);
		}

		// Create a Comment node whose data attribute is set to data and whose
		// node document is the same as that of the node in which the adjusted
		// insertion location finds itself.
		Document document = adjustedInsertionLocation.getParent()
				.getOwnerDocument();
		
		// If doc is null it means it is the document
				if (document == null)
					document = ((Document) adjustedInsertionLocation.getParent());
				
		Node textNode = document.createComment(data);
		adjustedInsertionLocation.insertElement(textNode);
		return textNode;
	}
}
