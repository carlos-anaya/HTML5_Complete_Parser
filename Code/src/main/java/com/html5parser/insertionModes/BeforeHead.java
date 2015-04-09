package com.html5parser.insertionModes;

import org.w3c.dom.Element;

import com.html5parser.algorithms.InsertAnHTMLElement;
import com.html5parser.algorithms.InsertComment;
import com.html5parser.classes.InsertionMode;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.classes.token.TagToken;
import com.html5parser.factories.InsertionModeFactory;
import com.html5parser.interfaces.IInsertionMode;
import com.html5parser.parseError.ParseErrorType;

public class BeforeHead implements IInsertionMode {

	public ParserContext process(ParserContext parserContext) {

		InsertionModeFactory factory = InsertionModeFactory.getInstance();
		Token token = parserContext.getTokenizerContext().getCurrentToken();
		TokenType tokenType = token.getType();

		/*
		 * A character token that is one of U+0009 CHARACTER TABULATION, 
		 * "LF" (U+000A), "FF" (U+000C), "CR" (U+000D), or U+0020 SPACE
		 * Ignore the token.
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
			return parserContext;
		}
		/*
		 * A comment token
		 * Insert a comment.
		 */
		else if (tokenType == TokenType.comment) {
			InsertComment.run(parserContext, token);
		}
		/*
		 * A DOCTYPE token Parse error. Ignore the token.
		 */
		else if (tokenType == TokenType.DOCTYPE) {
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
			return parserContext;
		}
		/*A start tag whose tag name is "html"
		 *Process the token using the rules for the "in body" insertion mode.
		 */
		else if (tokenType == TokenType.start_tag
				&& token.getValue().equals("html")){
			IInsertionMode inBody = factory.getInsertionMode(InsertionMode.in_body);
			parserContext = inBody.process(parserContext);
		}
		/*
		 * start tag whose tag name is "head"
		 * Insert an HTML element for the token.
		 * Set the head element pointer to the newly created head element.
		 * Switch the insertion mode to "in head".
		 */
		else if(tokenType == TokenType.start_tag
				&& token.getValue().equals("head")){
			Element element = InsertAnHTMLElement.run(parserContext, token);
			parserContext.setHeadElementPointer(element);
			parserContext.setInsertionMode(factory
					.getInsertionMode(InsertionMode.in_head));
			return parserContext;
		}
		/* An end tag whose tag name is one of: "head", "body", "html", "br"
		 * Act as described in the "anything else" entry below.
		 * Any other end tag
		 * Parse error. Ignore the token.
		 */
		else if (tokenType == TokenType.end_tag && !(token.getValue().equals("head")
				||token.getValue().equals("body")
				||token.getValue().equals("html")
				||token.getValue().equals("br")
				)){
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
			
		}
		/* Anything else
		 * Insert an HTML element for a "head" start tag token with no attributes.
		 * Set the head element pointer to the newly created head element.
		 * Switch the insertion mode to "in head".
		 * Reprocess the current token.
		 */
		else {
			Token inserttoken = new TagToken(TokenType.start_tag,"head");
			Element element = InsertAnHTMLElement.run(parserContext, inserttoken);
			parserContext.setHeadElementPointer(element);
			parserContext.setInsertionMode(factory
					.getInsertionMode(InsertionMode.in_head));
			parserContext.setFlagReconsumeToken(true);
			return parserContext;
		}
		return parserContext;
	}
}
