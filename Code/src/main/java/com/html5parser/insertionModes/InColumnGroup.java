package com.html5parser.insertionModes;

import com.html5parser.algorithms.InsertAnHTMLElement;
import com.html5parser.algorithms.InsertCharacter;
import com.html5parser.algorithms.InsertComment;
import com.html5parser.classes.InsertionMode;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.token.TagToken;
import com.html5parser.factories.InsertionModeFactory;
import com.html5parser.interfaces.IInsertionMode;
import com.html5parser.parseError.ParseErrorType;

public class InColumnGroup implements IInsertionMode {

	public ParserContext process(ParserContext parserContext) {

		InsertionModeFactory factory = InsertionModeFactory.getInstance();
		Token token = parserContext.getTokenizerContext().getCurrentToken();

		switch (token.getType()) {
		// A character token that is one of U+0009 CHARACTER TABULATION, "LF"
		// (U+000A), "FF" (U+000C), "CR" (U+000D), or U+0020 SPACE
		// Insert the character.
		case character:
			if (isSpaceCharacter(token.getIntValue()))
				InsertCharacter.run(parserContext, token);
			else
				anythingElse(parserContext);
			break;

		// A comment token
		// Insert a comment.
		case comment:
			InsertComment.run(parserContext, token);
			break;

		case start_tag:
			switch (token.getValue()) {
			// A start tag whose tag name is "html"
			// Process the token using the rules for the "in body" insertion
			// mode.
			case "html":
				// TODO Process using the rules for the "in body"
				throw new UnsupportedOperationException();

				// A start tag whose tag name is "col"
				// Insert an HTML element for the token. Immediately pop the
				// current node off the stack of open elements.
				// Acknowledge the token's self-closing flag, if it is set.
			case "col":
				InsertAnHTMLElement.run(parserContext, token);
				parserContext.getOpenElements().pop();
				((TagToken) token).setFlagAcknowledgeSelfClosingTag(true);
				break;

			// A start tag whose tag name is "template"
			// Process the token using the rules for the "in head" insertion
			// mode.
			case "template":
				// TODO Process using the rules for the "in head"
				throw new UnsupportedOperationException();

			default:
				anythingElse(parserContext);
				break;
			}
			break;
		case end_tag:
			switch (token.getValue()) {
			// An end tag whose tag name is "colgroup"
			// If the current node is not a colgroup element, then this is a
			// parse error; ignore the token.
			// Otherwise, pop the current node from the stack of open elements.
			// Switch the insertion mode to "in table".
			case "colgroup":
				if (!parserContext.getCurrentNode().getNodeName()
						.equals("colgroup"))
					parserContext
							.addParseErrors(ParseErrorType.UnexpectedToken);
				else {
					parserContext.getOpenElements().pop();
					parserContext.setInsertionMode(factory
							.getInsertionMode(InsertionMode.in_table));
				}
				break;

			// An end tag whose tag name is "col"
			// Parse error. Ignore the token.
			case "col":
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
				break;

			// An end tag whose tag name is "template"
			// Process the token using the rules for the "in head" insertion
			// mode.
			case "template":
				// TODO Process using the rules for the "in head"
				throw new UnsupportedOperationException();
			default:
				anythingElse(parserContext);
				break;
			}
			break;
		// An end-of-file token
		// Process the token using the rules for the "in body" insertion mode.
		case end_of_file:
			// TODO Process using the rules for the "in head"
			throw new UnsupportedOperationException();
		default:
			anythingElse(parserContext);
			break;
		}
		return parserContext;
	}

	public void anythingElse(ParserContext parserContext) {
		// Anything else
		// If the current node is not a colgroup element, then this is a parse
		// error; ignore the token.
		// Otherwise, pop the current node from the stack of open elements.
		// Switch the insertion mode to "in table".
		// Reprocess the token.
		if (!parserContext.getCurrentNode().getNodeName().equals("colgroup"))
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
		else {
			parserContext.getOpenElements().pop();
			parserContext.setInsertionMode(InsertionModeFactory.getInstance()
					.getInsertionMode(InsertionMode.in_table));
			parserContext.setFlagReconsumeToken(true);
		}
	}

	private boolean isSpaceCharacter(int value) {
		if (value == 0x0020 || value == 0x0009 || value == 0x000A
				|| value == 0x000C || value == 0x000D)
			return true;
		return false;

	}
}