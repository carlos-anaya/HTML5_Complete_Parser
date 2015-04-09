package com.html5parser.insertionModes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.html5parser.algorithms.AdjustedInsertionLocation;
import com.html5parser.algorithms.CreateAnElementForAToken;
import com.html5parser.algorithms.InsertAnHTMLElement;
import com.html5parser.algorithms.InsertCharacter;
import com.html5parser.algorithms.InsertComment;
import com.html5parser.algorithms.ListOfActiveFormattingElements;
import com.html5parser.classes.InsertionMode;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.classes.token.TagToken;
import com.html5parser.factories.InsertionModeFactory;
import com.html5parser.interfaces.IInsertionMode;
import com.html5parser.parseError.ParseErrorType;

public class InHeadNoScript implements IInsertionMode {

	public ParserContext process(ParserContext parserContext) {

		InsertionModeFactory factory = InsertionModeFactory.getInstance();
		Token token = parserContext.getTokenizerContext().getCurrentToken();
		Document doc = parserContext.getDocument();
		TokenType tokenType = token.getType();
		
		/*
		 * A DOCTYPE token Parse error. Ignore the token.
		 */
		if (tokenType == TokenType.DOCTYPE) {
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
		 * An end tag whose tag name is "noscript"
		 * Pop the current node (which will be a noscript element) from the stack of open elements; 
		 * the new current node will be a head element.
		 * Switch the insertion mode to "in head".
		 */
		else if (tokenType == TokenType.end_tag
				&& token.getValue().equals("noscript")){
			parserContext.getOpenElements().pop();
			parserContext.setInsertionMode(factory
					.getInsertionMode(InsertionMode.in_head));
			
		}
		/*
		 * A character token that is one of U+0009 CHARACTER TABULATION, 
		 * "LF" (U+000A), "FF" (U+000C), "CR" (U+000D), or U+0020 SPACE
		 * A comment token
		 * A start tag whose tag name is one of: "basefont", "bgsound", 
		 * "link", "meta", "noframes", "style"
		 * Process the token using the rules for the "in head" insertion mode.
		 */
		else if (tokenType == TokenType.character
				&& (token.getValue().equals(
						String.valueOf(Character.toChars(0x0009)))
						|| token.getValue().equals(
								String.valueOf(Character.toChars(0x000A)))
						|| token.getValue().equals(
								String.valueOf(Character.toChars(0x000C)))
						|| token.getValue().equals(
								String.valueOf(Character.toChars(0x000D))) 
						|| token.getValue().equals(
								String.valueOf(Character.toChars(0x0020))))) {
			IInsertionMode insertionMode = factory.getInsertionMode(InsertionMode.in_head);
			insertionMode.process(parserContext);
		}
		else if (tokenType == TokenType.comment) {
			IInsertionMode insertionMode = factory.getInsertionMode(InsertionMode.in_head);
			insertionMode.process(parserContext);
		}
		else if(tokenType == TokenType.start_tag
				&& (token.getValue().equals("basefont")
				|| token.getValue().equals("bgsound")
				|| token.getValue().equals("link")
				|| token.getValue().equals("noframes")
				|| token.getValue().equals("style")
				|| token.getValue().equals("meta"))){
			IInsertionMode insertionMode = factory.getInsertionMode(InsertionMode.in_head);
			insertionMode.process(parserContext);
		}
		/*
		 * An end tag whose tag name is "br"
		 * Act as described in the "anything else" entry below.
		 * A start tag whose tag name is one of: "head", "noscript"
		 * Any other end tag
		 * Parse error. Ignore the token.
		 */
		else if ((tokenType == TokenType.start_tag
				&& (token.getValue().equals("head")
						||token.getValue().equals("noscript"))
						|| (tokenType == TokenType.end_tag && 
								!token.getValue().equals("br")))){
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
		}
		/*
		 * Anything else
		 * Parse error.
		 * Pop the current node (which will be a noscript element) from the stack of open elements; 
		 * the new current node will be a head element.
		 * Switch the insertion mode to "in head".
		 * Reprocess the token.
		 */
		else {
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
			parserContext.getOpenElements().pop();
			parserContext.setInsertionMode(factory
					.getInsertionMode(InsertionMode.in_head));
			parserContext.setFlagReconsumeToken(true);
		}
		return parserContext;
	}
}
