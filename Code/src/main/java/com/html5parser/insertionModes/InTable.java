package com.html5parser.insertionModes;

import org.w3c.dom.Element;

import com.html5parser.algorithms.ElementInScope;
import com.html5parser.algorithms.InsertAnHTMLElement;
import com.html5parser.algorithms.InsertComment;
import com.html5parser.algorithms.ListOfActiveFormattingElements;
import com.html5parser.algorithms.ResetTheInsertionModeAppropriately;
import com.html5parser.classes.InsertionMode;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.classes.token.TagToken;
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
				parserContext.setFlagReconsumeToken(true);
			} else
				anythingElse(parserContext);
			break;

		// A comment token
		// Insert a comment.
		case comment:
			InsertComment.run(parserContext, token);
			break;

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
				InsertAnHTMLElement.run(parserContext, token);
				parserContext.setInsertionMode(factory
						.getInsertionMode(InsertionMode.in_caption));
				break;

			// A start tag whose tag name is "colgroup"
			// Clear the stack back to a table context. (See below.)
			// Insert an HTML element for the token, then switch the
			// insertion mode to "in column group".
			case "colgroup":
				clearTheStackBackToATableContext(parserContext);
				InsertAnHTMLElement.run(parserContext, token);
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
				InsertAnHTMLElement.run(parserContext, new TagToken(
						TokenType.start_tag, "colgrup"));
				parserContext.setInsertionMode(factory
						.getInsertionMode(InsertionMode.in_column_group));
				parserContext.setFlagReconsumeToken(true);
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
				InsertAnHTMLElement.run(parserContext, token);
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
				InsertAnHTMLElement.run(parserContext, new TagToken(
						TokenType.start_tag, "tbody"));
				parserContext.setInsertionMode(factory
						.getInsertionMode(InsertionMode.in_table_body));
				parserContext.setFlagReconsumeToken(true);
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
					parserContext.setFlagReconsumeToken(true);
				}
				break;
			// A start tag whose tag name is one of: "style", "script",
			// "template"
			// Process the token using the rules for the "in head" insertion
			// mode.
			case "style":
			case "script":
			case "template":
				new InHead().process(parserContext);
				break;

			// A start tag whose tag name is "input"
			// If the token does not have an attribute with the name "type",
			// or if it does, but that attribute's value is not an ASCII
			// case-insensitive match for the string "hidden", then: act as
			// described in the "anything else" entry below.
			// Otherwise:
			// Parse error.
			// Insert an HTML element for the token.
			// Pop that input element off the stack of open elements.
			// Acknowledge the token's self-closing flag, if it is set.
			case "input":
				Boolean isInputHidden = false;
				for (TagToken.Attribute a : ((TagToken) token).getAttributes())
					if (a.getName().equals("type")
							&& a.getValue().equals("hidden"))
						isInputHidden = true;
				if (!isInputHidden)
					anythingElse(parserContext);
				else {
					parserContext
							.addParseErrors(ParseErrorType.UnexpectedToken);
					InsertAnHTMLElement.run(parserContext, token);
					parserContext.getOpenElements().pop();
					((TagToken) token).setFlagAcknowledgeSelfClosingTag(true);
				}

				break;
			// A start tag whose tag name is "form"
			// Parse error.
			// If there is a template element on the stack of open elements, or
			// if the form element pointer is not null, ignore the token.
			// Otherwise:
			// Insert an HTML element for the token, and set the form element
			// pointer to point to the element created.
			// Pop that form element off the stack of open elements.
			case "form":
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
				Boolean isTemplateInStack = false;
				for (Element t : parserContext.getOpenElements())
					if (t.getNodeName().equals("template")) {
						isTemplateInStack = true;
						break;
					}
				if (parserContext.getFormElementPointer() == null
						&& !isTemplateInStack) {
					InsertAnHTMLElement.run(parserContext, token);
					parserContext.setFormElementPointer(parserContext
							.getCurrentNode());
					parserContext.getOpenElements().pop();
				}
				break;
			default:
				anythingElse(parserContext);
				break;
			}
			break;
		case end_tag:
			switch (token.getValue()) {
			// An end tag whose tag name is "table"
			// If the stack of open elements does not have a table element in
			// table scope, this is a parse error; ignore the token.
			// Otherwise:
			// Pop elements from this stack until a table element has been
			// popped from the stack.
			// Reset the insertion mode appropriately.
			case "table":
				if (ElementInScope.isInTableScope(parserContext, "table")) {
					while (true) {
						Element element = parserContext.getOpenElements().pop();
						if (element.getNodeName().equals("table"))
							break;
					}
					ResetTheInsertionModeAppropriately.Run(parserContext);
				}
				break;
			// An end tag whose tag name is one of: "body", "caption", "col",
			// "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", "tr"
			// Parse error. Ignore the token.
			case "body":
			case "caption":
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
			// An end tag whose tag name is "template"
			// Process the token using the rules for the "in head" insertion
			// mode.
			case "template":
				new InHead().process(parserContext);
				break;
			default:
				anythingElse(parserContext);
				break;
			}
			break;
		// An end-of-file token
		// Process the token using the rules for the "in body" insertion mode.
		case end_of_file:
			new InBody().process(parserContext);
			break;
		default:
			anythingElse(parserContext);
			break;
		}
		return parserContext;
	}

	public void anythingElse(ParserContext parserContext) {
		anythingElse(parserContext, parserContext.getTokenizerContext()
				.getCurrentToken());
	}

	public void anythingElse(ParserContext parserContext, Token token) {
		// Anything else
		// Parse error. Enable foster parenting, process the token using the
		// rules for the "in body" insertion mode, and then disable foster
		// parenting.
		parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
		parserContext.setFlagFosterParenting(true);
		parserContext = new InBody().process(parserContext);
		parserContext.setFlagFosterParenting(false);
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