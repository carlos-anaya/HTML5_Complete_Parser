package com.html5parser.insertionModes;

import com.html5parser.algorithms.InsertAnHTMLElement;
import com.html5parser.algorithms.InsertCharacter;
import com.html5parser.algorithms.InsertComment;
import com.html5parser.classes.InsertionMode;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.classes.token.TagToken;
import com.html5parser.factories.InsertionModeFactory;
import com.html5parser.interfaces.IInsertionMode;
import com.html5parser.parseError.ParseErrorType;

public class InFrameset implements IInsertionMode {

	public ParserContext process(ParserContext parserContext) {

		InsertionModeFactory factory = InsertionModeFactory.getInstance();
		Token token = parserContext.getTokenizerContext().getCurrentToken();
		TokenType tokenType = token.getType();

		/**
		 * A character token that is one of U+0009 CHARACTER TABULATION, U+000A
		 * LINE FEED (LF), U+000C FORM FEED (FF), U+000D CARRIAGE RETURN (CR),
		 * or U+0020 SPACE
		 * 
		 * Insert the character.
		 */
		if (tokenType == TokenType.character
				&& (token.getValue().equals(
						String.valueOf(Character.toChars(0x0009)))
						|| token.getValue().equals(
								String.valueOf(Character.toChars(0x000A)))
						|| token.getValue().equals(
								String.valueOf(Character.toChars(0x000C)))
						|| token.getValue().equals(
								String.valueOf(Character.toChars(0x000D))) || token
						.getValue().equals(
								String.valueOf(Character.toChars(0x0020))))) {
			InsertCharacter.run(parserContext, token);
			return parserContext;
		}

		/**
		 * A comment token
		 * 
		 * Insert a comment.
		 */
		if (tokenType == TokenType.comment) {
			InsertComment.run(parserContext, token);
			return parserContext;
		}

		/**
		 * A DOCTYPE token
		 * 
		 * Parse error. Ignore the token.
		 */
		if (tokenType == TokenType.DOCTYPE) {
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
			return parserContext;
		}

		/**
		 * A start tag whose tag name is "html"
		 * 
		 * Process the token using the rules for the "in body" insertion mode.
		 */
		if (tokenType == TokenType.start_tag && token.getValue().equals("html")) {
			IInsertionMode insertionMode = factory
					.getInsertionMode(InsertionMode.in_body);
			return parserContext = insertionMode.process(parserContext);
		}

		/**
		 * A start tag whose tag name is "frameset"
		 * 
		 * Insert an HTML element for the token.
		 */
		if (tokenType == TokenType.start_tag
				&& token.getValue().equals("frameset")) {
			InsertAnHTMLElement.run(parserContext, token);
			return parserContext;
		}

		/**
		 * An end tag whose tag name is "frameset"
		 * 
		 * If the current node is the root html element, then this is a parse
		 * error; ignore the token. (fragment case)
		 * 
		 * Otherwise, pop the current node from the stack of open elements.
		 * 
		 * If the parser was not originally created as part of the HTML fragment
		 * parsing algorithm (fragment case), and the current node is no longer
		 * a frameset element, then switch the insertion mode to
		 * "after frameset".
		 */
		if (tokenType == TokenType.end_tag
				&& token.getValue().equals("frameset")) {
			String currentNode = parserContext.getCurrentNode().getLocalName();

			if (currentNode.equals("html")) {
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
				return parserContext;
			} else {
				parserContext.getOpenElements().pop();
			}

			if (!parserContext.isFlagHTMLFragmentParser()
					&& !currentNode.equals("frameset")) {
				parserContext.setInsertionMode(factory
						.getInsertionMode(InsertionMode.after_frameset));
			}
			return parserContext;
		}

		/**
		 * A start tag whose tag name is "frame"
		 * 
		 * Insert an HTML element for the token. Immediately pop the current
		 * node off the stack of open elements.
		 * 
		 * Acknowledge the token's self-closing flag, if it is set.
		 */
		if (tokenType == TokenType.start_tag
				&& token.getValue().equals("frame")) {
			InsertAnHTMLElement.run(parserContext, token);
			parserContext.getOpenElements().pop();
			if (((TagToken) token).isFlagSelfClosingTag())
				((TagToken) token).setFlagAcknowledgeSelfClosingTag(true);
			return parserContext;
		}

		/**
		 * A start tag whose tag name is "noframes"
		 * 
		 * Process the token using the rules for the "in head" insertion mode.
		 */
		if (tokenType == TokenType.start_tag
				&& token.getValue().equals("noframes")) {
			IInsertionMode insertionMode = factory
					.getInsertionMode(InsertionMode.in_head);
			return parserContext = insertionMode.process(parserContext);
		}

		/**
		 * An end-of-file token
		 * 
		 * If the current node is not the root html element, then this is a
		 * parse error. Stop parsing.
		 */
		if (tokenType == TokenType.end_of_file) {
			String currentNode = parserContext.getCurrentNode().getLocalName();

			if (!currentNode.equals("html")) {
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
			}
			parserContext.setFlagStopParsing(true);
			return parserContext;

		}

		/**
		 * Anything else
		 * 
		 * Parse error. Ignore the token.
		 */
		parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
		return parserContext;
	}
}
