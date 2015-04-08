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

public class InCaption implements IInsertionMode {

	public ParserContext process(ParserContext parserContext) {

		Token token = parserContext.getTokenizerContext().getCurrentToken();

		switch (token.getType()) {

		case start_tag:
			switch (token.getValue()) {
			case "caption":
			case "col":
			case "colgroup":
			case "tbody":
			case "td":
			case "tfoot":
			case "th":
			case "thead":
			case "tr":
				case1(parserContext);
				break;

			default:
				anythingElse(parserContext);
				break;
			}
		case end_tag:
			switch (token.getValue()) {
			// An end tag whose tag name is "caption"
			// If the stack of open elements does not have a caption element in
			// table scope, this is a parse error; ignore the token. (fragment
			// case)
			// Otherwise:
			// Generate implied end tags.
			// Now, if the current node is not a caption element, then this is a
			// parse error.
			// Pop elements from this stack until a caption element has been
			// popped from the stack.
			// Clear the list of active formatting elements up to the last
			// marker.
			// Switch the insertion mode to "in table".
			case "caption":
				if (!ElementInScope.isInTableScope(parserContext, "caption")) {
					parserContext
							.addParseErrors(ParseErrorType.UnexpectedToken);
				} else {
					GenerateImpliedEndTags.run(parserContext);
					if (!parserContext.getCurrentNode().getNodeName()
							.equals("caption"))
						parserContext
								.addParseErrors(ParseErrorType.UnexpectedToken);
					while (true) {
						Element e = parserContext.getOpenElements().pop();
						if (e.getNodeName().equals("caption"))
							break;
					}
					ListOfActiveFormattingElements.clear(parserContext);
					parserContext.setInsertionMode(InsertionModeFactory
							.getInstance().getInsertionMode(
									InsertionMode.in_table));
				}
				break;
			case "table":
				case1(parserContext);
				break;
			// An end tag whose tag name is one of: "body", "col", "colgroup",
			// "html", "tbody", "td", "tfoot", "th", "thead", "tr"
			// Parse error. Ignore the token.
			case "body":
			case "col":
			case "colgroup":
			case "html":
			case "tbody":
			case "td":
			case "tfoot":
			case "th":
			case "thead":
			case "tr":
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
				break;
			default:
				anythingElse(parserContext);
				break;
			}
		default:
			anythingElse(parserContext);
			break;
		}
		return parserContext;
	}

	private void anythingElse(ParserContext parserContext) {
		// TODO process the token using the rules for the "in body"
		throw new UnsupportedOperationException();
	}

	private void case1(ParserContext parserContext) {
		// A start tag whose tag name is one of: "caption", "col", "colgroup",
		// "tbody", "td", "tfoot", "th", "thead", "tr"
		// An end tag whose tag name is "table"
		// Parse error.
		// If the stack of open elements does not have a caption element in
		// table scope, ignore the token. (fragment case)
		// Otherwise:
		// Pop elements from this stack until a caption element has been popped
		// from the stack.
		// Clear the list of active formatting elements up to the last marker.
		// Switch the insertion mode to "in table".
		// Reprocess the token.

		parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
		if (ElementInScope.isInTableScope(parserContext, "caption")) {
			while (true) {
				Element e = parserContext.getOpenElements().pop();
				if (e.getNodeName().equals("caption"))
					break;
			}
			ListOfActiveFormattingElements.clear(parserContext);
			parserContext.setInsertionMode(InsertionModeFactory.getInstance()
					.getInsertionMode(InsertionMode.in_table));
			parserContext.setFlagReconsumeToken(true);
		}
	}
}
