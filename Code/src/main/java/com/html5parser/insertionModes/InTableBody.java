package com.html5parser.insertionModes;

import org.w3c.dom.Element;

import com.html5parser.algorithms.ElementInScope;
import com.html5parser.algorithms.InsertAnHTMLElement;
import com.html5parser.classes.InsertionMode;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.factories.InsertionModeFactory;
import com.html5parser.interfaces.IInsertionMode;
import com.html5parser.parseError.ParseErrorType;

public class InTableBody implements IInsertionMode {

	public ParserContext process(ParserContext parserContext) {

		InsertionModeFactory factory = InsertionModeFactory.getInstance();
		Token token = parserContext.getTokenizerContext().getCurrentToken();

		switch (token.getType()) {
		case start_tag:
			switch (token.getValue()) {
			// A start tag whose tag name is "tr"
			// Clear the stack back to a table body context. (See below.)
			// Insert an HTML element for the token, then switch the insertion
			// mode to "in row".
			case "tr":
				clearTheStackBackToATableBodyContext(parserContext);
				InsertAnHTMLElement.run(parserContext, token);
				parserContext.setInsertionMode(factory
						.getInsertionMode(InsertionMode.in_row));
				break;

			// A start tag whose tag name is one of: "th", "td"
			// Parse error.
			// Clear the stack back to a table body context. (See below.)
			// Insert an HTML element for a "tr" start tag token with no
			// attributes, then switch the insertion mode to "in row".
			// Reprocess the current token.
			case "th":
			case "td":
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
				clearTheStackBackToATableBodyContext(parserContext);
				InsertAnHTMLElement.run(parserContext, new Token(
						TokenType.start_tag, "tr"));
				parserContext.setInsertionMode(factory
						.getInsertionMode(InsertionMode.in_row));
				parserContext.setFlagReconsumeToken(true);
				break;

			case "caption":
			case "col":
			case "colgroup":
			case "tbody":
			case "tfoot":
			case "thead":
				case1(parserContext);
				break;
			default:
				anythingElse(parserContext);
				break;
			}
			break;
		case end_tag:
			switch (token.getValue()) {
			// An end tag whose tag name is one of: "tbody", "tfoot", "thead"
			// If the stack of open elements does not have an element in table
			// scope that is an HTML element and with the same tag name as the
			// token, this is a parse error; ignore the token.
			// Otherwise:
			// Clear the stack back to a table body context. (See below.)
			// Pop the current node from the stack of open elements. Switch the
			// insertion mode to "in table".
			case "tbody":
			case "tfoot":
			case "thead":
				if (!ElementInScope.isInTableScope(parserContext,
						token.getValue()))
					parserContext
							.addParseErrors(ParseErrorType.UnexpectedToken);
				else {
					clearTheStackBackToATableBodyContext(parserContext);
					parserContext.getOpenElements().pop();
					parserContext.setInsertionMode(factory
							.getInsertionMode(InsertionMode.in_table));
				}
				break;

			case "table":
				case1(parserContext);
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
			case "tr":
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
		// A start tag whose tag name is one of: "caption", "col", "colgroup",
		// "tbody", "tfoot", "thead"
		// An end tag whose tag name is "table"
		// If the stack of open elements does not have a tbody, thead, or tfoot
		// element in table scope, this is a parse error; ignore the token.
		// Otherwise:
		// Clear the stack back to a table body context. (See below.)
		// Pop the current node from the stack of open elements. Switch the
		// insertion mode to "in table".
		// Reprocess the token.
		if (!ElementInScope.isInTableScope(parserContext, "tbody")
				|| !ElementInScope.isInTableScope(parserContext, "thead")
				|| !ElementInScope.isInTableScope(parserContext, "tfoot"))
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
		else {
			clearTheStackBackToATableBodyContext(parserContext);
			parserContext.getOpenElements().pop();
			parserContext.setInsertionMode(InsertionModeFactory.getInstance()
					.getInsertionMode(InsertionMode.in_table));
			parserContext.setFlagReconsumeToken(true);
		}
	}

	private void clearTheStackBackToATableBodyContext(
			ParserContext parserContext) {
		// it means that the UA must, while the current node is not a tbody,
		// tfoot, thead, template, or html element, pop elements from the stack
		// of open elements.
		while (true) {
			Element element = parserContext.getOpenElements().pop();
			if (element.getNodeName().equals("tbody")
					|| element.getNodeName().equals("tfoot")
					|| element.getNodeName().equals("thead")
					|| element.getNodeName().equals("template")
					|| element.getNodeName().equals("html")) {
				parserContext.getOpenElements().push(element);
				return;
			}
		}
	}
}