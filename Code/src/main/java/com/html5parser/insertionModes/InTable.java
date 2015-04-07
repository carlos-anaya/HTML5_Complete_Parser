package com.html5parser.insertionModes;

import org.w3c.dom.Element;

import com.html5parser.algorithms.ElementInScope;
import com.html5parser.algorithms.ListOfActiveFormattingElements;
import com.html5parser.algorithms.ResetTheInsertionModeAppropriately;
import com.html5parser.classes.InsertionMode;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.factories.InsertionModeFactory;
import com.html5parser.interfaces.IInsertionMode;
import com.html5parser.parseError.ParseErrorType;

public class InTable implements IInsertionMode {

	public ParserContext process(ParserContext parserContext) {

		InsertionModeFactory factory = InsertionModeFactory.getInstance();
		Token token = parserContext.getTokenizerContext().getCurrentToken();
		String currentNodeName = parserContext.getCurrentNode().getNodeName();

		switch (token.getType()) {

		// A character token, if the current node is table, tbody, tfoot, thead,
		// or tr element
		// Let the pending table character tokens be an empty list of tokens.
		// Let the original insertion mode be the current insertion mode.
		// Switch the insertion mode to "in table text" and reprocess the token.
		case character:
			if (currentNodeName.equals("table")
					|| currentNodeName.equals("tbody")
					|| currentNodeName.equals("tfoot")
					|| currentNodeName.equals("thead")
					|| currentNodeName.equals("tr")) {
				parserContext.setOriginalInsertionMode(parserContext
						.getInsertionMode());
				parserContext.setInsertionMode(factory
						.getInsertionMode(InsertionMode.in_table_text));
				parserContext.setFlagReprocessToken(true);
			} else
				anythingElse(parserContext);
			break;

		// A comment token
		// Insert a comment.
		case comment:
			// TODO insert comment.
			throw new UnsupportedOperationException();

			// A DOCTYPE token
			// Parse error. Ignore the token.
		case DOCTYPE:
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
			break;

		case start_tag:
			switch (token.getValue()) {
			// A start tag whose tag name is "caption"
			// Clear the stack back to a table context. (See below.)
			// Insert a marker at the end of the list of active formatting
			// elements.
			// Insert an HTML element for the token, then switch the insertion
			// mode to "in caption".
			case "caption":
				clearTheStackBackToATableContext(parserContext);
				ListOfActiveFormattingElements.insertMarker(parserContext);
				// TODO insert element
				parserContext.setInsertionMode(factory
						.getInsertionMode(InsertionMode.in_caption));
				break;

			// A start tag whose tag name is "colgroup"
			// Clear the stack back to a table context. (See below.)
			// Insert an HTML element for the token, then switch the
			// insertion mode to "in column group".
			case "colgroup":
				clearTheStackBackToATableContext(parserContext);
				// TODO insert element
				parserContext.setInsertionMode(factory
						.getInsertionMode(InsertionMode.in_column_group));
				break;

			// A start tag whose tag name is "col"
			// Clear the stack back to a table context. (See below.)
			// Insert an HTML element for a "colgroup" start tag token with
			// no attributes, then switch the insertion mode to
			// "in column group".
			// Reprocess the current token.
			case "col":
				clearTheStackBackToATableContext(parserContext);
				// TODO insert element
				parserContext.setInsertionMode(factory
						.getInsertionMode(InsertionMode.in_column_group));
				parserContext.setFlagReprocessToken(true);
				break;

			// A start tag whose tag name is one of: "tbody", "tfoot",
			// "thead"
			// Clear the stack back to a table context. (See below.)
			// Insert an HTML element for the token, then switch the
			// insertion mode to "in table body".
			case "tbody":
			case "tfoot":
			case "thead":
				clearTheStackBackToATableContext(parserContext);
				// TODO insert element
				parserContext.setInsertionMode(factory
						.getInsertionMode(InsertionMode.in_table_body));
				break;

			// A start tag whose tag name is one of: "td", "th", "tr"
			// Clear the stack back to a table context. (See below.)
			// Insert an HTML element for a "tbody" start tag token with no
			// attributes, then switch the insertion mode to
			// "in table body".
			// Reprocess the current token.
			case "td":
			case "th":
			case "tr":
				clearTheStackBackToATableContext(parserContext);
				// TODO insert element
				parserContext.setInsertionMode(factory
						.getInsertionMode(InsertionMode.in_table_body));
				parserContext.setFlagReprocessToken(true);
				break;

			// A start tag whose tag name is "table"
			// Parse error.
			// If the stack of open elements does not have a table element
			// in table scope, ignore the token.
			// Otherwise:
			// Pop elements from this stack until a table element has been
			// popped from the stack.
			// Reset the insertion mode appropriately.
			// Reprocess the token.
			case "table":
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
				if (ElementInScope.isInTableScope(parserContext, "table")) {
					while (true) {
						Element element = parserContext.getOpenElements().pop();
						if (element.getNodeName().equals("table"))
							break;
					}
					ResetTheInsertionModeAppropriately.Run(parserContext);
					parserContext.setFlagReprocessToken(true);
				}
				break;
			}
			break;
		default:
			break;
		}
		return parserContext;
	}

	public void anythingElse(ParserContext parserContext) {

	}

	private void clearTheStackBackToATableContext(ParserContext parserContext) {
		// it means that the UA must, while the current node is not a table,
		// template, or html element, pop elements from the stack of open
		// elements.
		while (true) {
			Element element = parserContext.getOpenElements().pop();
			if (element.getNodeName().equals("table")
					|| element.getNodeName().equals("template")
					|| element.getNodeName().equals("html")) {
				parserContext.getOpenElements().push(element);
				return;
			}
		}
	}

}