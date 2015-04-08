package com.html5parser.insertionModes;

import java.util.ArrayList;

import org.w3c.dom.Element;

import com.html5parser.algorithms.ElementInScope;
import com.html5parser.algorithms.InsertAnHTMLElement;
import com.html5parser.algorithms.InsertCharacter;
import com.html5parser.algorithms.InsertComment;
import com.html5parser.algorithms.ResetTheInsertionModeAppropriately;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.interfaces.IInsertionMode;
import com.html5parser.parseError.ParseErrorType;

public class InSelect implements IInsertionMode {

	public ParserContext process(ParserContext parserContext) {

		Token token = parserContext.getTokenizerContext().getCurrentToken();
		String currentNodeName = parserContext.getCurrentNode().getNodeName();

		switch (token.getType()) {
		// A character token that is U+0000 NULL
		// Parse error. Ignore the token.
		// Any other character token
		// Insert the token's character.
		case character:
			if (token.getIntValue() == 0x0000)
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
			else
				InsertCharacter.run(parserContext, token);
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
			// A start tag whose tag name is "html"
			// Process the token using the rules for the "in body" insertion
			// mode.
			case "html":
				// TODO process using in body
				throw new UnsupportedOperationException();

				// A start tag whose tag name is "option"
				// If the current node is an option element, pop that node from
				// the stack of open elements.
				// Insert an HTML element for the token.
			case "option":
				if (currentNodeName.equals("option"))
					parserContext.getOpenElements().pop();
				InsertAnHTMLElement.run(parserContext, token);

				// A start tag whose tag name is "optgroup"
				// If the current node is an option element, pop that node from
				// the stack of open elements.
				// If the current node is an optgroup element, pop that node
				// from the stack of open elements.
				// Insert an HTML element for the token.
			case "optgroup":
				if (currentNodeName.equals("option"))
					parserContext.getOpenElements().pop();
				if (currentNodeName.equals("optgroup"))
					parserContext.getOpenElements().pop();
				InsertAnHTMLElement.run(parserContext, token);
				break;

			// A start tag whose tag name is "select"
			// Parse error.
			// Pop elements from the stack of open elements until a select
			// element has been popped from the stack.
			// Reset the insertion mode appropriately.
			case "select":
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
				while (true) {
					Element element = parserContext.getOpenElements().pop();
					if (element.getNodeName().equals("select")) {
						break;
					}
				}
				ResetTheInsertionModeAppropriately.Run(parserContext);
				break;

			// A start tag whose tag name is one of: "input", "keygen",
			// "textarea"
			// Parse error.
			// If the stack of open elements does not have a select element in
			// select scope, ignore the token. (fragment case)
			// Pop elements from the stack of open elements until a select
			// element has been popped from the stack.
			// Reset the insertion mode appropriately.
			// Reprocess the token.
			case "input":
			case "keygen":
			case "textarea":
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
				if (ElementInScope.isInSelectScope(parserContext, "select")) {
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
			// A start tag whose tag name is one of: "script", "template"
			// Process the token using the rules for the "in head" insertion
			// mode.
			case "script":
			case "template":
				// TODO process using in head
				throw new UnsupportedOperationException();

			default:
				anythingElse(parserContext);
				break;
			}
			break;

		case end_tag:
			switch (token.getValue()) {
			// An end tag whose tag name is "optgroup"
			// First, if the current node is an option element, and the node
			// immediately before it in the stack of open elements is an
			// optgroup element, then pop the current node from the stack of
			// open elements.
			// If the current node is an optgroup element, then pop that node
			// from the stack of open elements. Otherwise, this is a parse
			// error; ignore the token.
			case "optgroup":
				if (currentNodeName.equals("option")) {
					ArrayList<Element> e = new ArrayList<>();
					e.addAll(parserContext.getOpenElements());
					if (e.get(e.size() - 2).getNodeName().equals("optgroup")){
						parserContext.getOpenElements().pop();
						currentNodeName = parserContext.getCurrentNode().getNodeName();
					}
				}
				if (currentNodeName.equals("optgroup"))
					parserContext.getOpenElements().pop();
				else
					parserContext
							.addParseErrors(ParseErrorType.UnexpectedToken);
				break;

			// An end tag whose tag name is "option"
			// If the current node is an option element, then pop that node from
			// the stack of open elements. Otherwise, this is a parse error;
			// ignore the token.
			case "option":
				if (currentNodeName.equals("option"))
					parserContext.getOpenElements().pop();
				else
					parserContext
							.addParseErrors(ParseErrorType.UnexpectedToken);
				break;
			// An end tag whose tag name is "select"
			// If the stack of open elements does not have a select element in
			// select scope, this is a parse error; ignore the token. (fragment
			// case)
			// Otherwise:
			// Pop elements from the stack of open elements until a select
			// element has been popped from the stack.
			// Reset the insertion mode appropriately.
			case "select":
				if (!ElementInScope.isInSelectScope(parserContext, "select"))
					parserContext
							.addParseErrors(ParseErrorType.UnexpectedToken);
				else {
					while (true) {
						Element element = parserContext.getOpenElements().pop();
						if (element.getNodeName().equals("select")) {
							break;
						}
					}
					ResetTheInsertionModeAppropriately.Run(parserContext);
				}
				break;

			// An end tag whose tag name is "template"
			// Process the token using the rules for the "in head" insertion
			// mode.
			case "template":
				// TODO process using in head
				throw new UnsupportedOperationException();

			default:
				anythingElse(parserContext);
				break;
			}
			break;

		// An end-of-file token
		// Process the token using the rules for the "in body" insertion mode.
		case end_of_file:
			// TODO process using in body
			throw new UnsupportedOperationException();

		default:
			anythingElse(parserContext);
			break;
		}
		return parserContext;
	}

	public void anythingElse(ParserContext parserContext) {
		// Anything else
		// Parse error. Ignore the token.
		parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
	}
}
