package com.html5parser.insertionModes;

import org.w3c.dom.Element;

import com.html5parser.algorithms.ElementInScope;
import com.html5parser.algorithms.InsertAnHTMLElement;
import com.html5parser.algorithms.ListOfActiveFormattingElements;
import com.html5parser.classes.InsertionMode;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.factories.InsertionModeFactory;
import com.html5parser.interfaces.IInsertionMode;
import com.html5parser.parseError.ParseErrorType;

public class InRow implements IInsertionMode {

	public ParserContext process(ParserContext parserContext) {

		InsertionModeFactory factory = InsertionModeFactory.getInstance();
		Token token = parserContext.getTokenizerContext().getCurrentToken();

		switch (token.getType()) {
		case start_tag:
			switch (token.getValue()) {
			// Clear the stack back to a table row context. (See below.)
			// Insert an HTML element for the token, then switch the insertion
			// mode to "in cell".
			// Insert a marker at the end of the list of active formatting
			// elements.
			case "th":
			case "td":
				clearTheStackBackToATableRowContext(parserContext);
				InsertAnHTMLElement.run(parserContext, token);
				parserContext.setInsertionMode(factory
						.getInsertionMode(InsertionMode.in_cell));
				ListOfActiveFormattingElements.insertMarker(parserContext);
				break;

			case "caption":
			case "col":
			case "colgroup":
			case "tbody":
			case "tfoot":
			case "thead":
			case "tr":
				case1(parserContext);
				break;
			default:
				anythingElse(parserContext);
				break;
			}
			break;
		case end_tag:
			switch (token.getValue()) {
			// An end tag whose tag name is "tr"
			// If the stack of open elements does not have a tr element in table
			// scope, this is a parse error; ignore the token.
			// Otherwise:
			// Clear the stack back to a table row context. (See below.)
			// Pop the current node (which will be a tr element) from the stack
			// of open elements. Switch the insertion mode to "in table body".
			case "tr":
				if (!ElementInScope.isInTableScope(parserContext, "tr"))
					parserContext
							.addParseErrors(ParseErrorType.UnexpectedToken);
				else {
					clearTheStackBackToATableRowContext(parserContext);
					parserContext.getOpenElements().pop();
					parserContext.setInsertionMode(factory
							.getInsertionMode(InsertionMode.in_table_body));
				}
				break;

			case "table":
				case1(parserContext);
				break;

			// An end tag whose tag name is one of: "tbody", "tfoot", "thead"
			// If the stack of open elements does not have an element in table
			// scope that is an HTML element and with the same tag name as the
			// token, this is a parse error; ignore the token.
			// If the stack of open elements does not have a tr element in table
			// scope, ignore the token.
			// Otherwise:
			// Clear the stack back to a table row context. (See below.)
			// Pop the current node (which will be a tr element) from the stack
			// of open elements. Switch the insertion mode to "in table body".
			// Reprocess the token.
			case "tbody":
			case "tfoot":
			case "thead":
				if (!ElementInScope.isInTableScope(parserContext,
						token.getValue()))
					parserContext
							.addParseErrors(ParseErrorType.UnexpectedToken);
				else if (!ElementInScope.isInTableScope(parserContext, "tr")) {
					clearTheStackBackToATableRowContext(parserContext);
					parserContext.getOpenElements().pop();
					parserContext.setInsertionMode(factory
							.getInsertionMode(InsertionMode.in_table_body));
					parserContext.setFlagReconsumeToken(true);
				}
				break;

			// An end tag whose tag name is one of: "body", "caption", "col",
			// "colgroup", "html", "td", "th", "tr"
			// Parse error. Ignore the token.
			case "body":
			case "caption":
			case "col":
			case "colgroup":
			case "html":
			case "td":
			case "th":
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
				break;
			default:
				anythingElse(parserContext);
				break;
			}
			break;
		default:
			anythingElse(parserContext);
			break;
		}
		return parserContext;
	}

	public void anythingElse(ParserContext parserContext) {
		// Anything else
		// Process the token using the rules for the "in table" insertion mode.
		new InTable().process(parserContext);
	}

	private void case1(ParserContext parserContext) {
		// If the stack of open elements does not have a tr element in table
		// scope, this is a parse error; ignore the token.
		// Otherwise:
		// Clear the stack back to a table row context. (See below.)
		// Pop the current node (which will be a tr element) from the stack of
		// open elements. Switch the insertion mode to "in table body".
		// Reprocess the token.
		if (!ElementInScope.isInTableScope(parserContext, "tr"))
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
		else {
			clearTheStackBackToATableRowContext(parserContext);
			parserContext.getOpenElements().pop();
			parserContext.setInsertionMode(InsertionModeFactory.getInstance()
					.getInsertionMode(InsertionMode.in_table_body));
			parserContext.setFlagReconsumeToken(true);
		}
	}

	private void clearTheStackBackToATableRowContext(ParserContext parserContext) {
		// it means that the UA must, while the current node is not a tr,
		// template, or html element, pop elements from the stack of open
		// elements.
		while (true) {
			Element element = parserContext.getOpenElements().pop();
			if (element.getNodeName().equals("tr")
					|| element.getNodeName().equals("template")
					|| element.getNodeName().equals("html")) {
				parserContext.getOpenElements().push(element);
				return;
			}
		}
	}
}