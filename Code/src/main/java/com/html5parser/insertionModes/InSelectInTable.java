package com.html5parser.insertionModes;

import org.w3c.dom.Element;

import com.html5parser.algorithms.ElementInScope;
import com.html5parser.algorithms.ResetTheInsertionModeAppropriately;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.interfaces.IInsertionMode;
import com.html5parser.parseError.ParseErrorType;

public class InSelectInTable implements IInsertionMode {

	public ParserContext process(ParserContext parserContext) {

		Token token = parserContext.getTokenizerContext().getCurrentToken();

		switch (token.getType()) {
		case start_tag:
			switch (token.getValue()) {
			// A start tag whose tag name is one of: "caption", "table",
			// "tbody", "tfoot", "thead", "tr", "td", "th"
			// Parse error.
			// Pop elements from the stack of open elements until a select
			// element has been popped from the stack.
			// Reset the insertion mode appropriately.
			// Reprocess the token.
			case "caption":
			case "table":
			case "tbody":
			case "tfoot":
			case "thead":
			case "tr":
			case "td":
			case "th":
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
				while (true) {
					Element element = parserContext.getOpenElements().pop();
					if (element.getNodeName().equals("select")) {
						break;
					}
				}
				ResetTheInsertionModeAppropriately.Run(parserContext);
				parserContext.setFlagReconsumeToken(true);
				break;
			default:
				anythingElse(parserContext);
				break;
			}
			break;
		case end_tag:
			switch (token.getValue()) {
			// An end tag whose tag name is one of: "caption", "table", "tbody",
			// "tfoot", "thead", "tr", "td", "th"
			// Parse error.
			// If the stack of open elements does not have an element in table
			// scope that is an HTML element and with the same tag name as that
			// of the token, then ignore the token.
			// Otherwise:
			// Pop elements from the stack of open elements until a select
			// element has been popped from the stack.
			// Reset the insertion mode appropriately.
			// Reprocess the token.
			case "caption":
			case "table":
			case "tbody":
			case "tfoot":
			case "thead":
			case "tr":
			case "td":
			case "th":
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
				if (ElementInScope.isInTableScope(parserContext,
						token.getValue())) {
					while (true) {
						Element element = parserContext.getOpenElements().pop();
						if (element.getNodeName().equals("select")) {
							break;
						}
					}
					ResetTheInsertionModeAppropriately.Run(parserContext);
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
		// Process the token using the rules for the "in select" insertion mode.
		new InSelect().process(parserContext);
	}
}