package com.html5parser.insertionModes;

import com.html5parser.algorithms.InsertComment;
import com.html5parser.classes.InsertionMode;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.factories.InsertionModeFactory;
import com.html5parser.interfaces.IInsertionMode;
import com.html5parser.parseError.ParseErrorType;

public class AfterBody implements IInsertionMode {

	public ParserContext process(ParserContext parserContext) {

		InsertionModeFactory factory = InsertionModeFactory.getInstance();
		Token token = parserContext.getTokenizerContext().getCurrentToken();
		TokenType tokenType = token.getType();

		/**
		 * A character token that is one of U+0009 CHARACTER TABULATION, U+000A
		 * LINE FEED (LF), U+000C FORM FEED (FF), U+000D CARRIAGE RETURN (CR),
		 * or U+0020 SPACE
		 * 
		 * Process the token using the rules for the "in body" insertion mode.
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
			IInsertionMode insertionMode = factory
					.getInsertionMode(InsertionMode.in_body);
			return parserContext = insertionMode.process(parserContext);
		}

		/**
		 * A comment token
		 * 
		 * Insert a comment as the last child of the first element in the stack
		 * of open elements (the html element).
		 */
		if (tokenType == TokenType.comment) {
			InsertComment.run(parserContext, token, parserContext
					.getOpenElements().get(0));
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
		 * An end tag whose tag name is "html"
		 * 
		 * If the parser was originally created as part of the HTML fragment
		 * parsing algorithm, this is a parse error; ignore the token. (fragment
		 * case)
		 * 
		 * Otherwise, switch the insertion mode to "after after body".
		 */
		if (tokenType == TokenType.end_tag && token.getValue().equals("html")) {
			if (parserContext.isFlagHTMLFragmentParser()) {
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
				return parserContext;
			}
			IInsertionMode insertionMode = factory
					.getInsertionMode(InsertionMode.after_after_body);
			parserContext.setInsertionMode(insertionMode);
			return parserContext; // = insertionMode.process(parserContext);
		}

		/**
		 * An end-of-file token
		 * 
		 * Stop parsing.
		 */
		if (tokenType == TokenType.end_of_file) {
			parserContext.setFlagStopParsing(true);
			return parserContext;
		}

		/**
		 * Anything else
		 * 
		 * Parse error. Switch the insertion mode to "in body" and reprocess the token.
		 */
		parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
		parserContext.setInsertionMode(factory
					.getInsertionMode(InsertionMode.in_body));
		parserContext.setFlagReconsumeToken(true);
		return parserContext; 
	}
}
