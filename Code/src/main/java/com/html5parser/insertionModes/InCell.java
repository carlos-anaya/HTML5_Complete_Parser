package com.html5parser.insertionModes;

import org.w3c.dom.Element;

import com.html5parser.algorithms.ElementInScope;
import com.html5parser.algorithms.GenerateImpliedEndTags;
import com.html5parser.algorithms.ListOfActiveFormattingElements;
import com.html5parser.classes.InsertionMode;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.factories.InsertionModeFactory;
import com.html5parser.interfaces.IInsertionMode;
import com.html5parser.parseError.ParseErrorType;

public class InCell implements IInsertionMode {

	public ParserContext process(ParserContext parserContext) {

		InsertionModeFactory factory = InsertionModeFactory.getInstance();
		Token token = parserContext.getTokenizerContext().getCurrentToken();

		switch (token.getType()) {
		case start_tag:
			switch (token.getValue()) {
			// A start tag whose tag name is one of: "caption", "col",
			// "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr"
			// If the stack of open elements does not have a td or th element in
			// table scope, then this is a parse error; ignore the token.
			// (fragment case)
			// Otherwise, close the cell (see below) and reprocess the token.
			case "caption":
			case "col":
			case "colgroup":
			case "tbody":
			case "td":
			case "tfoot":
			case "th":
			case "thead":
			case "tr":
				if (!ElementInScope.isInTableScope(parserContext, "td")
						|| !ElementInScope.isInTableScope(parserContext, "th"))
					parserContext
							.addParseErrors(ParseErrorType.UnexpectedToken);
				else {
					closeTheCell(parserContext);
					parserContext.setFlagReconsumeToken(true);
				}
				break;

			default:
				anythingElse(parserContext);
				break;
			}
			break;
		case end_tag:
			switch (token.getValue()) {

			// An end tag whose tag name is one of: "td", "th"
			// If the stack of open elements does not have an element in table
			// scope that is an HTML element and with the same tag name as that
			// of the token, then this is a parse error; ignore the token.
			// Otherwise:
			// Generate implied end tags.
			// Now, if the current node is not an HTML element with the same tag
			// name as the token, then this is a parse error.
			// Pop elements from the stack of open elements stack until an HTML
			// element with the same tag name as the token has been popped from
			// the stack.
			// Clear the list of active formatting elements up to the last
			// marker.
			// Switch the insertion mode to "in row".
			case "td":
			case "th":
				if (!ElementInScope.isInTableScope(parserContext,
						token.getValue()))
					parserContext
							.addParseErrors(ParseErrorType.UnexpectedToken);
				else {
					GenerateImpliedEndTags.run(parserContext);
					if (!parserContext.getCurrentNode().getNodeName()
							.equals(token.getValue()))
						parserContext
								.addParseErrors(ParseErrorType.UnexpectedToken);
					while (true) {
						Element element = parserContext.getOpenElements().pop();
						if (element.getNodeName().equals(token.getValue())) {
							break;
						}
					}
					ListOfActiveFormattingElements.clear(parserContext);
					parserContext.setInsertionMode(factory
							.getInsertionMode(InsertionMode.in_row));
				}
				break;

			// An end tag whose tag name is one of: "body", "caption", "col",
			// "colgroup", "html"
			// Parse error. Ignore the token.
			case "body":
			case "caption":
			case "col":
			case "colgroup":
			case "html":
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
				break;

			// An end tag whose tag name is one of: "table", "tbody", "tfoot",
			// "thead", "tr"
			// If the stack of open elements does not have an element in table
			// scope that is an HTML element and with the same tag name as that
			// of the token, then this is a parse error; ignore the token.
			// Otherwise, close the cell (see below) and reprocess the token.
			case "table":
			case "tbody":
			case "tfoot":
			case "thead":
			case "tr":
				if (!ElementInScope.isInTableScope(parserContext,
						token.getValue()))
					parserContext
							.addParseErrors(ParseErrorType.UnexpectedToken);
				else {
					closeTheCell(parserContext);
					parserContext.setFlagReconsumeToken(true);
				}
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
		// Process the token using the rules for the "in body" insertion mode.
		new InBody().process(parserContext);
	}

	private void closeTheCell(ParserContext parserContext) {

		// 1 Generate implied end tags.
		GenerateImpliedEndTags.run(parserContext);
		// 2 If the current node is not now a td element or a th element, then
		// this is a parse error.
		if (!(parserContext.getCurrentNode().getNodeName().equals("td") || parserContext
				.getCurrentNode().getNodeName().equals("th")))
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
		// 3 Pop elements from the stack of open elements stack until a td
		// element or a th element has been popped from the stack.
		while (true) {
			Element element = parserContext.getOpenElements().pop();
			if (element.getNodeName().equals("td")
					|| element.getNodeName().equals("th")) {
				break;
			}
		}
		// 4 Clear the list of active formatting elements up to the last marker.
		ListOfActiveFormattingElements.clear(parserContext);
		// 5 Switch the insertion mode to "in row".
		parserContext.setInsertionMode(InsertionModeFactory.getInstance()
				.getInsertionMode(InsertionMode.in_row));
	}
}